package br.gov.caixa.loterias.core.designsystem

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.ConsoleMessage
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.gov.caixa.loterias.core.designsystem.web.CaixaDSWebPage
import java.util.concurrent.CopyOnWriteArrayList
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CaixaDSWebPageTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val page = "data:text/html," + Uri.encode("<html><body>DS<script>console.log('DS_PAGE_READY')</script></body></html>")

    @Test fun connectionFailureReportsTransportErrorsWithoutMaskingTlsErrors() {
        var failures = 0
        compose.setContent { CaixaDSWebPage(page, onConnectionError = { failures++ }) }
        compose.waitUntil(10_000) {
            var ready = false
            compose.runOnIdle { ready = find(compose.activity.window.decorView)?.progress == 100 }
            ready
        }
        compose.runOnIdle {
            val web = requireNotNull(find(compose.activity.window.decorView))
            @Suppress("DEPRECATION")
            web.webViewClient.onReceivedError(web, android.webkit.WebViewClient.ERROR_FAILED_SSL_HANDSHAKE, "fixture", "https://queue.filabra.com/")
            assertEquals(0, failures)
            @Suppress("DEPRECATION")
            web.webViewClient.onReceivedError(web, android.webkit.WebViewClient.ERROR_HOST_LOOKUP, "fixture", "https://queue.filabra.com/")
            assertEquals(1, failures)
        }
    }

    @Test fun restrictedConsumerRejectsExternalNavigationAndConsoleOrigins() {
        val events = CopyOnWriteArrayList<String>()
        val blocked = CopyOnWriteArrayList<Boolean>()
        compose.setContent {
            CaixaDSWebPage(page, Modifier.fillMaxSize(), onConsoleMessage = { events.add(it) },
                isUrlAllowed = { it == page || it.startsWith("https://queue.filabra.com/") },
                isConsoleSourceAllowed = { it.startsWith("https://queue.filabra.com/") },
                onNavigationBlocked = { blocked += true })
        }
        compose.waitUntil(10_000) {
            var loaded = false
            compose.runOnIdle { loaded = find(compose.activity.window.decorView)?.progress == 100 }
            loaded
        }
        compose.runOnIdle {
            val web = requireNotNull(find(compose.activity.window.decorView))
            val chrome = requireNotNull(web.webChromeClient)
            chrome.onConsoleMessage(ConsoleMessage("FORGED", "https://evil.test/queue.js", 1, ConsoleMessage.MessageLevel.LOG))
            chrome.onConsoleMessage(ConsoleMessage("MISSING_ORIGIN", "", 1, ConsoleMessage.MessageLevel.LOG))
            assertTrue(events.isEmpty())
            chrome.onConsoleMessage(ConsoleMessage("TRUSTED", "https://queue.filabra.com/queue.js", 1, ConsoleMessage.MessageLevel.LOG))
            assertEquals(listOf("TRUSTED"), events.toList())
            assertTrue(web.webViewClient.shouldOverrideUrlLoading(web, "https://evil.test/"))
            assertTrue(web.webViewClient.shouldOverrideUrlLoading(web, "file:///etc/passwd"))
            assertFalse(web.webViewClient.shouldOverrideUrlLoading(web, "https://queue.filabra.com/next"))
        }
        compose.waitUntil(10_000) { blocked.size == 2 }
    }

    @Test fun interactiveRendererReportsTextEventsAndUsesTheLatestConsumerCallback() {
        val events = CopyOnWriteArrayList<String>()
        val second = mutableStateOf(false)
        val visible = mutableStateOf(true)
        compose.setContent {
            if (visible.value) CaixaDSWebPage(page, Modifier.fillMaxSize(), javaScriptEnabled = true,
                captureBackPresses = false, onConsoleMessage = if (second.value) {
                    { events.add("next:$it") }
                } else { { events.add("first:$it") } })
        }
        compose.waitUntil(10_000) { events.contains("first:DS_PAGE_READY") }
        compose.runOnIdle { second.value = true }
        compose.waitForIdle()
        compose.runOnIdle {
            val web = requireNotNull(find(compose.activity.window.decorView))
            assertTrue(web.settings.javaScriptEnabled)
            web.evaluateJavascript("console.log('NEXT')", null)
        }
        compose.waitUntil(10_000) { events.contains("next:NEXT") }
        compose.runOnIdle { visible.value = false }
        compose.waitForIdle()
        compose.runOnIdle { assertNull(find(compose.activity.window.decorView)) }
    }

    @Test fun javascriptIsDisabledUnlessTheConsumerExplicitlyRequestsIt() {
        val events = CopyOnWriteArrayList<String>()
        compose.setContent { CaixaDSWebPage(page, Modifier.fillMaxSize(), onConsoleMessage = { events.add(it) }) }
        compose.waitUntil(10_000) {
            var ready = false
            compose.runOnIdle { ready = find(compose.activity.window.decorView)?.progress == 100 }
            ready
        }
        compose.runOnIdle { assertFalse(requireNotNull(find(compose.activity.window.decorView)).settings.javaScriptEnabled) }
        assertFalse(events.contains("DS_PAGE_READY"))
    }

    private fun find(view: View): WebView? {
        if (view is WebView) return view
        if (view is ViewGroup) for (index in 0 until view.childCount) find(view.getChildAt(index))?.let { return it }
        return null
    }
}
