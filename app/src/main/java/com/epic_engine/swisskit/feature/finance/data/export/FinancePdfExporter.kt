package com.epic_engine.swisskit.feature.finance.data.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FinancePdfExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val pageWidth = 595
    private val pageHeight = 842
    private val margin = 40f
    private val rowHeight = 28f
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    fun export(items: List<Finance>): ByteArray {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        draw(page.canvas, items)
        document.finishPage(page)

        val stream = ByteArrayOutputStream()
        document.writeTo(stream)
        document.close()
        return stream.toByteArray()
    }

    private fun draw(canvas: Canvas, items: List<Finance>) {
        val titlePaint = Paint().apply {
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.parseColor("#0080FF")
        }
        val headerPaint = Paint().apply {
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.WHITE
        }
        val cellPaint = Paint().apply {
            textSize = 10f
            color = Color.DKGRAY
        }
        val summaryLabelPaint = Paint().apply {
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            color = Color.DKGRAY
        }
        val incomePaint = Paint().apply { textSize = 12f; color = Color.parseColor("#0FB380") }
        val expensePaint = Paint().apply { textSize = 12f; color = Color.parseColor("#FB2A2A") }
        val dividerPaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }
        val headerBgPaint = Paint().apply { color = Color.parseColor("#0080FF") }

        var y = margin + 30f

        canvas.drawText(context.getString(R.string.pdf_report_title), margin, y, titlePaint)
        y += 8f
        canvas.drawText(
            context.getString(R.string.pdf_generated_on, dateFormat.format(Date())),
            margin, y + 14f,
            cellPaint.apply { color = Color.GRAY }
        )
        y += 40f

        val colX = floatArrayOf(margin, margin + 65f, margin + 200f, margin + 290f, margin + 420f)
        val headers = arrayOf(
            context.getString(R.string.pdf_col_date),
            context.getString(R.string.pdf_col_title),
            context.getString(R.string.pdf_col_type),
            context.getString(R.string.pdf_col_category),
            context.getString(R.string.pdf_col_amount)
        )

        canvas.drawRect(margin, y, (pageWidth - margin).toFloat(), y + rowHeight, headerBgPaint)
        headers.forEachIndexed { i, h ->
            canvas.drawText(h, colX[i] + 4f, y + 18f, headerPaint)
        }
        y += rowHeight

        items.forEachIndexed { index, item ->
            if (y + rowHeight > pageHeight - 120) return@forEachIndexed

            val rowBg = Paint().apply {
                color = if (index % 2 == 0) Color.parseColor("#F8F9FA") else Color.WHITE
            }
            canvas.drawRect(margin, y, (pageWidth - margin).toFloat(), y + rowHeight, rowBg)

            val amountStr = currencyFormat.format(item.amount)
            val amountPaint = if (item.type == FinanceType.INCOME) incomePaint else expensePaint
            val typeLabel = if (item.type == FinanceType.INCOME)
                context.getString(R.string.pdf_type_income)
            else
                context.getString(R.string.pdf_type_expense)

            canvas.drawText(dateFormat.format(Date(item.date)), colX[0] + 4f, y + 18f, cellPaint.apply { color = Color.DKGRAY; textSize = 10f })
            canvas.drawText(item.title.take(20), colX[1] + 4f, y + 18f, cellPaint)
            canvas.drawText(typeLabel, colX[2] + 4f, y + 18f, cellPaint)
            canvas.drawText(item.category.take(16), colX[3] + 4f, y + 18f, cellPaint)
            canvas.drawText(amountStr, colX[4] + 4f, y + 18f, amountPaint.apply { textSize = 10f })

            canvas.drawLine(margin, y + rowHeight, (pageWidth - margin).toFloat(), y + rowHeight, dividerPaint)
            y += rowHeight
        }

        y += 24f
        canvas.drawLine(margin, y, (pageWidth - margin).toFloat(), y, dividerPaint.apply { strokeWidth = 2f })
        y += 20f

        val totalIncome = items.filter { it.type == FinanceType.INCOME }.sumOf { it.amount }
        val totalExpenses = items.filter { it.type == FinanceType.EXPENSE }.sumOf { it.amount }
        val net = totalIncome - totalExpenses

        canvas.drawText(context.getString(R.string.pdf_summary), margin, y, summaryLabelPaint)
        y += 20f
        canvas.drawText(context.getString(R.string.pdf_total_income), margin, y, summaryLabelPaint.apply { textSize = 11f })
        canvas.drawText(currencyFormat.format(totalIncome), margin + 150f, y, incomePaint.apply { textSize = 11f })
        y += 18f
        canvas.drawText(context.getString(R.string.pdf_total_expenses), margin, y, summaryLabelPaint)
        canvas.drawText(currencyFormat.format(totalExpenses), margin + 150f, y, expensePaint.apply { textSize = 11f })
        y += 18f
        val netColor = if (net >= 0) Color.parseColor("#0FB380") else Color.parseColor("#FB2A2A")
        canvas.drawText(context.getString(R.string.pdf_net_balance), margin, y, summaryLabelPaint)
        canvas.drawText(
            currencyFormat.format(net),
            margin + 150f, y,
            Paint().apply { textSize = 11f; typeface = Typeface.DEFAULT_BOLD; color = netColor }
        )
    }
}
