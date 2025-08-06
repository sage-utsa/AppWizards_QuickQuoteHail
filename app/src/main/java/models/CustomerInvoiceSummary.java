package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomerInvoiceSummary implements Serializable {
    private String customerName;
    private String customerVIN;
    private double totalCost;
    private List<Invoice> invoices; // List of individual invoices for this customer/VIN

    public CustomerInvoiceSummary(String customerName, String customerVIN) {
        this.customerName = customerName;
        this.customerVIN = customerVIN;
        this.totalCost = 0.0;
        this.invoices = new ArrayList<>();
    }

    public void addInvoice(Invoice invoice) {
        this.invoices.add(invoice);
        // Add to total cost, handling "CR" cases
        if (invoice.getEstimatedCost() != null && !invoice.getEstimatedCost().equals("N/A") && !invoice.getEstimatedCost().startsWith("CR")) {
            try {
                // Remove '$' before parsing to double
                totalCost += Double.parseDouble(invoice.getEstimatedCost().replace("$", ""));
            } catch (NumberFormatException e) {
                // Log or handle cases where cost isn't a valid number
                // Log.e("CustomerInvoiceSummary", "Error parsing cost: " + invoice.getEstimatedCost() + " - " + e.getMessage());
            }
        }
        // Keep invoices sorted by date within the group (newest first)
        Collections.sort(this.invoices, new Comparator<Invoice>() {
            @Override
            public int compare(Invoice o1, Invoice o2) {
                return Long.compare(o2.getCreationTimestamp(), o1.getCreationTimestamp()); // Newest first
            }
        });
    }

    // Getters
    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerVIN() {
        return customerVIN;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    // Important for HashMap grouping
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerInvoiceSummary that = (CustomerInvoiceSummary) o;
        return customerName.equals(that.customerName) &&
                customerVIN.equals(that.customerVIN);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(customerName, customerVIN);
    }
}