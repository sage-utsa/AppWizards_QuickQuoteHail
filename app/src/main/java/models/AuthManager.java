package models;

import android.content.Context;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * AuthManager
 *
 * Handles file-based credential storage in "credentials.txt".
 * File format: one user per line, formatted as "email:password".
 * Seeds a default test account (user@example.com / password123) if no file exists.
 *
 * This class abstracts authentication logic from Activities so UI code
 * does not directly handle file I/O.
 *
 * Author: Sage
 * Date: 2025-08-07
 */
public class AuthManager {

    // === Constants ===
    private static final String FILENAME = "credentials.txt";
    private static final String SEED_EMAIL = "user@example.com";
    private static final String SEED_PASS  = "password123";

    // === Context Reference ===
    private final Context ctx;

    /**
     * Constructs the AuthManager and seeds a test user if needed.
     *
     * @param context Application context (never store raw activity context)
     */
    public AuthManager(Context context) {
        this.ctx = context.getApplicationContext();
        ensureSeedUser();
    }

    // === Public Methods ===

    /**
     * Attempts to log in with the given email and password.
     *
     * @param email    User's email address
     * @param password User's password
     * @return true if credentials match a record in credentials.txt
     */
    public synchronized boolean login(String email, String password) {
        String e = norm(email);
        String p = password == null ? "" : password.trim();
        if (e.isEmpty() || p.isEmpty()) return false;

        try (FileInputStream in = ctx.openFileInput(FILENAME);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int idx = line.indexOf(':');
                if (idx <= 0) continue; // skip invalid lines

                String savedEmail = norm(line.substring(0, idx));
                String savedPass  = line.substring(idx + 1).trim();

                if (e.equals(savedEmail) && p.equals(savedPass)) {
                    return true; // match found
                }
            }
        } catch (IOException ignored) {
            // Treat missing file or read error as "no match"
        }
        return false;
    }

    /**
     * Registers a new account.
     *
     * @param email    New user's email
     * @param password New user's password
     * @return true if registration was successful, false if email already exists or error occurred
     */
    public synchronized boolean register(String email, String password) {
        String e = norm(email);
        String p = password == null ? "" : password.trim();
        if (e.isEmpty() || p.isEmpty()) return false;

        // Prevent duplicate emails
        if (exists(e)) return false;

        // Append to credentials file
        try (FileOutputStream out = ctx.openFileOutput(FILENAME, Context.MODE_APPEND)) {
            out.write((e + ":" + p + "\n").getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    // === Private Helpers ===

    /**
     * Checks if the given normalized email already exists in the file.
     *
     * @param emailNorm Normalized (lowercase + trimmed) email
     * @return true if found, false otherwise
     */
    private boolean exists(String emailNorm) {
        try (FileInputStream in = ctx.openFileInput(FILENAME);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int idx = line.indexOf(':');
                if (idx <= 0) continue;

                String savedEmail = norm(line.substring(0, idx));
                if (emailNorm.equals(savedEmail)) {
                    return true;
                }
            }
        } catch (IOException ignored) {}
        return false;
    }

    /**
     * Creates credentials.txt and seeds a default test user if it doesn't exist.
     */
    private void ensureSeedUser() {
        File file = new File(ctx.getFilesDir(), FILENAME);
        if (file.exists()) return; // already exists

        try (FileOutputStream out = ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            String seed = norm(SEED_EMAIL) + ":" + SEED_PASS + "\n";
            out.write(seed.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {}
    }

    /**
     * Normalizes an email string by trimming whitespace and converting to lowercase.
     *
     * @param email Raw email string
     * @return Normalized email or empty string if null
     */
    private static String norm(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
