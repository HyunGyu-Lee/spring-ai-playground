package com.hst.springaiplayground.web.chatapi;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {
    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("사용자 질문에 대해 한국어로 대답하세요")
                .defaultOptions(ChatOptions.builder()
                        .maxTokens(1000)  // 최대 토큰 수
                        .temperature(0.7) // 창의성 (값이 클수록 다양항 응답 생성)
                        .build())
                .build();
    }

    public String chat(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

    public Flux<String> chatStreaming(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .stream()
                .content();
    }

    public String translate(String statement, String language) {
        PromptTemplate userPrompt = PromptTemplate.builder()
                .template("다음 한국어 문장을 {language}로 번역해주세요.\n 문장: {statement}")
                .build();

        return chatClient.prompt(userPrompt.create(Map.of("language", language, "statement", statement)))
                .system("""
                    답변을 생성할 때 HTML와 CSS를 사용해서 파란 글자로 출력하세요.
                    <span> 태그 안에 들어갈 내용만 출력하세요.
                    """)
                .call()
                .content();
    }

    public String memorizedChat(String userMessage, List<Message> messages) {
        ChatResponse response = chatClient.prompt()
                .messages(messages) // 이전 대화 내용 추가
                .user(userMessage)
                .call()
                .chatResponse();

        messages.add(UserMessage.builder().text(userMessage).build());
        messages.add(response.getResult().getOutput());

        return response.getResult().getOutput().getText();
    }
}
