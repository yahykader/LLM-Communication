package com.example.Test_AI_LLM.controller;

import com.example.Test_AI_LLM.outputs.CarteVitale;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
public class AiAgentMultiModalController {


    private ChatClient chatClient;

    @Value("classpath:/images/CarteVitale.jpg")
    private Resource image;

    @Value("classpath:/images/1.jpg")
    private Resource image1;

    public AiAgentMultiModalController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @GetMapping("/describe")
    public CarteVitale describeImage() {
        return chatClient
                .prompt()
                .system("Donner moi les informations sur l'image Fourni en détaille svp")
                .user(u -> u.text(" Décrire cette image")
                        .media(MediaType.IMAGE_JPEG, image))
                .call()
                .entity(CarteVitale.class);
    }

    @GetMapping("/ask")
    public String askImage(String query) {
        return chatClient
                .prompt()
                .system("répond a la question d'utilisateur sur l'image manuscrite Fourni en détaille svp")
                .user(u -> u.text(query)
                        .media(MediaType.IMAGE_JPEG, image1))
                .call()
                .content();
    }


    @PostMapping(value="/askDowload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String askImageDowload(@RequestParam(name="file") MultipartFile file, String query) throws IOException {
        byte[] bytes = file.getBytes();
        return chatClient
                .prompt()
                .system("répond a la question d'utilisateur sur l'image manuscrite Fourni en détaille svp")
                .user(u -> u.text(query)
                        .media(MediaType.IMAGE_JPEG, new ByteArrayResource(bytes)))
                .call()
                .content();
    }
}