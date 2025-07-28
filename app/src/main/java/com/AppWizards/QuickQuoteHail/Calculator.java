package com.AppWizards.QuickQuoteHail;

import java.util.HashMap;
import java.util.Map;

public class Calculator {

    private String customerName;
    private String customerVIN;


    // Nested Map for the lookup table
    // Panel Type -> (Dents Range -> (Dent Size -> Cost))
    private static final Map<String, Map<String, Map<String, String>>> DENT_COSTS = new HashMap<>();

    static {
        // Panel: HOOD, ROOF, TRUNK - Each gets its own independent cost map
        String[] mainPanels = {"HOOD", "ROOF", "TRUNK"};
        for (String panel : mainPanels) {
            // A NEW HashMap is created for EACH panel (Hood, Roof, Trunk).
            // This ensures that HOOD's map is distinct from ROOF's map, etc.
            Map<String, Map<String, String>> panelCosts = new HashMap<>();
            panelCosts.put("1-5", createDentSizeMap("125", "155", "185", "215"));
            panelCosts.put("6-15", createDentSizeMap("185", "215", "255", "325"));
            panelCosts.put("16-30", createDentSizeMap("255", "325", "375", "425"));
            panelCosts.put("31-50", createDentSizeMap("375", "425", "475", "575")); // Note: 475,475 in H col
            panelCosts.put("51-75", createDentSizeMap("475", "525", "625", "800"));
            panelCosts.put("76-100", createDentSizeMap("625", "725", "850", "CR")); // CR for 'Call for Quote'
            panelCosts.put("101-150", createDentSizeMap("850", "1000", "1200", "1500"));
            panelCosts.put("151-200", createDentSizeMap("1200", "1500", "1800", "CR"));
            panelCosts.put("201-300", createDentSizeMap("CR", "CR", "CR", "CR"));
            DENT_COSTS.put(panel, panelCosts); // Each panel gets its own distinct map
        }
        // Panel: LFF (Left Front Fender) and other similar 95-start panels
        // This part remains the same as it already follows the distinct-map-per-panel pattern
        String[] panelsWith95Start = {"LFF", "LFD", "LG", "LQ", "LRAIL", "RFF", "RFD", "RG", "RQ", "RRAIL"};
        for (String panel : panelsWith95Start) {
            Map<String, Map<String, String>> panelCosts = new HashMap<>();
            panelCosts.put("1-5", createDentSizeMap("95", "125", "155", "185"));
            panelCosts.put("6-15", createDentSizeMap("135", "175", "215", "255"));
            panelCosts.put("16-30", createDentSizeMap("185", "255", "325", "375"));
            panelCosts.put("31-50", createDentSizeMap("325", "375", "425", "CR")); // CR in H col
            panelCosts.put("51-75", createDentSizeMap("CR", "CR", "CR", "CR")); // All CR
            panelCosts.put("76-100", createDentSizeMap("CR", "CR", "CR", "CR")); // All CR
            panelCosts.put("101-150", createDentSizeMap("CR", "CR", "CR", "CR")); // All CR
            panelCosts.put("151-200", createDentSizeMap("CR", "CR", "CR", "CR")); // All CR
            panelCosts.put("201-300", createDentSizeMap("CR", "CR", "CR", "CR")); // All CR
            DENT_COSTS.put(panel, panelCosts);
        }
    }

    // Helper method to create the innermost map for dent sizes
    private static Map<String, String> createDentSizeMap(String d, String n, String q, String h) {
        Map<String, String> map = new HashMap<>();
        map.put("D", d);
        map.put("N", n);
        map.put("Q", q);
        map.put("H", h);
        return map;
    }

    /**
     * Calculates the estimated cost of dent repair based on panel type, largest dent size, and number of dents.
     *
     * @param panelType The type of panel (e.g., "HOOD", "LFF").
     * @param largestDentSize The largest dent size category (e.g., "D", "N", "Q", "H").
     * @param numberOfDents The total number of dents on the panel.
     * @param isAluminum True if the car's body is aluminum, false otherwise.
     * @return The estimated cost as a String, or "N/A" if input is invalid/not found, or "CR" if it requires Custom Repair.
     */
    public String getEstimatedCost(String panelType, String largestDentSize, int numberOfDents, boolean isAluminum) {
        if (panelType == null || largestDentSize == null || numberOfDents <= 0) {
            return "N/A: Invalid input.";
        }

        // Convert panelType to uppercase to match map keys
        panelType = panelType.toUpperCase();
        largestDentSize = largestDentSize.toUpperCase();

        Map<String, Map<String, String>> panelData = DENT_COSTS.get(panelType);
        if (panelData == null) {
            return "N/A: Unknown panel type.";
        }

        String dentRangeKey = getDentRangeKey(numberOfDents);
        if (dentRangeKey == null) {
            return "N/A: Number of dents out of range (1-300)."; // Updated error message
        }

        Map<String, String> dentSizeData = panelData.get(dentRangeKey);
        if (dentSizeData == null) {
            return "N/A: Data for dent range not found.";
        }

        String costStr = dentSizeData.get(largestDentSize);
        if (costStr == null) {
            return "N/A: Unknown dent size category for selected panel/dents.";
        }

        if (costStr.equals("CR")) {
            return "CR: Custom Repair Needed";
        }

        // Apply aluminum multiplier if applicable
        try {
            double cost = Double.parseDouble(costStr);
            if (isAluminum) {
                cost *= 1.5;
            }
            // Format to 2 decimal places for currency, if it's not a whole number.
            // Or just cast to int if all your prices are whole numbers.
            // Here, using String.format for clean currency representation.
            return String.format("$%.2f", cost);
        } catch (NumberFormatException e) {
            // This should ideally not happen if data is correctly entered,
            // but good for robustness.
            return "N/A: Internal cost data error.";
        }
    }

    // Helper to determine the correct dent range key from the number of dents
    private String getDentRangeKey(int numDents) {
        if (numDents >= 1 && numDents <= 5) return "1-5";
        if (numDents >= 6 && numDents <= 15) return "6-15";
        if (numDents >= 16 && numDents <= 30) return "16-30";
        if (numDents >= 31 && numDents <= 50) return "31-50";
        if (numDents >= 51 && numDents <= 75) return "51-75";
        if (numDents >= 76 && numDents <= 100) return "76-100";
        if (numDents >= 101 && numDents <= 150) return "101-150";
        if (numDents >= 151 && numDents <= 200) return "151-200";
        if (numDents >= 201 && numDents <= 300) return "201-300";
        return null; // Out of defined ranges
    }

    // You might want to add other utility methods here in the future
}