package br.gov.caixa.loterias.core.designsystem.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Handler
import android.os.Looper
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
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
import com.kevinnzou.web.AccompanistWebChromeClient
import com.kevinnzou.web.AccompanistWebViewClient
import com.kevinnzou.web.WebView as ComposeWebView
import com.kevinnzou.web.rememberWebViewState
import kotlin.math.roundToInt

private const val TEXT_ZOOM_PERCENT = 100

/** Renderizador de URLs pertencente ao DS; consumidores recebem eventos de texto, nunca objetos WebView nativos. */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CaixaDSWebPage(
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
        object : AccompanistWebViewClient() {
            private fun blocked(url: String): Boolean = currentUrlPolicy.value?.invoke(url) == false
            private fun reportBlocked() {
                mainHandler.post { if (!disposed.get()) currentBlocked.value?.invoke() }
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
                blocked(request.url.toString()).also { if (it) reportBlocked() }

            @Suppress("OVERRIDE_DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean = blocked(url).also { if (it) reportBlocked() }

            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                if (!blocked(request.url.toString())) return super.shouldInterceptRequest(view, request)
                if (request.isForMainFrame) reportBlocked()
                return WebResourceResponse(
                    "text/plain",
                    "UTF-8",
                    403,
                    "Blocked",
                    emptyMap(),
                    java.io.ByteArrayInputStream(byteArrayOf())
                )
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                if (disposed.get()) return
                if (url != null && blocked(url)) {
                    view.stopLoading()
                    reportBlocked()
                    return
                }
                super.onPageStarted(view, url, favicon)
            }

            private fun reportConnectionError(view: WebView, code: Int): Boolean {
                val connectionFailure = code == ERROR_HOST_LOOKUP || code == ERROR_CONNECT ||
                    code == ERROR_TIMEOUT || code == ERROR_IO
                if (disposed.get() || !connectionFailure || currentConnectionError.value == null) return false
                view.stopLoading()
                currentConnectionError.value?.invoke()
                return true
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest?, error: WebResourceError?) {
                // Falhas em imagens ou scripts não devem substituir uma página que continua utilizável.
                if (request?.isForMainFrame == true && error != null && reportConnectionError(view, error.errorCode)) return
                super.onReceivedError(view, request, error)
            }

            @Suppress("OVERRIDE_DEPRECATION")
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                // A função de retorno obsoleta se aplica apenas ao documento principal.
                if (!reportConnectionError(view, errorCode)) super.onReceivedError(view, errorCode, description, failingUrl)
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.cancel()
                reportBlocked()
            }
        }
    }
    val chromeClient = remember {
        object : AccompanistWebChromeClient() {
            override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                if (disposed.get()) return true
                if (currentSourcePolicy.value?.invoke(message.sourceId()) == false) return true
                val callback = currentConsole.value ?: return super.onConsoleMessage(message)
                callback(message.message())
                return true
            }
        }
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
