package com.example.Test_AI_LLM.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ImageGenerationResponse(
        String imageUrl,
        String prompt,
        String error
) {
    public static ImageGenerationResponse success(String imageUrl, String prompt) {
        return new ImageGenerationResponse(imageUrl, prompt, null);
    }

    public static ImageGenerationResponse error(String prompt, String error) {
        return new ImageGenerationResponse(null, prompt, error);
    }
}
