package com.hst.springaiplayground.service.structuredoutput.model;

public class ReviewClassification {
    private String review;
    private Sentiment sentiment;

    public void setReview(String review) {
        this.review = review;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public String getReview() {
        return review;
    }

    public enum Sentiment {
        POSITIVE,
        NEGATIVE,
        NEUTRAL
    }
}
