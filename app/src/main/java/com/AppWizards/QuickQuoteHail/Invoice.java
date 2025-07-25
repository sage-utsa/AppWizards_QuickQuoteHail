package com.AppWizards.QuickQuoteHail;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable; // For potential future direct object serialization not actually sure what this is, could remove
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Invoice implements Serializable {

    private String panelType;
    private String largestDentSize;
    private int numberOfDents;
    private boolean isAluminum;
    private String calculatedCost; // Stored as String to include "$" or "CR"
    private long timestamp; // Unix timestamp for when the invoice was created

    public Invoice(String panelType, String largestDentSize, int numberOfDents,
                   boolean isAluminum, String calculatedCost) {
        this.panelType = panelType;
        this.largestDentSize = largestDentSize;
        this.numberOfDents = numberOfDents;
        this.isAluminum = isAluminum;
        this.calculatedCost = calculatedCost;
        this.timestamp = System.currentTimeMillis(); // Set current time
    }

    // Constructor for recreating from JSON
    public Invoice(JSONObject jsonObject) throws JSONException {
        this.panelType = jsonObject.getString("panelType");
        this.largestDentSize = jsonObject.getString("largestDentSize");
        this.numberOfDents = jsonObject.getInt("numberOfDents");
        this.isAluminum = jsonObject.getBoolean("isAluminum");
        this.calculatedCost = jsonObject.getString("calculatedCost");
        this.timestamp = jsonObject.getLong("timestamp");
    }

    // Getters
    public String getPanelType() {
        return panelType;
    }

    public String getLargestDentSize() {
        return largestDentSize;
    }

    public int getNumberOfDents() {
        return numberOfDents;
    }

    public boolean isAluminum() {
        return isAluminum;
    }

    public String getCalculatedCost() {
        return calculatedCost;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Helper to get formatted date string
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * Converts this Invoice object into a JSONObject for storage.
     * @return JSONObject representation of the invoice.
     * @throws JSONException if there's an error during JSON creation.
     */
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("panelType", panelType);
        jsonObject.put("largestDentSize", largestDentSize);
        jsonObject.put("numberOfDents", numberOfDents);
        jsonObject.put("isAluminum", isAluminum);
        jsonObject.put("calculatedCost", calculatedCost);
        jsonObject.put("timestamp", timestamp);
        return jsonObject;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "panelType='" + panelType + '\'' +
                ", largestDentSize='" + largestDentSize + '\'' +
                ", numberOfDents=" + numberOfDents +
                ", isAluminum=" + isAluminum +
                ", calculatedCost='" + calculatedCost + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
