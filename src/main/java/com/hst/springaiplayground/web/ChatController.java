package com.hst.springaiplayground.web;

import com.hst.springaiplayground.service.chatapi.ChatService;
import com.hst.springaiplayground.service.prompt.PromptService;
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
    private final PromptService promptService;

    public ChatController(ChatService chatService, PromptService promptService) {
        this.chatMemories = new ConcurrentHashMap<>();
        this.chatService = chatService;
        this.promptService = promptService;
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

    @GetMapping(path = "/review-classification")
    public String reviewClassification(@RequestParam String review) {
        return promptService.zeroShotExample(review);
    }

    @GetMapping(path = "/order-to-json")
    public String orderToJson(@RequestParam String orderDetail) {
        return promptService.fewShotExample(orderDetail);
    }

    @GetMapping(path = "/step-back")
    public String stepBackTest(@RequestParam String question) {
        return promptService.stepBackPrompt(question);
    }

    @GetMapping(path = "/chain-of-thought")
    public String chainOfThoughtTest(@RequestParam String question) {
        return promptService.chainOfThoughtPrompt(question);
    }

    @GetMapping(path = "/self-consistency")
    public String self(@RequestParam String content) {
        return promptService.selfConsistency(content);
    }

}
