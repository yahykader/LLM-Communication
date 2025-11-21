package com.example.Test_AI_LLM.controller;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Validated
public class AIAgentGenerateImageController {

    private final OpenAiImageModel openAiImageModel;

    /**
     * Génère une image basée sur le prompt fourni
     *
     * @param request l'objet contenant les paramètres de génération
     * @return Mono contenant l'URL de l'image générée
     */
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ImageGenerationResponse>> generateImage(
            @RequestBody @Validated ImageGenerationRequest request) {

        log.info("Génération d'image demandée avec le prompt: {}", request.getQuery());

        ImageOptions imageOptions = OpenAiImageOptions.builder()
                .quality(request.getQuality())
                .model(request.getModel())
                .width(request.getWidth())
                .height(request.getHeight())
                .style(request.getStyle())
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(request.getQuery(), imageOptions);

        return Mono.fromCallable(() -> {
                    log.debug("Appel API OpenAI pour génération d'image");
                    return openAiImageModel.call(imagePrompt);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(this::extractImageUrl)
                .map(url -> {
                    log.info("Image générée avec succès: {}", url);
                    return ResponseEntity.ok(new ImageGenerationResponse(url, "Image générée avec succès"));
                })
                .timeout(Duration.ofSeconds(request.getTimeoutSeconds()))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof TimeoutException)))
                .onErrorResume(TimeoutException.class, e -> {
                    log.error("Timeout lors de la génération d'image après {}s", request.getTimeoutSeconds(), e);
                    return Mono.error(new ImageGenerationTimeoutException(
                            "La génération d'image a dépassé le délai de " + request.getTimeoutSeconds() + "s", e));
                })
                .onErrorResume(Throwable.class, e -> {
                    log.error("Erreur lors de la génération d'image", e);
                    return Mono.error(new ImageGenerationException("Échec de la génération d'image: " + e.getMessage(), e));
                });
    }

    /**
     * Version simplifiée avec GET (pour compatibilité ascendante)
     */
    @GetMapping(value = "/generate", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> generateImageSimple(
            @RequestParam @NotBlank @Size(min = 3, max = 4000) String query,
            @RequestParam(defaultValue = "30") @Min(5) @Max(120) long timeoutSeconds) {

        ImageGenerationRequest request = ImageGenerationRequest.builder()
                .query(query)
                .timeoutSeconds(timeoutSeconds)
                .build();

        return generateImage(request)
                .map(response -> response.getBody().getImageUrl());
    }

    /**
     * Extrait l'URL de l'image de la réponse
     */
    private Mono<String> extractImageUrl(ImageResponse response) {
        return Mono.justOrEmpty(response.getResult())
                .map(result -> result.getOutput().getUrl())
                .switchIfEmpty(Mono.error(new ImageGenerationException("Aucune image générée dans la réponse", null)));
    }

    // ==================== DTOs ====================

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ImageGenerationRequest {

        @NotBlank(message = "Le prompt ne peut pas être vide")
        @Size(min = 3, max = 4000, message = "Le prompt doit contenir entre 3 et 4000 caractères")
        private String query;

        @Builder.Default
        @Min(value = 5, message = "Le timeout minimum est de 5 secondes")
        @Max(value = 120, message = "Le timeout maximum est de 120 secondes")
        private long timeoutSeconds = 30;

        @Builder.Default
        private String quality = "hd";

        @Builder.Default
        private String model = "dall-e-3";

        @Builder.Default
        private Integer width = 1024;

        @Builder.Default
        private Integer height = 1024;

        @Builder.Default
        private String style = "vivid";  // ou "natural"
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ImageGenerationResponse {
        private String imageUrl;
        private String message;
    }

    // ==================== Exceptions ====================

    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public static class ImageGenerationTimeoutException extends RuntimeException {
        public ImageGenerationTimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class ImageGenerationException extends RuntimeException {
        public ImageGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ==================== Exception Handler ====================

    @ExceptionHandler(ImageGenerationTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeout(ImageGenerationTimeoutException e) {
        log.error("Timeout exception", e);
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(new ErrorResponse("TIMEOUT", e.getMessage()));
    }

    @ExceptionHandler(ImageGenerationException.class)
    public ResponseEntity<ErrorResponse> handleGenerationError(ImageGenerationException e) {
        log.error("Generation exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("GENERATION_ERROR", e.getMessage()));
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ErrorResponse {
        private String errorCode;
        private String message;
    }
}