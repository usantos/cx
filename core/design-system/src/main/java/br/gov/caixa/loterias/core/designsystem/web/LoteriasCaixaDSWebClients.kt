package br.gov.caixa.loterias.core.designsystem.web

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Handler
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.compose.runtime.State
import com.kevinnzou.web.AccompanistWebChromeClient
import com.kevinnzou.web.AccompanistWebViewClient
import java.util.concurrent.atomic.AtomicBoolean

/** Clients retain the live Compose callback holders for native events and queued posts. */
internal fun loteriasCaixaDSWebViewClient(
    currentUrlPolicy: State<((String) -> Boolean)?>,
    currentBlocked: State<(() -> Unit)?>,
    currentConnectionError: State<(() -> Unit)?>,
    mainHandler: Handler,
    disposed: AtomicBoolean,
): AccompanistWebViewClient = object : AccompanistWebViewClient() {
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
            BLOCKED_RESPONSE_STATUS,
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

internal fun loteriasCaixaDSWebChromeClient(
    currentConsole: State<((String) -> Unit)?>,
    currentSourcePolicy: State<((String) -> Boolean)?>,
    disposed: AtomicBoolean,
): AccompanistWebChromeClient = object : AccompanistWebChromeClient() {
    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
        if (disposed.get()) return true
        if (currentSourcePolicy.value?.invoke(message.sourceId()) == false) return true
        val callback = currentConsole.value ?: return super.onConsoleMessage(message)
        callback(message.message())
        return true
    }
}

private const val BLOCKED_RESPONSE_STATUS = 403
