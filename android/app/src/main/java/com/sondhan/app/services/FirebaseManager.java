package com.sondhan.app.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.sondhan.app.config.AppConfig;
import com.sondhan.app.models.FactCheckResult;
import com.sondhan.app.models.SearchHistoryItem;
import com.sondhan.app.models.Source;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static FirebaseManager instance;

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final FirebaseDatabase realtimeDb;
    private final FirebaseStorage storage;

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface FirestoreCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    public interface RealtimeCallback<T> {
        void onDataChange(T data);
        void onError(String message);
    }

    public interface StorageCallback {
        void onSuccess(String downloadUrl);
        void onError(String message);
    }

    private FirebaseManager(Context context) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context.getApplicationContext());
            Log.d(TAG, "Firebase initialized.");
        }
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.realtimeDb = FirebaseDatabase.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseManager(context);
        }
        return instance;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void signOut() {
        auth.signOut();
    }

    public void signIn(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(authResult -> mainHandler.post(() -> callback.onSuccess(authResult.getUser())))
                .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
    }

    public void signInWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("fullName", user.getDisplayName() != null ? user.getDisplayName() : "Google User");
                        userData.put("email", user.getEmail() != null ? user.getEmail().toLowerCase() : "");
                        userData.put("lastLogin", FieldValue.serverTimestamp());

                        db.collection("users").document(user.getUid())
                                .set(userData, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> mainHandler.post(() -> callback.onSuccess(user)))
                                .addOnFailureListener(e -> mainHandler.post(() -> callback.onSuccess(user)));
                    } else {
                        mainHandler.post(() -> callback.onSuccess(null));
                    }
                })
                .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
    }

    public void register(String email, String password, String fullName, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("fullName", fullName.trim());
                        userData.put("email", email.trim().toLowerCase());
                        userData.put("createdAt", FieldValue.serverTimestamp());

                        db.collection("users").document(user.getUid()).set(userData)
                                .addOnSuccessListener(aVoid -> mainHandler.post(() -> callback.onSuccess(user)))
                                .addOnFailureListener(e -> mainHandler.post(() -> callback.onSuccess(user))); // user still created
                    }
                })
                .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
    }

    public void saveSearch(String userId, FactCheckResult result, FirestoreCallback<String> callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("input_type", result.getInputType() != null ? result.getInputType() : "text");
        data.put("original_input", result.getOriginalInput() != null ? result.getOriginalInput() : result.getClaim());
        data.put("claim", result.getClaim());
        data.put("verdict", result.getVerdict());
        data.put("confidence", result.getConfidence());
        data.put("explanation", result.getExplanation());
        data.put("summary", result.getSummary());
        data.put("createdAt", FieldValue.serverTimestamp());

        List<Map<String, String>> sourcesList = new ArrayList<>();
        if (result.getSources() != null) {
            for (Source s : result.getSources()) {
                Map<String, String> sMap = new HashMap<>();
                sMap.put("title", s.getTitle());
                sMap.put("url", s.getUrl());
                sourcesList.add(sMap);
            }
        }
        data.put("sources", sourcesList);

        db.collection("users").document(userId).collection("searches")
                .add(data)
                .addOnSuccessListener(docRef -> {
                    result.setId(docRef.getId());
                    mainHandler.post(() -> callback.onSuccess(docRef.getId()));
                })
                .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
    }

    public void updateSearchSummary(String userId, String searchId, String summaryText, SimpleCallback callback) {
        db.collection("users").document(userId).collection("searches").document(searchId)
                .update("summary", summaryText)
                .addOnSuccessListener(aVoid -> mainHandler.post(callback::onSuccess))
                .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
    }

    public void getUserSearches(String userId, String inputTypeFilter, FirestoreCallback<List<SearchHistoryItem>> callback) {
        Query query = db.collection("users").document(userId).collection("searches");
        if (inputTypeFilter != null && !inputTypeFilter.isEmpty() && !"all".equalsIgnoreCase(inputTypeFilter)) {
            query = query.whereEqualTo("input_type", inputTypeFilter.toLowerCase());
        }
        query.orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<SearchHistoryItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        SearchHistoryItem item = new SearchHistoryItem();
                        item.setId(doc.getId());
                        item.setInputType(doc.getString("input_type"));
                        item.setOriginalInput(doc.getString("original_input"));
                        item.setClaim(doc.getString("claim"));
                        item.setVerdict(doc.getString("verdict"));
                        Long conf = doc.getLong("confidence");
                        item.setConfidence(conf != null ? conf.intValue() : 0);
                        item.setExplanation(doc.getString("explanation"));
                        item.setSummary(doc.getString("summary"));
                        item.setCreatedAt(doc.getDate("createdAt"));

                        List<Map<String, Object>> srcRaw = (List<Map<String, Object>>) doc.get("sources");
                        List<Source> sources = new ArrayList<>();
                        if (srcRaw != null) {
                            for (Map<String, Object> sm : srcRaw) {
                                String t = sm.containsKey("title") && sm.get("title") != null ? sm.get("title").toString() : "";
                                String u = sm.containsKey("url") && sm.get("url") != null ? sm.get("url").toString() : "";
                                sources.add(new Source(t, u));
                            }
                        }
                        item.setSources(sources);
                        items.add(item);
                    }
                    mainHandler.post(() -> callback.onSuccess(items));
                })
                .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
    }

    public void getSearchCounts(String userId, FirestoreCallback<Map<String, Integer>> callback) {
        db.collection("users").document(userId).collection("searches")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, Integer> counts = new HashMap<>();
                    counts.put("text", 0);
                    counts.put("image", 0);
                    counts.put("url", 0);

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String type = doc.getString("input_type");
                        if (type != null) {
                            type = type.toLowerCase();
                            counts.put(type, counts.getOrDefault(type, 0) + 1);
                        }
                    }
                    mainHandler.post(() -> callback.onSuccess(counts));
                })
                .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
    }

    public void deleteSearch(String userId, String searchId, SimpleCallback callback) {
        db.collection("users").document(userId).collection("searches").document(searchId)
                .delete()
                .addOnSuccessListener(aVoid -> mainHandler.post(callback::onSuccess))
                .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
    }

    public void clearAllSearches(String userId, String inputTypeFilter, SimpleCallback callback) {
        Query query = db.collection("users").document(userId).collection("searches");
        if (inputTypeFilter != null && !inputTypeFilter.isEmpty() && !"all".equalsIgnoreCase(inputTypeFilter)) {
            query = query.whereEqualTo("input_type", inputTypeFilter.toLowerCase());
        }
        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit()
                            .addOnSuccessListener(aVoid -> mainHandler.post(callback::onSuccess))
                            .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
                })
                .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
    }

    // ─── Firebase Realtime Database ──────────────────────────────────────────────

    /**
     * Atomically increments the global verified claims counter in Firebase Realtime Database.
     */
    public void incrementLiveVerifiedCounter() {
        try {
            DatabaseReference ref = realtimeDb.getReference("stats").child("total_claims_verified");
            ref.setValue(ServerValue.increment(1));
        } catch (Exception e) {
            Log.w(TAG, "Realtime DB increment error: " + e.getMessage());
        }
    }

    /**
     * Listens for real-time changes to the verified claims counter across all connected devices.
     */
    public ValueEventListener listenToLiveClaimsCounter(RealtimeCallback<Long> callback) {
        try {
            DatabaseReference ref = realtimeDb.getReference("stats").child("total_claims_verified");
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Long count = snapshot.getValue(Long.class);
                    mainHandler.post(() -> callback.onDataChange(count != null ? count : 0L));
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    mainHandler.post(() -> callback.onError(error.getMessage()));
                }
            };
            ref.addValueEventListener(listener);
            return listener;
        } catch (Exception e) {
            Log.w(TAG, "Realtime DB listener error: " + e.getMessage());
            return null;
        }
    }

    public void removeRealtimeListener(ValueEventListener listener) {
        if (listener != null) {
            try {
                realtimeDb.getReference("stats").child("total_claims_verified").removeEventListener(listener);
            } catch (Exception ignored) {}
        }
    }

    // ─── Firebase Storage (Media / Picture Upload) ───────────────────────────────

    /**
     * Uploads user-submitted fact-check image to Firebase Cloud Storage
     * under fact_check_images/{userId}/{filename}.jpg and returns public download URL.
     */
    public void uploadFactCheckImage(String userId, byte[] imageBytes, StorageCallback callback) {
        if (imageBytes == null || imageBytes.length == 0) {
            callback.onError("No image bytes provided");
            return;
        }
        try {
            String filename = "claim_" + System.currentTimeMillis() + ".jpg";
            StorageReference ref = storage.getReference().child("fact_check_images").child(userId).child(filename);
            ref.putBytes(imageBytes)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                            .addOnSuccessListener(uri -> mainHandler.post(() -> callback.onSuccess(uri.toString())))
                            .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage()))))
                    .addOnFailureListener(e -> mainHandler.post(() -> callback.onError(e.getMessage())));
        } catch (Exception e) {
            mainHandler.post(() -> callback.onError(e.getMessage()));
        }
    }
}

