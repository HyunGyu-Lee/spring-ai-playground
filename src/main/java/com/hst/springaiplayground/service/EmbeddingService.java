package com.hst.springaiplayground.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 문서 임베딩 + 벡터 DB 상호작용 수행
 * - VectorStore 내부에서 설정에 따른 임베딩 호출하는 코드 수행
 */
@Service
public class EmbeddingService {
    private final VectorStore vectorStore;

    public EmbeddingService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void addDocuments(List<Document> documents) {
        vectorStore.add(documents);
    }

    public List<String> searchDocuments(String query, int topK) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThresholdAll()
                .build();
        return vectorStore.similaritySearch(searchRequest).stream()
                .map(Document::getText)
                .toList();
    }

}
