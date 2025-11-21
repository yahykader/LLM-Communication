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
import com.example.Test_AI_LLM.outputs.MovieList;
import java.util.List;

@RestController
public class AiAgentStructuredController {


    private ChatClient chatClient;

    public AiAgentStructuredController(ChatClient.Builder builder, ChatMemory chatMemory){
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @GetMapping("/askAgent")
    public MovieList askLLM(String query){
        String sytemMessages = """
                Vous étes spécialiste dans le domaine de cinema
                Répond a mla question des ulisateur a ce propos
                """;
        return chatClient.prompt()
                .system(sytemMessages)
                .user(query)
                .call()
                .entity(MovieList.class);
    }
}
