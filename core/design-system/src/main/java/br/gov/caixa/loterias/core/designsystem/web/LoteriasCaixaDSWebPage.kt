package br.gov.caixa.loterias.core.designsystem.web

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.kevinnzou.web.WebView as ComposeWebView
import com.kevinnzou.web.rememberWebViewState
import kotlin.math.roundToInt

private const val TEXT_ZOOM_PERCENT = 100

/** Renderizador de URLs pertencente ao DS; consumidores recebem eventos de texto, nunca objetos WebView nativos. */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoteriasCaixaDSWebPage(
    url: String,
    modifier: Modifier = Modifier,
    javaScriptEnabled: Boolean = false,
    captureBackPresses: Boolean = true,
    onConsoleMessage: ((String) -> Unit)? = null,
    isUrlAllowed: ((String) -> Boolean)? = null,
    isConsoleSourceAllowed: ((String) -> Boolean)? = null,
    onNavigationBlocked: (() -> Unit)? = null,
    onConnectionError: (() -> Unit)? = null,
) {
    val textZoom = (LocalDensity.current.fontScale * TEXT_ZOOM_PERCENT).roundToInt().coerceAtLeast(1)
    var nativeWebView by remember { mutableStateOf<WebView?>(null) }
    SideEffect { nativeWebView?.settings?.textZoom = textZoom }
    val state = rememberWebViewState(url)
    val currentConsole = rememberUpdatedState(onConsoleMessage)
    val currentUrlPolicy = rememberUpdatedState(isUrlAllowed)
    val currentSourcePolicy = rememberUpdatedState(isConsoleSourceAllowed)
    val currentBlocked = rememberUpdatedState(onNavigationBlocked)
    val currentConnectionError = rememberUpdatedState(onConnectionError)
    val mainHandler = remember { Handler(Looper.getMainLooper()) }
    val disposed = remember { java.util.concurrent.atomic.AtomicBoolean(false) }
    val webClient = remember {
        loteriasCaixaDSWebViewClient(currentUrlPolicy, currentBlocked, currentConnectionError, mainHandler, disposed)
    }
    val chromeClient = remember {
        loteriasCaixaDSWebChromeClient(currentConsole, currentSourcePolicy, disposed)
    }
    ComposeWebView(
        state = state,
        modifier = modifier,
        captureBackPresses = captureBackPresses,
        onCreated = {
            nativeWebView = it
            it.settings.textZoom = textZoom
            disposed.set(false)
            it.settings.javaScriptEnabled = javaScriptEnabled
            it.settings.allowFileAccess = false
            it.settings.allowContentAccess = false
            it.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            it.settings.setSupportMultipleWindows(false)
        },
        onDispose = {
            nativeWebView = null
            disposed.set(true)
            it.stopLoading()
            it.loadUrl("about:blank")
            it.destroy()
        },
        chromeClient = chromeClient,
        client = webClient
    )
}
