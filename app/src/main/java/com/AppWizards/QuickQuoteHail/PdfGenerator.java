package com.AppWizards.QuickQuoteHail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import models.CustomerInvoiceSummary;
import models.Invoice;

/**
 * PdfGenerator is responsible for creating a PDF invoice summary
 * from a given CustomerInvoiceSummary object. It includes customer details,
 * a list of all individual panel invoices, and a cost breakdown with tax.
 *
 * <p>This class handles rendering the invoice visually and saving it to internal storage.</p>
 */
public class PdfGenerator {

    private static final String TAG = "PdfGenerator";
    private static final String FILE_AUTHORITY_SUFFIX = ".fileprovider";

    /**
     * Generates a PDF invoice based on the provided customer summary and returns a content URI.
     *
     * @param context The application context used to access resources and internal file storage.
     * @param summary The summary object containing all invoice data to be printed.
     * @return A URI to the generated PDF file, or null if generation fails.
     */
    public static Uri generateInvoicePdf(Context context, CustomerInvoiceSummary summary) {
        PdfDocument document = new PdfDocument();

        // Define page size: standard A4 dimensions in points (595x842)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Set initial margins and spacing
        int x = 40;
        int y = 50;
        int lineHeight = 25;
        int rowHeight = 30;
        int tableRight = pageInfo.getPageWidth() - x;

        // === Add Transparent Watermark (Centered) ===
        Bitmap watermark = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_logo);
        if (watermark != null) {
            int wmWidth = 300;
            int wmHeight = (int) ((float) watermark.getHeight() / watermark.getWidth() * wmWidth);
            Bitmap scaledWatermark = Bitmap.createScaledBitmap(watermark, wmWidth, wmHeight, true);
            Paint watermarkPaint = new Paint();
            watermarkPaint.setAlpha(40);  // make it light and unobtrusive
            canvas.drawBitmap(scaledWatermark, (pageInfo.getPageWidth() - wmWidth) / 2f, (pageInfo.getPageHeight() - wmHeight) / 2f, watermarkPaint);
        }

        // === Add Logo at the Top ===
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_logo);
        if (logo != null) {
            int logoWidth = 100;
            int logoHeight = (int) ((float) logo.getHeight() / logo.getWidth() * logoWidth);
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, logoWidth, logoHeight, true);
            canvas.drawBitmap(scaledLogo, x, y, paint);
            y += logoHeight + lineHeight;
        }

        // === Invoice Title ===
        paint.setTextSize(24);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("QuickQuoteHail - Invoice Summary", x, y, paint);
        y += lineHeight * 2;

        // === Customer Information ===
        paint.setTextSize(16);
        paint.setTypeface(Typeface.DEFAULT);
        canvas.drawText("Customer: " + summary.getCustomerName(), x, y, paint);
        y += lineHeight;
        canvas.drawText("VIN: " + summary.getCustomerVIN(), x, y, paint);
        y += lineHeight;

        String date = summary.getInvoices().isEmpty() ? "N/A" : summary.getInvoices().get(0).getFormattedDate();
        canvas.drawText("Date of Estimate: " + date, x, y, paint);
        y += lineHeight * 2;

        // === Table Headers ===
        paint.setColor(Color.DKGRAY);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("Panel", x, y, paint);
        canvas.drawText("Dents", x + 120, y, paint);
        canvas.drawText("Alum.", x + 240, y, paint);
        canvas.drawText("Cost", x + 360, y, paint);
        y += rowHeight;

        // === Table Content ===
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(14);

        double totalCost = 0.0;
        boolean alternateRow = false; // for zebra-striping rows

        for (Invoice invoice : summary.getInvoices()) {
            // Check if a new page is needed
            if (y + rowHeight > pageInfo.getPageHeight() - 100) {
                document.finishPage(page);
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }

            // Parse the cost from the invoice string (e.g., "$300.00")
            double cost = 0.0;
            try {
                cost = Double.parseDouble(invoice.getEstimatedCost().replace("$", ""));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid cost format: " + invoice.getEstimatedCost());
            }

            totalCost += cost;

            // === Alternate row background color ===
            paint.setColor(alternateRow ? Color.parseColor("#F8F8F8") : Color.WHITE);
            canvas.drawRect(x, y - rowHeight + 10, tableRight, y + 10, paint);

            // === Row content ===
            paint.setColor(Color.BLACK);
            canvas.drawText(invoice.getPanelType(), x, y, paint);
            canvas.drawText(invoice.getNumberOfDents() + " (" + invoice.getLargestDentSize() + ")", x + 120, y, paint);
            canvas.drawText(invoice.isAluminum() ? "Yes" : "No", x + 240, y, paint);
            canvas.drawText(String.format(Locale.getDefault(), "$%.2f", cost), x + 360, y, paint);

            // === Row separator line ===
            paint.setColor(Color.LTGRAY);
            canvas.drawLine(x, y + 5, tableRight, y + 5, paint);

            y += rowHeight;
            alternateRow = !alternateRow;
        }

        // === Summary Totals ===
        y += lineHeight;
        double taxRate = 0.0825;
        double taxAmount = totalCost * taxRate;
        double grandTotal = totalCost + taxAmount;

        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(Color.DKGRAY);
        canvas.drawText("Summary", x, y, paint);
        y += lineHeight;

        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(Color.BLACK);
        canvas.drawText(String.format(Locale.getDefault(), "Subtotal: $%.2f", totalCost), x, y, paint);
        y += lineHeight;
        canvas.drawText(String.format(Locale.getDefault(), "Tax (8.25%%): $%.2f", taxAmount), x, y, paint);
        y += lineHeight;
        canvas.drawText(String.format(Locale.getDefault(), "Total: $%.2f", grandTotal), x, y, paint);

        // === Finish the page ===
        document.finishPage(page);

        // === Write the PDF file to internal storage ===
        File pdfFile;
        try {
            File filesDir = new File(context.getFilesDir(), "invoices");
            if (!filesDir.exists() && !filesDir.mkdirs()) {
                Log.e(TAG, "Failed to create invoice directory");
            }

            String fileName = "Invoice_" + summary.getCustomerName().replace(" ", "_") + "_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) + ".pdf";
            pdfFile = new File(filesDir, fileName);

            FileOutputStream fos = new FileOutputStream(pdfFile);
            document.writeTo(fos);
            fos.close();
            Log.d(TAG, "PDF generated successfully: " + pdfFile.getAbsolutePath());

            // Return a content URI for the saved PDF
            return FileProvider.getUriForFile(context, context.getPackageName() + FILE_AUTHORITY_SUFFIX, pdfFile);

        } catch (IOException e) {
            Log.e(TAG, "Error generating or saving PDF: " + e.getMessage());
            return null;

        } finally {
            document.close();
        }
    }
}
