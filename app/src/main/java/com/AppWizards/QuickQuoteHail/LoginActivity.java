/*
 * LoginActivity.java
 *
 * Displays the login screen for the QuickQuoteHail app.
 * This activity shows the app logo and background, and will eventually handle
 * user authentication input and logic.
 *
 * @author Sage
 * @date 2025-07-23
 */

package com.AppWizards.QuickQuoteHail;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * The initial screen shown to users when the app is launched.
 *
 * Loads the activity_login.xml layout, applies system bar padding,
 * and serves as the entry point for login functionality.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * Sets the content view and applies window insets to avoid overlap with system bars.
     *
     * @param savedInstanceState Previously saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enables edge-to-edge layout for full background display
        EdgeToEdge.enable(this);

        // Set the layout for the login screen
        setContentView(R.layout.activity_login);

        // Apply system bar padding to the root layout (id: main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
