package com.sondhan.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.sondhan.app.MainActivity;
import com.sondhan.app.config.AppConfig;
import com.sondhan.app.databinding.ActivityRegisterBinding;
import com.sondhan.app.services.FirebaseManager;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseManager firebaseManager;
    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignUpLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSignUpResult(task);
                } else {
                    setLoading(false);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance(this);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AppConfig.GOOGLE_WEB_CLIENT_ID)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvGoToLogin.setOnClickListener(v -> finish());

        binding.btnRegister.setOnClickListener(v -> handleRegister());
        binding.btnGoogleSignUp.setOnClickListener(v -> handleGoogleSignUp());
    }

    private void handleRegister() {
        String fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
        String email = binding.etRegEmail.getText() != null ? binding.etRegEmail.getText().toString().trim() : "";
        String password = binding.etRegPassword.getText() != null ? binding.etRegPassword.getText().toString() : "";
        String confirmPass = binding.etConfirmPassword.getText() != null ? binding.etConfirmPassword.getText().toString() : "";

        if (fullName.isEmpty()) {
            showError("Full name is required.");
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address.");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirmPass)) {
            showError("Passwords do not match.");
            return;
        }

        setLoading(true);
        binding.tvRegError.setVisibility(View.GONE);

        firebaseManager.register(email, password, fullName, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                showError(message != null ? message : "Registration failed.");
            }
        });
    }

    private void handleGoogleSignUp() {
        setLoading(true);
        binding.tvRegError.setVisibility(View.GONE);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignUpLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignUpResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null && account.getIdToken() != null) {
                firebaseManager.signInWithGoogle(account.getIdToken(), new FirebaseManager.AuthCallback() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        setLoading(false);
                        String name = user != null && user.getDisplayName() != null ? user.getDisplayName() : "User";
                        Toast.makeText(RegisterActivity.this, "Welcome " + name + "!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        setLoading(false);
                        showError(message != null ? message : "Google authentication failed.");
                    }
                });
            } else {
                setLoading(false);
                showError("Could not retrieve Google ID token.");
            }
        } catch (ApiException e) {
            setLoading(false);
            showError("Google sign-in error (" + e.getStatusCode() + "): " + e.getMessage());
        }
    }

    private void setLoading(boolean loading) {
        binding.regProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!loading);
        binding.btnGoogleSignUp.setEnabled(!loading);
        binding.etFullName.setEnabled(!loading);
        binding.etRegEmail.setEnabled(!loading);
        binding.etRegPassword.setEnabled(!loading);
        binding.etConfirmPassword.setEnabled(!loading);
    }

    private void showError(String msg) {
        binding.tvRegError.setText(msg);
        binding.tvRegError.setVisibility(View.VISIBLE);
    }
}
