package br.gov.caixa.loterias.core.designsystem.html

import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.kevinnzou.web.WebView as ComposeWebView
import com.kevinnzou.web.rememberWebViewStateWithHTMLData
import kotlin.math.roundToInt

private const val TEXT_ZOOM_PERCENT = 100

/** HTML do servidor somente para leitura: preserva CSS e links, desativa JS e libera o renderizador nativo. */
@Composable
fun CaixaDSHtmlDocument(html: String, modifier: Modifier = Modifier) {
    val textZoom = (LocalDensity.current.fontScale * TEXT_ZOOM_PERCENT).roundToInt().coerceAtLeast(1)
    var nativeWebView by remember { mutableStateOf<WebView?>(null) }
    SideEffect { nativeWebView?.settings?.textZoom = textZoom }
    val state = rememberWebViewStateWithHTMLData(
        data = html,
        baseUrl = "https://loterias.caixa.gov.br/",
        mimeType = "text/html",
        encoding = "UTF-8"
    )
    ComposeWebView(
        state = state,
        modifier = modifier,
        onCreated = { webView ->
            nativeWebView = webView
            webView.settings.textZoom = textZoom
            webView.tag = html
            webView.settings.javaScriptEnabled = false
            webView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        },
        onDispose = { webView ->
            nativeWebView = null
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.destroy()
        }
    )
}
