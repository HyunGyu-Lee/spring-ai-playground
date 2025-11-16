package com.hst.springaiplayground.service.structuredoutput;

import com.hst.springaiplayground.service.structuredoutput.model.HotelInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * LLM 이 응답을 JSON 과 같은 구조화된 형태로 출력하도록 유도하는 예시
 * StructuredOutputConverter
 * ㄴ ListOutputConverter
 * ㄴ MapOutputConverter
 * ㄴ BeanOutputConverter
 */
@Service
public class StructuredOutputExampleService {

    private final ChatClient chatClient;

    public StructuredOutputExampleService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("사용자 질문에 대해 한국어로 대답하세요.")
                .build();
    }

    /**
     * ListOutputConverter 저수준 API
     */
    public List<String> listOutputLowLevel(String city) {
        ListOutputConverter converter = new ListOutputConverter();

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template("{city}에서 유명한 호텔 목록 5개를 출력하세요. {format}")
                .build();

        Prompt prompt = promptTemplate.create(
                Map.of("city", city, "format", converter.getFormat())
        );

        String response = chatClient.prompt(prompt).call().content();

        return converter.convert(response);
    }

    /**
     * ListOutputConverter 고수준 API
     */
    public List<String> listOutputHighLevel(String city) {
        return chatClient.prompt()
                .user("%s에서 유명한 호텔 목록 5개를 출력하세요.".formatted(city))
                .call()
                .entity(new ListOutputConverter());
    }

    /**
     * BeanOutputConverter 저수준 API
     */
    public HotelInfo beanOutputLowLevel(String city) {
        BeanOutputConverter<HotelInfo> converter = new BeanOutputConverter<>(HotelInfo.class);
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template("{city}에서 유명한 호텔 목록 5개를 출력하세요. {format}")
                .build();

        Prompt prompt = promptTemplate.create(
                Map.of("city", city, "format", converter.getFormat())
        );

        String response = chatClient.prompt(prompt).call().content();
        return converter.convert(response);
    }

    /**
     * BeanOutputConverter 고수준 API
     */
    public List<HotelInfo> beanOutputHighLevel(String city) {
        return chatClient.prompt()
                .user("각 도시별로 유명한 호텔 목록 5개를 출력하세요. 도시: %s".formatted(city))
                .call()
                .entity(new BeanOutputConverter<>(new ParameterizedTypeReference<>() {}));
    }
}
