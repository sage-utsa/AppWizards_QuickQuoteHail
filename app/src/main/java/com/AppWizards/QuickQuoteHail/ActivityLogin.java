/*
 * ActivityLogin.java
 *
 * Displays the login screen for the QuickQuoteHail app.
 * Handles basic user login using a file-based credential system.
 * Beginner-friendly MVC: defers auth to models.AuthManager.
 *
 * Author: Sage
 * Date: 2025-07-23
 */

package com.AppWizards.QuickQuoteHail;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import models.AuthManager; // << use model layer

/**
 * ActivityLogin displays the login screen and handles basic credential-based authentication.
 * If credentials are valid, it moves to the main screen.
 */
public class ActivityLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge layout and set login screen view
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Auth helper (seeds test user on first run)
        final AuthManager auth = new AuthManager(this);

        // === Link UI elements ===
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        TextView signUpPrompt = findViewById(R.id.signUpPrompt); // "Sign up here" link
        ImageView eyeIcon = findViewById(R.id.eyeIcon); // toggle password visibility

        // === Password Visibility Toggle ===
        final boolean[] isPasswordVisible = {false};
        eyeIcon.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                // Hide password
                passwordInput.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeIcon.setImageResource(R.drawable.ic_visibility); // hidden icon
            } else {
                // Show password
                passwordInput.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeIcon.setImageResource(R.drawable.ic_visibility_off); // visible icon
            }
            isPasswordVisible[0] = !isPasswordVisible[0];
            passwordInput.setSelection(passwordInput.getText().length());
        });

        // === Handle Login Button Click ===
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(ActivityLogin.this,
                        "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Delegate to model layer (no direct file I/O here)
            boolean matchFound = auth.login(email, password);

            if (matchFound) {
                Toast.makeText(ActivityLogin.this, "Login successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ActivityLogin.this, ActivityDashboard.class);
                startActivity(intent);
                finish(); // Prevents returning to login screen
            } else {
                Toast.makeText(ActivityLogin.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
            }
        });

        // === Navigate to ActivityRegister when "Sign Up" is clicked ===
        signUpPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
            startActivity(intent);
        });

        // === Adjust layout padding ===
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
