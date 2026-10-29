package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetComboEntry
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddComboToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.BetComboDetailRoute
import br.gov.caixa.loterias.feature.betting.presentation.route.BetCombosRoute
import br.gov.caixa.loterias.feature.betting.presentation.route.LotteryModalitiesRoute
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogMode
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingCatalogViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Caracterização das três rotas públicas; ViewModels reais e fronteiras locais de serviço. */
class BettingCatalogCoordinationUiTest {
    @get:Rule val compose = createComposeRule()
    private val models = mutableListOf<BettingCatalogViewModel>()
    private val mega = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
    private val seven = LotteryModality(7, "Super Sete", "SUPER_7")
    private val lotogol = LotteryModality(14, "Lotogol", "LOTOGOL")
    private val combo = BetCombo(
        8,
        "Combo da Sorte",
        "Descrição do servidor",
        true,
        BigDecimal("20.50"),
        listOf(BetComboEntry(mega, "NORMAL", 3, 1, 6, 0, 2800))
    )

    @After fun cancelModels() {
        compose.runOnIdle { models.forEach { it.viewModelScope.cancel() } }
    }

    @Test fun modalitiesRecoverFromLoadingAndFailureAndOnlyClosedGamesBlockSelection() {
        val fixture = Fixture().apply { modalityGate = CompletableDeferred() }
        val model = fixture.model(BettingCatalogMode.MODALITIES)
        val selected = mutableListOf<Int>()
        compose.setContent { CaixaDSTheme { LotteryModalitiesRoute(model, {}, selected::add) } }
        compose.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        compose.runOnIdle { fixture.modalityGate!!.complete(AppResult.Failure(AppError.Network)) }
        compose.onNodeWithText("Não foi possível carregar as opções de apostas. Verifique sua internet.").assertIsDisplayed()
        compose.runOnIdle { fixture.modalityGate = null }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Todas").assertDoesNotExist()
        compose.onNode(hasText("Mega-Sena") and hasText("Concurso 2800 • 14/10/2026")).assertExists()
        compose.onNodeWithText("Consulte a disponibilidade").assertExists()
        compose.onNodeWithText("Super Sete").assertIsNotEnabled()
        compose.onNodeWithText("Mega-Sena").performClick()
        compose.onNodeWithText("Lotogol").performClick()
        compose.runOnIdle {
            assertEquals(listOf(2, 14), selected)
            assertEquals(2, fixture.modalityLoads)
        }
    }

    @Test fun oddComboRowKeepsItsColumnAndCardTouchReturnsItsIdWhileCartAndEmptyStateRemainAvailable() {
        val fixture = Fixture().apply {
            combos = listOf(combo, combo.copy(id = 9, name = "Combo Duplo"), combo.copy(id = 10, name = "Combo Triplo"))
        }
        val model = fixture.model(BettingCatalogMode.COMBOS)
        val selected = mutableListOf<Int>()
        var carts = 0
        compose.setContent {
            CaixaDSTheme {
                BetCombosRoute(model, {}, selected::add, BigDecimal("35.00"), 3, onCart = { carts++ })
            }
        }
        val first = compose.onNodeWithText("Combo da Sorte").fetchSemanticsNode().boundsInRoot
        val second = compose.onNodeWithText("Combo Duplo").fetchSemanticsNode().boundsInRoot
        val third = compose.onNodeWithText("Combo Triplo").fetchSemanticsNode().boundsInRoot
        assertEquals(first.center.x, third.center.x, 1f)
        assertTrue(second.center.x > first.center.x)
        assertTrue(third.top > first.bottom)
        compose.onNodeWithText("Combo Triplo").performTouchInput { click() }
        cart(3).performClick()
        compose.runOnIdle {
            assertEquals(listOf(10), selected)
            assertEquals(1, carts)
            fixture.combos = emptyList()
            model.load()
        }
        compose.onNodeWithText("Nenhum combo disponível no momento.").assertIsDisplayed()
        cart(3).assertIsDisplayed()
    }

    @Test fun confirmationIsConsumedBeforeNavigationAndDoesNotRepeatAfterReentry() {
        val fixture = Fixture()
        val model = fixture.model(BettingCatalogMode.COMBO_DETAIL)
        val visible = mutableStateOf(true)
        var additions = 0
        compose.setContent {
            CaixaDSTheme {
                if (visible.value) {
                    BetComboDetailRoute(model, {}, {
                        assertFalse(model.state.value.addedToCart)
                        additions++
                        visible.value = false
                    })
                }
            }
        }
        addButton().performClick()
        compose.runOnIdle {
            assertEquals(1, additions)
            visible.value = true
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, additions) }
    }

    @Test fun detailControlsEnforceQuantityBoundsAndSubmitTheInvertedPreviewChoice() {
        val fixture = Fixture()
        val model = fixture.model(BettingCatalogMode.COMBO_DETAIL)
        var additions = 0
        compose.setContent { CaixaDSTheme { BetComboDetailRoute(model, {}, { additions++ }) } }
        compose.onNodeWithContentDescription("Remover apostas").performClick()
        compose.runOnIdle { assertEquals(1, model.state.value.comboQuantity) }
        repeat(16) { compose.onNodeWithContentDescription("Adicionar apostas").performClick() }
        compose.onNodeWithText("15").assertExists()
        compose.onNodeWithText("307,50", substring = true).assertExists()
        repeat(14) { compose.onNodeWithContentDescription("Remover apostas").performClick() }
        compose.onNodeWithContentDescription("Adicionar apostas").performClick()
        compose.onNodeWithText("41,00", substring = true).assertExists()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Quero ver os números escolhidos antes de pagar."))
        compose.onNode(isToggleable()).assertIsOff().performClick().assertIsOn()
        addButton().performClick()
        compose.runOnIdle {
            assertEquals(listOf(Triple(8, 2, false)), fixture.submitted)
            assertEquals(1, additions)
            assertFalse(model.state.value.addedToCart)
        }
    }

    @Test fun missingComboAndSubmissionFailureCanRetryWithoutLosingTheFixedFooterOrChoices() {
        val fixture = Fixture().apply { combos = emptyList() }
        val model = fixture.model(BettingCatalogMode.COMBO_DETAIL)
        var additions = 0
        compose.setContent {
            CaixaDSTheme {
                BetComboDetailRoute(model, {}, { additions++ }, BigDecimal("35.00"), 3)
            }
        }
        compose.onNodeWithText("Combo não encontrado.").assertIsDisplayed()
        addButton().assertIsNotEnabled()
        cart(3).assertIsDisplayed()
        compose.runOnIdle { fixture.combos = listOf(combo) }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithContentDescription("Adicionar apostas").performClick()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Quero ver os números escolhidos antes de pagar."))
        compose.onNode(isToggleable()).performClick()
        compose.runOnIdle { fixture.addGate = CompletableDeferred() }
        addButton().performClick()
        compose.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        addButton().assertIsNotEnabled()
        cart(3).assertIsDisplayed()
        compose.runOnIdle { fixture.addGate!!.complete(AppResult.Failure(AppError.Network)) }
        compose.onNodeWithText("Não foi possível carregar as opções de apostas. Verifique sua internet.").assertIsDisplayed()
        compose.runOnIdle { fixture.addGate = null }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("41,00", substring = true).assertExists()
        compose.runOnIdle {
            assertEquals(2, model.state.value.comboQuantity)
            assertFalse(model.state.value.surprise)
            assertEquals(0, additions)
        }
        addButton().performClick()
        compose.runOnIdle {
            assertEquals(List(2) { Triple(8, 2, false) }, fixture.submitted)
            assertEquals(1, additions)
        }
    }

    @Test fun replacementViewModelReceivesCurrentControlsAndSuccessDoesNotRepeatOnCartRecomposition() {
        val first = Fixture()
        val second = Fixture().apply { combos = listOf(combo.copy(id = 9, name = "Segundo combo")) }
        val old = first.model(BettingCatalogMode.COMBO_DETAIL)
        val current = second.model(BettingCatalogMode.COMBO_DETAIL, 9)
        val selected = mutableStateOf(old)
        val count = mutableStateOf(1)
        val addedIds = mutableListOf<Int>()
        compose.setContent {
            CaixaDSTheme {
                BetComboDetailRoute(selected.value, {}, {
                    assertFalse(selected.value.state.value.addedToCart)
                    addedIds.add(selected.value.state.value.selectedCombo!!.id)
                }, BigDecimal.TEN, count.value)
            }
        }
        compose.waitForIdle()
        compose.runOnIdle { selected.value = current }
        compose.onNodeWithText("Segundo combo").assertIsDisplayed()
        compose.onNodeWithContentDescription("Adicionar apostas").performClick()
        addButton().performClick()
        compose.runOnIdle {
            assertTrue(first.submitted.isEmpty())
            assertEquals(listOf(Triple(9, 2, true)), second.submitted)
            assertEquals(listOf(9), addedIds)
            count.value = 5
        }
        cart(5).assertIsDisplayed()
        compose.runOnIdle { assertEquals(listOf(9), addedIds) }
    }

    @Test fun campaignChangesUpdateModalityTextColorWithoutReloadingTheCatalog() {
        val fixture = Fixture()
        val model = fixture.model(BettingCatalogMode.MODALITIES)
        val campaign = mutableStateOf(CaixaDSCampaign.DEFAULT)
        compose.setContent {
            CaixaDSTheme {
                CompositionLocalProvider(LocalCaixaDSMegaSenaCampaign provides campaign.value) {
                    LotteryModalitiesRoute(model, {}, {})
                }
            }
        }
        assertEquals(Color(0xFF00984A), modalityTextColor())
        compose.runOnIdle { campaign.value = CaixaDSCampaign.OCTOBER_ROSE }
        assertEquals(Color(0xFFEE3E7F), modalityTextColor())
        compose.runOnIdle { campaign.value = CaixaDSCampaign.SEPTEMBER_BLUE }
        assertEquals(Color(0xFF0077C8), modalityTextColor())
        compose.runOnIdle { campaign.value = CaixaDSCampaign.DEFAULT }
        assertEquals(Color(0xFF00984A), modalityTextColor())
        compose.runOnIdle { assertEquals(1, fixture.modalityLoads) }
    }

    private fun modalityTextColor(): Color {
        val results = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("Mega-Sena", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        return results.single().layoutInput.style.color
    }
    private fun cart(count: Int) = compose.onNode(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "$count apostas"))
    private fun addButton() = compose.onNodeWithText("Adicionar\nao carrinho de apostas")

    private inner class Fixture {
        var modalityGate: CompletableDeferred<AppResult<List<LotteryModality>>>? = null
        var addGate: CompletableDeferred<AppResult<ShoppingCart>>? = null
        var combos = listOf(combo)
        var modalityLoads = 0
        val submitted = mutableListOf<Triple<Int, Int, Boolean>>()
        fun model(mode: BettingCatalogMode, id: Int = 8) = BettingCatalogViewModel(
            mode,
            id,
            GetLotteryModalitiesUseCase {
                modalityLoads++
                modalityGate?.await() ?: AppResult.Success(listOf(LotteryModality(0, "Todas", "ALL"), mega, seven, lotogol))
            },
            GetBettingParametersUseCase {
                AppResult.Success(
                    BettingParameters(
                        BigDecimal("500"),
                        BigDecimal("30"),
                        50,
                        listOf(game(mega), game(seven).copy(open = false))
                    )
                )
            },
            GetBetCombosUseCase { AppResult.Success(combos) },
            AddComboToCartUseCase { comboId, quantity, surprise ->
                submitted.add(Triple(comboId, quantity, surprise))
                addGate?.await() ?: AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList()))
            },
            FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        ).also(models::add)
    }
    private fun game(modality: LotteryModality) = LotteryGameParameters(
        modality, 2800, "14/10/2026", 60, 6, 20, 10, emptyList(), emptyList()
    )
}
