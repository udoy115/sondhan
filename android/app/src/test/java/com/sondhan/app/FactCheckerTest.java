package com.sondhan.app;

import com.sondhan.app.models.FactCheckResult;
import com.sondhan.app.models.Source;
import com.sondhan.app.services.ChatService;
import com.sondhan.app.services.FactCheckerService;
import com.sondhan.app.services.PreloadedDatabase;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class FactCheckerTest {

    @Test
    public void testDhakaCapitalFallback() {
        FactCheckResult result = FactCheckerService.getIntelligentFallback("Is Dhaka the capital of Bangladesh?", "");
        assertNotNull(result);
        assertEquals("TRUE", result.getVerdict());
        assertEquals(99, result.getConfidence());
        assertTrue(result.getExplanation().contains("capital"));
    }

    @Test
    public void testWhiteHouseFallback() {
        FactCheckResult result = FactCheckerService.getIntelligentFallback("Secret service shot person near white house", "");
        assertNotNull(result);
        assertEquals("TRUE", result.getVerdict());
        assertEquals(96, result.getConfidence());
        assertTrue(result.getSources().size() >= 2);
    }

    @Test
    public void testGenericFallback() {
        FactCheckResult result = FactCheckerService.getIntelligentFallback("Alien landed in Tokyo", "API error");
        assertNotNull(result);
        assertEquals("UNVERIFIED", result.getVerdict());
        assertEquals(50, result.getConfidence());
    }

    @Test
    public void testDynamicSummaryGeneration() {
        Source source = new Source("Test Source", "https://example.com");
        String summary = FactCheckerService.generateDynamicFallbackSummary(
                "Test claim",
                "TRUE",
                "Test explanation",
                Collections.singletonList(source)
        );
        assertNotNull(summary);
        assertTrue(summary.contains("1. The primary claim"));
        assertTrue(summary.contains("10. In conclusion"));
        String[] lines = summary.split("\n");
        assertEquals(10, lines.length);
    }

    @Test
    public void testIntelligentChatFallback() {
        FactCheckResult result = new FactCheckResult(
                "Dhaka is capital of Bangladesh",
                "TRUE",
                99,
                "Explanation text",
                Collections.singletonList(new Source("Gov", "https://bangladesh.gov.bd"))
        );
        String reply = ChatService.getIntelligentChatResponse("tell me history and more info", result);
        assertNotNull(reply);
        assertTrue(reply.contains("Jahangirnagar") || reply.contains("Mughal"));
    }

    @Test
    public void testSha256Computation() {
        byte[] testBytes = "Hello Sondhan".getBytes();
        String hash = PreloadedDatabase.computeSha256(testBytes);
        assertNotNull(hash);
        assertEquals(64, hash.length()); // SHA-256 is 64 hex characters
    }
}

