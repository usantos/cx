// Literais numéricos descrevem seleções de loteria, identificadores e massas de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetComboEntry
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.components.AddFavoriteOptionsDialog
import br.gov.caixa.loterias.feature.betting.presentation.components.ComboDetailEntryCard
import br.gov.caixa.loterias.feature.betting.presentation.components.FavoriteBetsList
import br.gov.caixa.loterias.feature.betting.presentation.components.ModalityFilterDialog
import br.gov.caixa.loterias.feature.betting.presentation.screen.BetComboDetailScreen
import br.gov.caixa.loterias.feature.betting.presentation.screen.BetCombosScreen
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyMirror
import br.gov.caixa.loterias.feature.betting.presentation.screen.FavoriteBetsContent
import br.gov.caixa.loterias.feature.betting.presentation.screen.LotteryModalitiesScreen
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingCatalogUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BettingCatalogContentStateUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun modalitiesLoadingHidesContentThenFailureCanRetryAndRecover() {
        var retries = 0
        val state = mutableStateOf(BettingCatalogUiState(errorMessage = "Falha de catálogo"))
        show { LotteryModalitiesScreen(state.value, {}, {}, { retries++ }) }
        compose.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo.Indeterminate)).assertExists()
        compose.onNodeWithText("Falha de catálogo").assertDoesNotExist()
        compose.runOnIdle { state.value = state.value.copy(loading = false) }
        compose.onNodeWithText("Falha de catálogo").assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle {
            assertEquals(1, retries)
            state.value = BettingCatalogUiState(loading = false)
        }
        compose.onNodeWithText("Escolha uma modalidade").assertExists()
        compose.onNodeWithText("Falha de catálogo").assertDoesNotExist()
    }

    @Test fun modalitiesAllowUnavailableParameterLookupButDisableClosedContest() {
        val selected = mutableListOf<Int>()
        val mega = modality(2, "Mega-Sena")
        val lotogol = modality(14, "Lotogol")
        show {
            LotteryModalitiesScreen(
                BettingCatalogUiState(
                    loading = false,
                    modalities = listOf(mega, lotogol),
                    parameters = parameters(listOf(game(mega).copy(open = false)))
                ),
                {},
                selected::add,
                {}
            )
        }
        compose.onNodeWithText("Mega-Sena").assertIsNotEnabled()
        compose.onNodeWithText("Consulte a disponibilidade").assertExists()
        compose.onNodeWithText("Concurso 123 • 22/10/2026").assertExists()
        compose.onNodeWithText("Lotogol").performClick()
        compose.runOnIdle { assertEquals(listOf(14), selected) }
    }

    @Test fun emptyComboCatalogExplainsThatNoComboIsAvailable() {
        show { BetCombosScreen(BettingCatalogUiState(loading = false), {}, {}, BigDecimal.ZERO, 0, null, {}) }
        compose.onNodeWithText("Nenhum combo disponível no momento.").assertExists()
        compose.onNodeWithText(
            "Confira nossos combos e escolha o seu favorito!\nAproveite para aumentar suas chances de ganhar."
        ).assertExists()
    }

    @Test fun comboDetailWithNoSelectionDoesNotAllowAdding() {
        show { ComboDetail(BettingCatalogUiState(loading = false)) }
        compose.onNodeWithText("Adicionar\nao carrinho de apostas").assertIsNotEnabled()
        compose.onNodeWithText("Quantidade de Apostas").assertExists()
        compose.onNodeWithText("Quero ver os números escolhidos antes de pagar.").assertDoesNotExist()
    }

    @Test fun comboDetailQuantityActionsAndCartAdditionUseCallbacks() {
        var increase = 0
        var decrease = 0
        var add = 0
        show {
            ComboDetail(
                BettingCatalogUiState(loading = false, selectedCombo = combo(), comboQuantity = 3),
                increase = { increase++ },
                decrease = { decrease++ },
                add = { add++ }
            )
        }
        compose.onNodeWithText("Combo da semana").assertExists()
        compose.onNodeWithText("R$ 30,00").assertExists()
        compose.onNodeWithContentDescription("Adicionar apostas").performClick()
        compose.onNodeWithContentDescription("Remover apostas").performClick()
        compose.onNodeWithText("Adicionar\nao carrinho de apostas").assertIsEnabled().performClick()
        compose.runOnIdle {
            assertEquals(1, increase)
            assertEquals(1, decrease)
            assertEquals(1, add)
        }
    }

    @Test fun comboDetailCanToggleShowingNumbersBeforePayment() {
        var toggles = 0
        show { ComboDetail(BettingCatalogUiState(loading = false, selectedCombo = combo(), surprise = false), toggle = { toggles++ }) }
        compose.onNodeWithText("Quero ver os números escolhidos antes de pagar.").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, toggles) }
    }

    @Test fun comboEntryDisplaysQuantityModalityAndContest() {
        show { ComboDetailEntryCard(BetComboEntry(modality(8, "Lotofácil"), "NORMAL", 4, 0, 15, 0, 321)) }
        compose.onNodeWithText("4 jogos").assertExists()
        compose.onNodeWithText("lotofácil").assertExists()
        compose.onNodeWithText("concurso 321").assertExists()
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { Column { content() } } }
    }
    private fun modality(id: Int, name: String) = LotteryModality(id, name, "M$id")
    private fun game(modality: LotteryModality) =
        LotteryGameParameters(modality, 123, "22/10/2026", 60, 6, 20, 10, listOf(0, 2, 4), emptyList())
    private fun parameters(games: List<LotteryGameParameters>) = BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 50, games)
    private fun combo() = BetCombo(1, "Combo da semana", "", false, BigDecimal.TEN, emptyList())

    @Composable private fun ComboDetail(
        state: BettingCatalogUiState,
        increase: () -> Unit = {
        },
        decrease: () -> Unit = {},
        add: () -> Unit = {},
        toggle: () -> Unit = {},
    ) {
        BetComboDetailScreen(state, {}, BigDecimal.ZERO, 0, null, {}, decrease, increase, add, toggle)
    }
}
