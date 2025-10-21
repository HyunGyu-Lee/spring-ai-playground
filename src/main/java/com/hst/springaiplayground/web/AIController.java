package com.hst.springaiplayground.web;

import com.hst.springaiplayground.rag.Knowledge;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AIController {
    private final ChatClient openAIChatClient;
    private final VectorStore vectorStore;

    public AIController(ChatClient openAIChatClient, VectorStore vectorStore) {
        this.openAIChatClient = openAIChatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/ask")
    public ChatResponse ask(@RequestParam String userMessage) {
        return openAIChatClient.prompt()
                .user(userMessage)
                .call()
                .chatResponse();
    }

    @GetMapping("/training")
    public void training() {
        List<Knowledge> originData = List.of(
                new Knowledge(1L, "육아휴직 최대 기간", "육아휴직은 최대 12개월까지 사용할 수 있습니다."),
                new Knowledge(2L, "육아휴직 분할 사용", "육아휴직은 분할 사용이 가능합니다."),
                new Knowledge(3L, "육아휴직 신청 시기", "육아휴직은 최소 30일 전에 인사팀에 신청해야 합니다."),
                new Knowledge(4L, "육아휴직 승인 절차", "육아휴직은 신청 후 승인 절차가 필요합니다.")
        );

        List<Document> documents = originData.stream()
                .map(knowledge -> {
                    String content = String.format("title=%s\n description=%s", knowledge.question(), knowledge.answer());
                    return new Document(content, Map.of("id", knowledge.id(), "question", knowledge.question(), "answer", knowledge.answer()));
                })
                .toList();

        vectorStore.add(documents);
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String query) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(2)
                .similarityThresholdAll()
                .build();
        return vectorStore.similaritySearch(searchRequest).stream().map(Document::getFormattedContent).toList();
    }

}
