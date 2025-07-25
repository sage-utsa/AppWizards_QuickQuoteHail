package com.AppWizards.QuickQuoteHail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button calculateVehicleButton = findViewById(R.id.calculateVehicleButton);
        Button historyButton = findViewById(R.id.historyButton);

        // Set up listener for "Calculate Vehicle" button
        calculateVehicleButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivityCalculator.class);
            startActivity(intent);
        });

        // Set up listener for "History" button
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivityInvoice.class);
            startActivity(intent);
        });
    }
}
