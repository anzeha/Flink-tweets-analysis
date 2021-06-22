package com.habjan.model;

public class EsTweet {
    Long id;
    String created_at;
    Long user_id;
    String username;
    String text;
    String detected_language;
    Double language_confidence;
    String NER;

    public EsTweet() {
    }

    @Override
    public String toString() {
        return "NER= " + NER + "\n";
    }

    public Double getLanguage_confidence() {
        return language_confidence;
    }

    public void setLanguage_confidence(Double language_confidence) {
        this.language_confidence = language_confidence;
    }

    public String getNER() {
        return NER;
    }

    public void setNER(String NER) {
        this.NER = NER;
    }

    public EsTweet(Long id, String created_at, Long user_id, String username, String text, String detected_language, Double language_confidence, String NER) {
        this.id = id;
        this.created_at = created_at;
        this.user_id = user_id;
        this.username = username;
        this.text = text;
        this.detected_language = detected_language;
        this.language_confidence = language_confidence;
        this.NER = NER;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
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

    public String getDetected_language() {
        return detected_language;
    }

    public void setDetected_language(String detected_language) {
        this.detected_language = detected_language;
    }
}
