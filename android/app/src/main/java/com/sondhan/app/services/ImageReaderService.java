package com.sondhan.app.services;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sondhan.app.config.AppConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageReaderService {
    private static final String TAG = "ImageReaderService";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final Gson gson = new Gson();

    public interface ImageReaderCallback {
        void onSuccess(String extractedText);
        void onError(String message);
    }

    public static void extractTextFromImage(byte[] imageBytes, String mimeType, ImageReaderCallback callback) {
        String apiKey = AppConfig.ANTHROPIC_API_KEY.trim();
        if (apiKey.isEmpty()) {
            mainHandler.post(() -> callback.onError("Anthropic API key is not configured."));
            return;
        }

        String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        String finalMimeType = (mimeType != null && !mimeType.isEmpty()) ? mimeType : "image/jpeg";

        JsonObject root = new JsonObject();
        root.addProperty("model", AppConfig.MODEL);
        root.addProperty("max_tokens", 1024);

        JsonArray messages = new JsonArray();
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");

        JsonArray contentArr = new JsonArray();

        // Image source block
        JsonObject imageBlock = new JsonObject();
        imageBlock.addProperty("type", "image");
        JsonObject source = new JsonObject();
        source.addProperty("type", "base64");
        source.addProperty("media_type", finalMimeType);
        source.addProperty("data", base64Image);
        imageBlock.add("source", source);
        contentArr.add(imageBlock);

        // Instruction block
        JsonObject textBlock = new JsonObject();
        textBlock.addProperty("type", "text");
        textBlock.addProperty("text", "Extract ALL text visible in this image exactly as it appears. Include every word, number, and phrase you can see. Return only the extracted text, nothing else.");
        contentArr.add(textBlock);

        userMsg.add("content", contentArr);
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
                Log.e(TAG, "Image OCR failed", e);
                mainHandler.post(() -> callback.onError("Failed to analyze image: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.w(TAG, "Vision error code: " + response.code());
                    mainHandler.post(() -> callback.onError("Could not read text from this image. Please make sure the image is clear."));
                    return;
                }

                try {
                    String resBody = response.body() != null ? response.body().string() : "";
                    JsonObject jsonRes = gson.fromJson(resBody, JsonObject.class);
                    JsonArray resContent = jsonRes.getAsJsonArray("content");
                    if (resContent != null && resContent.size() > 0) {
                        String text = resContent.get(0).getAsJsonObject().get("text").getAsString();
                        if (text != null && !text.trim().isEmpty()) {
                            mainHandler.post(() -> callback.onSuccess(text.trim()));
                            return;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing vision result", e);
                }

                mainHandler.post(() -> callback.onError("No readable text found in the image."));
            }
        });
    }
}

