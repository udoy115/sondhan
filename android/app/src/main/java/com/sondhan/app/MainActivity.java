package com.sondhan.app;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.sondhan.app.config.AppConfig;
import com.sondhan.app.databinding.ActivityMainBinding;
import com.sondhan.app.models.ChatMessage;
import com.sondhan.app.models.FactCheckResult;
import com.sondhan.app.models.Source;
import com.sondhan.app.receivers.SystemStatusReceiver;
import com.sondhan.app.services.ChatService;
import com.sondhan.app.services.DeviceMediaHelper;
import com.sondhan.app.services.FactCheckForegroundService;
import com.sondhan.app.services.FactCheckerService;
import com.sondhan.app.services.FirebaseManager;
import com.sondhan.app.services.ImageReaderService;
import com.sondhan.app.services.PreloadedDatabase;
import com.sondhan.app.services.ScraperService;
import com.sondhan.app.ui.HistoryActivity;
import com.sondhan.app.ui.LoginActivity;
import com.sondhan.app.ui.adapters.ChatAdapter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseManager firebaseManager;
    private ChatAdapter chatAdapter;
    private ChatService currentChatService;
    private FactCheckResult currentResult;
    private byte[] selectedImageBytes;
    private String selectedImageMimeType = "image/jpeg";

    private SystemStatusReceiver systemStatusReceiver;
    private ValueEventListener realtimeStatsListener;
    private ActivityResultLauncher<String> notificationPermissionLauncher;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    processSelectedImage(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance(this);

        // Check auth status
        if (!firebaseManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize preloaded database assets
        PreloadedDatabase.initialize(this);

        setupUserAvatar();
        setupTabs();
        setupImagePicker();
        setupChat();
        setupBottomNav();
        setupSystemStatusReceiver();
        setupRealtimeCounter();
        setupNotificationPermission();

        binding.btnVerifyText.setOnClickListener(v -> handleVerifyText());
        binding.btnVerifyImage.setOnClickListener(v -> handleVerifyImage());
        binding.btnVerifyUrl.setOnClickListener(v -> handleVerifyUrl());
        binding.btnGetSummaryAction.setOnClickListener(v -> handleGetSummaryClick());
        binding.btnChatWithSondhaniAction.setOnClickListener(v -> handleChatWithSondhaniClick());
        binding.btnToggleSummary.setOnClickListener(v -> handleSummaryToggle());
        binding.btnSendChat.setOnClickListener(v -> handleSendChatMessage());
        binding.btnContentProviderMedia.setOnClickListener(v -> handleQueryMediaStore());
        binding.btnDismissBanner.setOnClickListener(v -> binding.layoutSystemBanner.setVisibility(View.GONE));

        handleIncomingIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (systemStatusReceiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            filter.addAction(Intent.ACTION_BATTERY_LOW);
            filter.addAction(Intent.ACTION_BATTERY_OKAY);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(systemStatusReceiver, filter);
        }

        // Check initial airplane mode
        try {
            boolean isAirplaneOn = Settings.Global.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
            if (isAirplaneOn) {
                showSystemBanner("✈️ Airplane Mode is ON: Fact check requires network connection.");
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (systemStatusReceiver != null) {
            try {
                unregisterReceiver(systemStatusReceiver);
            } catch (Exception ignored) {}
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realtimeStatsListener != null && firebaseManager != null) {
            firebaseManager.removeRealtimeListener(realtimeStatsListener);
        }
    }

    private void setupNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> Log.d("MainActivity", "Notification permission granted: " + isGranted)
            );
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void setupSystemStatusReceiver() {
        systemStatusReceiver = new SystemStatusReceiver();
        SystemStatusReceiver.setListener(new SystemStatusReceiver.SystemStatusListener() {
            @Override
            public void onAirplaneModeChanged(boolean isAirplaneModeOn) {
                if (isAirplaneModeOn) {
                    showSystemBanner("✈️ Airplane Mode is ON: Fact check requires network connection.");
                } else {
                    showSystemBanner("🌐 Back Online: Connected to network.");
                    binding.layoutSystemBanner.postDelayed(() -> binding.layoutSystemBanner.setVisibility(View.GONE), 3000);
                }
            }

            @Override
            public void onBatteryStatusChanged(boolean isLowBattery) {
                if (isLowBattery) {
                    showSystemBanner("🔋 Battery Low: Running in energy-saving fact check mode.");
                } else {
                    binding.layoutSystemBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNetworkStatusChanged(boolean isConnected) {
                if (!isConnected) {
                    showSystemBanner("⚠️ No Internet Connection: Reconnect to verify claims.");
                } else {
                    binding.layoutSystemBanner.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showSystemBanner(String message) {
        binding.tvSystemBannerText.setText(message);
        binding.layoutSystemBanner.setVisibility(View.VISIBLE);
    }

    private void setupRealtimeCounter() {
        realtimeStatsListener = firebaseManager.listenToLiveClaimsCounter(new FirebaseManager.RealtimeCallback<Long>() {
            @Override
            public void onDataChange(Long count) {
                if (count != null && count > 0) {
                    binding.tvLiveStats.setText("⚡ " + count + " Live");
                    binding.tvLiveStats.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {
                Log.w("MainActivity", "Realtime DB counter: " + message);
            }
        });
    }

    private void handleQueryMediaStore() {
        List<DeviceMediaHelper.MediaItem> items = DeviceMediaHelper.queryRecentImages(this, 1);
        if (items != null && !items.isEmpty()) {
            DeviceMediaHelper.MediaItem item = items.get(0);
            processSelectedImage(item.getUri());
            Toast.makeText(this, "Loaded via MediaStore ContentProvider:\n" + item.getDisplayName(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No recent images found in MediaStore ContentProvider.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("text/")) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null && !sharedText.trim().isEmpty()) {
                    String clean = sharedText.trim();
                    if (clean.startsWith("http://") || clean.startsWith("https://")) {
                        selectTab("url");
                        binding.etUrl.setText(clean);
                        handleVerifyUrl();
                    } else {
                        selectTab("text");
                        binding.etClaim.setText(clean);
                        handleVerifyText();
                    }
                }
            }
        }
    }

    private void setupUserAvatar() {
        FirebaseUser user = firebaseManager.getCurrentUser();
        if (user != null) {
            String initial = "U";
            if (user.getDisplayName() != null && !user.getDisplayName().trim().isEmpty()) {
                initial = user.getDisplayName().trim().substring(0, 1).toUpperCase(Locale.ROOT);
            } else if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                initial = user.getEmail().trim().substring(0, 1).toUpperCase(Locale.ROOT);
            }
            binding.tvUserInitial.setText(initial);
        }

        binding.btnAvatar.setOnClickListener(v -> {
            FirebaseUser currentUser = firebaseManager.getCurrentUser();
            String name = currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()
                    ? currentUser.getDisplayName()
                    : "Sondhan User";
            String email = currentUser != null && currentUser.getEmail() != null
                    ? currentUser.getEmail()
                    : "No email associated";

            new AlertDialog.Builder(this)
                    .setTitle("Account Profile")
                    .setMessage("Signed in as:\n" + name + " (" + email + ")")
                    .setPositiveButton("Log Out", (dialog, which) -> handleLogout())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupTabs() {
        binding.tabUrlLayout.setOnClickListener(v -> selectTab("url"));
        binding.tabImageLayout.setOnClickListener(v -> selectTab("image"));
        binding.tabTextLayout.setOnClickListener(v -> selectTab("text"));

        // Default to Image tab as featured in Screenshot 1
        selectTab("image");
    }

    private void selectTab(String tab) {
        int selectedColor = ContextCompat.getColor(this, R.color.tab_selected);
        int unselectedColor = ContextCompat.getColor(this, R.color.tab_unselected);

        boolean isUrl = "url".equalsIgnoreCase(tab);
        boolean isImage = "image".equalsIgnoreCase(tab);
        boolean isText = "text".equalsIgnoreCase(tab);

        binding.containerUrl.setVisibility(isUrl ? View.VISIBLE : View.GONE);
        binding.indicatorUrl.setVisibility(isUrl ? View.VISIBLE : View.INVISIBLE);
        binding.tvTabUrl.setTextColor(isUrl ? selectedColor : unselectedColor);

        binding.containerImage.setVisibility(isImage ? View.VISIBLE : View.GONE);
        binding.indicatorImage.setVisibility(isImage ? View.VISIBLE : View.INVISIBLE);
        binding.tvTabImage.setTextColor(isImage ? selectedColor : unselectedColor);

        binding.containerText.setVisibility(isText ? View.VISIBLE : View.GONE);
        binding.indicatorText.setVisibility(isText ? View.VISIBLE : View.INVISIBLE);
        binding.tvTabText.setTextColor(isText ? selectedColor : unselectedColor);
    }

    private void setupImagePicker() {
        binding.cardImagePicker.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });
    }

    private void processSelectedImage(Uri uri) {
        try {
            selectedImageMimeType = getContentResolver().getType(uri);
            if (selectedImageMimeType == null) selectedImageMimeType = "image/jpeg";

            InputStream is = getContentResolver().openInputStream(uri);
            if (is != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[8192];
                int nRead;
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                selectedImageBytes = buffer.toByteArray();
                is.close();

                // Show preview inside dashed dropzone
                Bitmap bitmap = BitmapFactory.decodeByteArray(selectedImageBytes, 0, selectedImageBytes.length);
                binding.ivPreview.setImageBitmap(bitmap);
                binding.ivPreview.setVisibility(View.VISIBLE);
                binding.layoutPickPrompt.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupChat() {
        chatAdapter = new ChatAdapter();
        binding.rvChat.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChat.setAdapter(chatAdapter);
        binding.rvChat.setNestedScrollingEnabled(false);

        // Enable smooth mouse wheel scrolling in Android Studio emulator and devices
        binding.scrollView.setOnGenericMotionListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_SCROLL && event.isFromSource(InputDevice.SOURCE_CLASS_POINTER)) {
                float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                if (vscroll != 0) {
                    int delta = (int) (-vscroll * 120);
                    binding.scrollView.smoothScrollBy(0, delta);
                    return true;
                }
            }
            return false;
        });
    }

    private void setupBottomNav() {
        binding.navHistory.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void handleLogout() {
        firebaseManager.signOut();
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(AppConfig.GOOGLE_WEB_CLIENT_ID)
                    .requestEmail()
                    .build();
            GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        } catch (Exception e) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void showLoading(String statusMessage) {
        binding.layoutLoading.setVisibility(View.VISIBLE);
        binding.tvLoadingStatus.setText(statusMessage);
        binding.layoutResult.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.layoutLoading.setVisibility(View.GONE);
    }

    // ─── Text Fact Check ─────────────────────────────────────────────────────────

    private void handleVerifyText() {
        String claim = binding.etClaim.getText() != null ? binding.etClaim.getText().toString().trim() : "";
        if (claim.isEmpty()) {
            Toast.makeText(this, "Please enter a claim to verify.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading("Checking claim with Sondhani AI...");
        FactCheckForegroundService.startVerification(this, claim);

        FactCheckerService.factCheck(claim, new FactCheckerService.FactCheckCallback() {
            @Override
            public void onSuccess(FactCheckResult result) {
                hideLoading();
                result.setInputType("text");
                result.setOriginalInput(claim);
                displayResult(result);
                saveResultToFirestore(result);
            }

            @Override
            public void onError(String message) {
                hideLoading();
                FactCheckForegroundService.stopVerification(MainActivity.this);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Image Fact Check ────────────────────────────────────────────────────────

    private void handleVerifyImage() {
        if (selectedImageBytes == null) {
            Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading("Checking image against verified database...");
        FactCheckForegroundService.startVerification(this, "Analyzing image screenshot with Claude Vision...");

        // Upload to Firebase Cloud Storage (Media / Picture Storage requirement)
        FirebaseUser user = firebaseManager.getCurrentUser();
        if (user != null) {
            firebaseManager.uploadFactCheckImage(user.getUid(), selectedImageBytes, new FirebaseManager.StorageCallback() {
                @Override
                public void onSuccess(String downloadUrl) {
                    Log.d("MainActivity", "Screenshot uploaded to Firebase Cloud Storage: " + downloadUrl);
                }

                @Override
                public void onError(String message) {
                    Log.w("MainActivity", "Firebase Storage upload: " + message);
                }
            });
        }

        // 1. Instant check against preloaded database
        FactCheckResult preloadedResult = PreloadedDatabase.matchImage(selectedImageBytes);
        if (preloadedResult != null) {
            hideLoading();
            preloadedResult.setInputType("image");
            preloadedResult.setOriginalInput("Image Fact Check");
            displayResult(preloadedResult);
            saveResultToFirestore(preloadedResult);
            return;
        }

        // 2. OCR text extraction with Claude Vision API
        showLoading("Reading text from image using Claude Vision...");
        ImageReaderService.extractTextFromImage(selectedImageBytes, selectedImageMimeType, new ImageReaderService.ImageReaderCallback() {
            @Override
            public void onSuccess(String extractedText) {
                showLoading("Verifying claim from image: \"" + (extractedText.length() > 40 ? extractedText.substring(0, 40) + "..." : extractedText) + "\"");
                FactCheckerService.factCheck(extractedText, new FactCheckerService.FactCheckCallback() {
                    @Override
                    public void onSuccess(FactCheckResult result) {
                        hideLoading();
                        result.setInputType("image");
                        result.setOriginalInput(extractedText);
                        displayResult(result);
                        saveResultToFirestore(result);
                    }

                    @Override
                    public void onError(String message) {
                        hideLoading();
                        FactCheckForegroundService.stopVerification(MainActivity.this);
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                hideLoading();
                FactCheckForegroundService.stopVerification(MainActivity.this);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── URL Fact Check ──────────────────────────────────────────────────────────

    private void handleVerifyUrl() {
        String url = binding.etUrl.getText() != null ? binding.etUrl.getText().toString().trim() : "";
        if (url.isEmpty()) {
            Toast.makeText(this, "Please enter a URL to verify.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading("Scraping content from URL...");
        FactCheckForegroundService.startVerification(this, "Scraping & verifying article: " + url);

        ScraperService.scrapeUrl(url, new ScraperService.ScraperCallback() {
            @Override
            public void onSuccess(String extractedClaim) {
                showLoading("Verifying claim from article...");
                FactCheckerService.factCheck(extractedClaim, new FactCheckerService.FactCheckCallback() {
                    @Override
                    public void onSuccess(FactCheckResult result) {
                        hideLoading();
                        result.setInputType("url");
                        result.setOriginalInput(url);
                        displayResult(result);
                        saveResultToFirestore(result);
                    }

                    @Override
                    public void onError(String message) {
                        hideLoading();
                        FactCheckForegroundService.stopVerification(MainActivity.this);
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                hideLoading();
                FactCheckForegroundService.stopVerification(MainActivity.this);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Result Display (Matching User Screenshot 5) ─────────────────────────────

    private void displayResult(FactCheckResult result) {
        this.currentResult = result;
        FactCheckForegroundService.completeVerification(this, result.getVerdict(), result.getConfidence());
        binding.layoutResult.setVisibility(View.VISIBLE);

        String verdict = result.getVerdict() != null ? result.getVerdict().toUpperCase(Locale.ROOT) : "UNVERIFIED";
        int verdictColor;
        String verdictBadgeText;

        if ("TRUE".equals(verdict)) {
            verdictBadgeText = "✅ TRUE";
            verdictColor = ContextCompat.getColor(this, R.color.verdict_true);
            binding.tvConfidence.setText(result.getConfidence() + "% confident this is TRUE");
        } else if ("FALSE".equals(verdict)) {
            verdictBadgeText = "❌ FALSE";
            verdictColor = ContextCompat.getColor(this, R.color.verdict_false);
            binding.tvConfidence.setText(result.getConfidence() + "% confident this is FALSE");
        } else if ("MISLEADING".equals(verdict)) {
            verdictBadgeText = "⚠️ MISLEADING";
            verdictColor = ContextCompat.getColor(this, R.color.verdict_misleading);
            binding.tvConfidence.setText(result.getConfidence() + "% confident this is MISLEADING");
        } else {
            verdictBadgeText = "❓ UNVERIFIED";
            verdictColor = ContextCompat.getColor(this, R.color.verdict_unverified);
            binding.tvConfidence.setText(result.getConfidence() + "% confidence");
        }

        binding.tvVerdictBadge.setText(verdictBadgeText);
        binding.tvVerdictBadge.setTextColor(verdictColor);
        binding.verdictLine.setBackgroundColor(verdictColor);
        binding.cardVerdict.setStrokeColor(ContextCompat.getColor(this, R.color.border_color));

        binding.tvClaimResult.setText(result.getClaim());
        binding.tvExplanation.setText(result.getExplanation());

        // Sources
        binding.layoutSources.removeAllViews();
        if (result.getSources() != null && !result.getSources().isEmpty()) {
            binding.tvSourcesHeader.setVisibility(View.VISIBLE);
            binding.layoutSources.setVisibility(View.VISIBLE);
            for (Source s : result.getSources()) {
                View chip = LayoutInflater.from(this).inflate(R.layout.item_source_chip, binding.layoutSources, false);
                TextView tvTitle = chip.findViewById(R.id.tvSourceTitle);
                tvTitle.setText(s.getTitle());
                chip.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s.getUrl())));
                    } catch (Exception ignored) {}
                });
                binding.layoutSources.addView(chip);
            }
        } else {
            binding.tvSourcesHeader.setVisibility(View.GONE);
            binding.layoutSources.setVisibility(View.GONE);
        }

        // Action buttons & expandable cards reset
        binding.cardSummary.setVisibility(View.GONE);
        binding.cardChat.setVisibility(View.GONE);
        binding.btnGetSummaryAction.setText("Get Summary");
        binding.btnChatWithSondhaniAction.setText("Chat with Sondhani");
        binding.tvSummaryContent.setVisibility(View.GONE);
        binding.pbSummary.setVisibility(View.GONE);
        if (result.getSummary() != null && !result.getSummary().isEmpty()) {
            binding.tvSummaryContent.setText(result.getSummary());
        }

        // Init Chat
        currentChatService = new ChatService(result);
        chatAdapter.setMessages(null);
        chatAdapter.addMessage(new ChatMessage(ChatMessage.TYPE_AI, currentChatService.getInitialMessage()));

        // Scroll to results smoothly
        binding.scrollView.postDelayed(() -> {
            int targetY = binding.layoutResult.getTop();
            binding.scrollView.smoothScrollTo(0, targetY);
        }, 200);
    }

    private void saveResultToFirestore(FactCheckResult result) {
        // Increment global verified claims counter in Firebase Realtime Database
        firebaseManager.incrementLiveVerifiedCounter();

        FirebaseUser user = firebaseManager.getCurrentUser();
        if (user != null) {
            firebaseManager.saveSearch(user.getUid(), result, new FirebaseManager.FirestoreCallback<String>() {
                @Override
                public void onSuccess(String docId) {
                    result.setId(docId);
                }

                @Override
                public void onError(String message) {}
            });
        }
    }

    private void handleGetSummaryClick() {
        if (currentResult == null) return;

        if (binding.cardSummary.getVisibility() == View.VISIBLE) {
            binding.cardSummary.setVisibility(View.GONE);
            binding.btnGetSummaryAction.setText("Get Summary");
            return;
        }

        binding.cardSummary.setVisibility(View.VISIBLE);
        binding.btnGetSummaryAction.setText("Hide Summary");

        if (currentResult.getSummary() != null && !currentResult.getSummary().isEmpty()) {
            binding.tvSummaryContent.setText(currentResult.getSummary());
            binding.tvSummaryContent.setVisibility(View.VISIBLE);
            binding.pbSummary.setVisibility(View.GONE);
            binding.scrollView.postDelayed(() -> {
                int summaryY = binding.layoutResult.getTop() + binding.cardSummary.getTop();
                binding.scrollView.smoothScrollTo(0, summaryY);
            }, 100);
            return;
        }

        // Need to generate summary with Claude
        binding.pbSummary.setVisibility(View.VISIBLE);
        binding.tvSummaryContent.setVisibility(View.GONE);
        FactCheckerService.generateSummary(currentResult, new FactCheckerService.SummaryCallback() {
            @Override
            public void onSuccess(String summary) {
                binding.pbSummary.setVisibility(View.GONE);
                currentResult.setSummary(summary);
                binding.tvSummaryContent.setText(summary);
                binding.tvSummaryContent.setVisibility(View.VISIBLE);
                binding.scrollView.postDelayed(() -> {
                    int summaryY = binding.layoutResult.getTop() + binding.cardSummary.getTop();
                    binding.scrollView.smoothScrollTo(0, summaryY);
                }, 100);

                // Update in Firestore
                FirebaseUser user = firebaseManager.getCurrentUser();
                if (user != null && currentResult.getId() != null) {
                    firebaseManager.updateSearchSummary(user.getUid(), currentResult.getId(), summary, new FirebaseManager.SimpleCallback() {
                        @Override
                        public void onSuccess() {}
                        @Override
                        public void onError(String message) {}
                    });
                }
            }

            @Override
            public void onError(String message) {
                binding.pbSummary.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Could not generate summary: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleChatWithSondhaniClick() {
        if (currentResult == null) return;

        if (binding.cardChat.getVisibility() == View.VISIBLE) {
            binding.cardChat.setVisibility(View.GONE);
            binding.btnChatWithSondhaniAction.setText("Chat with Sondhani");
            return;
        }

        binding.cardChat.setVisibility(View.VISIBLE);
        binding.btnChatWithSondhaniAction.setText("Close Chat");
        binding.scrollView.postDelayed(() -> {
            int chatY = binding.layoutResult.getTop() + binding.cardChat.getTop();
            binding.scrollView.smoothScrollTo(0, chatY);
            binding.etChatMessage.requestFocus();
        }, 100);
    }

    private void handleSummaryToggle() {
        if (binding.cardSummary.getVisibility() == View.VISIBLE) {
            binding.cardSummary.setVisibility(View.GONE);
            binding.btnGetSummaryAction.setText("Get Summary");
        }
    }

    private void handleSendChatMessage() {
        String msg = binding.etChatMessage.getText() != null ? binding.etChatMessage.getText().toString().trim() : "";
        if (msg.isEmpty() || currentChatService == null) return;

        binding.etChatMessage.setText("");
        chatAdapter.addMessage(new ChatMessage(ChatMessage.TYPE_USER, msg));
        binding.rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        binding.scrollView.postDelayed(() -> binding.scrollView.fullScroll(View.FOCUS_DOWN), 100);

        currentChatService.sendMessage(msg, new ChatService.ChatCallback() {
            @Override
            public void onSuccess(String reply) {
                chatAdapter.addMessage(new ChatMessage(ChatMessage.TYPE_AI, reply));
                binding.rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                binding.scrollView.postDelayed(() -> binding.scrollView.fullScroll(View.FOCUS_DOWN), 100);
            }

            @Override
            public void onError(String message) {
                chatAdapter.addMessage(new ChatMessage(ChatMessage.TYPE_AI, "Sorry, I couldn't reach the assistant right now."));
                binding.scrollView.postDelayed(() -> binding.scrollView.fullScroll(View.FOCUS_DOWN), 100);
            }
        });
    }
}
