package com.AppWizards.QuickQuoteHail;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomerGroupAdapter extends ArrayAdapter<CustomerInvoiceSummary> {

    private LayoutInflater inflater;

    public CustomerGroupAdapter(@NonNull Context context, @NonNull List<CustomerInvoiceSummary> summaries) {
        super(context, 0, summaries);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customer_group_item, parent, false);
            holder = new ViewHolder();
            holder.customerNameHeader = convertView.findViewById(R.id.customerNameHeader);
            holder.customerVinHeader = convertView.findViewById(R.id.customerVinHeader);
            holder.customerTotalCostHeader = convertView.findViewById(R.id.customerTotalCostHeader);
            holder.individualInvoicesContainer = convertView.findViewById(R.id.individualInvoicesContainer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CustomerInvoiceSummary currentSummary = getItem(position);

        if (currentSummary != null) {
            // Populate customer summary header
            holder.customerNameHeader.setText("Customer: " + currentSummary.getCustomerName());
            holder.customerVinHeader.setText("VIN: " + currentSummary.getCustomerVIN());
            holder.customerTotalCostHeader.setText(String.format("Total for Customer: $%.2f", currentSummary.getTotalCost()));

            // Clear previous invoice views to prevent duplicates due to recycling
            holder.individualInvoicesContainer.removeAllViews();

            // Add individual invoice details
            for (Invoice invoice : currentSummary.getInvoices()) {
                // Dynamically create TextViews or inflate a smaller layout for each invoice
                LinearLayout invoiceItemView = (LinearLayout) inflater.inflate(R.layout.single_invoice_detail_item, holder.individualInvoicesContainer, false);

                TextView dateTextView = invoiceItemView.findViewById(R.id.detailDate);
                TextView panelTypeTextView = invoiceItemView.findViewById(R.id.detailPanelType);
                TextView dentDetailsTextView = invoiceItemView.findViewById(R.id.detailDentDetails);
                TextView aluminumStatusTextView = invoiceItemView.findViewById(R.id.detailAluminumStatus);
                TextView costTextView = invoiceItemView.findViewById(R.id.detailCost);

                dateTextView.setText("Date: " + invoice.getFormattedDate());
                panelTypeTextView.setText("Panel: " + invoice.getPanelType());
                dentDetailsTextView.setText(String.format("Dents: %d (%s)", invoice.getNumberOfDents(), invoice.getLargestDentSize()));
                aluminumStatusTextView.setText("Aluminum: " + (invoice.isAluminum() ? "Yes" : "No"));
                costTextView.setText("Cost: " + invoice.getEstimatedCost());

                holder.individualInvoicesContainer.addView(invoiceItemView);
            }
        }

        return convertView;
    }

    static class ViewHolder {
        TextView customerNameHeader;
        TextView customerVinHeader;
        TextView customerTotalCostHeader;
        LinearLayout individualInvoicesContainer; // The container for dynamically added invoice views
    }
}