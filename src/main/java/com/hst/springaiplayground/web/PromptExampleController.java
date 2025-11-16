package com.hst.springaiplayground.web;

import com.hst.springaiplayground.service.prompt.PromptExampleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prompt")
public class PromptExampleController {

    private final PromptExampleService promptExampleService;

    public PromptExampleController(PromptExampleService promptExampleService) {
        this.promptExampleService = promptExampleService;
    }

    @GetMapping(path = "/review-classification")
    public String reviewClassification(@RequestParam String review) {
        return promptExampleService.zeroShotExample(review);
    }

    @GetMapping(path = "/order-to-json")
    public String orderToJson(@RequestParam String orderDetail) {
        return promptExampleService.fewShotExample(orderDetail);
    }

    @GetMapping(path = "/step-back")
    public String stepBackTest(@RequestParam String question) {
        return promptExampleService.stepBackPrompt(question);
    }

    @GetMapping(path = "/chain-of-thought")
    public String chainOfThoughtTest(@RequestParam String question) {
        return promptExampleService.chainOfThoughtPrompt(question);
    }

    @GetMapping(path = "/self-consistency")
    public String self(@RequestParam String content) {
        return promptExampleService.selfConsistency(content);
    }

}
