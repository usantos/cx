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
class FavoriteBetsContentStateUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun favoriteVisitorSeesSelectedHeaderWithoutPrivateRowsOrEmptyInvitation() {
        var terms = 0
        show {
            FavoriteBetsContent(
                FavoriteBetsUiState(
                    loading = false,
                    favorites = listOf(favorite()),
                    selectedModalityId = 2,
                    modalities = listOf(modality(2, "Mega-Sena"))
                ),
                {},
                { terms++ },
                {},
                false,
                {},
                {},
                {}
            )
        }
        compose.onNodeWithText("Mega-Sena").assertExists()
        compose.onNodeWithText("Números da família").assertDoesNotExist()
        compose.onNodeWithText("Você ainda não tem apostas favoritas. Que tal adicionar uma agora?").assertDoesNotExist()
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.runOnIdle { assertEquals(1, terms) }
    }

    @Test fun favoritesLoadingSuppressesEmptyInvitation() {
        show { FavoriteBetsContent(FavoriteBetsUiState(), {}, {}, {}, true, {}, {}, {}) }
        compose.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo.Indeterminate)).assertExists()
        compose.onNodeWithText("Você ainda não tem apostas favoritas. Que tal adicionar uma agora?").assertDoesNotExist()
    }

    @Test fun emptySelectedFavoritesCanCreateForSelectedModalityAndFilter() {
        val creations = mutableListOf<Int>()
        var filters = 0
        show {
            FavoriteBetsContent(
                FavoriteBetsUiState(loading = false, selectedModalityId = 8, modalities = listOf(modality(8, "Lotofácil"))),
                {},
                {},
                creations::add,
                true,
                { filters++ },
                {},
                {}
            )
        }
        compose.onNodeWithText("Você ainda não tem apostas favoritas desta modalidade. Que tal adicionar uma agora?").assertExists()
        compose.onNodeWithText("Incluir aposta favorita").performClick()
        compose.onNodeWithText("Selecione a modalidade").performClick()
        compose.runOnIdle {
            assertEquals(listOf(8), creations)
            assertEquals(1, filters)
        }
    }

    @Test fun emptyUnfilteredFavoritesOfferSupportedModalitiesAndOmitLoteca() {
        val creations = mutableListOf<Int>()
        show {
            FavoriteBetsContent(
                FavoriteBetsUiState(
                    loading = false,
                    modalities = listOf(modality(2, "Mega-Sena"), modality(19, "Loteca"), modality(8, "Lotofácil"))
                ),
                {},
                {},
                creations::add,
                true,
                {},
                {},
                {}
            )
        }
        compose.onNodeWithText("loteca").assertDoesNotExist()
        compose.onNodeWithText("lotofácil").performClick()
        compose.runOnIdle { assertEquals(listOf(8), creations) }
    }

    @Test fun favoriteRowsForwardTheActualFavoriteAndPaginationIsRequestedAtEnd() {
        val bet = favorite()
        val additions = mutableListOf<FavoriteBet>()
        val deletions = mutableListOf<FavoriteBet>()
        var pages = 0
        show {
            FavoriteBetsList(FavoriteBetsUiState(loading = false, favorites = listOf(bet), canLoadMore = true), PaddingValues(), {
            }, additions::add, deletions::add, { pages++ })
        }
        compose.onNodeWithText("01 - 02 - 00").assertExists()
        compose.onNodeWithContentDescription("Adicionar aposta").performClick()
        compose.onNodeWithContentDescription("Excluir aposta").performClick()
        compose.runOnIdle {
            assertEquals(listOf(bet), additions)
            assertEquals(listOf(bet), deletions)
            assertEquals(1, pages)
        }
    }

    @Test fun favoriteRowsRenderTeamMonthAndCloverComplements() {
        val current = mutableStateOf(favorite().copy(heartTeam = "Flamengo/RJ"))
        show { FavoriteBetsList(FavoriteBetsUiState(loading = false, favorites = listOf(current.value)), PaddingValues(), {}, {}, {}) }
        compose.onNodeWithText("Time do coração: Flamengo/RJ").assertExists()
        compose.runOnIdle { current.value = favorite().copy(luckyMonth = "Outubro") }
        compose.onNodeWithText("Mês de sorte: Outubro").assertExists()
        compose.runOnIdle { current.value = favorite().copy(clovers = listOf(2, 5)) }
        compose.onNodeWithText("Trevos: 2 - 5").assertExists()
    }

    @Test fun superSevenFavoriteDisplaysEachColumnRatherThanFlattenedNumbers() {
        show {
            FavoriteBetsList(
                FavoriteBetsUiState(
                    loading = false,
                    favorites = listOf(
                        favorite().copy(modalityId = 7, modalityName = "Super Sete", numberColumns = listOf(listOf(9, 2), listOf(8)))
                    )
                ),
                PaddingValues(),
                {
                },
                {},
                {}
            )
        }
        compose.onNodeWithText("super sete").assertExists()
        compose.onNodeWithText("9").assertExists()
        compose.onNodeWithText("8").assertExists()
        compose.onNodeWithText("09 - 02 - 08").assertDoesNotExist()
    }

    @Test fun modalityFilterDefersApplyingPendingChoiceUntilFilterPressed() {
        val selected = mutableListOf<Int?>()
        show { ModalityFilterDialog(null, listOf(2 to "Mega-Sena", 19 to "Loteca", 8 to "Lotofácil"), {}, selected::add) }
        compose.onNodeWithText("loteca").assertDoesNotExist()
        compose.onNodeWithText("mega-sena").performClick()
        compose.runOnIdle { assertEquals(emptyList<Int?>(), selected) }
        compose.onNodeWithText("Filtrar").performClick()
        compose.runOnIdle { assertEquals(listOf(2), selected) }
    }

    @Test fun modalityFilterCanResetToAllAndCancelOrCloseWithoutApplying() {
        val selected = mutableListOf<Int?>()
        var dismisses = 0
        show { ModalityFilterDialog(2, listOf(2 to "Mega-Sena"), { dismisses++ }, selected::add) }
        compose.onNodeWithText("Todas").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithContentDescription("Fechar").performClick()
        compose.runOnIdle {
            assertEquals(emptyList<Int?>(), selected)
            assertEquals(2, dismisses)
        }
        compose.onNodeWithText("Filtrar").performClick()
        compose.runOnIdle { assertEquals(listOf<Int?>(null), selected) }
    }

    @Test fun favoriteOptionsRequireOpeningContestChoiceBeforeConfirming() {
        val mega = modality(2, "Mega-Sena")
        var confirmations = 0
        show {
            AddFavoriteOptionsDialog(
                FavoriteBetsUiState(addCandidate = favorite(), games = listOf(game(mega), game(mega).copy(contestType = "ESPECIAL"))),
                {},
                { confirmations++ },
                {},
                {},
                {}
            )
        }
        compose.onNodeWithText("Adicionar ao carrinho").assertIsNotEnabled()
        compose.onNodeWithText("Tipo de Concurso").performClick()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsEnabled().performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, confirmations) }
    }

    @Test fun singleNormalFavoriteOptionsEnableConfirmAndCanExpandRecurringPicker() {
        var confirmations = 0
        var cancellations = 0
        show {
            AddFavoriteOptionsDialog(
                FavoriteBetsUiState(addCandidate = favorite(), games = listOf(game(modality(2, "Mega-Sena")))),
                { cancellations++ },
                { confirmations++ },
                {},
                {},
                {}
            )
        }
        compose.onNodeWithText("Tipo de Concurso").assertDoesNotExist()
        compose.onNodeWithText("Quantidade de teimosinhas").performClick()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsEnabled().performScrollTo().performClick()
        compose.onNodeWithText("Cancelar").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(1, confirmations)
            assertEquals(1, cancellations)
        }
    }

    @Test fun lotomaniaFavoriteOptionsExposeMirrorToggle() {
        var toggles = 0
        show {
            AddFavoriteOptionsDialog(FavoriteBetsUiState(addCandidate = favorite().copy(modalityName = "Lotomania", modalityId = 16)), {
            }, {}, {}, {}, { toggles++ })
        }
        compose.onNodeWithText(
            "Ao selecionar esta opção, o sistema fará a aposta automaticamente, escolhendo os 50 " +
                "números que não foram registrados na sua aposta original."
        ).performClick()
        compose.runOnIdle { assertEquals(1, toggles) }
    }

    @Test fun mirrorOptionRetainsManualExplanationAndSurpriseLabel() {
        var toggles = 0
        val manual = mutableStateOf(true)
        show { BettingLegacyMirror(false, { toggles++ }, manual.value) }
        compose.onNodeWithText("Aumente suas chances de ganhar com a aposta-espelho.", substring = true).performClick()
        compose.runOnIdle { manual.value = false }
        compose.onNodeWithText("Aposta espelho").performClick()
        compose.runOnIdle { assertEquals(2, toggles) }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { Column { content() } } }
    }
    private fun modality(id: Int, name: String) = LotteryModality(id, name, "M$id")
    private fun favorite() = FavoriteBet(1, 2, "Mega-Sena", "Números da família", listOf(listOf(1, 2, 100)))
    private fun game(modality: LotteryModality) =
        LotteryGameParameters(modality, 123, "22/10/2026", 60, 6, 20, 10, listOf(0, 2, 4), emptyList())
}
