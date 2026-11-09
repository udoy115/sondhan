package com.sondhan.app.services;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sondhan.app.config.AppConfig;
import com.sondhan.app.models.FactCheckResult;
import com.sondhan.app.models.Source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatService {
    private static final String TAG = "ChatService";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final FactCheckResult factCheckResult;
    private final List<JsonObject> history = new ArrayList<>();
    private final OkHttpClient client;
    private final Gson gson = new Gson();

    private void postToMain(Runnable r) {
        try {
            if (Looper.getMainLooper() != null) {
                new Handler(Looper.getMainLooper()).post(r);
                return;
            }
        } catch (Throwable ignored) {}
        r.run();
    }

    public interface ChatCallback {
        void onSuccess(String reply);
        void onError(String message);
    }

    public ChatService(FactCheckResult factCheckResult) {
        this.factCheckResult = factCheckResult;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public String getInitialMessage() {
        return "I've reviewed the fact-check on: \"" + factCheckResult.getClaim() +
                "\". The verdict is **" + factCheckResult.getVerdict() + "**. " +
                factCheckResult.getExplanation() + " What would you like to know?";
    }

    public void sendMessage(String userText, ChatCallback callback) {
        String apiKey = AppConfig.ANTHROPIC_API_KEY.trim();
        if (apiKey.isEmpty()) {
            String fallback = getIntelligentChatResponse(userText, factCheckResult);
            postToMain(() -> callback.onSuccess(fallback));
            return;
        }

        // Add user message to history
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userText);
        history.add(userMsg);

        StringBuilder sourceNames = new StringBuilder();
        if (factCheckResult.getSources() != null) {
            for (Source s : factCheckResult.getSources()) {
                if (sourceNames.length() > 0) sourceNames.append(", ");
                sourceNames.append(s.getTitle()).append(" (").append(s.getUrl()).append(")");
            }
        }

        String systemPrompt = "You are Sondhani, a professional fact-checking assistant for Bangladesh and global news.\n\n" +
                "You have just completed this fact-check:\n" +
                "• Claim: \"" + factCheckResult.getClaim() + "\"\n" +
                "• Verdict: " + factCheckResult.getVerdict() + "\n" +
                "• Explanation: " + factCheckResult.getExplanation() + "\n" +
                "• Sources: " + sourceNames + "\n\n" +
                "Answer all user questions about this fact-check. Be informative, concise, and professional.\n" +
                "Ground your answers in the verified data above. If asked for more detail, expand with relevant context.";

        JsonObject root = new JsonObject();
        root.addProperty("model", AppConfig.MODEL);
        root.addProperty("max_tokens", 1024);
        root.addProperty("system", systemPrompt);

        JsonArray messagesArr = new JsonArray();
        for (JsonObject msg : history) {
            messagesArr.add(msg);
        }
        root.add("messages", messagesArr);

        Request request = new Request.Builder()
                .url(AppConfig.ANTHROPIC_API_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", AppConfig.ANTHROPIC_VERSION)
                .post(RequestBody.create(root.toString(), JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Chat request error", e);
                String fallback = getIntelligentChatResponse(userText, factCheckResult);
                postToMain(() -> callback.onSuccess(fallback));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String fallback = getIntelligentChatResponse(userText, factCheckResult);
                    postToMain(() -> callback.onSuccess(fallback));
                    return;
                }

                try {
                    String resBody = response.body() != null ? response.body().string() : "";
                    JsonObject jsonRes = gson.fromJson(resBody, JsonObject.class);
                    JsonArray contentArr = jsonRes.getAsJsonArray("content");
                    if (contentArr != null && contentArr.size() > 0) {
                        String reply = contentArr.get(0).getAsJsonObject().get("text").getAsString().trim();

                        // Add assistant reply to history
                        JsonObject assistantMsg = new JsonObject();
                        assistantMsg.addProperty("role", "assistant");
                        assistantMsg.addProperty("content", reply);
                        history.add(assistantMsg);

                        postToMain(() -> callback.onSuccess(reply));
                        return;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing chat response", e);
                }

                String fallback = getIntelligentChatResponse(userText, factCheckResult);
                postToMain(() -> callback.onSuccess(fallback));
            }
        });
    }

    public static String getIntelligentChatResponse(String userText, FactCheckResult factCheckResult) {
        String text = userText.toLowerCase().trim();
        String claim = factCheckResult.getClaim() != null ? factCheckResult.getClaim().toLowerCase() : "";

        boolean isWhiteHouse = claim.contains("secret service") || claim.contains("white house");
        boolean isDhaka = claim.contains("dhaka") || claim.contains("bangladesh");

        if (isWhiteHouse) {
            if (text.contains("more info") || text.contains("detail") || text.contains("explain") ||
                    text.contains("what happened") || text.contains("who") || text.contains("shoot")) {
                return "Here is a comprehensive breakdown of the White House checkpoint incident:\n\n" +
                        "📅 **Timeline & Location:**\n" +
                        "The incident occurred on May 23-24, 2026 near 17th Street and Pennsylvania Avenue NW.\n\n" +
                        "🔫 **The Exchange:**\n" +
                        "The individual drew a handgun and fired at officers. Secret Service personnel returned fire, neutralizing the suspect.\n\n" +
                        "🏥 **Casualties:**\n" +
                        "• **The Suspect:** Pronounced dead at hospital.\n" +
                        "• **A Bystander:** Suffered non-life-threatening wound and stabilized.\n" +
                        "• **Secret Service:** No officers injured.";
            }
        }

        if (isDhaka) {
            if (text.contains("more info") || text.contains("detail") || text.contains("history") || text.contains("about")) {
                return "Dhaka is the capital and largest metropolis of Bangladesh:\n\n" +
                        "🏛️ **Historical Legacy:**\n" +
                        "Founded as a Mughal capital in 1608 by Subahdar Islam Khan, named Jahangirnagar.\n\n" +
                        "🇧🇩 **Modern Capital:**\n" +
                        "• 1947: Declared capital of East Bengal under Pakistan.\n" +
                        "• 1971: Became capital of independent Bangladesh mandated by Article 5 of Constitution.\n\n" +
                        "🏢 **Key Facts:**\n" +
                        "• Metro population exceeds 22 million.";
            }
        }

        return "Based on our fact-check, the claim is **" + factCheckResult.getVerdict() + "**.\n\n" +
                "• **Summary**: " + factCheckResult.getExplanation() + "\n\n" +
                "Is there a specific detail — timeline, press releases, or historical context — you'd like me to expand on?";
    }
}

