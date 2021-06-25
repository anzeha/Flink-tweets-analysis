package com.habjan.model;

import java.util.ArrayList;

public class EsTweet {
    Long id;
    String createdAt;
    Long userId;
    String username;
    String text;
    String detectedLanguage;
    Double languageConfidence;
    String sentiment;
    Double sentimentProbability;
    ArrayList<String> playersMentioned;

    public EsTweet() {
        this.playersMentioned = new ArrayList<String>();
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public Double getSentimentProbability() {
        return sentimentProbability;
    }

    public void setSentimentProbability(Double sentimentProbability) {
        this.sentimentProbability = sentimentProbability;
    }


    public Double getLanguageConfidence() {
        return languageConfidence;
    }

    public void setLanguageConfidence(Double languageConfidence) {
        this.languageConfidence = languageConfidence;
    }


    public EsTweet(Long id, String createdAt, Long userId, String username, String text, String detectedLanguage, Double languageConfidence, String NER, String sentiment, Double sentimentProbability, ArrayList<String> playersMentioned) {
        this.id = id;
        this.createdAt = createdAt;
        this.userId = userId;
        this.username = username;
        this.text = text;
        this.detectedLanguage = detectedLanguage;
        this.languageConfidence = languageConfidence;
        this.sentiment = sentiment;
        this.sentimentProbability = sentimentProbability;
        this.playersMentioned = playersMentioned;
    }

    public ArrayList<String> getPlayersMentioned() {
        return playersMentioned;
    }

    public void setPlayersMentioned(ArrayList<String> playersMentioned) {
        this.playersMentioned = playersMentioned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }
}
