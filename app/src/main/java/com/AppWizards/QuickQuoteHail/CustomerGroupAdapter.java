package com.AppWizards.QuickQuoteHail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button; // Import Button
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import models.CustomerInvoiceSummary;
import models.Invoice;
/**
 * An ArrayAdapter for displaying a list of {@link CustomerInvoiceSummary} objects.
 * This adapter is responsible for inflating a view for each customer group, populating
 * it with summary information (customer name, VIN, total cost), and dynamically
 * adding views for each individual invoice associated with that customer. It also
 * provides an interface to handle click events on an "Email Invoice" button.
 */
public class CustomerGroupAdapter extends ArrayAdapter<CustomerInvoiceSummary> {

    private LayoutInflater inflater;
    private OnEmailInvoiceClickListener emailClickListener; // New listener interface

    /**
     * An interface to handle click events on the "Email Invoice" button within a list item.
     * The implementing class (e.g., an Activity) will receive the {@link CustomerInvoiceSummary}
     * for the clicked item.
     */
    public interface OnEmailInvoiceClickListener {
        /**
         * Called when the email button for a customer's invoice is clicked.
         * @param summary The {@link CustomerInvoiceSummary} of the customer whose invoice is being emailed.
         */
        void onEmailInvoiceClick(CustomerInvoiceSummary summary);
    }

    /**
     * Constructs a new {@link CustomerGroupAdapter}.
     *
     * @param context The current context.
     * @param summaries A list of {@link CustomerInvoiceSummary} objects to be displayed.
     * @param listener The listener for email button clicks.
     */
    public CustomerGroupAdapter(@NonNull Context context, @NonNull List<CustomerInvoiceSummary> summaries, OnEmailInvoiceClickListener listener) {
        super(context, 0, summaries);
        inflater = LayoutInflater.from(context);
        this.emailClickListener = listener; // Assign the listener
    }
    /**
     * Get a View that displays the data at the specified position in the data set.
     * This method inflates a new view if needed, binds the data from the {@link CustomerInvoiceSummary}
     * object to the views, and handles the creation of sub-views for each individual invoice.
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position
     */
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
            holder.emailButton = convertView.findViewById(R.id.shareCustomerInvoiceButton); // Find the new button
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

            // Set OnClickListener for the email button
            holder.emailButton.setOnClickListener(v -> {
                if (emailClickListener != null) {
                    emailClickListener.onEmailInvoiceClick(currentSummary);
                }
            });

            // Clear previous invoice views to prevent duplicates due to recycling
            holder.individualInvoicesContainer.removeAllViews();

            // Add individual invoice details
            for (Invoice invoice : currentSummary.getInvoices()) {
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
    /**
     * A static ViewHolder class to hold references to the views in the list item layout.
     * This improves performance by avoiding repeated calls to findViewById().
     */
    static class ViewHolder {
        TextView customerNameHeader;
        TextView customerVinHeader;
        TextView customerTotalCostHeader;
        LinearLayout individualInvoicesContainer;
        Button emailButton; // Add the button here
    }
}