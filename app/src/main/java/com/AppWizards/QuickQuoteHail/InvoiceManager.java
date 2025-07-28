package com.AppWizards.QuickQuoteHail;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceManager {

    private static final String FILENAME = "invoices.json";
    private static final String TAG = "InvoiceManager";

    // Method to save a single invoice
    public static void saveInvoice(Context context, Invoice newInvoice) {
        List<Invoice> currentInvoices = loadRawInvoices(context);
        currentInvoices.add(newInvoice);
        saveAllInvoices(context, currentInvoices);
    }

    // Method to save the entire list of invoices
    public static void saveAllInvoices(Context context, List<Invoice> invoicesToSave) {
        JSONArray jsonArray = new JSONArray();
        for (Invoice inv : invoicesToSave) {
            try {
                jsonArray.put(inv.toJsonObject());
            } catch (JSONException e) {
                Log.e(TAG, "Error converting invoice to JSON: " + e.getMessage());
            }
        }

        try (FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            fos.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
            Log.d(TAG, "Invoices saved successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error saving invoices: " + e.getMessage());
        }
    }

    // Loads raw invoices from the file
    public static List<Invoice> loadRawInvoices(Context context) {
        List<Invoice> invoices = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FILENAME);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            if (sb.length() > 0) {
                JSONArray jsonArray = new JSONArray(sb.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    try {
                        invoices.add(new Invoice(jsonObject));
                    } catch (JSONException e) {
                        Log.e(TAG, "Error converting JSON to Invoice: " + e.getMessage());
                    }
                }
            }
            Log.d(TAG, "Raw invoices loaded successfully. Count: " + invoices.size());
        } catch (java.io.FileNotFoundException e) {
            Log.i(TAG, "No invoice file found, starting with empty list.");
        } catch (Exception e) {
            Log.e(TAG, "Error loading raw invoices: " + e.getMessage());
        }
        return invoices;
    }

    /**
     * Loads, groups, and sorts invoices by customer name and VIN,
     * calculating a total cost for each customer/VIN combination.
     *
     * @param context The application context.
     * @return A sorted list of CustomerInvoiceSummary objects.
     */
    public static List<CustomerInvoiceSummary> loadGroupedInvoices(Context context) {
        List<Invoice> allInvoices = loadRawInvoices(context);
        Map<String, CustomerInvoiceSummary> groupedMap = new HashMap<>();

        for (Invoice invoice : allInvoices) {
            String key = invoice.getCustomerName() + "_" + invoice.getCustomerVIN();
            if (!groupedMap.containsKey(key)) {
                groupedMap.put(key, new CustomerInvoiceSummary(invoice.getCustomerName(), invoice.getCustomerVIN()));
            }
            groupedMap.get(key).addInvoice(invoice);
        }

        List<CustomerInvoiceSummary> groupedList = new ArrayList<>(groupedMap.values());

        // Sort the grouped list by Customer Name, then by VIN
        Collections.sort(groupedList, new Comparator<CustomerInvoiceSummary>() {
            @Override
            public int compare(CustomerInvoiceSummary o1, CustomerInvoiceSummary o2) {
                int nameCompare = o1.getCustomerName().compareToIgnoreCase(o2.getCustomerName());
                if (nameCompare != 0) {
                    return nameCompare;
                }
                return o1.getCustomerVIN().compareToIgnoreCase(o2.getCustomerVIN());
            }
        });

        return groupedList;
    }
}