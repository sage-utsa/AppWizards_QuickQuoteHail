package com.AppWizards.QuickQuoteHail;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import models.AuthManager;

/**
 * Handles user registration for QuickQuoteHail.
 * Beginner-friendly MVC: delegates credential storage to models.AuthManager.
 */
public class ActivityRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // === UI Elements ===
        EditText emailInput = findViewById(R.id.registerEmailInput);
        EditText passwordInput = findViewById(R.id.registerPasswordInput);
        EditText confirmPasswordInput = findViewById(R.id.registerConfirmPasswordInput);
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginPrompt = findViewById(R.id.loginPrompt);

        // Auth helper (seeds default test user on first run)
        final AuthManager auth = new AuthManager(this);

        // "Already have an account? Log in." -> return to Login screen
        loginPrompt.setOnClickListener(v -> finish());

        // Handle Register button
        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            // --- Basic validation ---
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- Delegate to model layer ---
            boolean created = auth.register(email, password);

            if (created) {
                Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                finish(); // go back to Login
            } else {
                Toast.makeText(this, "Account already exists.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
