package com.AppWizards.QuickQuoteHail;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvoiceManager {

    private static final String FILENAME = "invoices.json";
    private static final String TAG = "InvoiceManager";

    /**
     * Saves a new Invoice to a file. Appends to existing invoices.
     * @param context The application context.
     * @param invoice The Invoice object to save.
     */
    public static void saveInvoice(Context context, Invoice invoice) {
        List<Invoice> currentInvoices = loadInvoices(context);
        currentInvoices.add(0, invoice); // Add new invoice to the beginning (most recent first)

        // Limit history to a reasonable number, e.g., 50
        if (currentInvoices.size() > 50) {
            currentInvoices = currentInvoices.subList(0, 50);
        }

        JSONArray jsonArray = new JSONArray();
        for (Invoice inv : currentInvoices) {
            try {
                jsonArray.put(inv.toJsonObject());
            } catch (JSONException e) {
                Log.e(TAG, "Error converting invoice to JSON: " + e.getMessage());
            }
        }

        try (FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            fos.write(jsonArray.toString().getBytes());
            Log.d(TAG, "Invoice saved successfully.");
        } catch (IOException e) {
            Log.e(TAG, "Error saving invoices: " + e.getMessage());
        }
    }

    /**
     * Loads all saved Invoice objects from the file.
     * @param context The application context.
     * @return A list of Invoice objects, or an empty list if no invoices or an error occurs.
     */
    public static List<Invoice> loadInvoices(Context context) {
        List<Invoice> invoices = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FILENAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader bufferedReader = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            if (sb.length() > 0) {
                JSONArray jsonArray = new JSONArray(sb.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        invoices.add(new Invoice(jsonObject));
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing invoice JSON object: " + e.getMessage());
                    }
                }
            }
            Log.d(TAG, "Invoices loaded successfully. Count: " + invoices.size());
        } catch (IOException | JSONException e) {
            // File not found is common on first run, or corrupted JSON
            Log.e(TAG, "Error loading invoices: " + e.getMessage());
        }
        return invoices;
    }

    /**
     * Clears all saved invoices from the file.
     * @param context The application context.
     */
    public static void clearAllInvoices(Context context) {
        try (FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            fos.write("[]".getBytes()); // Write an empty JSON array
            Log.d(TAG, "All invoices cleared.");
        } catch (IOException e) {
            Log.e(TAG, "Error clearing invoices: " + e.getMessage());
        }
    }
}
