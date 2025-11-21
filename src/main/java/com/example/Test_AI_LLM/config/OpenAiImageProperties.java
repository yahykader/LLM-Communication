package com.example.Test_AI_LLM.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openai.image")
public class OpenAiImageProperties {
    private String model = "dall-e-3";
    private String defaultQuality = "hd";
    private String defaultSize = "1024x1024";
}