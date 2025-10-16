package com.hst.springaiplayground.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient openAIClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem("당신은 유치원 교사입니다. 아이들을 대하듯한 어조로 이야기하세요.")
                .build();
    }

}
