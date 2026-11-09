package com.sondhan.app.models;

import java.util.List;

public class PreloadedEntry {
    private String id;
    private String imageFile;
    private String claim;
    private String verdict;
    private int confidence;
    private String explanation;
    private List<Source> sources;
    private List<String> summary;

    public PreloadedEntry(String id, String imageFile, String claim, String verdict, int confidence,
                          String explanation, List<Source> sources, List<String> summary) {
        this.id = id;
        this.imageFile = imageFile;
        this.claim = claim;
        this.verdict = verdict;
        this.confidence = confidence;
        this.explanation = explanation;
        this.sources = sources;
        this.summary = summary;
    }

    public String getId() {
        return id;
    }

    public String getImageFile() {
        return imageFile;
    }

    public String getClaim() {
        return claim;
    }

    public String getVerdict() {
        return verdict;
    }

    public int getConfidence() {
        return confidence;
    }

    public String getExplanation() {
        return explanation;
    }

    public List<Source> getSources() {
        return sources;
    }

    public List<String> getSummary() {
        return summary;
    }

    public FactCheckResult toFactCheckResult() {
        FactCheckResult result = new FactCheckResult(claim, verdict, confidence, explanation, sources);
        result.setId(id);
        result.setPreloaded(true);
        if (summary != null && !summary.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < summary.size(); i++) {
                sb.append(i + 1).append(". ").append(summary.get(i)).append("\n");
            }
            result.setSummary(sb.toString().trim());
        }
        return result;
    }
}

