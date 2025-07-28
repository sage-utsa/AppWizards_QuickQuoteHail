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

public class PdfGenerator {

    private static final String TAG = "PdfGenerator";
    private static final String FILE_AUTHORITY_SUFFIX = ".fileprovider";

    public static Uri generateInvoicePdf(Context context, CustomerInvoiceSummary summary) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        int x = 40;
        int y = 50;
        int lineHeight = 25;

        // === Draw logo at the top ===
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_logo);
        if (logo != null) {
            int logoWidth = 100;
            int logoHeight = (int) ((float) logo.getHeight() / logo.getWidth() * logoWidth);
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, logoWidth, logoHeight, true);
            canvas.drawBitmap(scaledLogo, x, y, paint);
            y += logoHeight + lineHeight;
        }

        // === Title ===
        paint.setTextSize(24);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("QuickQuoteHail - Invoice Summary", x, y, paint);
        y += lineHeight * 2;

        // === Customer info ===
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Customer: " + summary.getCustomerName(), x, y, paint);
        y += lineHeight;
        canvas.drawText("VIN: " + summary.getCustomerVIN(), x, y, paint);
        y += lineHeight * 2;

        // === Total cost header ===
        paint.setTextSize(18);
        paint.setColor(Color.BLUE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(String.format(Locale.getDefault(), "Total Estimated Cost: $%.2f", summary.getTotalCost()), x, y, paint);
        y += lineHeight * 2;

        // === Table Header ===
        paint.setTextSize(16);
        paint.setColor(Color.DKGRAY);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Date", x, y, paint);
        canvas.drawText("Panel", x + 120, y, paint);
        canvas.drawText("Dents", x + 220, y, paint);
        canvas.drawText("Alum.", x + 320, y, paint);
        canvas.drawText("Cost", x + 420, y, paint);
        y += lineHeight;

        // === Divider line ===
        paint.setColor(Color.BLACK);
        canvas.drawLine(x, y, pageInfo.getPageWidth() - x, y, paint);
        y += lineHeight / 2;

        // === Table Rows ===
        paint.setTextSize(14);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        double totalCost = 0.0;

        for (Invoice invoice : summary.getInvoices()) {
            if (y + lineHeight * 3 > pageInfo.getPageHeight() - 50) {
                document.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;

                paint.setTextSize(16);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                canvas.drawText("Invoices (continued)", x, y, paint);
                y += lineHeight;
            }

            double cost = 0.0;
            try {
                cost = Double.parseDouble(invoice.getEstimatedCost());
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid cost format: " + invoice.getEstimatedCost());
            }
            totalCost += cost;

            canvas.drawText(invoice.getFormattedDate(), x, y, paint);
            canvas.drawText(invoice.getPanelType(), x + 120, y, paint);
            canvas.drawText(invoice.getNumberOfDents() + " (" + invoice.getLargestDentSize() + ")", x + 220, y, paint);
            canvas.drawText(invoice.isAluminum() ? "Yes" : "No", x + 320, y, paint);
            canvas.drawText(String.format(Locale.getDefault(), "$%.2f", cost), x + 420, y, paint);
            y += lineHeight;

            // === Light divider ===
            paint.setColor(Color.LTGRAY);
            canvas.drawLine(x, y, pageInfo.getPageWidth() - x, y, paint);
            paint.setColor(Color.BLACK);
            y += 5;
        }

        // === Summary Footer ===
        y += lineHeight;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(Color.DKGRAY);
        canvas.drawText("Summary", x, y, paint);
        y += lineHeight;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(String.format(Locale.getDefault(), "Subtotal: $%.2f", totalCost), x, y, paint);
        y += lineHeight;
        canvas.drawText("Tax (0%): $0.00", x, y, paint);
        y += lineHeight;
        canvas.drawText(String.format(Locale.getDefault(), "Total: $%.2f", totalCost), x, y, paint);

        document.finishPage(page);

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

            return FileProvider.getUriForFile(context, context.getPackageName() + FILE_AUTHORITY_SUFFIX, pdfFile);

        } catch (IOException e) {
            Log.e(TAG, "Error generating or saving PDF: " + e.getMessage());
            return null;
        } finally {
            document.close();
        }
    }
}
