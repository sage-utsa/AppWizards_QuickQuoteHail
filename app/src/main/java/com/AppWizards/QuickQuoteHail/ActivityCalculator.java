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

    // UI elements
    private Spinner panelSpinner;
    private LinearLayout dynamicPanelsContainer;
    private Button calculateAllCostsButton;
    private TextView totalEstimatedCostDisplay;

    // Customer info fields
    private EditText customerNameEditText;
    private EditText customerVinEditText;

    // List of all panels user adds dynamically
    private List<PanelInputData> panelInputDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        // Initialize layout views
        panelSpinner = findViewById(R.id.panelSpinner);
        dynamicPanelsContainer = findViewById(R.id.dynamicPanelsContainer);
        calculateAllCostsButton = findViewById(R.id.calculateAllCostsButton);
        totalEstimatedCostDisplay = findViewById(R.id.totalEstimatedCostDisplay);

        customerNameEditText = findViewById(R.id.customerNameEditText);
        customerVinEditText = findViewById(R.id.customerVinEditText);

        panelInputDataList = new ArrayList<>();

        // Dropdown menu items
        String[] panelTypes = {
                "Add Panel",  // ← visible default item
                "HOOD", "ROOF", "TRUNK",
                "LFF", "LFD", "LG", "LQ", "LRAIL",
                "RFF", "RFD", "RG", "RQ", "RRAIL"
        };

        // Spinner adaptor
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_white, // this affects only the selected (closed) view
                panelTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // dropdown is fine
        panelSpinner.setAdapter(adapter);







        // When user picks a panel, dynamically add it
        panelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (position > 0) {
                    addPanelInputSection(selectedItem);
                    panelSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Button click calculates cost of all panels
        calculateAllCostsButton.setOnClickListener(v -> calculateAndDisplayAllCosts());
    }

    // Adds a new panel section with input options
    private void addPanelInputSection(String panelType) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View panelInputView = inflater.inflate(R.layout.panel_input_section, dynamicPanelsContainer, false);

        PanelInputData newPanelData = new PanelInputData(panelType);
        newPanelData.panelLayout = (LinearLayout) panelInputView;

        // Find and assign views
        newPanelData.selectedPanelDisplay = panelInputView.findViewById(R.id.selectedPanelDisplay);
        newPanelData.dentSizeDisplay = panelInputView.findViewById(R.id.dentSizeDisplay);
        newPanelData.numDentsDisplay = panelInputView.findViewById(R.id.numDentsDisplay);
        newPanelData.selectDentSizeButton = panelInputView.findViewById(R.id.selectDentSizeButton);
        newPanelData.enterNumDentsButton = panelInputView.findViewById(R.id.enterNumDentsButton);
        newPanelData.aluminumSwitch = panelInputView.findViewById(R.id.aluminumSwitch);
        newPanelData.panelEstimatedCostDisplay = panelInputView.findViewById(R.id.panelEstimatedCostDisplay);

        newPanelData.selectedPanelDisplay.setText("Selected Panel: " + newPanelData.panelType);
        newPanelData.dentSizeDisplay.setText("Largest Dent Size: " + newPanelData.largestDentSize);
        newPanelData.numDentsDisplay.setText("Number of Dents: " + (newPanelData.numberOfDents == -1 ? "Not Set" : String.valueOf(newPanelData.numberOfDents)));
        newPanelData.aluminumSwitch.setChecked(newPanelData.isAluminum);
        newPanelData.panelEstimatedCostDisplay.setText("Estimated Cost: " + newPanelData.estimatedCost);

        newPanelData.selectDentSizeButton.setOnClickListener(v -> showDentSizeDialog(newPanelData));
        newPanelData.enterNumDentsButton.setOnClickListener(v -> showNumDentsDialog(newPanelData));
        newPanelData.aluminumSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            newPanelData.isAluminum = isChecked;
            newPanelData.panelEstimatedCostDisplay.setText("Estimated Cost: N/A");
            updateTotalEstimatedCostDisplay();
        });

        Button removePanelButton = new Button(this);
        removePanelButton.setText("Remove Panel");
        removePanelButton.setBackgroundResource(R.drawable.button_background_red);
        removePanelButton.setTextColor(getResources().getColor(android.R.color.white));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 16, 0, 0);
        removePanelButton.setLayoutParams(layoutParams);
        removePanelButton.setOnClickListener(v -> removePanelInputSection(newPanelData));
        newPanelData.panelLayout.addView(removePanelButton);

        dynamicPanelsContainer.addView(panelInputView);
        panelInputDataList.add(newPanelData);

        updateTotalEstimatedCostDisplay();
    }

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
        Calculator calculator = new Calculator();

        String customerName = customerNameEditText.getText().toString().trim();
        String customerVin = customerVinEditText.getText().toString().trim();

        if (customerName.isEmpty()) {
            Toast.makeText(this, "Please enter the customer name.", Toast.LENGTH_SHORT).show();
            customerNameEditText.requestFocus();
            return;
        }

        if (customerVin.isEmpty()) {
            Toast.makeText(this, "Please enter the vehicle VIN.", Toast.LENGTH_SHORT).show();
            customerVinEditText.requestFocus();
            return;
        }

        if (panelInputDataList.isEmpty()) {
            Toast.makeText(this, "No panels added to calculate.", Toast.LENGTH_SHORT).show();
            totalEstimatedCostDisplay.setText("Total Estimated Cost: N/A");
            return;
        }

        for (PanelInputData panelData : panelInputDataList) {
            if (panelData.largestDentSize.equals("Not Set") || panelData.numberOfDents == -1) {
                allPanelsReady = false;
                Toast.makeText(this, "Please complete all inputs for " + panelData.panelType + " panel.", Toast.LENGTH_LONG).show();
                if (panelData.panelLayout != null) {
                    panelData.panelLayout.getParent().requestChildFocus(panelData.panelLayout, panelData.panelLayout);
                }
                return;
            }

            String costString = calculator.getEstimatedCost(
                    panelData.panelType,
                    panelData.largestDentSize,
                    panelData.numberOfDents,
                    panelData.isAluminum
            );
            panelData.estimatedCost = costString;
            panelData.panelEstimatedCostDisplay.setText("Estimated Cost: " + costString);

            try {
                totalCost += Double.parseDouble(costString.replace("$", ""));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Could not parse cost for " + panelData.panelType, Toast.LENGTH_SHORT).show();
                allPanelsReady = false;
            }

            Invoice newInvoice = new Invoice(
                    customerName,
                    customerVin,
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
        boolean hasCalculatedCosts = false;

        for (PanelInputData panelData : panelInputDataList) {
            if (panelData.estimatedCost != null && !panelData.estimatedCost.equals("N/A") && !panelData.estimatedCost.startsWith("CR")) {
                try {
                    totalCost += Double.parseDouble(panelData.estimatedCost.replace("$", ""));
                    hasCalculatedCosts = true;
                } catch (NumberFormatException e) {
                    // Ignore
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
