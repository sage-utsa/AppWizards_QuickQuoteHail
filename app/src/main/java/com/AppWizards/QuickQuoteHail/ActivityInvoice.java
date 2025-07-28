package com.AppWizards.QuickQuoteHail;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityInvoice extends AppCompatActivity {

    private ListView invoiceListView;
    private CustomerGroupAdapter adapter; // Change to your new adapter
    private TextView emptyStateTextView;
    private Button clearHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice); // Assuming this is your history screen layout

        invoiceListView = findViewById(R.id.invoiceListView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        clearHistoryButton = findViewById(R.id.clearHistoryButton);

        clearHistoryButton.setOnClickListener(v -> clearInvoiceHistory());

        loadAndDisplayInvoices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayInvoices(); // Refresh the list when returning to this activity
    }

    private void loadAndDisplayInvoices() {
        List<CustomerInvoiceSummary> groupedInvoices = InvoiceManager.loadGroupedInvoices(this);

        if (groupedInvoices.isEmpty()) {
            invoiceListView.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.VISIBLE);
            emptyStateTextView.setText("No invoices found. Start calculating costs to see history here!");
            clearHistoryButton.setVisibility(View.GONE); // Hide clear button if no history
        } else {
            invoiceListView.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.GONE);
            clearHistoryButton.setVisibility(View.VISIBLE); // Show clear button if history exists
            adapter = new CustomerGroupAdapter(this, groupedInvoices);
            invoiceListView.setAdapter(adapter);
        }
    }

    private void clearInvoiceHistory() {
        InvoiceManager.saveAllInvoices(this, new ArrayList<>()); // Save an empty list to clear
        loadAndDisplayInvoices(); // Reload to show empty state
        Toast.makeText(this, "Invoice history cleared.", Toast.LENGTH_SHORT).show();
    }
}