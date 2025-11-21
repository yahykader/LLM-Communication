package com.example.Test_AI_LLM.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AiAgentController {


    private ChatClient chatClient;

    public AiAgentController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @GetMapping("/chat")
    public String askLLM(String query){
        List<Message> exemples = List.of(
                new UserMessage("6+4"),
                new AssistantMessage("le résultats est : 10")
        );
        return chatClient.prompt()
                .system("répond toujours en Majuscule")
                .messages(exemples)
                .user(query)
                .call()
                .content();
    }
}
