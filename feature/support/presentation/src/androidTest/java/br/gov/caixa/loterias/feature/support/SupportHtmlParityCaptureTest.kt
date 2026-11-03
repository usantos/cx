package br.gov.caixa.loterias.feature.support

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.support.presentation.screen.SupportScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMode
import br.gov.caixa.loterias.feature.support.presentation.state.SupportTab
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Somente HTML local. A tag nativa da WebView verifica o conteúdo real fornecido à renderização. */
class SupportHtmlParityCaptureTest {
    @get:Rule val compose = createComposeRule()

    @Test fun drawerReadOnlyTermsUseMappedTermosAceiteFontWrapper() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                SupportScreen(SupportUiState(mode = SupportMode.TERMS_READ_ONLY, htmlContent = HTML), {
                    error("No external action allowed")
                }, faqContent = { _, _ -> })
            }
        }
        compose.onNodeWithTag("support_terms_document").assertExists()
        assertTrue(document().contains("@font-face"))
        assertTrue(document().contains(HTML))
        capture("terms-read-only")
    }

    @Test fun termsUsoAcceptancePreservesUnmodifiedHtmlWithoutAccepting() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                SupportScreen(
                    SupportUiState(
                        selectedTab = SupportTab.TERMS,
                        acceptanceRequired = true,
                        htmlContent = HTML,
                        termsAccepted = false
                    ),
                    { error("No acceptance allowed") },
                    faqContent = { _, _ -> }
                )
            }
        }
        compose.onNodeWithText("Continuar").assertIsNotEnabled()
        val document = document()
        assertEquals(HTML, document)
        assertTrue(document.contains(HTML))
        capture("terms-acceptance-unchecked")
    }

    private fun document(): String {
        var result = ""
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val activity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).first()
            result = requireNotNull(findWebView(activity.window.decorView)).tag as String
        }
        return result
    }

    private fun findWebView(view: View): WebView? {
        if (view is WebView) return view
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                findWebView(view.getChildAt(index))?.let { return it }
            }
        }
        return null
    }

    private fun capture(name: String, html: Boolean = true) {
        if (html) {
            compose.waitUntil(timeoutMillis = 10_000) {
                var loaded = false
                InstrumentationRegistry.getInstrumentation().runOnMainSync {
                    val activity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).first()
                    loaded = findWebView(activity.window.decorView)?.progress == 100
                }
                loaded
            }
        }
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = File(requireNotNull(instrumentation.targetContext.externalCacheDir), "remaining-support-parity").apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(directory, "$name.png").outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        bitmap.recycle()
    }

    private companion object {
        const val HTML = "<h1>Documento de Teste</h1><p>Conteúdo sintético de termos, sem dados pessoais.</p>" +
            "<p><b>Texto forte</b> e <i>texto em itálico</i>.</p>"
    }
}
