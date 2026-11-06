// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.queue

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.gov.caixa.loterias.core.queue.android.FilaBraQueueWebView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class QueueWebViewLifecycleTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val location = "https://caixa.filabra.com/queue?qfqid=test"
    private fun externalActivity() = Robolectric.buildActivity(Activity::class.java).setup().get()
    private fun broadcasts() = shadowOf(RuntimeEnvironment.getApplication()).broadcastIntents.filter {
        it.action?.endsWith("FILABRA_BROADCAST") ==
            true
    }
    private fun findWebView(view: View): WebView? = if (view is WebView) {
        view
    } else if (view is ViewGroup) {
        (0 until view.childCount).firstNotNullOfOrNull { findWebView(view.getChildAt(it)) }
    } else {
        null
    }
    private fun webView(): WebView = requireNotNull(findWebView(compose.activity.window.decorView))
    private fun console(view: WebView, message: String, source: String = location) {
        shadowOf(view).webChromeClient.onConsoleMessage(ConsoleMessage(message, source, 1, ConsoleMessage.MessageLevel.LOG))
    }

    @Test fun `invalid initial address fails closed broadcasts one error and finishes host`() {
        val activity = externalActivity()
        compose.setContent { FilaBraQueueWebView("http://evil.example/queue", activity) }
        compose.waitForIdle()
        assertTrue(activity.isFinishing)
        assertEquals(listOf("ERROR"), broadcasts().map { it.getStringExtra("result") })
    }

    @Test fun `trusted console sends queue join and immediate pass while foreign console is ignored`() {
        val activity = externalActivity()
        compose.setContent { FilaBraQueueWebView(location, activity) }
        compose.runOnIdle {
            val view = webView()
            assertEquals("$location&qfnoredirect=true", shadowOf(view).lastLoadedUrl)
            console(view, "JOIN {\"request\":8}", "https://evil.example")
            assertTrue(broadcasts().isEmpty())
            console(view, "JOIN {\"request\":8}")
            assertEquals(8, broadcasts().single().getIntExtra("request", -1))
            console(
                view,
                "REDIRECT qfpt {\"target\":\"https://mobileloterias.caixa.gov.br/?qfqid=test\",\"type\":\"passed\",\"when\":0,\"pl\":5}"
            )
        }
        compose.waitForIdle()
        assertTrue(activity.isFinishing)
        assertEquals(listOf("JOIN", "SUCCESS"), broadcasts().map { it.getStringExtra("result") })
    }

    @Test fun `blocked navigation invalidates the console and repeated rejections emit no additional events`() {
        val activity = externalActivity()
        compose.setContent { FilaBraQueueWebView("https://caixa.filabra.com/queue", activity) }
        lateinit var view: WebView
        compose.runOnIdle {
            view = webView()
            assertEquals("https://caixa.filabra.com/queue?qfnoredirect=true", shadowOf(view).lastLoadedUrl)
            assertTrue(shadowOf(view).webViewClient.shouldOverrideUrlLoading(view, "https://evil.example"))
        }
        compose.waitForIdle()
        compose.runOnIdle {
            console(view, "JOIN {\"request\":9}")
            shadowOf(view).webViewClient.shouldOverrideUrlLoading(view, "https://evil.example/again")
        }
        compose.waitForIdle()
        assertTrue(activity.isFinishing)
        assertEquals(listOf("ERROR"), broadcasts().map { it.getStringExtra("result") })
    }

    @Test fun `connection failure notifies once and closes console processing until retry`() {
        val activity = externalActivity()
        var failures = 0
        compose.setContent { FilaBraQueueWebView(location, activity) { failures++ } }
        compose.runOnIdle {
            val view = webView()
            shadowOf(view).webViewClient.onReceivedError(view, WebViewClient.ERROR_CONNECT, "offline", location)
        }
        compose.waitForIdle()
        compose.runOnIdle {
            val view = webView()
            shadowOf(view).webViewClient.onReceivedError(view, WebViewClient.ERROR_CONNECT, "offline", location)
            console(view, "JOIN {\"request\":9}")
        }
        compose.waitForIdle()
        assertEquals(1, failures)
        assertTrue(broadcasts().isEmpty())
        assertFalse(activity.isFinishing)
    }

    @Test fun `Back broadcasts explicit abandonment and finishes queue host`() {
        val activity = externalActivity()
        compose.setContent { FilaBraQueueWebView(location, activity) }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        assertTrue(activity.isFinishing)
        assertEquals("ABANDON", broadcasts().single().getStringExtra("result"))
        assertEquals("Back", broadcasts().single().getStringExtra("cause"))
    }
}
