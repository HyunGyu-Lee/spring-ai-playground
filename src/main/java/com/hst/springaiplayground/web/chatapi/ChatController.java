package com.hst.springaiplayground.web.chatapi;

import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final Map<String, List<Message>> chatMemories;
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatMemories = new ConcurrentHashMap<>();
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

    @GetMapping(path = "/memorized")
    public String memorizedChat(@RequestParam String uid, @RequestParam String userMessage) {
        chatMemories.computeIfAbsent(uid, key -> new ArrayList<>());
        return chatService.memorizedChat(userMessage, chatMemories.computeIfAbsent(uid, key -> new ArrayList<>()));
    }

}
