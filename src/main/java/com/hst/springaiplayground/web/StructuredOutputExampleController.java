package com.hst.springaiplayground.web;

import com.hst.springaiplayground.service.structuredoutput.StructuredOutputExampleService;
import com.hst.springaiplayground.service.structuredoutput.model.HotelInfo;
import com.hst.springaiplayground.service.structuredoutput.model.ReviewClassification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/bean-output/low-level")
    public HotelInfo beanOutputLowLevel(@RequestParam String question) {
        return service.beanOutputLowLevel(question);
    }

    @GetMapping("/bean-output/high-level")
    public List<HotelInfo> beanOutputHighLevel(@RequestParam String question) {
        return service.beanOutputHighLevel(question);
    }

    @GetMapping("/map-output/low-level")
    public Map<String, Object> mapOutputLowLevel(@RequestParam String question) {
        return service.mapOutputLowLevel(question);
    }

    @GetMapping("/map-output/high-level")
    public Map<String, Object> mapOutputHighLevel(@RequestParam String question) {
        return service.mapOutputHighLevel(question);
    }

    @GetMapping("/review-classification")
    public ReviewClassification reviewClassification(@RequestParam String question) {
        return service.reviewClassification(question);
    }

}
