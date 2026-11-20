package com.agriledger.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.agriledger.app.data.model.BusinessProfile
import com.agriledger.app.data.model.Customer
import com.agriledger.app.data.model.PaymentStatus
import com.agriledger.app.data.model.ServiceJob
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReceiptGenerator {

    /**
     * Generates a high-quality PDF receipt for an agricultural service invoice.
     * Uses Android's native PdfDocument (100% offline, zero external dependencies).
     */
    fun generateInvoicePdf(
        context: Context,
        job: ServiceJob,
        customer: Customer,
        profile: BusinessProfile
    ): File {
        val pdfDocument = PdfDocument()

        // Standard A4 dimensions in PostScript points: 595 x 842 pt
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // Palette
        val primaryColor = Color.rgb(0x17, 0x4D, 0x38)       // #174D38 Deep Forest Green
        val primaryDark = Color.rgb(0x0E, 0x38, 0x27)
        val accentGreen = Color.rgb(0x58, 0xB3, 0x68)       // #58B368 Accent Green
        val darkText = Color.rgb(0x17, 0x23, 0x1C)
        val secondaryText = Color.rgb(0x74, 0x80, 0x78)
        val lightBg = Color.rgb(0xF6, 0xF8, 0xF5)
        val cardBorder = Color.rgb(0xE0, 0xE8, 0xE2)
        val warningGold = Color.rgb(0xF0, 0xB4, 0x4D)
        val errorRed = Color.rgb(0xD9, 0x53, 0x4F)
        val successGreen = Color.rgb(0x3B, 0x9C, 0x67)

        val margin = 40f
        var currentY = 50f

        // 1. Header Banner
        paint.color = primaryColor
        canvas.drawRoundRect(RectF(margin, currentY, pageWidth - margin, currentY + 95f), 12f, 12f, paint)

        // AgriLedger Brand & Business Info
        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 20f
        canvas.drawText("AgriLedger — ${profile.businessName}", margin + 20f, currentY + 35f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f
        paint.color = Color.argb(220, 255, 255, 255)
        canvas.drawText("${profile.villageOrCity}, ${profile.state} • Ph: ${profile.phone}", margin + 20f, currentY + 55f, paint)
        canvas.drawText("Agricultural Service Accounting & Official Invoice", margin + 20f, currentY + 75f, paint)

        // Status badge in header
        val statusText = job.status.displayName.uppercase(Locale.getDefault())
        val badgeColor = when (job.status) {
            PaymentStatus.PAID -> successGreen
            PaymentStatus.PARTIAL -> warningGold
            PaymentStatus.UNPAID -> errorRed
        }
        paint.color = badgeColor
        canvas.drawRoundRect(RectF(pageWidth - margin - 110f, currentY + 25f, pageWidth - margin - 20f, currentY + 55f), 8f, 8f, paint)
        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 12f
        canvas.drawText(statusText, pageWidth - margin - 98f, currentY + 45f, paint)

        currentY += 120f

        // 2. Metadata Columns (Customer info & Invoice Meta)
        // Customer Box
        paint.color = lightBg
        canvas.drawRoundRect(RectF(margin, currentY, (pageWidth / 2f) - 10f, currentY + 90f), 10f, 10f, paint)
        paint.color = cardBorder
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(RectF(margin, currentY, (pageWidth / 2f) - 10f, currentY + 90f), 10f, 10f, paint)
        paint.style = Paint.Style.FILL

        paint.color = secondaryText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        canvas.drawText("BILLED TO / CUSTOMER", margin + 15f, currentY + 22f, paint)

        paint.color = darkText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 14f
        canvas.drawText(customer.name, margin + 15f, currentY + 42f, paint)

        paint.color = secondaryText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f
        canvas.drawText("Village / Location: ${customer.village.ifBlank { "N/A" }}", margin + 15f, currentY + 60f, paint)
        canvas.drawText("Phone: ${customer.phone}", margin + 15f, currentY + 76f, paint)

        // Invoice Meta Box
        val rightBoxStart = (pageWidth / 2f) + 10f
        paint.color = lightBg
        canvas.drawRoundRect(RectF(rightBoxStart, currentY, pageWidth - margin, currentY + 90f), 10f, 10f, paint)
        paint.color = cardBorder
        paint.style = Paint.Style.STROKE
        canvas.drawRoundRect(RectF(rightBoxStart, currentY, pageWidth - margin, currentY + 90f), 10f, 10f, paint)
        paint.style = Paint.Style.FILL

        paint.color = secondaryText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        canvas.drawText("INVOICE DETAILS", rightBoxStart + 15f, currentY + 22f, paint)

        paint.color = primaryColor
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13f
        canvas.drawText("Invoice #: ${job.invoiceNumber}", rightBoxStart + 15f, currentY + 42f, paint)

        paint.color = secondaryText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 11f
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        canvas.drawText("Service Date: ${sdf.format(Date(job.serviceDate))}", rightBoxStart + 15f, currentY + 60f, paint)
        canvas.drawText("Receipt Printed: ${sdf.format(Date())}", rightBoxStart + 15f, currentY + 76f, paint)

        currentY += 115f

        // 3. Line Items Table Header
        paint.color = primaryDark
        canvas.drawRoundRect(RectF(margin, currentY, pageWidth - margin, currentY + 30f), 6f, 6f, paint)
        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11f
        canvas.drawText("SERVICE DESCRIPTION", margin + 15f, currentY + 20f, paint)
        canvas.drawText("AREA", margin + 260f, currentY + 20f, paint)
        canvas.drawText("RATE / ACRE", margin + 340f, currentY + 20f, paint)
        canvas.drawText("TOTAL AMOUNT", pageWidth - margin - 110f, currentY + 20f, paint)

        currentY += 32f

        // Table Row
        paint.color = Color.WHITE
        canvas.drawRect(margin, currentY, pageWidth - margin, currentY + 45f, paint)
        paint.color = cardBorder
        paint.style = Paint.Style.STROKE
        canvas.drawRect(margin, currentY, pageWidth - margin, currentY + 45f, paint)
        paint.style = Paint.Style.FILL

        paint.color = darkText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13f
        canvas.drawText(job.serviceType.displayName, margin + 15f, currentY + 24f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        paint.color = secondaryText
        canvas.drawText(job.notes.ifBlank { "Standard Agricultural Service Operation" }, margin + 15f, currentY + 38f, paint)

        paint.color = darkText
        paint.textSize = 12f
        canvas.drawText(Formatters.formatAcres(job.acres), margin + 260f, currentY + 26f, paint)
        canvas.drawText(Formatters.formatCurrency(job.ratePerAcre, profile.currencySymbol), margin + 340f, currentY + 26f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(Formatters.formatCurrency(job.totalAmount, profile.currencySymbol), pageWidth - margin - 110f, currentY + 26f, paint)

        currentY += 65f

        // 4. Financial Totals Section
        val totalBoxX = pageWidth - margin - 240f
        paint.color = lightBg
        canvas.drawRoundRect(RectF(totalBoxX, currentY, pageWidth - margin, currentY + 115f), 10f, 10f, paint)
        paint.color = cardBorder
        paint.style = Paint.Style.STROKE
        canvas.drawRoundRect(RectF(totalBoxX, currentY, pageWidth - margin, currentY + 115f), 10f, 10f, paint)
        paint.style = Paint.Style.FILL

        paint.textSize = 11f
        paint.color = secondaryText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Total Bill Amount:", totalBoxX + 15f, currentY + 26f, paint)
        paint.color = darkText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(Formatters.formatCurrency(job.totalAmount, profile.currencySymbol), pageWidth - margin - 90f, currentY + 26f, paint)

        paint.color = secondaryText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Amount Paid / Advance:", totalBoxX + 15f, currentY + 52f, paint)
        paint.color = accentGreen
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(Formatters.formatCurrency(job.amountPaid, profile.currencySymbol), pageWidth - margin - 90f, currentY + 52f, paint)

        paint.color = cardBorder
        canvas.drawLine(totalBoxX + 10f, currentY + 68f, pageWidth - margin - 10f, currentY + 68f, paint)

        paint.color = if (job.pendingAmount > 0) errorRed else successGreen
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13f
        canvas.drawText("BALANCE DUE:", totalBoxX + 15f, currentY + 95f, paint)
        canvas.drawText(Formatters.formatCurrency(job.pendingAmount, profile.currencySymbol), pageWidth - margin - 90f, currentY + 95f, paint)

        // 5. Notes & Terms
        currentY += 140f
        paint.color = darkText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 11f
        canvas.drawText("Terms & Agricultural Service Notes:", margin, currentY, paint)

        paint.color = secondaryText
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 10f
        canvas.drawText("1. All machine work, fuel consumption and service acreage have been verified.", margin, currentY + 18f, paint)
        canvas.drawText("2. Please settle any outstanding balance via Cash, UPI, or Cheque to ${profile.businessName}.", margin, currentY + 34f, paint)
        canvas.drawText("3. This is an authentic digital offline invoice generated via AgriLedger.", margin, currentY + 50f, paint)

        // 6. Signatures
        currentY += 95f
        paint.color = cardBorder
        canvas.drawLine(margin, currentY, margin + 160f, currentY, paint)
        canvas.drawLine(pageWidth - margin - 160f, currentY, pageWidth - margin, currentY, paint)

        paint.color = secondaryText
        paint.textSize = 10f
        canvas.drawText("Customer / Farmer Signature", margin + 15f, currentY + 16f, paint)
        canvas.drawText("Authorized Service Provider", pageWidth - margin - 155f, currentY + 16f, paint)

        pdfDocument.finishPage(page)

        // Write to output file
        val outputDir = File(context.cacheDir, "invoices").apply { if (!exists()) mkdirs() }
        val sanitizedInvoiceNo = job.invoiceNumber.replace("[^a-zA-Z0-9-]".toRegex(), "_")
        val file = File(outputDir, "Invoice_${sanitizedInvoiceNo}.pdf")

        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return file
    }

    /**
     * Opens Android's system share / view / print intent with FileProvider URI.
     */
    fun openOrSharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Open or Print Receipt").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    /**
     * Shares the PDF directly as a document file to other apps (WhatsApp, Drive, Email).
     */
    fun sharePdfFile(context: Context, file: File, subject: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(sendIntent, "Share PDF Receipt").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
