package com.AppWizards.QuickQuoteHail;

import android.os.Bundle;
import android.text.Html; // <-- Needed for styled text
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ========== UI Elements ==========
        EditText emailInput = findViewById(R.id.registerEmailInput);
        EditText passwordInput = findViewById(R.id.registerPasswordInput);
        EditText confirmPasswordInput = findViewById(R.id.registerConfirmPasswordInput);
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginPrompt = findViewById(R.id.loginPrompt);



        // ========== Set styled text: "Already have an account? Log in." ==========
        loginPrompt.setOnClickListener(v -> {
            finish(); // Go back to login screen
        });






        // ========== Handle Login Prompt Tap ==========
        loginPrompt.setOnClickListener(v -> {
            finish(); // Go back to login screen
        });

        // ========== Handle Register Button Tap ==========
        registerButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            // --- Step 1: Basic validation ---
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- Step 2: Check if email is already registered ---
            boolean userExists = false;

            try {
                FileInputStream input = openFileInput("credentials.txt");
                Scanner scanner = new Scanner(input);

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(":");
                    if (parts.length == 2 && parts[0].trim().equals(email)) {
                        userExists = true;
                        break;
                    }
                }

                scanner.close();
            } catch (FileNotFoundException e) {
                // No credentials file yet — totally fine
                userExists = false;
            } catch (IOException e) {
                Toast.makeText(this, "Error reading file.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }

            // --- Step 3: Save credentials if not taken ---
            if (userExists) {
                Toast.makeText(this, "Account already exists.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    FileOutputStream output = openFileOutput("credentials.txt", MODE_APPEND);
                    String entry = email + ":" + password + "\n";
                    output.write(entry.getBytes());
                    output.close();

                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to login screen
                } catch (IOException e) {
                    Toast.makeText(this, "Error saving account.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
