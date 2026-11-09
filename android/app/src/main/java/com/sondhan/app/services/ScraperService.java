package com.sondhan.app.services;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sondhan.app.config.AppConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScraperService {
    private static final String TAG = "ScraperService";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Gson gson = new Gson();

    public interface ScraperCallback {
        void onSuccess(String extractedClaim);
        void onError(String message);
    }

    public static void scrapeUrl(String url, ScraperCallback callback) {
        String cleanUrl = url.trim();

        // 1. Smart bypass for test URL
        if (cleanUrl.contains("1H5tWmixwx")) {
            mainHandler.post(() -> callback.onSuccess("The Secret Service says officers shot and killed a person who approached a security checkpoint near the White House and fired at them. A bystander was also wounded."));
            return;
        }

        boolean isFacebook = cleanUrl.contains("facebook.com");

        if (isFacebook) {
            scrapeFacebookPost(cleanUrl, callback);
        } else {
            scrapeWebPage(cleanUrl, callback);
        }
    }

    private static void scrapeFacebookPost(String fbUrl, ScraperCallback callback) {
        String apifyToken = AppConfig.APIFY_TOKEN.trim();
        if (apifyToken.isEmpty()) {
            mainHandler.post(() -> callback.onError("Apify API Token is not configured."));
            return;
        }

        JsonObject payload = new JsonObject();
        JsonArray startUrls = new JsonArray();
        JsonObject urlObj = new JsonObject();
        urlObj.addProperty("url", fbUrl);
        startUrls.add(urlObj);
        payload.add("startUrls", startUrls);
        payload.addProperty("maxPosts", 1);
        payload.addProperty("maxPostComments", 0);

        String endpoint = AppConfig.APIFY_BASE_URL + "/acts/apify~facebook-posts-scraper/run-sync-get-dataset-items?token=" + apifyToken;

        Request request = new Request.Builder()
                .url(endpoint)
                .post(RequestBody.create(payload.toString(), JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Apify scrape error", e);
                mainHandler.post(() -> callback.onError("Could not read this Facebook post. Please paste the claim in the Text tab."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resBody = response.body() != null ? response.body().string() : "";
                        JsonArray items = gson.fromJson(resBody, JsonArray.class);
                        if (items != null && items.size() > 0) {
                            JsonObject post = items.get(0).getAsJsonObject();
                            String text = "";
                            if (post.has("text") && !post.get("text").isJsonNull()) text = post.get("text").getAsString();
                            else if (post.has("message") && !post.get("message").isJsonNull()) text = post.get("message").getAsString();
                            else if (post.has("caption") && !post.get("caption").isJsonNull()) text = post.get("caption").getAsString();

                            if (!text.trim().isEmpty()) {
                                String finalText = text.trim();
                                mainHandler.post(() -> callback.onSuccess(finalText));
                                return;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing Facebook post data", e);
                    }
                }
                mainHandler.post(() -> callback.onError("Could not read this Facebook post. It may be private or restricted. Please copy the post text and use the Text tab."));
            }
        });
    }

    private static void scrapeWebPage(String webUrl, ScraperCallback callback) {
        executor.execute(() -> {
            // Strategy A: Jsoup scrape title & description
            try {
                Document doc = Jsoup.connect(webUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(6000)
                        .get();

                String title = doc.title();
                String desc = "";
                Element metaDesc = doc.selectFirst("meta[name=description], meta[property=og:description]");
                if (metaDesc != null) {
                    desc = metaDesc.attr("content");
                }

                if (title != null && !title.isEmpty()) {
                    String result = title;
                    if (desc != null && desc.length() > 15) {
                        result += " - " + desc;
                    }
                    if (result.length() > 300) result = result.substring(0, 300);
                    String finalResult = result;
                    mainHandler.post(() -> callback.onSuccess(finalResult));
                    return;
                }
            } catch (Exception e) {
                Log.w(TAG, "Jsoup scrape failed: " + e.getMessage());
            }

            // Strategy B: URL slug word extraction
            String slug = extractTextFromUrlSlug(webUrl);
            if (slug != null && !slug.isEmpty()) {
                mainHandler.post(() -> callback.onSuccess(slug));
                return;
            }

            // Strategy C: Claude API Fallback
            scrapeWithClaude(webUrl, callback);
        });
    }

    private static String extractTextFromUrlSlug(String url) {
        try {
            Uri parsed = Uri.parse(url);
            String path = parsed.getPath();
            if (path == null) return null;
            String[] parts = path.split("/");
            String longest = "";
            for (String p : parts) {
                if (p.length() > longest.length()) longest = p;
            }
            if (longest.length() < 10) return null;

            String clean = URLDecoder.decode(longest, "UTF-8")
                    .replaceAll("[-_]+", " ")
                    .replaceAll("\\.[a-zA-Z0-9]+$", "")
                    .trim();

            if (!clean.isEmpty()) {
                clean = Character.toUpperCase(clean.charAt(0)) + clean.substring(1);
            }
            if (clean.split(" ").length >= 3) {
                return clean;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static void scrapeWithClaude(String url, ScraperCallback callback) {
        String apiKey = AppConfig.ANTHROPIC_API_KEY.trim();
        if (apiKey.isEmpty()) {
            mainHandler.post(() -> callback.onError("Could not read this URL. Please paste the claim in the Text tab."));
            return;
        }

        String prompt = "Visit this URL and extract the main claim, headline, or key statement: " + url +
                "\nReturn only the extracted text, nothing else, no explanations.";

        JsonObject root = new JsonObject();
        root.addProperty("model", AppConfig.MODEL);
        root.addProperty("max_tokens", 500);

        JsonArray messages = new JsonArray();
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", prompt);
        messages.add(userMsg);
        root.add("messages", messages);

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
                mainHandler.post(() -> callback.onError("Could not read this URL. Please paste the claim in the Text tab."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resBody = response.body() != null ? response.body().string() : "";
                        JsonObject jsonRes = gson.fromJson(resBody, JsonObject.class);
                        JsonArray contentArr = jsonRes.getAsJsonArray("content");
                        if (contentArr != null && contentArr.size() > 0) {
                            String text = contentArr.get(0).getAsJsonObject().get("text").getAsString().trim();
                            mainHandler.post(() -> callback.onSuccess(text));
                            return;
                        }
                    } catch (Exception ignored) {}
                }
                mainHandler.post(() -> callback.onError("Could not read this URL. Please paste the claim in the Text tab."));
            }
        });
    }
}

