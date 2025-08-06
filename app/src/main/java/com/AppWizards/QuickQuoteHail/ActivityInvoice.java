package com.AppWizards.QuickQuoteHail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import models.CustomerInvoiceSummary;
import models.InvoiceManager;

public class ActivityInvoice extends AppCompatActivity implements CustomerGroupAdapter.OnEmailInvoiceClickListener {

    private ListView invoiceListView;
    private CustomerGroupAdapter adapter;
    private TextView emptyStateTextView;
    private Button clearHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        invoiceListView = findViewById(R.id.invoiceListView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);
        clearHistoryButton = findViewById(R.id.clearHistoryButton);

        clearHistoryButton.setOnClickListener(v -> clearInvoiceHistory());

        loadAndDisplayInvoices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayInvoices();
    }

    private void loadAndDisplayInvoices() {
        List<CustomerInvoiceSummary> groupedInvoices = InvoiceManager.loadGroupedInvoices(this);

        if (groupedInvoices.isEmpty()) {
            invoiceListView.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.VISIBLE);
            emptyStateTextView.setText("No invoices found. Start calculating costs to see history here!");
            clearHistoryButton.setVisibility(View.GONE);
        } else {
            invoiceListView.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.GONE);
            clearHistoryButton.setVisibility(View.VISIBLE);
            adapter = new CustomerGroupAdapter(this, groupedInvoices, this);
            invoiceListView.setAdapter(adapter);
        }
    }

    private void clearInvoiceHistory() {
        InvoiceManager.saveAllInvoices(this, new ArrayList<>());
        loadAndDisplayInvoices();
        Toast.makeText(this, "Invoice history cleared.", Toast.LENGTH_SHORT).show();
    }

    // Implementation of the OnEmailInvoiceClickListener interface
    @Override
    public void onEmailInvoiceClick(CustomerInvoiceSummary summary) {
        // --- Generate PDF first ---
        Uri pdfUri = PdfGenerator.generateInvoicePdf(this, summary);

        if (pdfUri == null) {
            Toast.makeText(this, "Failed to generate PDF for email.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Prepare Email Intent ---
        String recipientEmail = ""; // If you have a customer email field, use it here
        String subject = "Your Hail Damage Invoice from QuickQuoteHail - " + summary.getCustomerName();

        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(summary.getCustomerName()).append(",\n\n");
        body.append("Please find attached your hail damage repair estimate for VIN: ").append(summary.getCustomerVIN()).append(".\n\n");
        body.append(String.format("Total Estimated Cost: $%.2f\n\n", summary.getTotalCost()));
        body.append("For detailed information, please refer to the attached PDF invoice.\n\n");
        body.append("Thank you for choosing QuickQuoteHail!");

        Intent emailIntent = new Intent(Intent.ACTION_SEND); // Changed from SENDTO to SEND for attachment
        emailIntent.setType("application/pdf"); // Set MIME type for PDF attachment
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail}); // Empty if no known email
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
        emailIntent.putExtra(Intent.EXTRA_STREAM, pdfUri); // Attach the PDF
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant temporary read permission to the email app

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send invoice via...")); // User can choose email app
        } else {
            Toast.makeText(this, "No email app found to send invoice.", Toast.LENGTH_SHORT).show();
        }
    }
}