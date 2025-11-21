package com.example.Test_AI_LLM.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ImageGenerationRequest(
        @NotBlank(message = "Prompt cannot be empty")
        @Size(min = 3, max = 1000, message = "Prompt must be between 3 and 1000 characters")
        String prompt,

        String quality,  // optional: "standard" or "hd"
        String size      // optional: "1024x1024", "1792x1024", "1024x1792"
) {
    // Constructor avec valeurs par défaut
    public ImageGenerationRequest {
        if (quality == null || quality.isBlank()) {
            quality = "hd";
        }
        if (size == null || size.isBlank()) {
            size = "1024x1024";
        }
    }
}