package com.AppWizards.QuickQuoteHail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCalculator extends AppCompatActivity {

    private Spinner panelSpinner;
    private LinearLayout dynamicPanelsContainer;
    private Button calculateAllCostsButton;
    private TextView totalEstimatedCostDisplay;

    // List to hold data for each added panel
    private List<PanelInputData> panelInputDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // === 1. Initialize UI Elements ===
        panelSpinner = findViewById(R.id.panelSpinner);
        dynamicPanelsContainer = findViewById(R.id.dynamicPanelsContainer);
        calculateAllCostsButton = findViewById(R.id.calculateAllCostsButton);
        totalEstimatedCostDisplay = findViewById(R.id.totalEstimatedCostDisplay);

        panelInputDataList = new ArrayList<>();

        // === 2. Populate Spinner ===
        String[] panelTypes = {
                "Select Panel", // Default hint
                "HOOD", "ROOF", "TRUNK",
                "LFF", "LFD", "LG", "LQ", "LRAIL",
                "RFF", "RFD", "RG", "RQ", "RRAIL"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, panelTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        panelSpinner.setAdapter(adapter);

        // === 3. Spinner Item Selection Listener ===
        panelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (position > 0) { // If a real panel is selected (not "Select Panel")
                    addPanelInputSection(selectedItem);
                    panelSpinner.setSelection(0); // Reset spinner to "Select Panel" after selection
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // === 4. Calculate All Costs Button Listener ===
        calculateAllCostsButton.setOnClickListener(v -> calculateAndDisplayAllCosts());
    }

    /**
     * Dynamically adds a new panel input section to the UI.
     * @param panelType The type of panel selected.
     */
    private void addPanelInputSection(String panelType) {
        // Check if the panel of this type already exists
        for (PanelInputData data : panelInputDataList) {
            if (data.panelType.equals(panelType)) {
                Toast.makeText(this, panelType + " panel already added.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        // Inflate the panel_input_section.xml and cast to LinearLayout
        View panelInputView = inflater.inflate(R.layout.panel_input_section, dynamicPanelsContainer, false);


        // Create a new PanelInputData object for this panel
        PanelInputData newPanelData = new PanelInputData(panelType);
        // Store reference to the inflated layout in the PanelInputData object
        newPanelData.panelLayout = (LinearLayout) panelInputView;

        // Initialize UI elements for the new panel input section
        newPanelData.selectedPanelDisplay = panelInputView.findViewById(R.id.selectedPanelDisplay);
        newPanelData.dentSizeDisplay = panelInputView.findViewById(R.id.dentSizeDisplay);
        newPanelData.numDentsDisplay = panelInputView.findViewById(R.id.numDentsDisplay);
        newPanelData.selectDentSizeButton = panelInputView.findViewById(R.id.selectDentSizeButton);
        newPanelData.enterNumDentsButton = panelInputView.findViewById(R.id.enterNumDentsButton);
        newPanelData.aluminumSwitch = panelInputView.findViewById(R.id.aluminumSwitch);
        newPanelData.panelEstimatedCostDisplay = panelInputView.findViewById(R.id.panelEstimatedCostDisplay); // New TextView for individual panel cost

        // Set initial values
        newPanelData.selectedPanelDisplay.setText("Selected Panel: " + newPanelData.panelType);
        newPanelData.dentSizeDisplay.setText("Largest Dent Size: " + newPanelData.largestDentSize);
        newPanelData.numDentsDisplay.setText("Number of Dents: " + (newPanelData.numberOfDents == -1 ? "Not Set" : String.valueOf(newPanelData.numberOfDents)));
        newPanelData.aluminumSwitch.setChecked(newPanelData.isAluminum);
        newPanelData.panelEstimatedCostDisplay.setText("Estimated Cost: " + newPanelData.estimatedCost);


        // Set listeners for this specific panel's buttons and switch
        newPanelData.selectDentSizeButton.setOnClickListener(v -> showDentSizeDialog(newPanelData));
        newPanelData.enterNumDentsButton.setOnClickListener(v -> showNumDentsDialog(newPanelData));
        newPanelData.aluminumSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            newPanelData.isAluminum = isChecked;
            newPanelData.panelEstimatedCostDisplay.setText("Estimated Cost: N/A");
            updateTotalEstimatedCostDisplay(); // Recalculate total if aluminum status changes
        });

        // Adds a "Remove Panel" button to each dynamically added panel
        Button removePanelButton = new Button(this);
        removePanelButton.setText("Remove " + panelType + " Panel");
        // Ensure you have this drawable created
        removePanelButton.setBackgroundResource(R.drawable.button_background_red);
        removePanelButton.setTextColor(getResources().getColor(android.R.color.white));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 16, 0, 0); // Add some margin
        removePanelButton.setLayoutParams(layoutParams);
        removePanelButton.setOnClickListener(v -> removePanelInputSection(newPanelData));
        newPanelData.panelLayout.addView(removePanelButton);

        dynamicPanelsContainer.addView(panelInputView);
        panelInputDataList.add(newPanelData);

        updateTotalEstimatedCostDisplay(); // Update total cost after adding a new panel
    }

    /**
     * Removes a panel input section from the UI and its data.
     * @param panelData The PanelInputData object to remove.
     */
    private void removePanelInputSection(PanelInputData panelData) {
        dynamicPanelsContainer.removeView(panelData.panelLayout);
        panelInputDataList.remove(panelData);
        updateTotalEstimatedCostDisplay();
        Toast.makeText(this, panelData.panelType + " panel removed.", Toast.LENGTH_SHORT).show();
    }


    private void showDentSizeDialog(PanelInputData currentPanelData) {
        final String[] dentSizes = {"D", "N", "Q", "H"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Largest Dent Size for " + currentPanelData.panelType);
        builder.setSingleChoiceItems(dentSizes, -1, (dialog, which) -> {
            currentPanelData.largestDentSize = dentSizes[which];
            currentPanelData.dentSizeDisplay.setText("Largest Dent Size: " + currentPanelData.largestDentSize);
            dialog.dismiss();
            currentPanelData.panelEstimatedCostDisplay.setText("Estimated Cost: N/A");
            updateTotalEstimatedCostDisplay();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showNumDentsDialog(PanelInputData currentPanelData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Number of Dents for " + currentPanelData.panelType);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("e.g., 15");
        // Pre-fill if a number was previously entered
        if (currentPanelData.numberOfDents != -1) {
            input.setText(String.valueOf(currentPanelData.numberOfDents));
        }
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String numStr = input.getText().toString().trim();
            if (!numStr.isEmpty()) {
                try {
                    int num = Integer.parseInt(numStr);
                    if (num >= 1) {
                        currentPanelData.numberOfDents = num;
                        currentPanelData.numDentsDisplay.setText("Number of Dents: " + currentPanelData.numberOfDents);
                        currentPanelData.panelEstimatedCostDisplay.setText("Estimated Cost: N/A");
                        updateTotalEstimatedCostDisplay();
                    } else {
                        Toast.makeText(ActivityCalculator.this, "Please enter a positive number.", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(ActivityCalculator.this, "Invalid number.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ActivityCalculator.this, "Please enter a number.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void calculateAndDisplayAllCosts() {
        double totalCost = 0.0;
        boolean allPanelsReady = true;
        Calculator calculator = new Calculator(); // Initialize Calculator once

        if (panelInputDataList.isEmpty()) {
            Toast.makeText(this, "No panels added to calculate.", Toast.LENGTH_SHORT).show();
            totalEstimatedCostDisplay.setText("Total Estimated Cost: N/A");
            return;
        }

        for (PanelInputData panelData : panelInputDataList) {
            if (panelData.largestDentSize.equals("Not Set") || panelData.numberOfDents == -1) {
                allPanelsReady = false;
                Toast.makeText(this, "Please complete all inputs for " + panelData.panelType + " panel.", Toast.LENGTH_LONG).show();
                // Optionally, scroll to the incomplete panel
                if (panelData.panelLayout != null) {
                    panelData.panelLayout.getParent().requestChildFocus(panelData.panelLayout, panelData.panelLayout);
                }
                return; // Exit if any panel is incomplete
            }

            // Calculate cost for this individual panel
            String costString = calculator.getEstimatedCost(
                    panelData.panelType,
                    panelData.largestDentSize,
                    panelData.numberOfDents,
                    panelData.isAluminum
            );
            panelData.estimatedCost = costString; // Update the panel data's cost
            panelData.panelEstimatedCostDisplay.setText("Estimated Cost: " + costString); // Update individual display

            // Try to parse the cost string to add to total
            try {
                // Assuming the cost string format is "$X.XX"
                totalCost += Double.parseDouble(costString.replace("$", ""));
            } catch (NumberFormatException e) {
                // Handle cases where costString might be "N/A" or some error
                Toast.makeText(this, "Could not parse cost for " + panelData.panelType, Toast.LENGTH_SHORT).show();
                allPanelsReady = false; // Mark as not ready if parsing fails
            }

            // Save individual invoice to history
            Invoice newInvoice = new Invoice(
                    panelData.panelType,
                    panelData.largestDentSize,
                    panelData.numberOfDents,
                    panelData.isAluminum,
                    panelData.estimatedCost
            );
            InvoiceManager.saveInvoice(this, newInvoice);
        }

        if (allPanelsReady) {
            totalEstimatedCostDisplay.setText(String.format("Total Estimated Cost: $%.2f", totalCost));
            Toast.makeText(this, "All invoices saved to history!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTotalEstimatedCostDisplay() {
        double totalCost = 0.0;
        boolean hasCalculatedCosts = false; // Flag to check if any panel has a calculated cost

        for (PanelInputData panelData : panelInputDataList) {
            if (panelData.estimatedCost != null && !panelData.estimatedCost.equals("N/A")) {
                try {
                    totalCost += Double.parseDouble(panelData.estimatedCost.replace("$", ""));
                    hasCalculatedCosts = true;
                } catch (NumberFormatException e) {
                    // Ignore if a panel's cost is not a valid number yet
                }
            }
        }

        if (hasCalculatedCosts) {
            totalEstimatedCostDisplay.setText(String.format("Total Estimated Cost: $%.2f", totalCost));
        } else {
            totalEstimatedCostDisplay.setText("Total Estimated Cost: N/A");
        }
    }
}