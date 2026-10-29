package br.gov.caixa.loterias.feature.support

import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.support.presentation.screen.ReadOnlyTermsScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

/** Dados locais de teste no provedor real de WebView; nunca habilita JavaScript para um documento remoto. */
class SupportTermsReflowUiTest {
    @get:Rule val compose = createComposeRule()
    private val url = "https://www.loteriasonline.caixa.gov.br/silce-web/consulta/resultados/modalidades"

    @Test fun longTermsLinkReflowsAtDoubleFontWithoutChangingItsDestination() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                CaixaDSTheme {
                    ReadOnlyTermsScreen(SupportUiState(htmlContent = "<p><a href='$url'>$url</a></p>"), {}, Modifier)
                }
            }
        }
        compose.waitUntil(PROVIDER_TIMEOUT_MS) {
            var ready = false
            onMain { ready = webView()?.progress == COMPLETE_PAGE_PROGRESS }
            ready
        }
        onMain {
            val web = requireNotNull(webView())
            assertEquals(LARGE_TEXT_ZOOM, web.settings.textZoom)
            assertFalse(web.settings.javaScriptEnabled)
            web.settings.javaScriptEnabled = true // Medição do DOM exclusiva dos dados de teste.
        }
        assertEquals("true", evaluate("document.documentElement.scrollWidth <= window.innerWidth + 1"))
        assertEquals("true", evaluate("document.querySelector('a').href === '$url'"))
    }

    private fun evaluate(script: String): String {
        var result: String? = null
        onMain { requireNotNull(webView()).evaluateJavascript(script) { result = it } }
        compose.waitUntil(PROVIDER_TIMEOUT_MS) { result != null }
        return requireNotNull(result)
    }

    private fun onMain(block: () -> Unit) = InstrumentationRegistry.getInstrumentation().runOnMainSync(block)

    private fun webView(): WebView? {
        val activity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).first()
        return find(activity.window.decorView)
    }

    private fun find(view: View): WebView? {
        if (view is WebView) return view
        if (view is ViewGroup) for (index in 0 until view.childCount) find(view.getChildAt(index))?.let { return it }
        return null
    }
}

private const val PROVIDER_TIMEOUT_MS = 10_000L
private const val COMPLETE_PAGE_PROGRESS = 100

private const val LARGE_TEXT_ZOOM = 200
