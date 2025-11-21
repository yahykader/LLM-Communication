package com.example.Test_AI_LLM.controller;

import com.example.Test_AI_LLM.dto.ImageGenerationRequest;
import com.example.Test_AI_LLM.dto.ImageGenerationResponse;
import com.example.Test_AI_LLM.service.ImageGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageGenerationController {

    private final ImageGenerationService imageGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<ImageGenerationResponse> generateImage(
            @Valid @RequestBody ImageGenerationRequest request) {

        log.info("Received image generation request: {}", request.prompt());

        try {
            ImageGenerationResponse response = imageGenerationService.generateImage(request);
            log.info("Image generated successfully");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ImageGenerationResponse(null, request.prompt(), e.getMessage()));

        } catch (Exception e) {
            log.error("Error generating image", e);
            return ResponseEntity.internalServerError()
                    .body(new ImageGenerationResponse(null, request.prompt(),
                            "Internal error: " + e.getMessage()));
        }
    }
}
