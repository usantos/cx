package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.html.CaixaDSHtmlDocument
import br.gov.caixa.loterias.core.designsystem.text.withCaixaDSAccessibleLinks
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.support.presentation.mapper.supportRegularStyle
import br.gov.caixa.loterias.feature.support.presentation.route.SupportAnswerLinkDialog
import br.gov.caixa.loterias.feature.support.presentation.route.supportAnswerText

@Composable
internal fun FaqAnswer(answer: String, modifier: Modifier) {
    val context = LocalContext.current
    val linkColor = CaixaDSColorTokens.Blue900
    var pendingUrl by remember(answer) { mutableStateOf<String?>(null) }
    val text = remember(answer, linkColor) {
        supportAnswerText(answer, linkColor)
    }
    Text(
        text = text.withCaixaDSAccessibleLinks { pendingUrl = it },
        modifier = (modifier).uiId("help.0018"),
        style = supportRegularStyle(14).copy(color = CaixaDS.colors.graphite)
    )
    pendingUrl?.let { url ->
        SupportAnswerLinkDialog(url, context, onDismiss = { pendingUrl = null })
    }
}

@Composable
internal fun HtmlContent(html: String) {
    // TermosUsoActivity carrega o documento original do servidor em WebView; preserva seu CSS e links.
    CaixaDSHtmlDocument(
        html = html,
        modifier = (
            Modifier.fillMaxSize().padding(start = 15.dp, end = 15.dp, top = 40.dp, bottom = 30.dp)
                .legacyAutomationId("support_html")
            ).uiId("help.0019")
    )
}
