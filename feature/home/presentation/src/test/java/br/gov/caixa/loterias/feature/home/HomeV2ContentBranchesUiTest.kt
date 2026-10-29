// Literais numéricos descrevem entradas explícitas de teste e valores esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.components.HeroCarousel
import br.gov.caixa.loterias.feature.home.presentation.components.PoolBanner
import br.gov.caixa.loterias.feature.home.presentation.components.ReorderableModalities
import br.gov.caixa.loterias.feature.home.presentation.components.ResultCard
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Content
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class HomeV2ContentBranchesUiTest {
    @get:Rule val compose = createComposeRule()
    private val actions = Actions()
    private val presentation = HomeV2Presentation({ emptyList() }, { _, _ -> }) { actions }

    @Test fun loadingRetainsNavigationButDoesNotExposeBettingContent() {
        show {
            HomeV2Content(listOf(lottery()), true, emptyMap(), emptyList(), { _, _ ->
            }, actions, CaixaDSCampaign.DEFAULT, CaixaDSCampaign.DEFAULT)
        }
        compose.onNodeWithText("Carrinho").performClick()
        compose.onNodeWithText("Aposte").assertDoesNotExist()
        compose.onNodeWithText("Escolha sua modalidade").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("cart"), actions.calls) }
    }

    @Test fun loadingCanTransitionToRealContentWithoutRecreatingActions() {
        val loading = mutableStateOf(true)
        show {
            HomeV2Content(listOf(lottery()), loading.value, emptyMap(), listOf("MEGA_SENA"), { _, _ ->
            }, actions, CaixaDSCampaign.DEFAULT, CaixaDSCampaign.DEFAULT)
        }
        compose.runOnIdle { loading.value = false }
        compose.onNodeWithText("Escolha sua modalidade").assertExists()
        compose.onNodeWithText("Aposte").performClick()
        compose.runOnIdle { assertEquals(listOf("bet:megasena"), actions.calls) }
    }

    @Test fun navigationDispatchesGamesResultsFavoritesCartMenuAndNotifications() {
        show {
            HomeV2Content(emptyList(), true, emptyMap(), emptyList(), { _, _ ->
            }, actions, CaixaDSCampaign.DEFAULT, CaixaDSCampaign.DEFAULT)
        }
        compose.onNodeWithText("Minhas\nApostas").performClick()
        compose.onNodeWithText("Resultados").performClick()
        compose.onNodeWithText("Apostas\nFavoritas").performClick()
        compose.onNodeWithText("Carrinho").performClick()
        compose.onNodeWithText("☰").performClick()
        compose.onNodeWithContentDescription("Notificações").performClick()
        compose.runOnIdle { assertEquals(listOf("games", "results", "favorites", "cart", "menu", "notifications"), actions.calls) }
    }

    @Test fun heroOffersPoolOnlyWhenActionsAllowThatModality() {
        actions.pools = true
        show { presentation.HeroCarousel(listOf(lottery())) }
        compose.onNodeWithText("Compre seu bolão").performClick()
        compose.onNodeWithText("Aposte").performClick()
        compose.onNodeWithText("Último resultado ›").performClick()
        compose.runOnIdle { assertEquals(listOf("pool:megasena", "bet:megasena", "modality:MEGA_SENA"), actions.calls) }
    }

    @Test fun heroWithoutPoolPermissionOnlyOffersIndividualBet() {
        show { presentation.HeroCarousel(listOf(lottery())) }
        compose.onNodeWithText("Compre seu bolão").assertDoesNotExist()
        compose.onNodeWithText("Prêmio estimado").assertExists()
        compose.onNodeWithText("Concurso 123").assertExists()
    }

    @Test fun heroCarouselUsesAtMostFourCards() {
        show {
            presentation.HeroCarousel(
                listOf(lottery(), lottery(9, "maismilionaria"), lottery(3, "quina"), lottery(8, "lotofacil"), lottery(7, "supersete"))
            )
        }
        compose.onNodeWithText("Mega-Sena").assertExists()
        repeat(4) { compose.onNode(hasScrollAction()).performTouchInput { swipeLeft() } }
        compose.onNodeWithText("Lotofácil").assertExists()
        compose.onNodeWithText("Super Sete").assertDoesNotExist()
    }

    @Test fun emptyHeroAndPoolBannerDoNotInventLotteryActions() {
        show {
            Column {
                presentation.HeroCarousel(emptyList())
                presentation.PoolBanner(null)
            }
        }
        compose.onNodeWithText("Aposte").assertDoesNotExist()
        compose.onNodeWithText("Compre seu bolão").assertDoesNotExist()
    }

    @Test fun poolBannerReportsTheSelectedLottery() {
        show { presentation.PoolBanner(lottery(8, "lotofacil")) }
        compose.onNodeWithText("Compre seu bolão").performClick()
        compose.runOnIdle { assertEquals(listOf("pool:lotofacil"), actions.calls) }
    }

    @Test fun missingResultOffersConsultationAndRoutesToModality() {
        show { presentation.ResultCard(lottery(), null) }
        compose.onNodeWithText("Consultar último resultado").assertExists()
        compose.onNodeWithText("Ver resultado ›").performClick()
        compose.runOnIdle { assertEquals(listOf("modality:MEGA_SENA"), actions.calls) }
    }

    @Test fun resultCardLimitsNumberPreviewToSixAndIncludesClovers() {
        show {
            presentation.ResultCard(
                lottery(9, "maismilionaria"),
                result().copy(firstDrawNumbers = (11..18).toList(), clovers = listOf(2, 5))
            )
        }
        compose.onNodeWithText("Concurso 456").assertExists()
        compose.onNodeWithText("11").assertExists()
        compose.onNodeWithText("16").assertExists()
        compose.onNodeWithText("17").assertDoesNotExist()
        compose.onNodeWithText("Trevos: 2 • 5").assertExists()
    }

    @Test fun resultCardWithoutCloversDoesNotExposeComplementCaption() {
        show { presentation.ResultCard(lottery(), result()) }
        compose.onNodeWithText("Trevos:", substring = true).assertDoesNotExist()
        compose.onNodeWithText("Consultar último resultado").assertDoesNotExist()
    }

    @Test fun modalityAccessibilityMovesReportSourceAndTargetAndRetainCardBetting() {
        val moves = mutableListOf<Pair<String, String>>()
        val local = HomeV2Presentation({ listOf("MEGA_SENA", "QUINA") }, { from, to -> moves.add(from to to) }) { actions }
        show { local.ReorderableModalities(listOf(lottery(), lottery(3, "quina")), rememberScrollState(), Rect.Zero, {}) }
        compose.onNodeWithText("Mega-Sena").performCustomAccessibilityActionWithLabel("Mover para próxima posição")
        compose.onNodeWithText("Quina").performCustomAccessibilityActionWithLabel("Mover para posição anterior")
        compose.onNodeWithText("Quina").performClick()
        compose.runOnIdle {
            assertEquals(listOf("MEGA_SENA" to "QUINA", "QUINA" to "MEGA_SENA"), moves)
            assertEquals(listOf("bet:quina"), actions.calls)
        }
    }

    @Test fun showAllExpandsBeyondEightModalitiesAndCanCollapseAgain() {
        val cards =
            listOf(
                lottery(),
                lottery(
                    9,
                    "maismilionaria"
                ),
                lottery(
                    19,
                    "loteca"
                ),
                lottery(
                    11,
                    "diadesorte"
                ),
                lottery(
                    18,
                    "duplasena"
                ),
                lottery(16, "lotomania"), lottery(3, "quina"), lottery(20, "timemania"), lottery(8, "lotofacil"), lottery(7, "supersete")
            )
        show {
            HomeV2Content(
                cards,
                false,
                emptyMap(),
                HomeV2Modality.entries.map {
                    it.name
                },
                { _, _ -> },
                actions,
                CaixaDSCampaign.DEFAULT,
                CaixaDSCampaign.DEFAULT
            )
        }
        compose.onNodeWithText("Lotofácil").assertDoesNotExist()
        compose.onAllNodesWithText("Ver todas  ›")[0].performScrollTo().performClick()
        compose.onNodeWithText("Lotofácil").assertExists()
        compose.onNodeWithText("Ver menos  ›").performScrollTo().performClick()
        compose.onNodeWithText("Lotofácil").assertDoesNotExist()
    }

    @Test fun replacingActionsUpdatesExistingRememberedHomePresentation() {
        val next = Actions()
        val useNext = mutableStateOf(false)
        show {
            HomeV2Content(emptyList(), true, emptyMap(), emptyList(), { _, _ ->
            }, if (useNext.value) next else actions, CaixaDSCampaign.DEFAULT, CaixaDSCampaign.DEFAULT)
        }
        compose.runOnIdle { useNext.value = true }
        compose.onNodeWithText("Carrinho").performClick()
        compose.runOnIdle {
            assertEquals(emptyList<String>(), actions.calls)
            assertEquals(listOf("cart"), next.calls)
        }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { content() } }
    }
    private fun lottery(id: Int = 2, code: String = "megasena") = HomeLottery(
        id, "NORMAL", code, code, 123,
        BigDecimal(
            "1200000"
        ),
        BigDecimal.ONE, "22/10/2026", "", true, HomeLotteryKind.LOTTERY,
        Color.Green, Color.DarkGray, Color.White,
        R.drawable.icon_loteria, R.drawable.icon_loteria, R.drawable.icon_loteria
    )
    private fun result() =
        DrawResult("megasena", 456, "22/10/2026", "", listOf(1, 2, 3), emptyList(), emptyList(), false, "", "", "", emptyList())
    private class Actions : HomeV2Presentation.Actions {
        val calls = mutableListOf<String>()
        var pools = false
        override fun bet(modalidade: HomeLottery) {
            calls.add("bet:${modalidade.resultCode}")
        }
        override fun pool(modalidade: HomeLottery) {
            calls.add("pool:${modalidade.resultCode}")
        }
        override fun results() {
            calls.add("results")
        }
        override fun modalityResults(modalidade: HomeV2Modality) {
            calls.add("modality:${modalidade.name}")
        }
        override fun games() {
            calls.add("games")
        }
        override fun favorites() {
            calls.add("favorites")
        }
        override fun menu() {
            calls.add("menu")
        }
        override fun notifications() {
            calls.add("notifications")
        }
        override fun cart() {
            calls.add("cart")
        }
        override fun profile() {
            calls.add("profile")
        }
        override fun canPool(modalidade: HomeV2Modality) = pools
    }
}
