package com.sondhan.app.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SearchHistoryItem implements Serializable {
    private String id;
    private String inputType;
    private String originalInput;
    private String claim;
    private String verdict;
    private int confidence;
    private String explanation;
    private List<Source> sources;
    private String summary;
    private Date createdAt;

    public SearchHistoryItem() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getOriginalInput() {
        return originalInput;
    }

    public void setOriginalInput(String originalInput) {
        this.originalInput = originalInput;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

