package com.example.Test_AI_LLM.controller;

import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiAgentGenerationImageController {

    private OpenAiImageModel openAiImageModel;


    public AiAgentGenerationImageController(OpenAiImageModel openAiImageModel) {
        this.openAiImageModel = openAiImageModel;
    }

    @GetMapping("/generateImage")
    public String generateImage(String query) {

        ImageOptions imageOptions = OpenAiImageOptions.builder()
                .quality("hd")
                .model("dall-e-3")
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(query, imageOptions);


        String url = openAiImageModel.call(imagePrompt).getResult().getOutput().getUrl();
        return url;
    }
}