# QuickQuoteHail: Estimate hail damage and generate invoices on the go

<img src="circle_logo.png" alt="AppWizards Logo" width="150"/>

Created by **Team AppWizards**

---

<img src="app_logo.png" alt="QuickQuoteHail Logo" width="150"/>

## Motivation
QuickQuoteHail was created to simplify the process of estimating hail damage for auto repair technicians. The app provides an efficient way to record panel damage, calculate costs, and generate professional invoices—all without needing internet access. Our goal is to make quoting fast, accurate, and stress-free.

---

## Build Status
The current version of QuickQuoteHail is **fully functional** and passes all internal tests. Future improvements may include cloud syncing, advanced sorting options, and client tracking features.

---

## Code Style
- Language: **Java**
- IDE: **Android Studio**
- Architecture: **MVC (Model-View-Controller)**

---

## Video Demonstration
🎥 [Watch the demo](https://drive.google.com/file/d/1E0CWEUIhW-4bnAReD1ggmaGIQpnurovc/view?usp=drive_link)

---

## Tech Used
- **Android Studio** for development
- **Java 17** for logic and UI
- **Adobe XD** for app design
- **GitHub** for version control
- **Google Drive** for shared assets
- **Group Chat/Instagram** for team communication

---

## Example Code Snippet
```java
public double calculateTotalCost(Panel panel) {
    double baseRate = panel.getDentCount() * getDentRate(panel.getDentSize());
    if (panel.getMaterial().equals("Aluminum")) {
        baseRate *= ALUMINUM_MULTIPLIER;
    }
    return baseRate;
}
```

---

## Known Issues
- No database integration—data is stored locally
- Invoice history lacks advanced filtering/sorting

---

## Installation

> The app is not currently available on the Google Play Store.

### Requirements
- Android Studio (Giraffe or newer)
- Java SDK 17+
- Android SDK (API 33+)
- Android Emulator or physical device with USB debugging

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/sage-utsa/AppWizards_QuickQuoteHail.git
   ```

2. Open the project in **Android Studio**
   - Click **"Open"** and select the project folder
   - Let Gradle sync and install any required components

3. Connect a device or launch an emulator

4. Run the app using the **green ▶ button**

5. Login with test credentials:
   - **Email:** `user@example.com`
   - **Password:** `password123`

> No internet required — all data is stored locally on the device.

---

## How to Use

- **Login/Register** with file-based credentials
- **Add Panels** by selecting dent size, count, and material
- **View Estimate** to calculate and review total cost
- **Generate PDF** to create a branded invoice
- **Check History** to review and reopen past estimates

---

## Credits

Created by **Team AppWizards**
- [Justice](https://github.com/JusticeHurt)
- [Khristian](https://github.com/kneal26)
- [Sage](https://github.com/sage-utsa)
