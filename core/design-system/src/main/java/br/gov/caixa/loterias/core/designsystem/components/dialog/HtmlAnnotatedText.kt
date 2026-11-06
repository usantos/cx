package br.gov.caixa.loterias.core.designsystem.components.dialog

import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily

fun loteriasCaixaDSHtmlToAnnotatedString(html: String): AnnotatedString {
    val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
    return buildAnnotatedString {
        append(spanned)
        val fullText = spanned.toString()
        spanned.getSpans(0, spanned.length, Any::class.java).forEach { span ->
            val start = spanned.getSpanStart(span).coerceIn(0, fullText.length)
            val end = spanned.getSpanEnd(span).coerceIn(start, fullText.length)
            if (start == end) return@forEach
            val style = when (span) {
                is StyleSpan -> when (span.style) {
                    android.graphics.Typeface.BOLD, android.graphics.Typeface.BOLD_ITALIC -> SpanStyle(
                        fontFamily = LoteriasCaixaDSFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontStyle = if (span.style == android.graphics.Typeface.BOLD_ITALIC) FontStyle.Italic else FontStyle.Normal
                    )

                    android.graphics.Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)

                    else -> null
                }

                is ForegroundColorSpan -> SpanStyle(color = Color(span.foregroundColor))

                is UnderlineSpan -> SpanStyle(textDecoration = TextDecoration.Underline)

                else -> null
            }
            style?.let { addStyle(it, start, end) }
            if (span is URLSpan) addStringAnnotation("URL", span.url, start, end)
        }
    }
}
