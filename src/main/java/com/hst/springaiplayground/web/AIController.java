package com.hst.springaiplayground.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AIController {

    private final ChatClient openAIChatClient;

    public AIController(ChatClient openAIChatClient) {
        this.openAIChatClient = openAIChatClient;
    }

    @GetMapping("/ask")
    public ChatResponse hello(@RequestParam String userMessage) {
        return openAIChatClient.prompt()
                .user(userMessage)
                .call()
                .chatResponse();
    }

}
