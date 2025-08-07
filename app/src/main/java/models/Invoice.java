package models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * The Invoice class represents a single repair estimate for a specific vehicle panel.
 * It stores all the necessary information for a quote, including customer details,
 * panel specifics, and the calculated cost. This class is designed to be
 * serializable to allow for easy storage and retrieval from a file
 */
public class Invoice implements Serializable {
    private String customerName;
    private String customerVIN;
    private String panelType;
    private String largestDentSize;
    private int numberOfDents;
    private boolean isAluminum;
    private String estimatedCost;
    private long creationTimestamp; // Add this field

    /**
     * Constructs a new Invoice with all the necessary details.
     * The creation timestamp is automatically set to the current time.
     *
     * @param customerName The name of the customer.
     * @param customerVIN The vehicle's VIN.
     * @param panelType The type of panel being repaired (e.g., "HOOD").
     * @param largestDentSize The largest dent size category (e.g., "D", "N").
     * @param numberOfDents The total number of dents on the panel.
     * @param isAluminum True if the panel is aluminum, false otherwise.
     * @param estimatedCost The estimated cost as a formatted string (e.g., "$125.00").
     */
    public Invoice(String customerName, String customerVIN, String panelType, String largestDentSize, int numberOfDents, boolean isAluminum, String estimatedCost) {
        this.customerName = customerName;
        this.customerVIN = customerVIN;
        this.panelType = panelType;
        this.largestDentSize = largestDentSize;
        this.numberOfDents = numberOfDents;
        this.isAluminum = isAluminum;
        this.estimatedCost = estimatedCost;
        this.creationTimestamp = System.currentTimeMillis(); // Initialize timestamp
    }

    /**
     * Constructs an Invoice object from a JSON object. This is typically used when
     * loading saved invoices from a file. It includes a check for backward compatibility
     * in case the 'creationTimestamp' field was not present in older saved files
     *
     * @param jsonObject The {@link JSONObject} to deserialize.
     * @throws JSONException If the JSON object is malformed or a required key is missing
     */
    public Invoice(JSONObject jsonObject) throws JSONException {
        this.customerName = jsonObject.getString("customerName");
        this.customerVIN = jsonObject.getString("customerVIN");
        this.panelType = jsonObject.getString("panelType");
        this.largestDentSize = jsonObject.getString("largestDentSize");
        this.numberOfDents = jsonObject.getInt("numberOfDents");
        this.isAluminum = jsonObject.getBoolean("isAluminum");
        this.estimatedCost = jsonObject.getString("estimatedCost");
        this.creationTimestamp = jsonObject.optLong("creationTimestamp", System.currentTimeMillis()); // Use optLong for backward compatibility
    }

    // Getters for all fields
    public String getCustomerName() { return customerName; }
    public String getCustomerVIN() { return customerVIN; }
    public String getPanelType() { return panelType; }
    public String getLargestDentSize() { return largestDentSize; }
    public int getNumberOfDents() { return numberOfDents; }
    public boolean isAluminum() { return isAluminum; }
    public String getEstimatedCost() { return estimatedCost; }
    public long getCreationTimestamp() { return creationTimestamp; } // New getter for timestamp

    /**
     * Returns the creation timestamp formatted as a readable date and time string.
     * The format is "MMM dd, yyyy HH:mm" (e.g., "Aug 06, 2025 10:30").
     *
     * @return A formatted string of the creation date and time.
     */
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(creationTimestamp));
    }

    /**
     * Returns the estimated cost as a string. This method exists to maintain
     * consistency with how the cost is stored and used in the application
     *
     * @return The estimated cost as a string.
     */
    public String getCalculatedCost() {
        return estimatedCost;
    }


    /**
     * Converts the Invoice object into a {@link JSONObject}. This is used
     * to save the invoice to a file in a structured format.
     *
     * @return A {@link JSONObject} representation of the invoice.
     * @throws JSONException If an error occurs during JSON creation.
     */    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customerName", customerName);
        jsonObject.put("customerVIN", customerVIN);
        jsonObject.put("panelType", panelType);
        jsonObject.put("largestDentSize", largestDentSize);
        jsonObject.put("numberOfDents", numberOfDents);
        jsonObject.put("isAluminum", isAluminum);
        jsonObject.put("estimatedCost", estimatedCost);
        jsonObject.put("creationTimestamp", creationTimestamp); // Save timestamp
        return jsonObject;
    }

    /**
     * Provides a string representation of the Invoice object for debugging purposes.
     *
     * @return A string detailing the invoice's fields.
     */
    @Override
    public String toString() {
        return "Invoice{" +
                "customerName='" + customerName + '\'' +
                ", customerVIN='" + customerVIN + '\'' +
                ", panelType='" + panelType + '\'' +
                ", largestDentSize='" + largestDentSize + '\'' +
                ", numberOfDents=" + numberOfDents +
                ", isAluminum=" + isAluminum +
                ", estimatedCost='" + estimatedCost + '\'' +
                ", creationTimestamp=" + creationTimestamp +
                '}';
    }
}