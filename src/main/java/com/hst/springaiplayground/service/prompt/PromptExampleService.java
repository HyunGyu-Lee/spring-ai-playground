package com.hst.springaiplayground.service.prompt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 프롬프트 기본 가이드라인
 * 1. 명확하고 구체적인 요청 : 프롬프트는 모호하지 않고 구체적이어야 하다. 원하는 답변의 범위와 방향을 명확히 정의해야한다.
 * 2. 모델의 이해를 돕는 배경 정보 제공 : LLM 이 답변을 더 정확히 이해할 수 있도록 사용자 메시지에 배경 정보나 문맥을 제공
 * 3. 간결하고 직관적인 문장 사용 : 모호하고 수식어가 많은 복잡한 문장보다는 간단하고 직관적인 문장을 사용
 * 4. 적절한 예시 사용 : LLM 이 사용자가 원하는 스타일이나 출력 형식을 정확히 이해할 수 있도록 예시를 포함
 * 5. 다단계 질문 피하기 : 여러 질문을 한 프롬프트에 담지 말고, 하나의 질문에 집중하여 LLM 이 정확한 답변을 할 수 있도록 한다.
 *
 * 프롬프트 기법
 * 1. 제로-샷 프롬프트 : 예시, 추가 정보 제공 없이 단일 프롬프트로 작업 수행 가능한 경우 사용 (번역, 분류 등)
 * 2. 퓨-샷 프롬프트 : 몇가지 예시를 제공하여 LLM 이 원하는 답변 스타일이나 형식을 이해하도록 돕는 기법
 * 3. 스텝-백 프롬프트 : 복잡한 문제를 단계별로 나누어 해결하는 기법. 문제를 작은 하위 문제로 분해하여 각 단계를 순차적으로 해결
 * 4. Chain-Of-Thought 프롬프트 (COT) : LLM 이 문제 해결 과정을 단계별로 설명하도록 유도하는 기법. 답변 전에 사고 과정을 서술하도록 요청
 */
@Service
public class PromptExampleService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ChatClient chatClient;
    private final PromptTemplate zeroShotPromptTemplate;

    public PromptExampleService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(ChatOptions.builder()
                        .temperature(0.0) // 창의성이 필요한 작업이 아니므로 0 설정 (셋 중 하나만 답변)
                        .build())
                .build();
        this.zeroShotPromptTemplate = PromptTemplate.builder()
                .template("""
                영화 리뷰를 [긍정적, 중립적, 부정적] 중에서 하나로 분류하세요.
                레이블만 반환하세요.
                리뷰: {review}
                """)
                .build();
    }

    public String zeroShotExample(String review) {
        return chatClient.prompt(
                zeroShotPromptTemplate.create(
                        java.util.Map.of("review", review)
                ))
                .call()
                .content();
    }

    public String fewShotExample(String orderDetail) {
        String fewShotPrompt = """
                고객 주문을 JSON 형식으로 변환하시오.
                추가 설명은 포함하지 마시오.
                
                예시1. 작은 피자 하나, 치즈랑 토마토 소스, 페퍼로니 올려주세요
                {
                  "size": "small",
                  "toppings": ["cheese", "tomato source", "peperoni"],
                  "quantity": 1
                }
                
                예시2. 큰 피자 하나, 토마토 소스, 바질, 모짜렐라 올려주세요
                {
                  "size": "large",
                  "toppings": ["tomato source", "basil", "mozzarella"],
                  "quantity": 1
                }
                
                주문 내역: %s
                """.formatted(orderDetail);

        return chatClient.prompt()
                .user(fewShotPrompt)
                .call()
                .content();
    }

    public String stepBackPrompt(String question) {
        String rawQuestions = chatClient.prompt()
                .user("""
                당신은 뛰어난 문제 해결사입니다.
                사용자 문제를 해결하기 위해 Step-Back 기법을 사용합니다.
                주어진 질문을 단계별 질문들로 재구성해 주세요.
                마지막 도출된 질문은 원래 사용자 질문과 정확히 일치해야합니다.
                
                각 단계별 질문을 JSON 배열로 응답하세요. (코드 하이라이팅 등 부수적인 내용 없이 JSON 문법에 부합하는 내용 응답할 것)
                
                질문: %s
                """.formatted(question))
                .call()
                .content();

        List<String> stepQuestions;
        try {
            stepQuestions = objectMapper.readValue(rawQuestions, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            return "질문 분해과정에서 오류가 발생하였습니다.";
        }

        List<String> answers = new ArrayList<>();

        for (String stepQuestion : stepQuestions) {
            String stepAnswer = getStepAnswer(stepQuestion, answers);
            answers.add(stepAnswer);
            System.out.println("Q: " + stepQuestion + " A: " + stepAnswer);
        }

        return answers.getLast();
    }

    private String getStepAnswer(String question, List<String> answers) {
        String context = String.join("\n", answers);
        return chatClient.prompt()
                .user("""
                        %s
                        문맥: %s
                        """.formatted(question, context))
                .call()
                .content();
    }

    public String chainOfThoughtPrompt(String question) {
        return chatClient.prompt()
                .user("""
                        질문: %s
                        한 걸음씩 생각해 봅시다.
                        
                        [예시]
                        질문: 제 동생이 2살일 때, 저는 그의 나이의 두 배였어요.
                        지금 저는 40살인데, 제 동생은 몇 살일까요? 한 걸음씩 생각해봅시다.
                        
                        답변: 제 동생이 2살일 때, 저는 2 x 2 = 4살이었어요.
                        그럼 제가 동생보다 2살 나이가 많습니다.
                        지금 저는 40살이니까, 제 동생은 40 - 2 = 38살입니다.
                        """.formatted(question))
                .call()
                .content();
    }

    public String selfConsistency(String content) {
        String selfConsistencyPrompt = """
                다음 내용을 [IMPORTANT, NOT IMPORTANT] 중 하나로 분류하세요.
                추가 설명은 포함하지 마시오.
                
                질문: %s
                """.formatted(content);
        Map<String, Integer> counts = new HashMap<>();
        counts.put("IMPORTANT", 0);
        counts.put("NOT IMPORTANT", 0);
        for (int i = 0; i < 5; i++) {
            String important = chatClient.prompt()
                    .user(selfConsistencyPrompt)
                    .call()
                    .content();
            if (counts.containsKey(important)) {
                counts.put(important, counts.get(important) + 1);
            }
        }
        return counts.get("IMPORTANT") >= counts.get("NOT IMPORTANT") ? "IMPORTANT" : "NOT IMPORTANT";
    }

}
