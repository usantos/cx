package br.gov.caixa.loterias.feature.support.faq.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.text.withLoteriasCaixaDSAccessibleLinks
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.support.faq.mapper.supportRegularStyle
import br.gov.caixa.loterias.feature.support.faq.route.SupportAnswerLinkDialog
import br.gov.caixa.loterias.feature.support.faq.route.supportAnswerText

@Composable
internal fun FaqAnswer(answer: String, modifier: Modifier) {
    val context = LocalContext.current
    val linkColor = LoteriasCaixaDSColorTokens.Blue900
    var pendingUrl by remember(answer) { mutableStateOf<String?>(null) }
    val text = remember(answer, linkColor) {
        supportAnswerText(answer, linkColor)
    }
    Text(
        text = text.withLoteriasCaixaDSAccessibleLinks { pendingUrl = it },
        modifier = (modifier).uiId("help.0018"),
        style = supportRegularStyle(14).copy(color = LoteriasCaixaDS.colors.graphite)
    )
    pendingUrl?.let { url ->
        SupportAnswerLinkDialog(url, context, onDismiss = { pendingUrl = null })
    }
}
