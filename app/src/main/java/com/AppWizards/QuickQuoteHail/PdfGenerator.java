package com.AppWizards.QuickQuoteHail;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PdfGenerator {

    // Used for logging errors or messages
    private static final String TAG = "PdfGenerator";

    // This must match the FileProvider authority in AndroidManifest.xml
    private static final String FILE_AUTHORITY_SUFFIX = ".fileprovider";

    // This method creates a PDF invoice and returns the file as a URI so it can be shared
    public static Uri generateInvoicePdf(Context context, CustomerInvoiceSummary summary) {
        // Create a new blank PDF document
        PdfDocument document = new PdfDocument();

        // Define the size of the PDF page (A4 size)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Canvas is like a blank piece of paper we draw on
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // x and y are the starting positions for drawing text
        int x = 40;
        int y = 50;
        int lineHeight = 25; // space between lines

        // ===== Title =====
        paint.setTextSize(24);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("QuickQuoteHail - Invoice Summary", x, y, paint);
        y += lineHeight * 2;

        // ===== Customer Info =====
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Customer: " + summary.getCustomerName(), x, y, paint);
        y += lineHeight;
        canvas.drawText("VIN: " + summary.getCustomerVIN(), x, y, paint);
        y += lineHeight * 2;

        // ===== Total Cost =====
        paint.setTextSize(18);
        paint.setColor(Color.BLUE); // Use blue to highlight the total
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(String.format("Total Estimated Cost: $%.2f", summary.getTotalCost()), x, y, paint);
        y += lineHeight * 2;

        // ===== Invoices Header =====
        paint.setTextSize(16);
        paint.setColor(Color.DKGRAY);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Invoices", x, y, paint);
        y += lineHeight;

        // ===== Loop through each invoice and draw it =====
        paint.setTextSize(14);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        for (Invoice invoice : summary.getInvoices()) {
            // If we get near the bottom of the page, start a new one
            if (y + lineHeight * 6 > pageInfo.getPageHeight() - 50) {
                document.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50; // reset y position

                // Add a continued header
                paint.setTextSize(16);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                canvas.drawText("Invoices (continued)", x, y, paint);
                y += lineHeight;
                paint.setTextSize(14);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            }

            // Draw each invoice's details line by line
            canvas.drawText("Date: " + invoice.getFormattedDate(), x, y, paint); y += lineHeight;
            canvas.drawText("Panel: " + invoice.getPanelType(), x, y, paint); y += lineHeight;
            canvas.drawText("Dents: " + invoice.getNumberOfDents() + " (" + invoice.getLargestDentSize() + ")", x, y, paint); y += lineHeight;
            canvas.drawText("Aluminum: " + (invoice.isAluminum() ? "Yes" : "No"), x, y, paint); y += lineHeight;
            canvas.drawText(String.format("Estimated Cost: $%.2f", invoice.getEstimatedCost()), x, y, paint); y += lineHeight;

            // Add a light gray line between invoices for readability
            paint.setColor(Color.LTGRAY);
            canvas.drawLine(x, y, pageInfo.getPageWidth() - x, y, paint);
            paint.setColor(Color.BLACK); // Reset color for next invoice
            y += lineHeight;
        }

        // Finish writing the last page
        document.finishPage(page);

        // Save the PDF as a file and return its URI
        File pdfFile = null;
        try {
            // Save to internal storage in a folder called "invoices"
            File filesDir = new File(context.getFilesDir(), "invoices");
            if (!filesDir.exists()) {
                filesDir.mkdirs();
            }

            // Create a unique file name using customer name and timestamp
            String fileName = "Invoice_" + summary.getCustomerName().replace(" ", "_") + "_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) + ".pdf";
            pdfFile = new File(filesDir, fileName);

            // Write the PDF to the file
            FileOutputStream fos = new FileOutputStream(pdfFile);
            document.writeTo(fos);
            fos.close();
            Log.d(TAG, "PDF generated successfully: " + pdfFile.getAbsolutePath());

            // Return a URI for the PDF so it can be shared or emailed
            return FileProvider.getUriForFile(context, context.getPackageName() + FILE_AUTHORITY_SUFFIX, pdfFile);

        } catch (IOException e) {
            Log.e(TAG, "Error generating or saving PDF: " + e.getMessage());
            return null;
        } finally {
            document.close();
        }
    }
}
