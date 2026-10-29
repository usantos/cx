package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeV2CoordinationUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun replacementActionsAndContestReachClicksWithoutReloadingPreferences() {
        val calls = mutableListOf<String>()
        val preferences = Preferences()
        val actions = mutableStateOf<HomeV2Presentation.Actions>(Actions("old", calls))
        val lottery = mutableStateOf(lottery("megasena", 4321))
        compose.setContent {
            HomeV2TestContent(listOf(lottery.value), false, emptyMap(), preferences, actions.value)
        }
        compose.onNodeWithText("Aposte").performClick()
        compose.runOnIdle {
            actions.value = Actions("new", calls)
            lottery.value = lottery("megasena", 9876)
        }
        compose.onNodeWithText("Aposte").performClick()
        compose.onNodeWithContentDescription("Notificações").performClick()
        compose.onNodeWithText("Carrinho").performClick()
        compose.runOnIdle {
            assertEquals(listOf("old:bet:4321", "new:bet:9876", "new:notifications", "new:cart"), calls)
            assertEquals(1, preferences.loads)
        }
    }

    @Test fun longPressExpandsAndOnlyCompletedDragPersistsOrder() {
        val preferences = Preferences()
        val actions = Actions("home", mutableListOf())
        compose.setContent { HomeV2TestContent(lotteries(), false, emptyMap(), preferences, actions) }
        card("Mega-Sena").performScrollTo()
        val from = card("Mega-Sena").fetchSemanticsNode().boundsInRoot.center
        val to = card("+Milionária").fetchSemanticsNode().boundsInRoot.center

        compose.onRoot().performTouchInput {
            down(from)
            advanceEventTime(700)
            moveTo(to, delayMillis = 200)
            cancel()
        }
        compose.onNodeWithText("Ver menos  ›").assertExists()
        compose.runOnIdle { assertTrue(preferences.saved.isEmpty()) }

        compose.onRoot().performTouchInput {
            down(from)
            advanceEventTime(700)
            moveTo(to, delayMillis = 200)
            up()
        }
        compose.runOnIdle {
            assertEquals(listOf("MAIS_MILIONARIA,MEGA_SENA,LOTECA,DIA_DE_SORTE,DUPLA_SENA,LOTOMANIA,QUINA,TIMEMANIA,LOTOFACIL,SUPER_7"), preferences.saved)
        }
        val first = card("+Milionária").fetchSemanticsNode().boundsInRoot
        val second = card("Mega-Sena").fetchSemanticsNode().boundsInRoot
        assertTrue(first.left < second.left)
        assertEquals(first.top, second.top, 1f)
    }

    @Test fun loadingKeepsNavigationAndRestoredGridDoesNotReorderCarousel() {
        val calls = mutableListOf<String>()
        val preferences = Preferences("QUINA,MEGA_SENA")
        val loading = mutableStateOf(true)
        val actions = Actions("home", calls)
        compose.setContent { HomeV2TestContent(lotteries(), loading.value, emptyMap(), preferences, actions) }
        compose.onNodeWithContentDescription("Carregando a home").assertExists()
        compose.onNodeWithText("Carrinho").performClick()
        compose.runOnIdle { loading.value = false }
        compose.onNodeWithContentDescription("Carregando a home").assertDoesNotExist()
        compose.onNodeWithText("Concurso 4321").assertIsDisplayed()
        card("Quina").performScrollTo()
        val first = card("Quina").fetchSemanticsNode().boundsInRoot
        val second = card("Mega-Sena").fetchSemanticsNode().boundsInRoot
        assertTrue(first.left < second.left)
        assertEquals(first.top, second.top, 1f)
        compose.runOnIdle {
            assertEquals(listOf("home:cart"), calls)
            assertEquals(1, preferences.loads)
            assertTrue(preferences.saved.isEmpty())
        }
    }

    private fun card(name: String) = compose.onNode(SemanticsMatcher("Apostar em $name") {
        it.config.getOrNull(SemanticsActions.OnClick)?.label == "Apostar em $name"
    })

    private class Preferences(private var order: String? = null) : HomeLayoutRepository {
        var loads = 0
        val saved = mutableListOf<String>()
        override suspend fun loadOrder(): String? {
            loads++
            return order
        }
        override suspend fun saveOrder(order: String) {
            this.order = order
            saved += order
        }
    }

    private class Actions(private val owner: String, private val calls: MutableList<String>) : HomeV2Presentation.Actions {
        override fun bet(modalidade: HomeLottery) {
            calls += "$owner:bet:${modalidade.contestNumber}"
        }
        override fun pool(modalidade: HomeLottery) {
            calls += "$owner:pool"
        }
        override fun results() {
            calls += "$owner:results"
        }
        override fun modalityResults(modalidade: HomeV2Modality) {
            calls += "$owner:${modalidade.resultCode}"
        }
        override fun games() {
            calls += "$owner:games"
        }
        override fun favorites() {
            calls += "$owner:favorites"
        }
        override fun menu() {
            calls += "$owner:menu"
        }
        override fun notifications() {
            calls += "$owner:notifications"
        }
        override fun cart() {
            calls += "$owner:cart"
        }
        override fun profile() {
            calls += "$owner:profile"
        }
        override fun canPool(modalidade: HomeV2Modality) = false
    }

    private fun lotteries() = listOf(
        "megasena", "maismilionaria", "loteca", "diadesorte",
        "duplasena", "lotomania", "quina", "timemania", "lotofacil", "supersete"
    )
        .mapIndexed { index, code -> lottery(code, 4321 + index) }

    private fun lottery(code: String, contest: Int) = HomeLottery(
        2, "NORMAL", code, "Mega-Sena", contest,
        BigDecimal("100000000"), BigDecimal("6"), "2026-10-15T21:00:00", "", true,
        HomeLotteryKind.LOTTERY, CaixaDSColorTokens.MegaSenaLight,
        CaixaDSColorTokens.MegaSenaDark, CaixaDSColorTokens.White,
        br.gov.caixa.loterias.feature.home.R.drawable.trevo_megasena_fundo_branco,
        br.gov.caixa.loterias.feature.home.R.drawable.ic_calendario,
        br.gov.caixa.loterias.feature.home.R.drawable.ic_calendario
    )
}
