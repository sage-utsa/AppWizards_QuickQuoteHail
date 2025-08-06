/*
 * LoginActivity.java
 *
 * Displays the login screen for the QuickQuoteHail app.
 * Handles basic user login using a file-based credential system.
 * This version includes clear comments and Javadoc to aid beginner understanding.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * LoginActivity displays the login screen and handles basic credential-based authentication.
 * If credentials are valid, it moves to the main screen.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * Sets up the UI and handles user login and sign-up interactions.
     *
     * @param savedInstanceState saved app state from previous instances (not used here)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge layout and set login screen view
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // === Step 1: Create a sample credentials file if it doesn't already exist ===
        String filename = "credentials.txt";
        String testData = "user@example.com:password123\n";  // sample user

        try {
            File file = new File(getFilesDir(), filename);
            if (!file.exists()) {
                FileOutputStream output = openFileOutput(filename, MODE_PRIVATE);
                output.write(testData.getBytes());
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();  // Log the error to help with debugging
        }

        // === Step 2: Link all UI elements to their XML layout IDs ===
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        TextView signUpPrompt = findViewById(R.id.signUpPrompt); // "Sign up here" link
        ImageView eyeIcon = findViewById(R.id.eyeIcon); // toggle password visibility

        // === Password Visibility Toggle ===

        /**
         * This boolean array is used to track password visibility.
         * Arrays are used here because variables in lambdas must be final or effectively final.
         */
        final boolean[] isPasswordVisible = {false};

        // Toggle password visibility when eye icon is clicked
        eyeIcon.setOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                // Hide password
                passwordInput.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeIcon.setImageResource(R.drawable.ic_visibility); // icon for hidden password
            } else {
                // Show password
                passwordInput.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeIcon.setImageResource(R.drawable.ic_visibility_off); // icon for visible password
            }

            // Toggle the flag and move cursor to end of text
            isPasswordVisible[0] = !isPasswordVisible[0];
            passwordInput.setSelection(passwordInput.getText().length());
        });

        // === Step 3: Handle Login Button Click ===
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // === Step 3.1: Read the credentials file and check for match ===
            try {
                FileInputStream input = openFileInput("credentials.txt");
                Scanner scanner = new Scanner(input);
                boolean matchFound = false;

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine(); // e.g., user@example.com:password123
                    String[] parts = line.split(":");

                    if (parts.length == 2) {
                        String savedEmail = parts[0].trim();
                        String savedPassword = parts[1].trim();

                        if (email.equals(savedEmail) && password.equals(savedPassword)) {
                            matchFound = true;
                            break;
                        }
                    }
                }

                scanner.close();

                // === Step 3.2: Respond to login attempt ===
                if (matchFound) {
                    Toast.makeText(LoginActivity.this,
                            "Login successful!", Toast.LENGTH_SHORT).show();

                    // Navigate to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Prevents returning to login screen
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Invalid email or password.", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                Toast.makeText(LoginActivity.this,
                        "Error reading credentials file.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        // === Step 4: Navigate to RegisterActivity when "Sign Up" is clicked ===
        signUpPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // === Step 5: Adjust layout padding ===
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
