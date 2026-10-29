package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.presentation.route.ResultsRoute
import br.gov.caixa.loterias.feature.results.presentation.viewmodel.ResultsViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ResultsCoordinationUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private val models = mutableListOf<ResultsViewModel>()

    @After fun cleanup() { models.forEach { it.viewModelScope.cancel() } }

    @Test fun backClearsTheSelectedModalityBeforeLeavingResults() {
        val fixture = Fixture()
        var exits = 0
        compose.setContent {
            val model = remember { fixture.create() }
            CaixaDSTheme { ResultsRoute(model, onBack = { exits++ }) }
        }
        compose.onNodeWithText("quina").performScrollTo().performClick()
        compose.onNodeWithText("Concurso nº 100").assertExists()
        compose.runOnIdle {
            assertEquals("quina", fixture.model.state.value.modality)
            compose.activity.onBackPressedDispatcher.onBackPressed()
        }
        compose.onNodeWithText("Modalidades").assertIsDisplayed()
        compose.runOnIdle {
            assertNull(fixture.model.state.value.modality)
            assertEquals(0, exits)
        }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle { assertEquals(1, exits) }
    }

    @Test fun invalidSearchKeepsTheFieldAndLoadingClosesIt() {
        val fixture = Fixture()
        val response = CompletableDeferred<DrawResult>()
        fixture.pending = response
        compose.setContent {
            val model = remember { fixture.create().also { it.selectModality("megasena") } }
            CaixaDSTheme { ResultsRoute(model, onBack = {}, showDetail = true) }
        }
        compose.onNodeWithContentDescription("Pesquisar concurso").performClick()
        compose.onNode(hasSetTextAction()).assertTextContains("Número do concurso").performTextReplacement("999")
        compose.onNode(hasSetTextAction()).performImeAction()
        compose.onNodeWithText("O último concurso apurado é o 100").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNode(hasSetTextAction()).assertTextEquals("999")
        compose.runOnIdle { assertTrue(fixture.requests.isEmpty()) }

        compose.onNode(hasSetTextAction()).performTextReplacement("50")
        compose.onNode(hasSetTextAction()).performImeAction()
        compose.waitUntil { fixture.model.state.value.isLoading }
        compose.onNode(hasSetTextAction()).assertDoesNotExist()
        compose.onNodeWithContentDescription("Pesquisar concurso").assertExists()
        compose.runOnIdle {
            assertEquals(listOf("megasena:50"), fixture.requests)
            response.complete(draw("megasena", 50))
        }
        compose.waitUntil { !fixture.model.state.value.isLoading }
        compose.onNodeWithText("Concurso nº 50").assertExists()
    }

    @Test fun replacingTheViewModelUsesCurrentSearchAndBackCallbacks() {
        val first = Fixture(100)
        val second = Fixture(200)
        val selected = mutableIntStateOf(0)
        val exits = mutableListOf<Int>()
        compose.setContent {
            val old = remember { first.create().also { it.selectModality("megasena") } }
            val current = remember { second.create().also { it.selectModality("quina") } }
            val index = selected.intValue
            CaixaDSTheme {
                ResultsRoute(if (index == 0) old else current, onBack = { exits += index }, showDetail = true)
            }
        }
        compose.runOnIdle { selected.intValue = 1 }
        compose.onNodeWithText("Concurso nº 200").assertExists()
        compose.onNodeWithContentDescription("Pesquisar concurso").performClick()
        compose.runOnIdle {
            assertEquals("100", first.model.state.value.contestInput)
            assertEquals("", second.model.state.value.contestInput)
        }
        compose.onNode(hasSetTextAction()).performTextReplacement("180")
        compose.onNode(hasSetTextAction()).performImeAction()
        compose.runOnIdle { assertEquals(180, second.model.state.value.result?.contest) }
        compose.onNode(hasSetTextAction()).assertTextEquals("180")
        compose.onNodeWithContentDescription("Fechar pesquisa").performClick()
        compose.onNodeWithText("Concurso nº 180").assertExists()
        compose.onNodeWithText("Voltar").performClick()
        compose.runOnIdle {
            assertTrue(first.requests.isEmpty())
            assertEquals(listOf("quina:180"), second.requests)
            assertEquals(listOf(1), exits)
        }
    }

    private inner class Fixture(private val latest: Int = 100) {
        val requests = mutableListOf<String>()
        var pending: CompletableDeferred<DrawResult>? = null
        lateinit var model: ResultsViewModel

        fun create() = ResultsViewModel(
            getLatestResult = { modality -> AppResult.Success(draw(modality, latest)) },
            getResultByContest = { modality, contest ->
                requests += "$modality:$contest"
                AppResult.Success(pending?.await() ?: draw(modality, contest))
            },
            dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
        ).also { model = it; models += it }
    }

    private fun draw(modality: String, contest: Int) = DrawResult(
        modality, contest, "18 out 2026", "SÃO PAULO - SP", listOf(1, 2, 3, 4, 5, 6),
        emptyList(), emptyList(), false, "0", "100", "", listOf(DrawPayout("0001", 0, "0")),
    )
}
