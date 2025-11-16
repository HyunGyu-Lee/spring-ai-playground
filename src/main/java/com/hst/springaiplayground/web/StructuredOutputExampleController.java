package com.hst.springaiplayground.web;

import com.hst.springaiplayground.service.structuredoutput.StructuredOutputExampleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/structured-output")
public class StructuredOutputExampleController {

    private final StructuredOutputExampleService service;

    public StructuredOutputExampleController(StructuredOutputExampleService service) {
        this.service = service;
    }

    @GetMapping("/list-output/low-level")
    public List<String> listOutputLowLevel(@RequestParam String question) {
        return service.listOutputLowLevel(question);
    }

    @GetMapping("/list-output/high-level")
    public List<String> listOutputHighLevel(@RequestParam String question) {
        return service.listOutputHighLevel(question);
    }

}
