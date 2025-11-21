package com.example.Test_AI_LLM.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin("*")
public class AiAgentStreamingController {

    private final ChatClient chatClient;

    public AiAgentStreamingController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @GetMapping("/nostream")
    public String nostream(@RequestParam String query) {
        return chatClient
                .prompt()
                .user(query)
                .call()
                .content();
    }

    @GetMapping(value="/stream", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> stream(@RequestParam String query) {
        return chatClient.prompt()
                .user(query)
                .stream()
                .content();
    }
}
