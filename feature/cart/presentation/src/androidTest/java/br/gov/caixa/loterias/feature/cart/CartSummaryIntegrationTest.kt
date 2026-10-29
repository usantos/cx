package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.route.CurrentCartRoute
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CartSummaryIntegrationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun groupedComboUsesCompleteBetCountAndClearingKeepsZeroSummary() {
        val combo = CartItem(8, CartItemKind.COMBO, "Combo", "2 apostas", BigDecimal("9.25"), comboBetCount = 2)
        show(ShoppingCart(1, BigDecimal("25.50"), listOf(combo), totalBetCount = 4))
        compose.onNodeWithText("Total do carrinho", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("R$ 25,50", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("4", useUnmergedTree = true).assertIsDisplayed()
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
        compose.onNodeWithText("0", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithContentDescription("Excluir todas as apostas").assertIsDisplayed()
        compose.onNodeWithContentDescription("Salvar carrinho favorito").assertIsDisplayed()
    }

    @Test fun emptyCartDoesNotOfferPaymentOrStartCheckoutGates() {
        val calls = mutableListOf<String>()
        compose.setContent {
            val model = remember {
                CartViewModel(
                    getCurrentCart = { AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList())) },
                    removeCartItem = { error("An empty cart has no item to remove") },
                    clearCart = { error("Checkout must preserve the empty cart") },
                    serverTime = ServerTimeSource { Instant.parse("2026-09-07T12:00:00Z") },
                    verifyPaymentAccount = {
                        calls += "account"
                        AppResult.Success(CartPaymentAccountDecision.LoginRequired)
                    },
                    validateCart = { error("Guest checkout must not invoke remote validation") },
                    hasRecentPurchase = { total ->
                        assertEquals(BigDecimal.ZERO, total)
                        calls += "recent"
                        AppResult.Success(false)
                    },
                    checkCheckoutLocation = {
                        calls += "location"
                        AppResult.Success(CheckoutLocationStatus.IN_BRAZIL)
                    },
                    recordLocationRequest = { error("An available location must not request permission") },
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
                )
            }
            CaixaDSTheme(darkTheme = false) {
                CurrentCartRoute(
                    model,
                    onBack = {},
                    onContinueBetting = {},
                    onPayment = { error("Guest checkout must not open payment") },
                    onLogin = { calls += "login" },
                    onAccountRequired = { error("Unexpected account completion") },
                    onLocationBlocked = { error("Location is inside Brazil") },
                    onValidationRedirect = { error("Unexpected validation redirect") }
                )
            }
        }
        compose.onNodeWithText("Carrinho de apostas").assertIsDisplayed()
        compose.onNodeWithText("Seu carrinho está vazio").assertIsDisplayed()
        compose.onNodeWithText("Ir para pagamento").assertDoesNotExist()
        compose.onNodeWithContentDescription("Carrinho").assertDoesNotExist()
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
        assertEquals(
            "",
            compose.onNodeWithContentDescription("Nome do carrinho favorito")
                .fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsProperties.EditableText].text
        )
    }

    private fun show(initial: ShoppingCart, checkoutCalls: MutableList<String>? = null, minimum: BigDecimal = BigDecimal.ZERO) {
        compose.setContent {
            val model = remember {
                CartViewModel(
                    getCurrentCart = { AppResult.Success(initial) },
                    removeCartItem = { error("Unexpected item removal") },
                    clearCart = { AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList(), totalBetCount = 0)) },
                    serverTime = ServerTimeSource { Instant.parse("2026-09-07T12:00:00Z") },
                    verifyPaymentAccount = {
                        checkNotNull(checkoutCalls).add("account")
                        AppResult.Success(CartPaymentAccountDecision.Allowed)
                    },
                    validateCart = {
                        checkNotNull(checkoutCalls).add("validate")
                        AppResult.Failure(AppError.Validation("Seu carrinho está vazio"))
                    },
                    hasRecentPurchase = {
                        checkNotNull(checkoutCalls).add("recent:$it")
                        AppResult.Success(false)
                    },
                    checkCheckoutLocation = {
                        checkNotNull(checkoutCalls).add("location")
                        AppResult.Success(CheckoutLocationStatus.IN_BRAZIL)
                    },
                    recordLocationRequest = { error("Unexpected location mutation") },
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                    getBettingParameters = br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase {
                        AppResult.Success(
                            br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters(
                                BigDecimal("500"),
                                minimum,
                                50,
                                listOf(
                                    br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters(
                                        br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
                                        2900, "10/09/2026", 60, 6, 20, 15, emptyList(), emptyList()
                                    )
                                )
                            )
                        )
                    },
                    authenticated = true
                )
            }
            CaixaDSTheme(darkTheme = false) {
                CurrentCartRoute(
                    model,
                    onBack = {},
                    onContinueBetting = {},
                    onPayment = {},
                    onLogin = {},
                    onAccountRequired = {},
                    onLocationBlocked = {},
                    onValidationRedirect = {}
                )
            }
        }
    }
}
