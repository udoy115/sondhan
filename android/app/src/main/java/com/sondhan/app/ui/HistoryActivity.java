package com.sondhan.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseUser;
import com.sondhan.app.MainActivity;
import com.sondhan.app.R;
import com.sondhan.app.databinding.ActivityHistoryBinding;
import com.sondhan.app.models.SearchHistoryItem;
import com.sondhan.app.services.FirebaseManager;
import com.sondhan.app.ui.adapters.HistoryAdapter;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private ActivityHistoryBinding binding;
    private FirebaseManager firebaseManager;
    private HistoryAdapter adapter;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance(this);

        setupRecyclerView();
        setupFilters();
        setupBottomNav();

        binding.btnBackHistory.setOnClickListener(v -> finish());
        binding.btnClearAll.setOnClickListener(v -> handleClearAll());

        fetchHistory();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(item -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Fact Check")
                    .setMessage("Are you sure you want to delete this fact check from history?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        FirebaseUser user = firebaseManager.getCurrentUser();
                        if (user != null) {
                            firebaseManager.deleteSearch(user.getUid(), item.getId(), new FirebaseManager.SimpleCallback() {
                                @Override
                                public void onSuccess() {
                                    adapter.removeItem(item);
                                    Toast.makeText(HistoryActivity.this, "Item deleted.", Toast.LENGTH_SHORT).show();
                                    if (adapter.getItemCount() == 0) {
                                        binding.tvEmptyHistory.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onError(String message) {
                                    Toast.makeText(HistoryActivity.this, "Failed to delete: " + message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(adapter);
    }

    private void setupFilters() {
        binding.historyToggleGroup.check(R.id.tabHistAll);
        binding.historyToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.tabHistAll) {
                    currentFilter = "all";
                } else if (checkedId == R.id.tabHistUrl) {
                    currentFilter = "url";
                } else if (checkedId == R.id.tabHistImage) {
                    currentFilter = "image";
                } else if (checkedId == R.id.tabHistText) {
                    currentFilter = "text";
                }
                fetchHistory();
            }
        });
    }

    private void setupBottomNav() {
        binding.navHistHome.setOnClickListener(v -> {
            startActivity(new Intent(HistoryActivity.this, MainActivity.class));
            finish();
            overridePendingTransition(0, 0);
        });
    }

    private void fetchHistory() {
        FirebaseUser user = firebaseManager.getCurrentUser();
        if (user == null) return;

        binding.pbHistory.setVisibility(View.VISIBLE);
        binding.tvEmptyHistory.setVisibility(View.GONE);

        firebaseManager.getUserSearches(user.getUid(), currentFilter, new FirebaseManager.FirestoreCallback<List<SearchHistoryItem>>() {
            @Override
            public void onSuccess(List<SearchHistoryItem> data) {
                binding.pbHistory.setVisibility(View.GONE);
                adapter.setItems(data);
                if (data == null || data.isEmpty()) {
                    binding.tvEmptyHistory.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmptyHistory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String message) {
                binding.pbHistory.setVisibility(View.GONE);
                Toast.makeText(HistoryActivity.this, "Error fetching history: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleClearAll() {
        FirebaseUser user = firebaseManager.getCurrentUser();
        if (user == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Clear History")
                .setMessage("Are you sure you want to delete all entries in this category?")
                .setPositiveButton("Clear All", (dialog, which) -> {
                    firebaseManager.clearAllSearches(user.getUid(), currentFilter, new FirebaseManager.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(HistoryActivity.this, "History cleared.", Toast.LENGTH_SHORT).show();
                            fetchHistory();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(HistoryActivity.this, "Failed to clear: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

