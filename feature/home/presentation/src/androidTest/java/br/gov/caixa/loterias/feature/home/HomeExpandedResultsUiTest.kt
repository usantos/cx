package br.gov.caixa.loterias.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeDrawResult
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSelectionReward
import java.math.BigDecimal
import org.junit.Rule
import org.junit.Test

/** Referências de regressão de renderização de ModalidadeSorteioResultadoView e dos XMLs de resultado das modalidades. */
class HomeExpandedResultsUiTest {
    @get:Rule val compose = createComposeRule()
    private val lottery = HomeLottery(
        18, "NORMAL", "duplasena", "Dupla Sena", 3073, BigDecimal.ZERO, BigDecimal.ONE,
        "22/10/2026", "", true, HomeLotteryKind.LOTTERY, Color(0xFF9B1B4A), Color(0xFF681536),
        Color.White, R.drawable.legacy_home_trevo_dupla_sena_fundo_escuro,
        R.drawable.icon_data_duplasena, R.drawable.icon_horario_duplasena,
    )

    private fun show(result: DrawResult) {
        compose.setContent {
            CaixaDSTheme {
                Box(Modifier.size(280.dp, 300.dp).background(lottery.darkColor)) {
                    HomeDrawResult(result, lottery.copy(resultCode = result.modality), Modifier.fillMaxSize())
                }
            }
        }
    }

    @Test fun duplaSenaShowsBothDrawsAndPayoutGroupsInScrollableResults() {
        show(result("duplasena").copy(
            firstDrawNumbers = listOf(1, 2, 3, 4, 5, 6),
            secondDrawNumbers = listOf(11, 12, 13, 14, 15, 16),
            payouts = (1..8).map { DrawPayout("000$it", 1, "10") },
        ))
        compose.onNodeWithText("1º sorteio").assertIsDisplayed()
        compose.onNodeWithText("01").assertIsDisplayed()
        compose.onNodeWithText("2º sorteio").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("11").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Premiação – 1º sorteio").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Premiação – 2º sorteio").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Arrecadação total").performScrollTo().assertIsDisplayed()
    }

    @Test fun superSetePreservesSevenColumnHeadersAndSingleDigitDraws() {
        show(result("supersete").copy(
            firstDrawNumbers = List(7) { 0 }, firstDrawNumberColumns = List(7) { listOf(0) },
        ))
        (1..7).forEach { compose.onNodeWithText("$it").assertIsDisplayed() }
        compose.onAllNodesWithText("0").assertCountEquals(7)
        compose.onNodeWithText("00").assertDoesNotExist()
    }

    @Test fun diaDeSorteShowsDrawnMonthAndRewardBandSelection() {
        show(result("diadesorte").copy(
            firstDrawNumbers = listOf(1, 2, 3, 4, 5, 6, 7),
            luckyMonthReward = DrawSelectionReward("Outubro", 3, "2.5"),
            payouts = listOf(DrawPayout("0005", 3, "2.5")),
        ))
        compose.onNodeWithText("Mês de sorte").assertIsDisplayed()
        compose.onNodeWithText("Outubro").assertIsDisplayed()
        compose.onNodeWithText("Mês de sorte\nOutubro").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("3 apostas ganhadoras").performScrollTo().assertIsDisplayed()
    }

    @Test fun zeroWinnerBandHidesPrizeAndUsesLegacyWording() {
        show(result("megasena").copy(
            firstDrawNumbers = listOf(1, 2, 3, 4, 5, 6),
            payouts = listOf(DrawPayout("0001", 0, "999999.99")),
        ))
        compose.onNodeWithText("Sena - 6 números acertados").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Sem ganhadores").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Prêmio:", substring = true).assertDoesNotExist()
    }

    private fun result(modality: String) = DrawResult(
        modality, 3073, "21 outubro 2026", "", emptyList(), emptyList(), emptyList(), false,
        "", "1000", "", emptyList(),
    )
}
