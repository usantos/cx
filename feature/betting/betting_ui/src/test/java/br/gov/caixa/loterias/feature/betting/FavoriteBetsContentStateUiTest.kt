// Literais numéricos descrevem seleções de loteria, identificadores e massas de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.components.FavoriteBetsList
import br.gov.caixa.loterias.feature.betting.presentation.screen.BettingLegacyMirrorDelivered
import br.gov.caixa.loterias.feature.betting.presentation.screen.FavoriteBetsContentDelivered
import br.gov.caixa.loterias.feature.betting.presentation.state.FavoriteBetsUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36])
class FavoriteBetsContentStateUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun favoriteVisitorSeesSelectedHeaderWithoutPrivateRowsOrEmptyInvitation() {
        var terms = 0
        show {
            FavoriteBetsContentDelivered(
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
        show { FavoriteBetsContentDelivered(FavoriteBetsUiState(), {}, {}, {}, true, {}, {}, {}) }
        compose.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo.Indeterminate)).assertExists()
        compose.onNodeWithText("Você ainda não tem apostas favoritas. Que tal adicionar uma agora?").assertDoesNotExist()
    }

    @Test fun emptySelectedFavoritesCanCreateForSelectedModalityAndFilter() {
        val creations = mutableListOf<Int>()
        var filters = 0
        show {
            FavoriteBetsContentDelivered(
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
            FavoriteBetsContentDelivered(
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

    @Test fun mirrorOptionRetainsManualExplanationAndSurpriseLabel() {
        var toggles = 0
        val manual = mutableStateOf(true)
        show { BettingLegacyMirrorDelivered(false, { toggles++ }, manual.value) }
        compose.onNodeWithText("Aumente suas chances de ganhar com a aposta-espelho.", substring = true).performClick()
        compose.runOnIdle { manual.value = false }
        compose.onNodeWithText("Aposta espelho").performClick()
        compose.runOnIdle { assertEquals(2, toggles) }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setFeatureContent { LoteriasCaixaDSTheme { Column { content() } } }
    }
    private fun modality(id: Int, name: String) = LotteryModality(id, name, "M$id")
    private fun favorite() = FavoriteBet(1, 2, "Mega-Sena", "Números da família", listOf(listOf(1, 2, 100)))
}
