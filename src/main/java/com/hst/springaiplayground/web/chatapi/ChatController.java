package com.hst.springaiplayground.web.chatapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public String chat(@RequestParam String userMessage) {
        return chatService.chat(userMessage);
    }

    @GetMapping(path = "/streaming")
    public Flux<String> chatStreaming(@RequestParam String userMessage) {
        return chatService.chatStreaming(userMessage);
    }

    @GetMapping(path = "/translate")
    public String translate(@RequestParam String statement, @RequestParam String language) {
        return chatService.translate(statement, language);
    }

}
