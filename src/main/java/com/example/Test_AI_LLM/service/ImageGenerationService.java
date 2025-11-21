package com.example.Test_AI_LLM.service;


import com.example.Test_AI_LLM.config.OpenAiImageProperties;
import com.example.Test_AI_LLM.dto.ImageGenerationRequest;
import com.example.Test_AI_LLM.dto.ImageGenerationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private final OpenAiImageModel openAiImageModel;
    private final OpenAiImageProperties imageProperties;

    public ImageGenerationResponse generateImage(ImageGenerationRequest request) {

        validateRequest(request);

        ImageOptions imageOptions = OpenAiImageOptions.builder()
                .quality(request.quality())
                .model(imageProperties.getModel())
                .width(extractWidth(request.size()))
                .height(extractHeight(request.size()))
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(request.prompt(), imageOptions);

        ImageResponse response = openAiImageModel.call(imagePrompt);

        if (response == null || response.getResult() == null
                || response.getResult().getOutput() == null) {
            throw new RuntimeException("Invalid response from OpenAI API");
        }

        String url = response.getResult().getOutput().getUrl();

        return ImageGenerationResponse.success(url, request.prompt());
    }

    private void validateRequest(ImageGenerationRequest request) {
        if (!isValidQuality(request.quality())) {
            throw new IllegalArgumentException(
                    "Quality must be 'standard' or 'hd', got: " + request.quality());
        }

        if (!isValidSize(request.size())) {
            throw new IllegalArgumentException(
                    "Size must be '1024x1024', '1792x1024', or '1024x1792', got: " + request.size());
        }
    }

    private boolean isValidQuality(String quality) {
        return "standard".equals(quality) || "hd".equals(quality);
    }

    private boolean isValidSize(String size) {
        return "1024x1024".equals(size)
                || "1792x1024".equals(size)
                || "1024x1792".equals(size);
    }

    private int extractWidth(String size) {
        return Integer.parseInt(size.split("x")[0]);
    }

    private int extractHeight(String size) {
        return Integer.parseInt(size.split("x")[1]);
    }
}