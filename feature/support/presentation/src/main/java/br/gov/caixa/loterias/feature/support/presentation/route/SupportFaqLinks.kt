package br.gov.caixa.loterias.feature.support.presentation.route

import android.content.Context
import android.content.Intent
import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.net.toUri
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.support.R as LocalizedR

internal fun supportAnswerText(answer: String, linkColor: Color): AnnotatedString {
    val builder = AnnotatedString.Builder(answer)
    val matcher = Patterns.WEB_URL.matcher(answer)
    while (matcher.find()) {
        val candidate = matcher.group()
        val parsed = candidate.toUri()
        val scheme = parsed.scheme
        val host = if (scheme == null) "https://$candidate".toUri().host else parsed.host
        val url = supportAnswerUrl(candidate, matcher.start(), answer, scheme, host) ?: continue
        builder.addStyle(
            SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
            matcher.start(),
            matcher.end()
        )
        builder.addStringAnnotation("url", url, matcher.start(), matcher.end())
    }
    return builder.toAnnotatedString()
}

@Composable
internal fun SupportAnswerLinkDialog(url: String, context: Context, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = uiStrings.getString(LocalizedR.string.ui_voce_sera_direcionado_para_a_web_des_022470),
        positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
        onPositive = {
            onDismiss()
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
        },
        negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
        onNegative = { onDismiss() }
    )
}
