package br.gov.caixa.loterias.core.designsystem.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.text.withLoteriasCaixaDSAccessibleLinks
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

/** Versão Compose/DS da ajuda HTML e do diálogo de confirmação legados. */
@Composable
fun LoteriasCaixaDSHtmlDialog(
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
        val parsed = if (parseHtml) loteriasCaixaDSHtmlToAnnotatedString(html) else AnnotatedString(html)
        parsed.withLoteriasCaixaDSAccessibleLinks { url ->
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
            modifier = Modifier.loteriasCaixaDSModalWidth(),
            shape = RoundedCornerShape(8.dp),
            color = LoteriasCaixaDSColorTokens.White,
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
