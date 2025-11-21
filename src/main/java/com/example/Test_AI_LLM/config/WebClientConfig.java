package com.example.Test_AI_LLM.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {


    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // Créer un ConnectionProvider pour gérer le pool de connexions
        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(50) // nombre max de connexions simultanées
                .pendingAcquireMaxCount(100) // max demandes en attente
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .tcpConfiguration(tcp -> tcp.option(ChannelOption.SO_KEEPALIVE, true));

        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
