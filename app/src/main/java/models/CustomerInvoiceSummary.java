package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * A data model class representing a summary of all invoices for a single customer and vehicle.
 * It aggregates individual {@link Invoice} objects, calculates the total estimated cost,
 * and groups them together for easy display and management (e.g., in a history view).
 * This class is serializable to allow its objects to be written to and read from a file.
 */
public class CustomerInvoiceSummary implements Serializable {
    private String customerName;
    private String customerVIN;
    private double totalCost;
    private List<Invoice> invoices; // List of individual invoices for this customer/VIN

    /**
     * Constructs a new CustomerInvoiceSummary.
     *
     * @param customerName The name of the customer.
     * @param customerVIN The VIN (Vehicle Identification Number) of the customer's vehicle.
     */
    public CustomerInvoiceSummary(String customerName, String customerVIN) {
        this.customerName = customerName;
        this.customerVIN = customerVIN;
        this.totalCost = 0.0;
        this.invoices = new ArrayList<>();
    }

    /**
     * Adds an individual invoice to this summary. The method also updates the
     * total cost by parsing the estimated cost from the invoice and adds it to the running total.
     * Invoices are kept sorted by their creation timestamp in descending order (newest first).
     *
     * @param invoice The {@link Invoice} object to add.
     */
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

    /**
     * Compares this object to another object for equality.
     * Two CustomerInvoiceSummary objects are considered equal if they have the same
     * customer name and customer VIN. This is crucial for using these objects as keys in a HashMap.
     *
     * @param o The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
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