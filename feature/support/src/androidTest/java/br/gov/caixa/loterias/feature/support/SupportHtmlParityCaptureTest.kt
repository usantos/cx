package br.gov.caixa.loterias.feature.support

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.support.SupportQuestion
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Local HTML only. Native WebView tag verifies the actual content supplied to rendering. */
class SupportHtmlParityCaptureTest {
    @get:Rule val compose = createComposeRule()

    @Test fun drawerReadOnlyTermsUseMappedTermosAceiteFontWrapper() {
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            SupportScreen(SupportUiState(mode = SupportMode.TERMS_READ_ONLY, htmlContent = HTML), { error("No external action allowed") })
        } }
        compose.onNodeWithTag("support_terms_document").assertExists()
        assertTrue(document().contains("@font-face"))
        assertTrue(document().contains(HTML))
        capture("terms-read-only")
    }

    @Test fun termsUsoAcceptancePreservesUnmodifiedHtmlWithoutAccepting() {
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            SupportScreen(SupportUiState(selectedTab = SupportTab.TERMS, acceptanceRequired = true,
                htmlContent = HTML, termsAccepted = false), { error("No acceptance allowed") })
        } }
        compose.onNodeWithText("Continuar").assertIsNotEnabled()
        val document = document()
        assertEquals(HTML, document)
        assertTrue(document.contains(HTML))
        capture("terms-acceptance-unchecked")
    }

    @Test fun faqClosedExpandedLoadingAndFailureRemainLocal() {
        val state = mutableStateOf(SupportUiState(selectedSectionId = 1, selectedSectionTitle = "PERGUNTAS",
            questions = listOf(SupportQuestion(1, "Pergunta de Teste", "Resposta sintética de suporte, sem informações pessoais."))))
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            SupportScreen(state.value, { action ->
                if (action is SupportAction.ToggleQuestion) state.value = state.value.copy(expandedQuestionIds = setOf(action.id))
                else error("No external support action allowed")
            })
        } }
        compose.onNodeWithText("Pergunta de Teste").assertIsDisplayed()
        capture("faq-closed", false)
        compose.onNodeWithText("Pergunta de Teste").performClick()
        compose.onNodeWithText("Resposta sintética de suporte, sem informações pessoais.").assertIsDisplayed()
        capture("faq-expanded", false)
        compose.runOnIdle { state.value = state.value.copy(isLoading = true) }
        compose.onNodeWithText("Carregando…").assertIsDisplayed()
        capture("faq-loading", false)
        compose.runOnIdle { state.value = state.value.copy(isLoading = false, errorMessage = "Falha controlada de conteúdo") }
        compose.onNodeWithText("Falha controlada de conteúdo").assertIsDisplayed()
        capture("faq-failure", false)
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
        if (view is ViewGroup) for (index in 0 until view.childCount) {
            findWebView(view.getChildAt(index))?.let { return it }
        }
        return null
    }

    private fun capture(name: String, html: Boolean = true) {
        if (html) compose.waitUntil(timeoutMillis = 10_000) {
            var loaded = false
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val activity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).first()
                loaded = findWebView(activity.window.decorView)?.progress == 100
            }
            loaded
        }
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = File(requireNotNull(instrumentation.targetContext.externalCacheDir), "remaining-support-parity").apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(directory, "$name.png").outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        bitmap.recycle()
    }

    private companion object {
        const val HTML = "<h1>Documento de Teste</h1><p>Conteúdo sintético de termos, sem dados pessoais.</p><p><b>Texto forte</b> e <i>texto em itálico</i>.</p>"
    }
}
