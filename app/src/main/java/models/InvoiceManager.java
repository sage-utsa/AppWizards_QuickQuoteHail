package models;

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

/**
 * Manages the persistence of invoice data for the application. This class handles
 * saving, loading, and grouping invoices. It stores invoice data as a JSON array in a
 * private file and provides methods to interact with this data, including loading
 * all invoices and organizing them into a summary format grouped by customer and vehicle.
 */
public class InvoiceManager {

    private static final String FILENAME = "invoices.json";
    private static final String TAG = "InvoiceManager";
    /**
     * Appends a new invoice to the existing list of invoices and saves the
     * updated list back to the file.
     *
     * @param context The application context, used for file I/O.
     * @param newInvoice The new {@link Invoice} object to be saved.
     */
    public static void saveInvoice(Context context, Invoice newInvoice) {
        List<Invoice> currentInvoices = loadRawInvoices(context);
        currentInvoices.add(newInvoice);
        saveAllInvoices(context, currentInvoices);
    }

    /**
     * Saves an entire list of invoices to the application's private file storage.
     * The invoices are converted to a JSON array string before being written.
     * This method overwrites any existing file with the same name.
     *
     * @param context The application context.
     * @param invoicesToSave The list of {@link Invoice} objects to save.
     */
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

    /**
     * Loads all raw invoices from the saved file and returns them as a list.
     * If the file does not exist or is empty, an empty list is returned.
     *
     * @param context The application context.
     * @return A list of all {@link Invoice} objects found in the file.
     */
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