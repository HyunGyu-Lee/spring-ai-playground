package com.hst.springaiplayground.rag;

import java.util.Map;

public record Knowledge(
        Long id,
        String question,
        String answer
) {

    public Map<String, Object> toMetadata() {
        return Map.of(
                "id", id,
                "question", question,
                "answer", answer
        );
    }

}
