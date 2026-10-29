// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.net.toUri
import br.gov.caixa.loterias.core.designsystem.html.CaixaDSHtmlDocument
import br.gov.caixa.loterias.core.designsystem.web.CaixaDSWebPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/** Eventos do navegador chegam pela interface do cliente Android; o Chromium continua sendo testado no dispositivo. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSWebClientJvmTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun htmlDocumentUsesTrustedBaseUrlAndReadOnlySettingsAndReleasesItsRenderer() {
        val visible = mutableStateOf(true)
        val html = "<html><body><b>Regulamento</b></body></html>"
        compose.setContent { if (visible.value) CaixaDSHtmlDocument(html) }
        lateinit var web: WebView
        compose.runOnIdle {
            web = webView()
            val loaded = requireNotNull(shadowOf(web).lastLoadDataWithBaseURL)
            assertEquals(html, loaded.data)
            assertEquals(html, web.tag)
            assertEquals("https://loterias.caixa.gov.br/", loaded.baseUrl)
            assertEquals("text/html", loaded.mimeType)
            assertEquals("UTF-8", loaded.encoding)
            assertFalse(web.settings.javaScriptEnabled)
            assertEquals(android.graphics.Color.TRANSPARENT, shadowOf(web).backgroundColor)
            visible.value = false
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertTrue(shadowOf(web).wasDestroyCalled())
            assertEquals("about:blank", shadowOf(web).lastLoadedUrl)
            assertNull(find(compose.activity.window.decorView))
        }
    }

    @Test
    fun transportErrorsReportFailuresWhileTlsAndOtherErrorsNeverBecomeConnectionFailures() {
        var failures = 0
        compose.setContent { CaixaDSWebPage("https://trusted.test/", onConnectionError = { failures++ }) }
        compose.runOnIdle {
            val web = webView()
            assertEquals("https://trusted.test/", shadowOf(web).lastLoadedUrl)
            val client = web.webViewClient
            listOf(WebViewClient.ERROR_HOST_LOOKUP, WebViewClient.ERROR_CONNECT, WebViewClient.ERROR_TIMEOUT, WebViewClient.ERROR_IO)
                .forEach { client.onReceivedError(web, it, "transport", "https://trusted.test/") }
            assertEquals(4, failures)
            listOf(
                WebViewClient.ERROR_FAILED_SSL_HANDSHAKE,
                WebViewClient.ERROR_AUTHENTICATION,
                WebViewClient.ERROR_BAD_URL,
                WebViewClient.ERROR_UNKNOWN
            ).forEach { client.onReceivedError(web, it, "nontransport", "https://trusted.test/") }
            assertEquals(4, failures)
        }
    }

    @Test
    fun navigationPolicyAppliesToBothCallbackOverloadsAndRequestInterception() {
        var blocks = 0
        compose.setContent {
            CaixaDSWebPage("https://trusted.test/", isUrlAllowed = {
                it.startsWith("https://trusted.test/")
            }, onNavigationBlocked = { blocks++ })
        }
        compose.runOnIdle {
            val web = webView()
            val client = web.webViewClient
            assertFalse(client.shouldOverrideUrlLoading(web, "https://trusted.test/next"))
            assertTrue(client.shouldOverrideUrlLoading(web, "file:///private/data"))
            assertFalse(client.shouldOverrideUrlLoading(web, Request("https://trusted.test/next")))
            assertTrue(client.shouldOverrideUrlLoading(web, Request("https://evil.test/")))
            assertNull(client.shouldInterceptRequest(web, Request("https://trusted.test/script.js", false)))
            val blockedResource = client.shouldInterceptRequest(web, Request("https://evil.test/script.js", false))
            assertNotNull(blockedResource)
            assertEquals(403, blockedResource?.statusCode)
            assertEquals(-1, blockedResource?.data?.read())
            assertEquals(403, client.shouldInterceptRequest(web, Request("https://evil.test/"))?.statusCode)
            client.onPageStarted(web, "https://evil.test/", null)
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(4, blocks) }
    }

    @Test
    fun consoleSourcePolicyAndLatestConsumerCallbackArePreservedAcrossRecomposition() {
        val revision = mutableStateOf(0)
        val events = mutableListOf<String>()
        compose.setContent {
            val current = revision.value
            CaixaDSWebPage(
                "https://trusted.test/",
                javaScriptEnabled = true,
                isConsoleSourceAllowed = { it.startsWith("https://trusted.test/") },
                onConsoleMessage = { events.add("$current:$it") }
            )
        }
        compose.runOnIdle {
            val web = webView()
            assertTrue(web.settings.javaScriptEnabled)
            val chrome = requireNotNull(web.webChromeClient)
            chrome.onConsoleMessage(ConsoleMessage("FORGED", "https://evil.test/script.js", 1, ConsoleMessage.MessageLevel.LOG))
            chrome.onConsoleMessage(ConsoleMessage("MISSING", "", 1, ConsoleMessage.MessageLevel.LOG))
            assertTrue(events.isEmpty())
            chrome.onConsoleMessage(ConsoleMessage("FIRST", "https://trusted.test/script.js", 1, ConsoleMessage.MessageLevel.LOG))
            revision.value = 1
        }
        compose.waitForIdle()
        compose.runOnIdle {
            requireNotNull(
                webView().webChromeClient
            ).onConsoleMessage(ConsoleMessage("NEXT", "https://trusted.test/script.js", 1, ConsoleMessage.MessageLevel.LOG))
            assertEquals(listOf("0:FIRST", "1:NEXT"), events)
        }
    }

    @Test
    fun safeSettingsAndDisposalPreventLateCallbacksAndReleaseTheView() {
        val visible = mutableStateOf(true)
        var errors = 0
        var blocks = 0
        val events = mutableListOf<String>()
        compose.setContent {
            if (visible.value) {
                CaixaDSWebPage(
                    "https://trusted.test/",
                    onConsoleMessage = { events.add(it) },
                    isUrlAllowed = {
                        it.startsWith("https://trusted.test/")
                    },
                    onConnectionError = { errors++ },
                    onNavigationBlocked = { blocks++ }
                )
            }
        }
        lateinit var web: WebView
        compose.runOnIdle {
            web = webView()
            assertFalse(web.settings.javaScriptEnabled)
            assertFalse(web.settings.allowFileAccess)
            assertFalse(web.settings.allowContentAccess)
            assertEquals(android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW, web.settings.mixedContentMode)
            assertFalse(web.settings.supportMultipleWindows())
            visible.value = false
        }
        compose.waitForIdle()
        compose.runOnIdle {
            web.webViewClient.onReceivedError(web, WebViewClient.ERROR_CONNECT, "late", "https://trusted.test/")
            web.webViewClient.shouldOverrideUrlLoading(web, "https://evil.test/")
            requireNotNull(
                web.webChromeClient
            ).onConsoleMessage(ConsoleMessage("LATE", "https://trusted.test/", 1, ConsoleMessage.MessageLevel.LOG))
            assertEquals(0, errors)
            assertTrue(events.isEmpty())
            assertTrue(shadowOf(web).wasDestroyCalled())
            assertEquals("about:blank", shadowOf(web).lastLoadedUrl)
            assertNull(find(compose.activity.window.decorView))
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(0, blocks) }
    }

    private fun webView(): WebView = requireNotNull(find(compose.activity.window.decorView))

    private fun find(view: View): WebView? {
        if (view is WebView) return view
        if (view is ViewGroup) for (index in 0 until view.childCount) find(view.getChildAt(index))?.let { return it }
        return null
    }

    private class Request(private val address: String, private val mainFrame: Boolean = true) : WebResourceRequest {
        override fun getUrl(): Uri = address.toUri()
        override fun isForMainFrame(): Boolean = mainFrame
        override fun isRedirect(): Boolean = false
        override fun hasGesture(): Boolean = false
        override fun getMethod(): String = "GET"
        override fun getRequestHeaders(): Map<String, String> = emptyMap()
    }
}
