package com.sondhan.app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FactCheckResult implements Serializable {
    private String id;
    private String claim;
    private String verdict; // "TRUE", "FALSE", "MISLEADING", "UNVERIFIED"
    private int confidence;
    private String explanation;
    private List<Source> sources;
    private String summary;
    private String inputType; // "text", "image", "url"
    private String originalInput;
    private boolean isPreloaded;

    public FactCheckResult() {
        this.sources = new ArrayList<>();
    }

    public FactCheckResult(String claim, String verdict, int confidence, String explanation, List<Source> sources) {
        this.claim = claim;
        this.verdict = verdict;
        this.confidence = confidence;
        this.explanation = explanation;
        this.sources = sources != null ? sources : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isPreloaded() {
        return isPreloaded;
    }

    public void setPreloaded(boolean preloaded) {
        isPreloaded = preloaded;
    }
}

