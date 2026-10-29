package br.gov.caixa.loterias.core.designsystem.components.dialog

import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.text.withCaixaDSAccessibleLinks
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

/** Versão Compose/DS da ajuda HTML e do diálogo de confirmação legados. */
@Composable
fun CaixaDSHtmlDialog(
    title: String,
    html: String,
    onPositive: () -> Unit,
    positiveLabel: String = stringResource(LocalizedR.string.format_understood),
    negativeLabel: String? = null,
    onNegative: () -> Unit = {},
    parseHtml: Boolean = true,
) {
    val focusRequester = remember { FocusRequester() }
    val uriHandler = LocalUriHandler.current
    val body = remember(html, parseHtml, uriHandler) {
        val parsed = if (parseHtml) caixaDSHtmlToAnnotatedString(html) else AnnotatedString(html)
        parsed.withCaixaDSAccessibleLinks { url ->
            // A navegação pertence a esta interface da plataforma; dados do servidor não podem abrir esquemas arbitrários.
            val scheme = url.toUri().scheme?.lowercase(java.util.Locale.ROOT)
            if (scheme == "https" || scheme == "http") {
                runCatching { uriHandler.openUri(url) }
            }
        }
    }

    LaunchedEffect(title) {
        focusRequester.requestFocus()
    }
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.caixaDSModalWidth(),
            shape = RoundedCornerShape(8.dp),
            color = CaixaDSColorTokens.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().uiId("ds.0076").verticalScroll(
                    rememberScrollState()
                ).padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 14.dp)
            ) {
                HtmlDialogMessage(title, body, focusRequester)
                HtmlDialogActions(positiveLabel, onPositive, negativeLabel, onNegative)
            }
        }
    }
}

@Composable
private fun HtmlDialogMessage(title: String, body: AnnotatedString, focusRequester: FocusRequester) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth()
            .focusRequester(focusRequester)
            .focusable()
            .semantics { heading() },
        color = CaixaDSColorTokens.ComboDark,
        style = CaixaDS.typography.bodyLarge.copy(
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = TextStyle.Default.lineHeight,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
    Box(
        Modifier.fillMaxWidth().padding(top = 17.dp).height(1.dp)
            .background(CaixaDSColorTokens.TutorialDivider)
    )
    Text(
        text = body,
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        color = CaixaDSColorTokens.SocialGraphite,
        style = CaixaDS.typography.bodyLarge.copy(
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 16.sp,
            lineHeight = TextStyle.Default.lineHeight,
            platformStyle = PlatformTextStyle(includeFontPadding = true)
        )
    )
}

@Composable
private fun HtmlDialogActions(positiveLabel: String, onPositive: () -> Unit, negativeLabel: String?, onNegative: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        negativeLabel?.let { label ->
            TextButton(
                onClick = onNegative,
                modifier = (Modifier.padding(end = 8.dp)).uiId("ds.0077")
            ) {
                Text(
                    label,
                    color = CaixaDSColorTokens.SocialGraphite,
                    style = CaixaDS.typography.labelLarge.copy(
                        fontFamily = CaixaDSRegularFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
        TextButton(onClick = onPositive, modifier = Modifier.uiId("ds.0078")) {
            Text(
                positiveLabel,
                color = CaixaDSColorTokens.ComboDark,
                style = CaixaDS.typography.labelLarge.copy(
                    fontFamily = CaixaDSFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

fun caixaDSHtmlToAnnotatedString(html: String): AnnotatedString {
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
                        fontFamily = CaixaDSFontFamily,
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
