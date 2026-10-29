// Literais numéricos descrevem seleções de loteria, identificadores e massas de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidAmountPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidModalitiesPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidNumberCells
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidNumbersPage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidPrizePage
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidWelcomePage
import br.gov.caixa.loterias.feature.betting.presentation.screen.LotogolBetContent
import br.gov.caixa.loterias.feature.betting.presentation.screen.LuckyMonthContent
import br.gov.caixa.loterias.feature.betting.presentation.screen.SuperSevenMatrix
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class BettingUncoveredPagesUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun lotogolShowsBothTeamsAndRoutesHomeAwayScoresAndClear() {
        val scores = mutableListOf<Triple<Int, Boolean, Int>>()
        var clears = 0
        val match = SportsMatch(7, SportsTeam(1, "Flamengo"), SportsTeam(2, "Vasco"))
        show {
            LotogolBetContent(
                BettingGameUiState(game = game(14).copy(matches = listOf(match)), lotogolScores = mapOf(7 to (5 to 2))),
                actions(score = {
                        number,
                        home,
                        score,
                    ->
                    scores.add(Triple(number, home, score))
                }, clear = { clears++ })
            )
        }
        compose.onNodeWithText("Jogo 07").assertExists()
        compose.onNodeWithContentDescription("Escudo Flamengo").assertExists()
        compose.onNodeWithContentDescription("Escudo Vasco").assertExists()
        compose.onAllNodesWithText("5+")[0].performClick()
        compose.onAllNodesWithText("2")[1].performClick()
        compose.onNodeWithText("Limpar aposta").performClick()
        compose.runOnIdle {
            assertEquals(listOf(Triple(7, true, 5), Triple(7, false, 2)), scores)
            assertEquals(1, clears)
        }
    }

    @Test fun superSevenDigitsReportColumnAndDigitAndReflectControlledSelection() {
        val current = mutableStateOf(BettingGameUiState(game = game(7), numberQuantity = 7))
        val selected = mutableListOf<Pair<Int, Int>>()
        show {
            SuperSevenMatrix(
                current.value,
                actions(digit = { column, digit ->
                    selected.add(column to digit)
                    current.value =
                        current.value.copy(
                            superSevenColumns = current.value.superSevenColumns.mapIndexed { index, values ->
                                if (index ==
                                    column
                                ) {
                                    values + digit
                                } else {
                                    values
                                }
                            }
                        )
                })
            )
        }
        compose.onNodeWithText("Selecione os números:").assertExists()
        compose.onNodeWithTag("super-sete-0-0").assertIsNotSelected().performClick()
        compose.onNodeWithTag("super-sete-0-0").assertIsSelected()
        compose.onNodeWithText("Selecionados: 1/7").assertExists()
        compose.onNodeWithContentDescription("Coluna 7, número 9").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Coluna 7, número 9").assertIsSelected()
        compose.runOnIdle { assertEquals(listOf(0 to 0, 6 to 9), selected) }
    }

    @Test fun luckyMonthOddGridKeepsIdentityAndShowsSelection() {
        val months = listOf(LuckyMonth(1, "Janeiro"), LuckyMonth(2, "Fevereiro"), LuckyMonth(3, "Março"))
        val current = mutableStateOf(BettingGameUiState(game = game(11).copy(luckyMonths = months)))
        val selected = mutableListOf<LuckyMonth>()
        show {
            LuckyMonthContent(
                current.value,
                actions(month = {
                    selected.add(it)
                    current.value =
                        current.value.copy(selectedLuckyMonth = it)
                })
            )
        }
        compose.onNodeWithTag("lucky-month-Março").assertIsNotSelected().performClick()
        compose.onNodeWithTag("lucky-month-Março").assertIsSelected()
        compose.onNodeWithTag("lucky-month-Janeiro").assertIsNotSelected()
        compose.runOnIdle { assertEquals(listOf(months[2]), selected) }
    }

    @Test fun missingLuckyMonthsExplainUnavailableContestData() {
        show { LuckyMonthContent(BettingGameUiState(), actions()) }
        compose.onNodeWithText("Meses indisponíveis para este concurso.").assertExists()
    }

    @Test fun rapidWelcomeExplainsMinimumLimitsAndModalities() {
        show { RapidWelcomePage() }
        compose.onNodeWithText("Bem-vindo ao").assertExists()
        compose.onNodeWithText("Rapidão").assertExists()
        compose.onNodeWithText("Selecione os limites da sua compra e suas modalidades favoritas.").assertExists()
    }

    @Test fun rapidPrizePresetsSendActualMillionAmounts() {
        val selected = mutableListOf<BigDecimal>()
        show { RapidPrizePage(BigDecimal("40000000"), selected::add) }
        compose.onNodeWithText("Prêmio mínimo").assertExists()
        compose.onNodeWithText("60 Mi").performScrollTo().performClick()
        compose.onNodeWithText("100 Mi").performClick()
        compose.runOnIdle { assertEquals(listOf(BigDecimal("60000000"), BigDecimal("100000000")), selected) }
    }

    @Test fun rapidModalitiesSupportLotogolAndOddNumberOfCards() {
        val selected = mutableListOf<Int>()
        show {
            RapidModalitiesPage(
                listOf(LotteryModality(2, "Mega-Sena", ""), LotteryModality(14, "Lotogol", ""), LotteryModality(8, "Lotofácil", "")),
                setOf(2),
                selected::add
            )
        }
        compose.onNodeWithText("lotogol").performClick()
        compose.onNodeWithText("lotofácil").performClick()
        compose.runOnIdle { assertEquals(listOf(14, 8), selected) }
    }

    @Test fun rapidNumberSelectionCanClearAndHidesClearAfterControlledReset() {
        val selected = mutableStateOf(setOf(1))
        var clears = 0
        show {
            RapidNumbersPage("Números excluídos", "Não usar estas dezenas", selected.value, { selected.value += it }, {
                clears++
                selected.value =
                    emptySet()
            }, danger = true)
        }
        compose.onNodeWithText("02").performClick()
        compose.onNodeWithText("Limpar seleção").performClick()
        compose.onNodeWithText("Limpar seleção").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, clears)
            assertEquals(emptySet<Int>(), selected.value)
        }
    }

    @Test fun readOnlyRapidNumberCellsDoNotExposeMutationActions() {
        show { RapidNumberCells(listOf(1, 2, 3), setOf(2), false, null) }
        compose.onNodeWithText("01").assertHasNoClickAction()
        compose.onNodeWithText("02").assertHasNoClickAction()
        compose.onNodeWithText("03").assertExists()
    }

    @Test fun unavailableRapidAmountLimitsDisableCustomEntryAndCanRetry() {
        var retries = 0
        show {
            RapidAmountPage(RapidConfigurationUiState(amountLimitsError = "Limite indisponível"), {}, {}, { _, _ -> false }, { retries++ })
        }
        compose.onNodeWithText("Limite indisponível").assertExists()
        compose.onAllNodesWithText("...").assertCountEquals(2)
        compose.onAllNodesWithText("...")[0].assertHasNoClickAction()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun rapidAmountPresetsUseMultiplesAndNearDailyLimitReducesSuggestions() {
        val current = mutableStateOf(amountState())
        val maximum = mutableListOf<BigDecimal>()
        val minimum = mutableListOf<BigDecimal>()
        show { RapidAmountPage(current.value, minimum::add, maximum::add, { _, _ -> true }, {}) }
        compose.onNodeWithText("40").performClick()
        compose.onNodeWithText("20").performClick()
        compose.runOnIdle {
            assertEquals(listOf(BigDecimal("40")), maximum)
            assertEquals(listOf(BigDecimal("20")), minimum)
            current.value =
                amountState().copy(maximumAllowedAmount = BigDecimal("30"))
        }
        compose.onNodeWithText("40").assertDoesNotExist()
        compose.onAllNodesWithText("30").assertCountEquals(1)
    }

    @Test fun rapidCustomMinimumRejectsBlankThenAcceptsEnteredAmount() {
        val attempts = mutableListOf<Pair<BigDecimal?, Boolean>>()
        show {
            RapidAmountPage(amountState(), {}, {}, { amount, minimum ->
                attempts.add(amount to minimum)
                amount != null
            }, {})
        }
        compose.onAllNodesWithText("...")[1].performScrollTo().performClick()
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("O valor selecionado tem que estar entre R$ 10,00 e R$ 100,00").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNode(hasSetTextAction()).performTextInput("2500")
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("Digite o valor mínimo").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(2, attempts.size)
            assertEquals(null to true, attempts[0])
            assertEquals(0, attempts[1].first!!.compareTo(BigDecimal("25")))
            assertEquals(true, attempts[1].second)
        }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { content() } }
    }
    private fun amountState() = RapidConfigurationUiState(
        minimumAmount = BigDecimal.TEN,
        maximumAmount = BigDecimal("100"),
        minimumAllowedAmount = BigDecimal.TEN,
        maximumAllowedAmount = BigDecimal("100")
    )
    private fun game(id: Int) = LotteryGameParameters(LotteryModality(id, "Jogo", ""), 123, "", 60, 6, 20, 10, emptyList(), emptyList())
    private fun actions(
        score: (Int, Boolean, Int) -> Unit = { _, _, _ -> },
        clear: () -> Unit = {},
        digit: (Int, Int) -> Unit = { _, _ -> },
        month: (LuckyMonth) -> Unit = {},
    ) = BettingGameActions({}, {}, {}, {}, {}, {}, {}, {}, digit, month, score, clear, {}, {}, {})
}
