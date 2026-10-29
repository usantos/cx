package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import java.util.Locale
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CatalogPreviewsTest {
    @get:Rule val compose = createComposeRule()

    private val previousLocale: Locale = Locale.getDefault()

    @Before
    fun useBrazilianCurrency() {
        Locale.setDefault(Locale.forLanguageTag("pt-BR"))
    }

    @After
    fun restoreLocale() {
        Locale.setDefault(previousLocale)
    }

    @Test
    fun everyRegisteredPreviewLaysOutAndItsSemanticActionsUpdateLocalState() {
        val selected = mutableStateOf(CatalogRegistry.entries.first())
        val state = mutableStateOf(PreviewState())
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Column(Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("preview")) {
                        catalogPreviews.getValue(selected.value.id)(state.value)
                    }
                }
            }
        }
        CatalogRegistry.entries.forEach { entry ->
            compose.runOnIdle {
                selected.value = entry
                state.value = PreviewState()
            }
            compose.onNodeWithTag("preview").assertIsDisplayed()
            val bounds = compose.onNodeWithTag("preview").fetchSemanticsNode().boundsInRoot
            assertTrue("${entry.id} must fit a scrollable preview", bounds.width > 0 && bounds.height > 0)
            // A primeira passagem abre exemplos transitórios; a segunda edita e exercita suas ações reais.
            repeat(2) {
                editAvailableInputs(entry, state.value)
                exerciseAvailableActions(entry, state.value)
            }
            compose.runOnIdle { state.value.show = false }
        }
    }

    private fun editAvailableInputs(entry: CatalogEntry, state: PreviewState) {
        val numeric = entry.name == "CaixaDSRapidAmountDialog"
        val input = if (numeric) "1234" else "CAIXA"
        val expected = if (numeric) "R$\u00a012,34" else "CAIXA"
        val inputs = compose.onAllNodes(hasSetTextAction()).fetchSemanticsNodes()
        inputs.forEach { node ->
            compose.runOnIdle {
                val changed = node.config[SemanticsActions.SetText].action?.invoke(AnnotatedString(input))
                assertEquals("${entry.id}: input action must be accepted", true, changed)
                assertTrue(
                    "${entry.id}: expected <$expected>, text=<${state.text}>, richText=<${state.richText.text}>",
                    state.text == expected || state.richText.text == expected
                )
            }
        }
    }

    private fun exerciseAvailableActions(entry: CatalogEntry, state: PreviewState) {
        val ids = compose.onAllNodes(hasClickAction()).fetchSemanticsNodes().map { it.id }
        ids.forEach { id ->
            // Fechar pode remover todos os nós do popup. Atualiza em vez de invocar funções de retorno desvinculadas.
            val current = compose.onAllNodes(hasClickAction()).fetchSemanticsNodes().find { it.id == id }
            if (current != null) {
                val previousCount = state.count
                try {
                    compose.onNode(SemanticsMatcher("${entry.id}: current node $id") { it.id == id })
                        .performSemanticsAction(SemanticsActions.OnClick) { it() }
                } catch (failure: IllegalStateException) {
                    throw IllegalStateException("${entry.id}: action on node $id failed", failure)
                }
                compose.runOnIdle {
                    assertTrue("${entry.id}: interactions must stay local and monotonic", state.count >= previousCount)
                }
            }
        }
    }
}
