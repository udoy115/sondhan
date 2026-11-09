package com.sondhan.app.config;

import com.sondhan.app.BuildConfig;

public class AppConfig {
    // Anthropic API configuration (Loaded securely from local.properties via BuildConfig)
    public static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    public static final String ANTHROPIC_API_KEY = BuildConfig.ANTHROPIC_API_KEY != null && !BuildConfig.ANTHROPIC_API_KEY.isEmpty()
            ? BuildConfig.ANTHROPIC_API_KEY
            : "YOUR_ANTHROPIC_API_KEY_HERE";
    public static final String ANTHROPIC_VERSION = "2023-06-01";
    public static final String MODEL = "claude-sonnet-4-6";
    public static final String HAIKU_MODEL = "claude-sonnet-4-6";

    // Apify API configuration (Loaded securely from local.properties via BuildConfig)
    public static final String APIFY_TOKEN = BuildConfig.APIFY_TOKEN != null && !BuildConfig.APIFY_TOKEN.isEmpty()
            ? BuildConfig.APIFY_TOKEN
            : "YOUR_APIFY_TOKEN_HERE";
    public static final String APIFY_BASE_URL = "https://api.apify.com/v2";

    // Firebase Project Configuration
    public static final String FIREBASE_PROJECT_ID = "sondhan-9e65d";
    public static final String FIREBASE_STORAGE_BUCKET = "sondhan-9e65d.firebasestorage.app";

    // Google Sign-In Web Client ID (from google-services.json oauth_client type 3)
    public static final String GOOGLE_WEB_CLIENT_ID = "91720458801-uni348lbdga4ujbouqk47iv7r71ccbo2.apps.googleusercontent.com";
}

