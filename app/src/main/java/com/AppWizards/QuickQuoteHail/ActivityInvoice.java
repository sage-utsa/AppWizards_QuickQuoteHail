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

/**
 * The ActivityInvoice class displays a history of all generated invoices.
 * It loads and groups invoices by customer, presents them in a list, and provides
 * functionality to clear the history or email a specific invoice to a customer.
 */
public class ActivityInvoice extends AppCompatActivity implements CustomerGroupAdapter.OnEmailInvoiceClickListener {

    private ListView invoiceListView;
    private CustomerGroupAdapter adapter;
    private TextView emptyStateTextView;
    private Button clearHistoryButton;

    /**
     * Called when the activity is first created. This method initializes the UI components,
     * sets up the click listener for the clear history button, and loads the invoice data.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state.
     */
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
    /**
     * Called when the activity resumes. the purpose of this method is to ensure that invoice list is refreshed
     * every time the user returns to this activity, showing any new invoices that might
     * have been created.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayInvoices();
    }
    /**
     * Loads invoices from the InvoiceManager, groups them by customer, and populates
     * the ListView. If no invoices are found, it displays a message and hides the list.
     */
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
    /**
     * Clears all saved invoice history. It does this by saving an empty list of invoices
     * to the persistent storage and then re-calling {@link #loadAndDisplayInvoices()} to
     * refresh the UI.
     */
    private void clearInvoiceHistory() {
        InvoiceManager.saveAllInvoices(this, new ArrayList<>());
        loadAndDisplayInvoices();
        Toast.makeText(this, "Invoice history cleared.", Toast.LENGTH_SHORT).show();
    }


    /**
     * Handles the event when the "Email Invoice" button is clicked for a specific customer summary.
     * This method first generates a PDF of the invoice using {@link PdfGenerator} and then
     * creates and starts an email intent with the PDF attached.
     *
     * @param summary The {@link CustomerInvoiceSummary} object for the invoice to be emailed.
     */
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