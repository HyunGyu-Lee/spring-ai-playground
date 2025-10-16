package com.hst.springaiplayground.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient openAIClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .build();
    }

}
