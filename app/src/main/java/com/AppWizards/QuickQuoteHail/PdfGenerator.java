package com.AppWizards.QuickQuoteHail;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
// Import the Typeface class
import android.graphics.Typeface; // <--- ADD THIS LINE

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PdfGenerator {

    private static final String TAG = "PdfGenerator";
    private static final String FILE_AUTHORITY_SUFFIX = ".fileprovider"; // Matches your manifest

    public static Uri generateInvoicePdf(Context context, CustomerInvoiceSummary summary) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size (approx. 595x842 pts)
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        int x = 40; // X start position
        int y = 50; // Y start position
        int lineHeight = 25;

        // Title
        paint.setTextSize(24);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("QuickQuoteHail - Invoice Summary", x, y, paint);
        y += lineHeight * 2;

        // Customer Details
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Customer: " + summary.getCustomerName(), x, y, paint);
        y += lineHeight;
        canvas.drawText("VIN: " + summary.getCustomerVIN(), x, y, paint);
        y += lineHeight * 2;

        // Total Cost
        paint.setTextSize(20);
        paint.setColor(Color.BLUE); // Highlight total
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(String.format("Total Estimated Cost: $%.2f", summary.getTotalCost()), x, y, paint);
        y += lineHeight * 2;

        // Individual Invoices Header
        paint.setTextSize(18);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Individual Invoices:", x, y, paint);
        y += lineHeight;

        // Draw each individual invoice
        paint.setTextSize(14);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        for (Invoice invoice : summary.getInvoices()) {
            if (y + lineHeight * 5 > pageInfo.getPageHeight() - 50) { // Check if new page is needed
                document.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50; // Reset Y for new page
                canvas.drawText("Individual Invoices (continued):", x, y, paint);
                y += lineHeight;
            }

            canvas.drawText("  Date: " + invoice.getFormattedDate(), x, y, paint);
            y += lineHeight;
            canvas.drawText("  Panel: " + invoice.getPanelType(), x, y, paint);
            y += lineHeight;
            canvas.drawText("  Dent Details: " + invoice.getNumberOfDents() + " (" + invoice.getLargestDentSize() + ")", x, y, paint);
            y += lineHeight;
            canvas.drawText("  Aluminum: " + (invoice.isAluminum() ? "Yes" : "No"), x, y, paint);
            y += lineHeight;
            canvas.drawText("  Estimated Cost: " + invoice.getEstimatedCost(), x, y, paint);
            y += lineHeight * 1.5; // Extra space between invoices
        }

        document.finishPage(page);

        // Save the PDF to a file
        File pdfFile = null;
        try {
            // Get directory for app-specific files that are not visible to other apps directly
            File filesDir = new File(context.getFilesDir(), "invoices");
            if (!filesDir.exists()) {
                filesDir.mkdirs(); // Create the directory if it doesn't exist
            }

            String fileName = "Invoice_" + summary.getCustomerName().replace(" ", "_") + "_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) + ".pdf";
            pdfFile = new File(filesDir, fileName);

            FileOutputStream fos = new FileOutputStream(pdfFile);
            document.writeTo(fos);
            fos.close();
            Log.d(TAG, "PDF generated successfully: " + pdfFile.getAbsolutePath());

            // Use FileProvider to get a content URI
            return FileProvider.getUriForFile(context, context.getPackageName() + FILE_AUTHORITY_SUFFIX, pdfFile);

        } catch (IOException e) {
            Log.e(TAG, "Error generating or saving PDF: " + e.getMessage());
            return null;
        } finally {
            document.close();
        }
    }
}