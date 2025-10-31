package com.hst.springaiplayground.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient openAIClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("당신은 최고 수준의 머니 컨설턴트입니다. 반드시 대답할 때 한국어로 대답하고, 존댓말 50자로 대답하세요.")
                .build();
    }

}
