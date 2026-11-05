package br.gov.caixa.loterias.feature.tickets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.contract.betting.model.LotecaMatchSelection
import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsMatch
import br.gov.caixa.loterias.core.domain.contract.betting.model.SportsTeam
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.tickets.presentation.components.ConfirmedBetNumbers
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketResultContent
import br.gov.caixa.loterias.feature.tickets.presentation.components.LotecaSelection
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultAwardRow
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultVisualData
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketContentDelivered
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(sdk = [36], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
class TicketsAccessibilityTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun filledManualInputsRetainDistinctNames() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                ManualTicketContentDelivered(TicketsUiState(ticketCode = "123456", ticketRetailerCode = "987654"), {
                }, {}, false, ManualTicketActions({}, {}, {}))
            }
        }
        compose.onNode(hasSetTextAction() and hasContentDescription("NSB")).assertExists()
        compose.onNode(hasSetTextAction() and hasContentDescription("Lotérico")).assertExists()
    }

    @Test fun confirmedNumberAnnouncesHitWithoutRelyingOnColor() {
        val bet = ConfirmedBet(1, "Mega-Sena", 1, "27/10/2026", "Apurada", listOf(1, UNHIT_NUMBER))
        val draw = DrawResult("Mega-Sena", 1, "27/10/2026", "", listOf(1), emptyList(), emptyList(), false, "", "", "", emptyList())
        compose.setFeatureContent { LoteriasCaixaDSTheme { ConfirmedBetNumbers(bet, Color.Black, Color.White, draw) } }
        compose.onNodeWithContentDescription("Número 01, acertou").assertExists()
        compose.onNodeWithContentDescription("Número 09, não acertou").assertExists()
    }

    @Test fun ticketAwardsExposeHeaderValuePairsAsRow() {
        val row = TicketResultAwardRow("123", "Premiado", "Sena", "1", "2", "R$ 10,00")
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
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
        compose.setFeatureContent {
            LoteriasCaixaDSTheme { LotecaSelection(listOf(LotecaMatchSelection(match, true, true, false)), Color.Black) }
        }
        compose.onNodeWithContentDescription("Jogo 1: Casa x Visitante; selecionados: Casa, Empate").assertExists()
    }
    private companion object {
        const val UNHIT_NUMBER = 9
    }
}
