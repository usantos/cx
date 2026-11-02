package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import kotlinx.coroutines.delay

/** A cópia usa exatamente o texto exibido; não depende de seleção manual ou rede. */
@Composable
internal fun CatalogCodePanel(title: String, code: String, copyId: String, copyLabel: String, note: String? = null) {
    val clipboard = LocalClipboardManager.current
    var copyFeedback by remember(code) { mutableStateOf<CopyFeedback?>(null) }
    val activeFeedback = copyFeedback
    LaunchedEffect(code, activeFeedback) {
        if (activeFeedback != null) {
            delay(COPY_FEEDBACK_DURATION_MILLIS)
            if (copyFeedback === activeFeedback) copyFeedback = null
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LoteriasCaixaDSSectionHeader(title, subtitle = note)
        LoteriasCaixaDSButton(
            text = if (copyFeedback != null) stringResource(R.string.catalog_copied) else copyLabel,
            onClick = {
                clipboard.setText(AnnotatedString(code))
                copyFeedback = CopyFeedback()
            },
            modifier = Modifier.uiId(copyId).semantics { liveRegion = LiveRegionMode.Polite }
        )
        SelectionContainer {
            LoteriasCaixaDSText(
                code,
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                fontFamily = FontFamily.Monospace,
                style = LoteriasCaixaDS.typography.bodySmall
            )
        }
    }
}

private const val COPY_FEEDBACK_DURATION_MILLIS = 3_000L

/** Cada toque tem identidade própria, inclusive no mesmo frame da expiração anterior. */
private class CopyFeedback
