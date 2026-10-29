// Literais numéricos descrevem seleções de loteria, identificadores e dados de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSelectionReward
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDetailFavoriteEditor
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDetailTeimosinhaDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.ConfirmedBetComplements
import br.gov.caixa.loterias.feature.tickets.presentation.components.ConfirmedBetDrawnResults
import br.gov.caixa.loterias.feature.tickets.presentation.components.ConfirmedBetNumbers
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketResultContent
import br.gov.caixa.loterias.feature.tickets.presentation.components.LotecaSelection
import br.gov.caixa.loterias.feature.tickets.presentation.components.LotogolSelection
import br.gov.caixa.loterias.feature.tickets.presentation.components.SuperSevenSelection
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultAwardRow
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultVisualData
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.TeimosinhaNotice
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TicketSelectionResultUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun numericBetShowsCloversAndRepeatsNumbersForSecondDraw() {
        show {
            ConfirmedBetNumbers(
                bet().copy(clovers = listOf(4)),
                Color.Black,
                Color.White,
                draw().copy(secondDrawNumbers = listOf(2, 3), clovers = listOf(4))
            )
        }
        compose.onNodeWithText("Meus números").assertExists()
        compose.onNodeWithText("Trevos").assertExists()
        compose.onNodeWithText("2º Sorteio").assertExists()
        compose.onAllNodesWithText("01").assertCountEquals(2)
        compose.onNodeWithText("04").assertExists()
    }

    @Test fun drawnResultsShowFirstSecondDrawCloversAndSelectionRewards() {
        show {
            ConfirmedBetDrawnResults(
                bet(),
                Color.Black,
                Color.White,
                draw().copy(
                    secondDrawNumbers = listOf(3),
                    clovers = listOf(5),
                    favoriteTeamReward = DrawSelectionReward("Flamengo", 1, "10"),
                    luckyMonthReward = DrawSelectionReward("Outubro", 1, "10")
                )
            )
        }
        compose.onNodeWithText("Números sorteados").assertExists()
        compose.onNodeWithText("1º Sorteio").assertExists()
        compose.onNodeWithText("2º Sorteio").assertExists()
        compose.onNodeWithText("Trevos").assertExists()
        compose.onNodeWithText("Time sorteado: Flamengo").assertExists()
        compose.onNodeWithText("Mês sorteado: Outubro").assertExists()
    }

    @Test fun missingDrawDoesNotInventNumbersOrSportsResults() {
        show { ConfirmedBetDrawnResults(bet(), Color.Black, Color.White, null) }
        compose.onNodeWithText("Números sorteados").assertDoesNotExist()
        compose.onNodeWithText("Resultado dos jogos").assertDoesNotExist()
    }

    @Test fun superSevenUsesColumnsForBothSelectedAndDrawnNumbers() {
        show {
            Column {
                SuperSevenSelection(
                    bet().copy(numberColumns = listOf(listOf(9, 8), listOf(7))),
                    Color.Black,
                    Color.White,
                    draw().copy(
                        firstDrawNumberColumns = listOf(
                            listOf(9),
                            listOf(1),
                            emptyList(),
                            emptyList(),
                            emptyList(),
                            emptyList(),
                            emptyList()
                        )
                    )
                )
                ConfirmedBetDrawnResults(
                    bet().copy(modalityId = 7),
                    Color.Black,
                    Color.White,
                    draw().copy(
                        firstDrawNumberColumns = listOf(
                            listOf(9),
                            listOf(1),
                            emptyList(),
                            emptyList(),
                            emptyList(),
                            emptyList(),
                            emptyList()
                        )
                    )
                )
            }
        }
        compose.onNodeWithText("Suas colunas").assertExists()
        compose.onAllNodesWithText("9").assertCountEquals(2)
        compose.onNodeWithText("8").assertExists()
        compose.onAllNodesWithText("7").assertCountEquals(2)
        compose.onNodeWithText("09").assertDoesNotExist()
    }

    @Test fun lotecaRendersHomeAwayAndOneMarkPerChosenOutcome() {
        val match = SportsMatch(1, SportsTeam(1, "Flamengo"), SportsTeam(2, "Vasco"))
        show { LotecaSelection(listOf(LotecaMatchSelection(match, true, true, false)), Color.Black) }
        compose.onNodeWithText("Sua aposta").assertExists()
        compose.onNodeWithText("Flamengo").assertExists()
        compose.onNodeWithText("Vasco").assertExists()
        compose.onAllNodesWithText("✓").assertCountEquals(2)
    }

    @Test fun lotogolDisplaysFivePlusScoreAndExactLowerScore() {
        val match = SportsMatch(1, SportsTeam(1, "Flamengo"), SportsTeam(2, "Vasco"))
        show { LotogolSelection(bet().copy(lotogolSelections = listOf(LotogolMatchSelection(match, 5, 2))), Color.Black) }
        compose.onNodeWithText("Flamengo").assertExists()
        compose.onNodeWithText("Vasco").assertExists()
        compose.onNodeWithText("5+").assertExists()
        compose.onNodeWithText("2").assertExists()
    }

    @Test fun complementsAddStateOnlyWhenProvidedAndShowLuckyMonth() {
        val current = mutableStateOf(bet().copy(favoriteTeam = SportsTeam(1, "Flamengo", "RJ"), luckyMonth = LuckyMonth(10, "Outubro")))
        show { ConfirmedBetComplements(current.value, Color.Black) }
        compose.onNodeWithText("Time do coração: Flamengo/RJ").assertExists()
        compose.onNodeWithText("Mês da sorte: Outubro").assertExists()
        compose.runOnIdle { current.value = bet().copy(favoriteTeam = SportsTeam(1, "Flamengo")) }
        compose.onNodeWithText("Time do coração: Flamengo").assertExists()
        compose.onNodeWithText("Mês da sorte: Outubro").assertDoesNotExist()
    }

    @Test fun ticketStatusLabelsDistinguishExpiredPaidAndUnfinishedResults() {
        val status = mutableStateOf(10)
        show { LegacyTicketResultContent(listOf(bet()), TicketResultVisualData(ticketStatusId = status.value), null) }
        mapOf(
            10 to "Concurso prescrito",
            11 to "O prêmio deste bilhete já foi pago. Parabéns :)",
            9 to "Não premiado ou ainda não apurado",
            101 to "Não premiado ou ainda não apurado",
            999 to "Efetivada"
        ).forEach { (code, label) ->
            compose.runOnIdle { status.value = code }
            compose.onNodeWithText(label).assertExists()
        }
        compose.onNodeWithText("Aposte\nagora").assertDoesNotExist()
    }

    @Test fun winningTicketUsesPrizeSumWhenVisualDataIsAbsentAndCanBetAgain() {
        var bets = 0
        show {
            LegacyTicketResultContent(
                listOf(bet().copy(prize = BigDecimal("12.50")), bet().copy(id = 2, prize = BigDecimal("7.50"))),
                null
            ) {
                bets++
            }
        }
        compose.onNodeWithText("Pode comemorar!").assertExists()
        compose.onNodeWithText("20,00", substring = true).assertExists()
        compose.onNodeWithText("Aposte\nagora").performClick()
        compose.runOnIdle { assertEquals(1, bets) }
    }

    @Test fun winningTicketWithoutAmountUsesAwardMessageAndCurrentContestInformation() {
        show {
            LegacyTicketResultContent(
                emptyList(),
                TicketResultVisualData(
                    modalityId = 8,
                    modalityName = "Lotofácil",
                    ticketStatusId = 103,
                    netPrize = BigDecimal.ZERO,
                    currentEstimate = "R$ 1.000.000,00",
                    currentClosing = "22/10 | 19h00"
                ),
                null
            )
        }
        compose.onNodeWithText("você foi premiado!").assertExists()
        compose.onNodeWithText("lotofácil").assertExists()
        compose.onNodeWithText("Estimativa de prêmio").assertExists()
        compose.onNodeWithText("R$ 1.000.000,00").assertExists()
        compose.onNodeWithText("Encerramento das apostas").assertExists()
        compose.onNodeWithText("22/10 | 19h00").assertExists()
    }

    @Test fun awardRowsAreShownEvenWithoutWinningTicketStatus() {
        show {
            LegacyTicketResultContent(
                emptyList(),
                TicketResultVisualData(
                    ticketStatusId = 9,
                    awardRows = listOf(TicketResultAwardRow("333", "Apurada", "Quadra", "4", "1", "R$ 25,00"))
                ),
                null
            )
        }
        listOf("Concurso", "Situação", "Faixa", "Qtde", "Sorteio", "Valor", "333", "Apurada", "Quadra", "R$ 25,00").forEach {
            compose.onNodeWithText(it).assertExists()
        }
        compose.onNodeWithText("Pode comemorar!").assertDoesNotExist()
    }

    @Test fun favoriteEditorIsHiddenWhenCollapsedOrAlreadySaved() {
        val expanded = mutableStateOf(false)
        show { BetDetailFavoriteEditor(expanded.value, true, "Nome", {}, {}, {}) }
        compose.onNodeWithText("Favoritar aposta").assertDoesNotExist()
        compose.runOnIdle { expanded.value = true }
        compose.onNodeWithText("Favoritar aposta").assertDoesNotExist()
    }

    @Test fun originalPaidTeimosinhaExplainsExchangeAndDispatchesConfirmation() {
        var confirmations = 0
        show { BetDetailTeimosinhaDialog(TeimosinhaNotice.ORIGINAL_PAID) { confirmations++ } }
        compose.onNodeWithText("aposta de troca", substring = true).assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(1, confirmations) }
    }

    @Test fun ordinaryTeimosinhaExplainsFirstContestOnly() {
        show { BetDetailTeimosinhaDialog(null) {} }
        compose.onNodeWithText(
            "A conferência dos jogos da Teimosinha é realizada para todos os concursos apostados, no " +
                "entanto, é exibida apenas a do primeiro concurso."
        ).assertExists()
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { Column { content() } } }
    }
    private fun bet() = ConfirmedBet(1, "Mega-Sena", 123, "22/10/2026", "Efetivada", listOf(1, 2), modalityId = 2)
    private fun draw() =
        DrawResult("Mega-Sena", 123, "22/10/2026", "São Paulo", listOf(1, 3), emptyList(), emptyList(), false, "", "", "", emptyList())
}
