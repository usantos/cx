package br.gov.caixa.loterias.feature.tickets

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.ConfirmedBetNumbers
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketResultContent
import br.gov.caixa.loterias.feature.tickets.presentation.components.LotecaSelection
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultAwardRow
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultVisualData
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketContent
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class TicketsAccessibilityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun filledManualInputsRetainDistinctNames() {
        compose.setContent {
            CaixaDSTheme {
                ManualTicketContent(TicketsUiState(ticketCode = "123456", ticketRetailerCode = "987654"), {
                }, {}, false, ManualTicketActions({}, {}, {}))
            }
        }
        compose.onNode(hasSetTextAction() and hasContentDescription("NSB")).assertExists()
        compose.onNode(hasSetTextAction() and hasContentDescription("Lotérico")).assertExists()
    }

    @Test fun confirmedNumberAnnouncesHitWithoutRelyingOnColor() {
        val bet = ConfirmedBet(1, "Mega-Sena", 1, "27/10/2026", "Apurada", listOf(1, UNHIT_NUMBER))
        val draw = DrawResult("Mega-Sena", 1, "27/10/2026", "", listOf(1), emptyList(), emptyList(), false, "", "", "", emptyList())
        compose.setContent { CaixaDSTheme { ConfirmedBetNumbers(bet, Color.Black, Color.White, draw) } }
        compose.onNodeWithContentDescription("Número 01, acertou").assertExists()
        compose.onNodeWithContentDescription("Número 09, não acertou").assertExists()
    }

    @Test fun ticketAwardsExposeHeaderValuePairsAsRow() {
        val row = TicketResultAwardRow("123", "Premiado", "Sena", "1", "2", "R$ 10,00")
        compose.setContent {
            CaixaDSTheme {
                LegacyTicketResultContent(
                    emptyList(),
                    TicketResultVisualData(modalityId = 2, modalityName = "Mega-Sena", ticketStatusId = 100, awardRows = listOf(row)),
                    null
                )
            }
        }
        compose.onNodeWithContentDescription(
            "Concurso: 123; Situação: Premiado; Faixa: Sena; Qtde: 1; Sorteio: 2; Valor: R$ 10,00"
        ).assertExists()
    }

    @Test fun lotecaSelectionAnnouncesMatchAndAllMarkedOutcomes() {
        val match = SportsMatch(1, SportsTeam(1, "Casa"), SportsTeam(2, "Visitante"))
        compose.setContent { CaixaDSTheme { LotecaSelection(listOf(LotecaMatchSelection(match, true, true, false)), Color.Black) } }
        compose.onNodeWithContentDescription("Jogo 1: Casa x Visitante; selecionados: Casa, Empate").assertExists()
    }
    private companion object {
        const val UNHIT_NUMBER = 9
    }
}
