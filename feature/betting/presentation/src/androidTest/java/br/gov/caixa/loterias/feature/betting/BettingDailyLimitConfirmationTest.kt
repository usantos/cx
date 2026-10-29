package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Todos os callbacks de carrinho são locais; este hospedeiro não tem dependências de rede ou pagamento. */
class BettingDailyLimitConfirmationTest {
    @get:Rule val compose = createComposeRule()
    private val message = "O valor máximo de apostas permitida diariamente será excedido com a inclusão dessa aposta. " +
        "Deseja prosseguir com a inclusão da aposta?"

    @Test fun rejectingMA014PreservesNumbersAndAcceptingIncludesOnlyAfterSim() {
        val fixture = Fixture()
        show(fixture, authenticated = true, total = "495.01")
        compose.onNodeWithText("Adicionar ao carrinho").performClick()
        compose.onNodeWithText(message).assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, fixture.sent.size) }
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText(message).assertDoesNotExist()
        compose.runOnIdle {
            assertEquals((1..6).toSet(), fixture.model.state.value.selectedNumbers)
            assertEquals(0, fixture.sent.size)
        }
        compose.onNodeWithText("Adicionar ao carrinho").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, fixture.sent.size)
            assertEquals((1..6).toList(), fixture.sent.single().numbers)
        }
    }

    @Test fun guestAboveTheLimitKeepsNativeDirectAdditionWithoutMA014() {
        val fixture = Fixture()
        show(fixture, authenticated = false, total = "1000")
        compose.onNodeWithText("Adicionar ao carrinho").performClick()
        compose.onNodeWithText(message).assertDoesNotExist()
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, fixture.sent.size) }
    }

    @Test fun authenticatedCartExactlyAtLimitDoesNotAskConfirmation() {
        val fixture = Fixture()
        show(fixture, authenticated = true, total = "495.00")
        compose.onNodeWithText("Adicionar ao carrinho").performClick()
        compose.onNodeWithText(message).assertDoesNotExist()
        compose.onNodeWithText("Aposta adicionada ao carrinho com sucesso!").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, fixture.sent.size) }
    }

    private fun show(fixture: Fixture, authenticated: Boolean, total: String) {
        compose.setContent {
            val model = remember { fixture.create() }
            CaixaDSTheme {
                BettingGameRoute(model, {}, {}, cartTotal = BigDecimal(total), isAuthenticated = authenticated)
            }
        }
    }

    private class Fixture {
        val sent = mutableListOf<BetDraft>()
        lateinit var model: BettingGameViewModel
        fun create(): BettingGameViewModel {
            val game = LotteryGameParameters(
                LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 2900, "21/10/2026",
                60, 6, 7, 10, listOf(2), listOf(BetPrice(6, 0, BigDecimal("5.00")))
            )
            return BettingGameViewModel(
                2,
                GetBettingParametersUseCase {
                    AppResult.Success(BettingParameters(BigDecimal("500"), BigDecimal("30"), 100, listOf(game)))
                },
                AddBetToCartUseCase {
                    sent += it
                    AppResult.Success(ShoppingCart(1, BigDecimal("500"), emptyList()))
                },
                FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                initialNumbers = (1..6).toList()
            ).also { model = it }
        }
    }
}
