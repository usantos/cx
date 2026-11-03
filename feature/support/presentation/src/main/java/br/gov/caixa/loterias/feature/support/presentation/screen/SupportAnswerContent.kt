package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.html.LoteriasCaixaDSHtmlDocument

@Composable
internal fun HtmlContent(html: String) {
    // TermosUsoActivity carrega o documento original do servidor em WebView; preserva seu CSS e links.
    LoteriasCaixaDSHtmlDocument(
        html = html,
        modifier = (
            Modifier.fillMaxSize().padding(start = 15.dp, end = 15.dp, top = 40.dp, bottom = 30.dp)
                .legacyAutomationId("support_html")
            ).uiId("help.0019")
    )
}
