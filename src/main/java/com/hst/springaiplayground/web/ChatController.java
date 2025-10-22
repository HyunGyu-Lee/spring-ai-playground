package com.hst.springaiplayground.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatClient openAIChatClient;

    public ChatController(ChatClient openAIChatClient) {
        this.openAIChatClient = openAIChatClient;
    }

    @GetMapping
    public ChatResponse chat(@RequestParam String userMessage) {
        return openAIChatClient.prompt()
                .user(userMessage)
                .call()
                .chatResponse();
    }

}
