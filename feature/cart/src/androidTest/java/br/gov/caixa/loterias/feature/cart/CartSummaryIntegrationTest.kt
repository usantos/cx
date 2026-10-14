package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

class CartSummaryIntegrationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun groupedComboUsesDisplayedCardCountAndClearingKeepsZeroSummary() {
        val combo = CartItem(8, CartItemKind.COMBO, "Combo", "2 apostas", BigDecimal("9.25"), comboBetCount = 2)
        show(ShoppingCart(1, BigDecimal("25.50"), listOf(combo), totalBetCount = 4))
        compose.onNodeWithText("Total do carrinho", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("R$ 25,50", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("1", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").assertIsDisplayed()
        compose.onNodeWithContentDescription("Excluir todas as apostas").performClick()
        compose.onNodeWithText("Deseja excluir todas as apostas do carrinho?").assertIsDisplayed()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("R$ 0,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithContentDescription("Excluir todas as apostas").assertDoesNotExist()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").assertDoesNotExist()
    }

    @Test fun displayedIndividualCardControlsToolbarDespiteOldEmptyFlatCount() {
        val individual = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "01 02 03 04 05 06", BigDecimal("3.50"))
        show(ShoppingCart(1, BigDecimal.ZERO, listOf(individual), totalBetCount = 0))
        compose.onNodeWithText("mega-sena").assertIsDisplayed()
        compose.onNodeWithText("R$ 0,00", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("1", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithContentDescription("Excluir todas as apostas").assertIsDisplayed()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").assertIsDisplayed()
    }

    @Test fun emptyCartDisablesPaymentBeforeStartingCheckoutGates() {
        val calls = mutableListOf<String>()
        show(ShoppingCart(1, BigDecimal.ZERO, emptyList()), checkoutCalls = calls, minimum = BigDecimal("30"))
        compose.onNodeWithText("Carrinho de apostas").assertIsDisplayed()
        compose.onNodeWithText("Carrinho vazio").assertIsDisplayed()
        compose.onNodeWithText("Ir para pagamento").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(emptyList<String>(), calls) }
    }

    @Test fun favoriteDialogLimitsInputToThirtyCharactersAndCancelClearsTheDraft() {
        val individual = CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "01 02 03 04 05 06", BigDecimal("3.50"))
        show(ShoppingCart(1, BigDecimal("3.50"), listOf(individual)))
        compose.onNodeWithContentDescription("Salvar carrinho favorito").performClick()
        compose.onNodeWithText("Carrinho Favorito").assertIsDisplayed()
        compose.onNodeWithContentDescription("Nome do carrinho favorito")
            .performTextInput("x".repeat(40))
        compose.onNodeWithContentDescription("Nome do carrinho favorito").assertTextEquals("x".repeat(30))
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Carrinho Favorito").assertDoesNotExist()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").performClick()
        assertEquals("", compose.onNodeWithContentDescription("Nome do carrinho favorito")
            .fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsProperties.EditableText].text)
    }

    private fun show(initial: ShoppingCart, checkoutCalls: MutableList<String>? = null, minimum: BigDecimal = BigDecimal.ZERO) {
        compose.setContent {
            val model = remember {
                CartViewModel(
                    getCurrentCart = { AppResult.Success(initial) },
                    removeCartItem = { error("Unexpected item removal") },
                    clearCart = { AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList(), totalBetCount = 0)) },
                    serverTime = ServerTimeSource { Instant.parse("2026-09-07T12:00:00Z") },
                    verifyPaymentAccount = { checkNotNull(checkoutCalls).add("account"); AppResult.Success(CartPaymentAccountDecision.Allowed) },
                    validateCart = { checkNotNull(checkoutCalls).add("validate"); AppResult.Failure(AppError.Validation("Carrinho vazio")) },
                    hasRecentPurchase = { checkNotNull(checkoutCalls).add("recent:$it"); AppResult.Success(false) },
                    checkCheckoutLocation = { checkNotNull(checkoutCalls).add("location"); AppResult.Success(CheckoutLocationStatus.IN_BRAZIL) },
                    recordLocationRequest = { error("Unexpected location mutation") },
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                    getBettingParameters = br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase {
                        AppResult.Success(br.gov.caixa.loterias.core.domain.betting.BettingParameters(
                            BigDecimal("500"), minimum, 50,
                            listOf(br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters(
                                br.gov.caixa.loterias.core.domain.betting.LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
                                2900, "10/09/2026", 60, 6, 20, 15, emptyList(), emptyList(),
                            )),
                        ))
                    },
                    authenticated = true,
                )
            }
            CaixaDSTheme(darkTheme = false) {
                CurrentCartRoute(model, onBack = {}, onContinueBetting = {}, onPayment = {}, onLogin = {},
                    onAccountRequired = {}, onLocationBlocked = {}, onValidationRedirect = {})
            }
        }
    }
}
