# AgriLedger — Smart Agricultural Service Accounting

**AgriLedger** is a native, offline-first Android application designed for agricultural service contractors, custom harvesters, tractor owners, and agritech operators to manage customer billing, field service jobs, payment collections, operating expenses, and financial reporting.

---

## 🌾 Key Features

1. **Executive Financial Dashboard:**
   - Real-time **Net Realized Cash Balance** ($\text{Collections} - \text{Expenses}$), strictly excluding unpaid invoices from realized cash.
   - Key metrics: Total Billed, Total Collected, Total Pending, Total Customers, Farmland Acres Serviced.
   - Quick action shortcuts: Add Customer, New Service Bill, Record Payment, Add Expense.
   - Outstanding balances list with direct 1-tap **Call** and **Collect** actions.
   - Recent activity feeds across invoices, payments, and expenses.

2. **Customer Relationship & Ledger:**
   - Searchable customer directory with village, phone, and total farmland acreage.
   - Detailed customer ledger with complete service history, payments history, and balance due.
   - 1-tap **Account Statement Sharing** via WhatsApp and SMS.

3. **Agricultural Service Invoicing & Billing:**
   - Built-in support for: Combine Harvesting, Ploughing / Tillage, Seeding / Sowing, Rotavator, Crop Spraying, Threshing, Laser Leveling, and Transport.
   - Automatic live calculation: $\text{Acres} \times \text{Rate/Acre} = \text{Total Bill}$.
   - Auto-generated sequential invoice numbering.
   - Payment status tracking: **Paid**, **Partial**, and **Unpaid**.

4. **Multi-Item Expense Calculator:**
   - Dedicated multi-item calculator for tracking complex maintenance or multi-item operations (e.g. 50L diesel + filters + labor + oil).
   - Add, edit, and remove line items with automatic total calculation.
   - **Save as Operating Expense** with auto-generated breakdown notes.
   - **Unsaved-state protection** with confirmation alerts.

5. **Real Offline PDF Receipt Generation & Printing:**
   - 100% offline, native vector PDF receipt generation using Android `PdfDocument`.
   - Beautiful A4 invoice layout with custom branding, line item breakdown, and balance due.
   - Direct integration with Android `PrintManager` and document sharing via secure `FileProvider`.

6. **Complete Offline JSON Backup & Restore:**
   - Export all database tables (customers, service bills, payments, expenses, and business profile) to a structured JSON file.
   - Instant restore from backup text/file with transactional integrity.

7. **Financial & Operational Reporting:**
   - Comprehensive income statement: Billed, Collected, Pending, Expenses, and Net Cash Flow.
   - Operational metrics: Average Revenue per Acre and Farmland Serviced.
   - Visual category-wise expense breakdown with percentage progress indicators.
   - 1-tap export of formatted financial reports.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin 2.0.21
- **UI Toolkit:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel) with Unidirectional Data Flow
- **Persistence:** Android Room Database 2.6.1 with Coroutines Flow
- **Printing / PDF:** Native Android `PdfDocument` & `FileProvider`
- **CI/CD:** GitHub Actions with Temurin JDK 17 & Gradle 8.9

---

## 🎨 Design System

- **Primary Forest Green:** `#174D38`
- **Forest Variant:** `#236B4C`
- **Accent Green:** `#58B368`
- **Background:** `#F6F8F5` (Light) | `#101914` (Dark)
- **Surface:** `#FFFFFF` (Light) | `#17241E` (Dark)
- **Card Corners:** 18–24dp rounded corners with soft elevation

---

## 🚀 Building & Testing

### Via GitHub Actions (Recommended)
This repository includes an automated GitHub Actions CI/CD workflow at [`.github/workflows/android.yml`](.github/workflows/android.yml).
- Automatically triggers on every push and pull request to `main` or `master`.
- Can also be triggered manually via **Actions** -> **Run workflow**.
- Runs all unit tests.
- Compiles `assembleDebug` and uploads the APK artifact as **`AgriLedger-Debug-APK`**.

### Local Build (Command Line)
```bash
# Run unit tests
./gradlew test

# Assemble debug APK
./gradlew assembleDebug
# Generated APK will be at: app/build/outputs/apk/debug/app-debug.apk
```
