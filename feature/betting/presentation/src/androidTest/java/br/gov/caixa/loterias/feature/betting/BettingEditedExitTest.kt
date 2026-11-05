package br.gov.caixa.loterias.feature.betting

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.contract.betting.model.BetPrice
import br.gov.caixa.loterias.core.domain.contract.betting.model.BettingParameters
import br.gov.caixa.loterias.core.domain.contract.betting.model.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.contract.betting.model.LotteryModality
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BettingEditedExitTest {
    @get:Rule val compose = createComposeRule()

    @Test fun editedTicketRequiresCancelOrConfirmBeforeReturningHome() {
        var exits = 0
        val game =
            LotteryGameParameters(
                LotteryModality(
                    2,
                    "Mega-Sena",
                    "MEGA_SENA"
                ),
                3067, "06/10/2026", 60, 6, 6, 100, listOf(0), listOf(BetPrice(6, 0, BigDecimal("6")))
            )
        val vm =
            BettingGameViewModel(
                2,
                GetBettingParametersUseCase {
                    AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game)))
                },
                object : AddBetToCartUseCase {
                    override suspend fun invoke(draft: BetDraft): AppResult<ShoppingCart> = error("No cart submission permitted")
                }
            )
        compose.setFeatureContent { LoteriasCaixaDSTheme(darkTheme = false) { BettingGameRoute(vm, { exits++ }, {}) } }
        compose.waitUntil(10_000) { vm.state.value.game != null }
        compose.runOnIdle { vm.toggleNumber(1) }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText(
            "Ao voltar para a página inicial, sua aposta em edição será descartada. Deseja continuar?"
        ).assertIsDisplayed()
        assertEquals(0, exits)
        compose.onNodeWithText("Cancelar").performClick()
        assertEquals(0, exits)
        assertEquals(setOf(1), vm.state.value.selectedNumbers)
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Confirmar").performClick()
        assertEquals(1, exits)
    }
}
