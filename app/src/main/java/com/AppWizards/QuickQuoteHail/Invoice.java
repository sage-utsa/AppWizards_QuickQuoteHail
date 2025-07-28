package com.AppWizards.QuickQuoteHail;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class Invoice implements Serializable {
    private String customerName;
    private String customerVIN;
    private String panelType;
    private String largestDentSize;
    private int numberOfDents;
    private boolean isAluminum;
    private String estimatedCost;
    private long creationTimestamp; // This was added in the previous step

    // THIS IS THE CONSTRUCTOR THAT ActivityCalculator IS TRYING TO CALL
    public Invoice(String customerName, String customerVIN, String panelType, String largestDentSize, int numberOfDents, boolean isAluminum, String estimatedCost) {
        this.customerName = customerName;
        this.customerVIN = customerVIN;
        this.panelType = panelType;
        this.largestDentSize = largestDentSize;
        this.numberOfDents = numberOfDents;
        this.isAluminum = isAluminum;
        this.estimatedCost = estimatedCost;
        this.creationTimestamp = System.currentTimeMillis(); // Initialize timestamp here
    }

    // Constructor to create an Invoice from a JSONObject (for loading, as discussed previously)
    public Invoice(JSONObject jsonObject) throws JSONException {
        this.customerName = jsonObject.getString("customerName");
        this.customerVIN = jsonObject.getString("customerVIN");
        this.panelType = jsonObject.getString("panelType");
        this.largestDentSize = jsonObject.getString("largestDentSize");
        this.numberOfDents = jsonObject.getInt("numberOfDents");
        this.isAluminum = jsonObject.getBoolean("isAluminum");
        this.estimatedCost = jsonObject.getString("estimatedCost");
        // Ensure you handle the timestamp if it might be missing in old JSON data,
        // or ensure your saving logic always includes it.
        this.creationTimestamp = jsonObject.optLong("creationTimestamp", System.currentTimeMillis());
    }


    // Getters
    public String getCustomerName() { return customerName; }
    public String getCustomerVIN() { return customerVIN; }
    public String getPanelType() { return panelType; }
    public String getLargestDentSize() { return largestDentSize; }
    public int getNumberOfDents() { return numberOfDents; }
    public boolean isAluminum() { return isAluminum; }
    public String getEstimatedCost() { return estimatedCost; }
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(creationTimestamp));
    }
    public String getCalculatedCost() {
        return estimatedCost;
    }

    // toJsonObject method
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customerName", customerName);
        jsonObject.put("customerVIN", customerVIN);
        jsonObject.put("panelType", panelType);
        jsonObject.put("largestDentSize", largestDentSize);
        jsonObject.put("numberOfDents", numberOfDents);
        jsonObject.put("isAluminum", isAluminum);
        jsonObject.put("estimatedCost", estimatedCost);
        jsonObject.put("creationTimestamp", creationTimestamp);
        return jsonObject;
    }

    @Override
    public String toString() {
        // ... (your existing toString)
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