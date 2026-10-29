package br.gov.caixa.loterias.core.designsystem

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import br.gov.caixa.loterias.core.designsystem.html.CaixaDSHtmlDocument
import br.gov.caixa.loterias.core.designsystem.web.CaixaDSWebPage
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val PROVIDER_TIMEOUT_MS = 10_000L
private const val COMPLETE_PAGE_PROGRESS = 100
private const val MINIMUM_ENLARGEMENT_RATIO = 1.8

/** Evidência do provedor nativo: sem conta, rede, JavaScript de servidor ou fluxo financeiro. */
class CaixaDSWebFontScaleTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val html = "<html lang='pt-BR'><meta name='viewport' content='width=device-width,initial-scale=1'>" +
        "<body><p id='sample' style='font-size:16px'>Texto acessível para leitura.</p></body></html>"

    @Test fun htmlDocumentTracksConfigurationScaleWithoutRecreatingScreen() = assertScaleTracksConfiguration(false)

    @Test fun urlDocumentTracksConfigurationScaleWithoutRecreatingScreen() = assertScaleTracksConfiguration(true)

    private fun assertScaleTracksConfiguration(urlRenderer: Boolean) {
        val scale = mutableStateOf(1f)
        compose.setContent {
            val parent = LocalContext.current
            val density = LocalDensity.current
            val configuration = Configuration(LocalConfiguration.current).apply { fontScale = scale.value }
            val context = parent.createConfigurationContext(configuration)
            CompositionLocalProvider(LocalContext provides context, LocalDensity provides Density(density.density, scale.value)) {
                if (urlRenderer) {
                    CaixaDSWebPage("data:text/html," + Uri.encode(html), Modifier.fillMaxSize())
                } else {
                    CaixaDSHtmlDocument(html, Modifier.fillMaxSize())
                }
            }
        }
        awaitReady()
        val first = renderedTextHeight()
        var zoomAtOne = 0
        compose.runOnIdle {
            zoomAtOne = requireNotNull(find(compose.activity.window.decorView)).settings.textZoom
            scale.value = 2f
        }
        compose.waitForIdle()
        awaitReady()
        val second = renderedTextHeight()
        var zoomAtTwo = 0
        compose.runOnIdle { zoomAtTwo = requireNotNull(find(compose.activity.window.decorView)).settings.textZoom }
        Log.i("CaixaDSFontScale", "url=$urlRenderer scale1 zoom=$zoomAtOne height=$first; scale2 zoom=$zoomAtTwo height=$second")
        assertTrue(
            "Rendered glyph height should grow at scale2: first=$first second=$second zoom=$zoomAtOne/$zoomAtTwo",
            second >= first * MINIMUM_ENLARGEMENT_RATIO
        )
    }

    private fun awaitReady() {
        compose.waitUntil(PROVIDER_TIMEOUT_MS) {
            var ready = false
            compose.runOnIdle { ready = find(compose.activity.window.decorView)?.progress == COMPLETE_PAGE_PROGRESS }
            ready
        }
    }

    private fun renderedTextHeight(): Double {
        var result: Double? = null
        compose.runOnIdle {
            val web = requireNotNull(find(compose.activity.window.decorView))
            // Medição apenas da fixture; preserva a política de JavaScript do DS em produção.
            web.settings.javaScriptEnabled = true
            web.evaluateJavascript(
                "(function(){var r=document.createRange();" +
                    "r.selectNodeContents(document.getElementById('sample'));" +
                    "return r.getBoundingClientRect().height;})()"
            ) {
                result = it.toDoubleOrNull()
            }
        }
        compose.waitUntil(PROVIDER_TIMEOUT_MS) { result != null }
        return requireNotNull(result)
    }

    private fun find(view: View): WebView? {
        if (view is WebView) return view
        if (view is ViewGroup) for (index in 0 until view.childCount) find(view.getChildAt(index))?.let { return it }
        return null
    }
}
