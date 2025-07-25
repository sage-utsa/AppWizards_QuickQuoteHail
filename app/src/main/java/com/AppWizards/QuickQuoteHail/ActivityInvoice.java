package com.AppWizards.QuickQuoteHail;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ActivityInvoice extends AppCompatActivity {

    private ListView invoiceListView;
    private TextView noHistoryMessage;
    private Button clearHistoryButton;
    private InvoiceAdapter adapter;
    private List<Invoice> invoiceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        invoiceListView = findViewById(R.id.invoiceListView);
        noHistoryMessage = findViewById(R.id.noHistoryMessage);
        clearHistoryButton = findViewById(R.id.clearHistoryButton);

        loadInvoiceHistory();

        clearHistoryButton.setOnClickListener(v -> {
            InvoiceManager.clearAllInvoices(ActivityInvoice.this);
            loadInvoiceHistory(); // Reload to show empty list
            Toast.makeText(ActivityInvoice.this, "History cleared.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadInvoiceHistory() {
        invoiceList = InvoiceManager.loadInvoices(this);

        if (invoiceList.isEmpty()) {
            noHistoryMessage.setVisibility(View.VISIBLE);
            invoiceListView.setVisibility(View.GONE);
            clearHistoryButton.setVisibility(View.GONE); // Hide clear button if no history
        } else {
            noHistoryMessage.setVisibility(View.GONE);
            invoiceListView.setVisibility(View.VISIBLE);
            clearHistoryButton.setVisibility(View.VISIBLE); // Show clear button
            adapter = new InvoiceAdapter(this, invoiceList);
            invoiceListView.setAdapter(adapter);
        }
    }

    // Custom ArrayAdapter for displaying Invoice objects in the ListView
    private static class InvoiceAdapter extends ArrayAdapter<Invoice> {

        public InvoiceAdapter(@NonNull android.content.Context context, @NonNull List<Invoice> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_invoice_breakdown, null);
            }

            Invoice invoice = getItem(position);

            TextView dateTextView = convertView.findViewById(R.id.invoiceDate);
            TextView panelTypeTextView = convertView.findViewById(R.id.invoicePanelType);
            TextView dentDetailsTextView = convertView.findViewById(R.id.invoiceDentDetails);
            TextView aluminumStatusTextView = convertView.findViewById(R.id.invoiceAluminumStatus);
            TextView costTextView = convertView.findViewById(R.id.invoiceCost);

            if (invoice != null) {
                dateTextView.setText(invoice.getFormattedDate());
                panelTypeTextView.setText("Panel: " + invoice.getPanelType());
                dentDetailsTextView.setText(String.format("Dents: %d (%s)",
                        invoice.getNumberOfDents(), invoice.getLargestDentSize()));
                aluminumStatusTextView.setText("Aluminum: " + (invoice.isAluminum() ? "Yes" : "No"));
                costTextView.setText("Cost: " + invoice.getCalculatedCost());
            }

            return convertView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload history when returning to this activity
        loadInvoiceHistory();
    }
}
