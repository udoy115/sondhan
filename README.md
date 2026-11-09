# Sondhan — AI-Powered Real-Time Fact Checking App

<p align="center">
  <b>100% Native Android Java Application</b><br>
  Combating disinformation and fake news through real-time multi-modal AI verification, verified trusted sources, and conversational assistance.
</p>

---

## 🌟 Overview & Core Features

**Sondhan** (অনুসন্ধান - *to search / investigate*) is a native Android application designed to detect, analyze, and debunk viral misinformation across text claims, news screenshots, and social media URLs.

### 🔍 Key Capabilities
* **Multi-Modal Verification Engine**:
  * **Direct Text Claims**: Instant fact-checking grounded in journalistic sources via the Anthropic Claude API.
  * **Image OCR & Vision Analysis**: Automated text extraction and optical verification from screenshot evidence.
  * **Web & Social Scraping**: Automated content and metadata extraction from news sites and social posts.
  * **Zero-Cost Preloaded Database**: Instant client-side SHA-256 hash matching against verified archives for lightning-fast results without API latency.
* **Granular Confidence & Verdict Breakdown**:
  * Verdicts: `TRUE`, `FALSE`, `MISLEADING`, and `UNVERIFIED`.
  * Visual confidence gauge with percentage indicator.
  * Detailed explanatory breakdown with clickable verified source chips (`Source`).
* **On-Demand AI Tools**:
  * **10-Sentence AI Summary**: Structured breakdown of the claim and verification context.
  * **Chat with Sondhani**: Interactive multi-turn AI chatbot grounded strictly in the verified evidence to answer follow-up questions.
* **Search History & Cloud Sync**:
  * Filterable search history (All, True, False, Misleading).
  * Swipe-to-delete management.
  * Cloud sync across user devices.

---

## 🏛️ Architecture & Android Components

Sondhan adheres to modern Android architecture principles, leveraging native components and Android Jetpack libraries:

### 1. Multi-Activity Navigation & Intents
* **Explicit Navigation**:
  * `MainActivity`: Main fact-checking workspace and results dashboard.
  * `LoginActivity` & `RegisterActivity`: Secure user authentication flow.
  * `HistoryActivity`: Past searches, timeline filters, and metrics.
* **Implicit Intents**:
  * System Share Target (`Intent.ACTION_SEND` with `text/*`): Other apps (browsers, Facebook, WhatsApp) can share highlighted text directly to Sondhan for one-tap verification.
  * External Links (`Intent.ACTION_VIEW`): Opens verified news sources safely in the user's default browser.
  * Photo Picker: Modern Android photo picker contracts for uploading evidence.

### 2. Android System Components
* **BroadcastReceiver (`SystemStatusReceiver`)**:
  * Intercepts `Intent.ACTION_AIRPLANE_MODE_CHANGED`, `Intent.ACTION_BATTERY_LOW`, and `ConnectivityManager.CONNECTIVITY_ACTION`.
  * Dynamically alerts users when offline with a top banner and adapts gracefully to power-saving mode.
* **Foreground Service (`FactCheckForegroundService`)**:
  * Runs an ongoing Android service with an active status bar notification (`sondhan_channel_verification`) while deep verification is in progress.
  * Posts final verdict updates directly into the notification shade upon completion.
* **Content Provider (`DeviceMediaHelper`)**:
  * Demonstrates querying the shared Android system `MediaStore` ContentProvider (`MediaStore.Images.Media.EXTERNAL_CONTENT_URI`) via `ContentResolver` to load recent screenshots.
  * Uses `FileProvider` for secure inter-process file sharing.

### 3. Dynamic Lists & View Binding
* **View Binding**: Project-wide type-safe view binding (`ActivityMainBinding`, `ActivityHistoryBinding`, etc.).
* **RecyclerViews**:
  * `HistoryAdapter` with `HistoryViewHolder` for search history cards.
  * `ChatAdapter` with `ChatViewHolder` for conversational message bubbles.

### 4. Full Firebase Suite
* **Firebase Authentication**: Email/Password + Google Sign-In (`play-services-auth`).
* **Cloud Firestore**: Search history document store with sub-collections per user.
* **Firebase Realtime Database**: Real-time atomic synchronized live counter (`stats/total_claims_verified`).
* **Firebase Cloud Storage**: Secure cloud storage for user-submitted fact-check screenshot evidence.

---

## 🔒 Security & Key Management

**Private API keys are never committed to version control.**

* **Local Configuration**: Secret keys are stored in `local.properties` (which is excluded by `.gitignore`).
* **BuildConfig Injection**: The Android Gradle Plugin injects the keys into `BuildConfig` at compile time:
  ```groovy
  buildConfigField "String", "ANTHROPIC_API_KEY", "\"${anthropicKey}\""
  buildConfigField "String", "APIFY_TOKEN", "\"${apifyToken}\""
  ```
* **Safe Fallbacks**: `AppConfig.java` reads from `BuildConfig`, keeping the repository completely free of exposed secrets.

---

## 🚀 Setup & Build Instructions

### Prerequisites
* Android Studio Iguana / Jellyfish / Ladybug or newer.
* Android SDK (API 34, Min SDK 24).
* JDK 17 or JDK 21.

### 1. Clone the Repository
```bash
git clone https://github.com/udoy115/sondhan.git
cd sondhan
```

### 2. Configure Secret Keys
Copy `local.properties.example` to `local.properties`:
```bash
cp local.properties.example local.properties
```
Open `local.properties` and add your SDK path and API keys:
```properties
sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk
ANTHROPIC_API_KEY=your_anthropic_api_key_here
APIFY_TOKEN=your_apify_token_here
```

### 3. Build & Run
Open the project in **Android Studio** and click **Run**, or build via command line:
```bash
./gradlew assembleDebug
```
The compiled APK will be located at:
`android/app/build/outputs/apk/debug/app-debug.apk`

---

## 🎬 Live Demonstration Checklist

During demonstrations or presentations, follow this flow to showcase all key rubric areas:

1. **Authentication Flow**:
   * Open the app $\rightarrow$ showcase Google Sign-In or Email/Password login.
   * Tap user avatar top right $\rightarrow$ profile dialog and logout options.
2. **System Broadcast Demonstration**:
   * Toggle **Airplane Mode** in device settings $\rightarrow$ point out the immediate offline warning banner at the top of the app.
   * Toggle Airplane Mode off $\rightarrow$ point out the "Back Online" restoration.
3. **Multi-Modal Fact Checking**:
   * **Text**: Enter a claim (e.g. *"Bernie Sanders warned that AI could replace workers"*) $\rightarrow$ Tap **Fact Check**.
   * Pull down the **Notification Shade** $\rightarrow$ show the active **Foreground Service notification** with real-time status.
   * Review Verdict badge, confidence percentage, explanation, and clickable source chips.
4. **On-Demand Features**:
   * Tap **"Get Summary"** at the bottom of the explanation $\rightarrow$ reveals the 10-Sentence AI Summary.
   * Tap **"Chat with Sondhani"** $\rightarrow$ reveals the chatbot and ask a follow-up question.
5. **Content Provider Media Query**:
   * Switch to the **Image** tab $\rightarrow$ Tap **"Query Recent Image via MediaStore ContentProvider"** to showcase direct interaction with Android's system Content Provider.
6. **Realtime Database & History**:
   * Point out the live `⚡ X Live` counter in the top bar powered by Firebase Realtime Database.
   * Navigate to the **History** tab $\rightarrow$ demonstrate category filters (True, False, Misleading) and deletion.

---

## 📄 License
This project is developed for educational and academic presentation purposes.
