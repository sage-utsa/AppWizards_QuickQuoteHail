/*
 * LoginActivity.java
 *
 * Displays the login screen for the QuickQuoteHail app.
 * Handles basic user login using a simple file-based credential system.
 * This version is written to be beginner-friendly and easy to follow.
 *
 * Author: Sage
 * Date: 2025-07-23
 */

package com.AppWizards.QuickQuoteHail;

// === Android Libraries ===
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

// === UI Layout Utilities ===
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// === Java Utilities for File Handling ===
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    // Called when the login screen is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // === Setup full screen layout and load login screen ===
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // === Step 1: Create a sample credentials file if it doesn't exist ===
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
            e.printStackTrace();  // log the error for debugging
        }

        // === Step 2: Link the UI elements (email/password input and buttons) ===
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        TextView signUpPrompt = findViewById(R.id.signUpPrompt); // "Sign up here" text

        // === Step 3: Handle Login Button Click ===
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // === Step 3.1: Read the credentials file and check for a match ===
            try {
                FileInputStream input = openFileInput("credentials.txt");
                Scanner scanner = new Scanner(input);
                boolean matchFound = false;

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine(); // ex: user@example.com:password123
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

                // === Step 3.2: Show result to user ===
                if (matchFound) {
                    Toast.makeText(LoginActivity.this,
                            "Login successful!", Toast.LENGTH_SHORT).show();
                    // TODO: Navigate to dashboard or main screen
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

        // === Step 4: Make "Sign Up" text clickable to open RegisterActivity ===
        signUpPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // === Step 5: Apply system bar padding to the root layout (for edge-to-edge) ===
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
