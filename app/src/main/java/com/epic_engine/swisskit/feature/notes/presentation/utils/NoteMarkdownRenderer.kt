package com.epic_engine.swisskit.feature.notes.presentation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

object NoteMarkdownRenderer {

    private val BOLD_REGEX = Regex("""\*\*(.+?)\*\*""")
    private val ITALIC_REGEX = Regex("""\*(.+?)\*""")
    private val BULLET_REGEX = Regex("""^- (.+)""", RegexOption.MULTILINE)

    fun render(markdown: String): AnnotatedString = buildAnnotatedString {
        val lines = markdown.split("\n")
        lines.forEachIndexed { index, line ->
            val isBullet = BULLET_REGEX.matches(line)
            val processedLine = if (isBullet) "• ${line.removePrefix("- ")}" else line

            appendStyledLine(processedLine)
            if (index < lines.lastIndex) append("\n")
        }
    }

    private fun AnnotatedString.Builder.appendStyledLine(line: String) {
        var cursor = 0
        val combined = buildList {
            BOLD_REGEX.findAll(line).forEach { add(Triple(it.range, it.groupValues[1], "bold")) }
            ITALIC_REGEX.findAll(line).forEach { add(Triple(it.range, it.groupValues[1], "italic")) }
        }.sortedBy { it.first.first }

        for ((range, text, style) in combined) {
            if (cursor < range.first) append(line.substring(cursor, range.first))
            when (style) {
                "bold" -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(text) }
                "italic" -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(text) }
            }
            cursor = range.last + 1
        }
        if (cursor <= line.lastIndex) append(line.substring(cursor))
    }
}
