package com.hst.springaiplayground.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatClient openAIChatClient;

    public ChatController(ChatClient openAIChatClient) {
        this.openAIChatClient = openAIChatClient;
    }

    @GetMapping(path = "/streaming")
    public Flux<String> chatStreaming(@RequestParam String userMessage) {
        return openAIChatClient.prompt()
                .user(userMessage)
                .stream()
                .content();
    }

}
