package br.gov.caixa.loterias.feature.support.presentation.screen

import android.util.Base64
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSSupportTopBar
import br.gov.caixa.loterias.core.designsystem.html.CaixaDSHtmlDocument
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.support.R as LocalizedR
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

/** Documento original renderizado em WebView Compose para preservar HTML, CSS, tabelas e links do servidor. */
@Composable
internal fun ReadOnlyTermsScreen(state: SupportUiState, onBack: () -> Unit, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_termo_de_uso_d57d7b),
        onBack = onBack,
        modifier = (modifier.legacyAutomationId("support_terms_view")).uiId("help.0015"),
        containerColor = CaixaDSColorTokens.White,
        customTopBar = {
            CaixaDSSupportTopBar(uiStrings.getString(LocalizedR.string.ui_termo_de_uso_d57d7b), onBack)
        }
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding)
                .padding(start = 15.dp, end = 15.dp, top = 40.dp, bottom = 30.dp)
        ) {
            when {
                state.isLoading -> Unit

                // O diálogo de carregamento DS em tela cheia é hospedado por SupportScreen.
                state.errorMessage != null -> Text(
                    state.errorMessage,
                    color = CaixaDSColorTokens.NotificationDeleteAction,
                    modifier = Modifier.uiId("help.0016")
                )

                else -> CaixaDSHtmlDocument(
                    html = legacyTermsDocument(state.htmlContent, caixaFontBase64()),
                    modifier = (Modifier.fillMaxSize().legacyAutomationId("support_terms_document")).uiId("help.0017")
                )
            }
        }
    }
}

@Composable
private fun caixaFontBase64(): String {
    val resources = LocalResources.current
    return remember(resources) {
        resources.openRawResource(CaixaDSR.raw.caixa_std_regular_html).use { input ->
            Base64.encodeToString(input.readBytes(), Base64.NO_WRAP)
        }
    }
}

/** Invólucro literal de font-face de TermosAceiteActivity; o CSS e o conteúdo do servidor permanecem intactos. */
internal fun legacyTermsDocument(content: String, base64Font: String): String =
    """<html><head><style type="text/css">@font-face {font-family: 'FonteCaixa';src: url('data:font/ttf;base64,$base64Font');}body, p, div, span, h1, h2, h3, h4, h5, h6, a {font-family: 'FonteCaixa' !important;}</style></head><body>$content</body></html>"""
