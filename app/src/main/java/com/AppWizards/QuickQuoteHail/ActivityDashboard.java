package com.AppWizards.QuickQuoteHail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * ActivityDashboard serves as the main navigation hub for the QuickQuoteHail app.
 * After logging in, users are directed here to choose between:
 * - Calculating hail damage for a vehicle
 * - Viewing previously saved invoices
 */
public class ActivityDashboard extends AppCompatActivity {

    /**
     * Called when the dashboard activity is created.
     * Sets up the layout and initializes button listeners for navigation.
     *
     * @param savedInstanceState Previously saved state (not used here)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // If renamed XML, change to activity_dashboard

        // Link buttons to their views
        Button calculateVehicleButton = findViewById(R.id.calculateVehicleButton);
        Button historyButton = findViewById(R.id.historyButton);

        // When "Calculate Vehicle" is clicked, navigate to the calculator screen
        calculateVehicleButton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityDashboard.this, ActivityCalculator.class);
            startActivity(intent);
        });

        // When "History" is clicked, navigate to the invoice history screen
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityDashboard.this, ActivityInvoice.class);
            startActivity(intent);
        });
    }
}
