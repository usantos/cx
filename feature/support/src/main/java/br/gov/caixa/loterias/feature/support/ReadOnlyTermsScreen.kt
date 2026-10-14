package br.gov.caixa.loterias.feature.support

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Base64
import androidx.compose.runtime.remember
import com.kevinnzou.web.WebView as ComposeWebView
import com.kevinnzou.web.rememberWebViewStateWithHTMLData
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.caixaDSLegacyHtmlToAnnotatedString
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSR

/** Documento original renderizado em WebView Compose to preserve server HTML, CSS, tables, and links. */
@Composable
internal fun ReadOnlyTermsScreen(state: SupportUiState, onBack: () -> Unit, modifier: Modifier) {
    CaixaDSLegacyScaffold(
        title = "Termo de uso", onBack = onBack, modifier = modifier.testTag("support_terms_view"),
        containerColor = CaixaDSColorTokens.White,
        customTopBar = {
            SupportCustomTopBar("Termo de uso", onBack)
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)
            .padding(start = 15.dp, end = 15.dp, top = 40.dp, bottom = 30.dp)) {
            when {
                state.isLoading -> Unit // The fullscreen DS loading dialog is hosted by SupportScreen.
                state.errorMessage != null -> Text(state.errorMessage, color = CaixaDSColorTokens.LegacyNotificationDeleteAction)
                else -> CaixaDSComposeTermsWebView(
                    html = legacyTermsDocument(state.htmlContent, caixaFontBase64()),
                    modifier = Modifier.fillMaxSize().testTag("support_terms_document"),
                )
            }
        }
    }
}

@Composable
internal fun SupportCustomTopBar(title: String, onBack: () -> Unit) {
    Column(Modifier.background(CaixaDSColorTokens.White).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp).background(CaixaDSColorTokens.Blue700),
            verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(start = 32.dp).size(15.dp, 23.dp).clickable(onClick = onBack)) {
                Image(painterResource(R.drawable.legacy_support_ic_arrow_back), "Voltar", Modifier.fillMaxSize())
            }
            Text(title, Modifier.padding(start = 16.dp, end = 16.dp),
                color = CaixaDSColorTokens.White, maxLines = 1,
                style = TextStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp, platformStyle = PlatformTextStyle(includeFontPadding = true)))
        }
    }
}

@Composable
internal fun CaixaDSComposeTermsWebView(html: String, modifier: Modifier = Modifier) {
    val state = rememberWebViewStateWithHTMLData(
        data = html,
        baseUrl = "https://loterias.caixa.gov.br/",
        mimeType = "text/html",
        encoding = "UTF-8",
    )
    ComposeWebView(
        state = state,
        modifier = modifier,
        onCreated = { webView ->
            webView.tag = html
            webView.settings.javaScriptEnabled = false
            webView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        },
        onDispose = { webView ->
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.destroy()
        },
    )
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

/** Literal font-face wrapper from TermosAceiteActivity; backend CSS and content are untouched. */
internal fun legacyTermsDocument(content: String, base64Font: String): String =
    """<html><head><style type="text/css">@font-face {font-family: 'FonteCaixa';src: url('data:font/ttf;base64,$base64Font');}body, p, div, span, h1, h2, h3, h4, h5, h6, a {font-family: 'FonteCaixa' !important;}</style></head><body>$content</body></html>"""
