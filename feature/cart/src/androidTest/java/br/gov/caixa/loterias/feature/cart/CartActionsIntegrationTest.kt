package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

class CartActionsIntegrationTest {
    @get:Rule val compose = createComposeRule()
    private val calls = mutableListOf<String>()
    private val individual = CartItem(1, CartItemKind.INDIVIDUAL_BET, "+Milionária", "", BigDecimal.TEN,
        modalityCode = "MAIS_MILIONARIA", contestNumber = 2800, numberQuantity = 6, recurringDraws = 2,
        selectedNumbers = listOf(listOf(1, 2, 3, 4, 5, 6)), selectedClovers = listOf(2, 4),
        badges = setOf(CartItemBadge.RECURRING, CartItemBadge.MIRROR), complementaryLabel = "Complemento")

    @Test fun individualDetailsStayVisibleAndRemovalRequiresConfirmation() {
        show(listOf(individual))
        compose.onNodeWithText("Conc. 2800").assertIsDisplayed()
        compose.onNodeWithText("01 - 02 - 03 - 04 - 05 - 06").assertIsDisplayed()
        compose.onNodeWithText("trevos: 02 - 04").assertIsDisplayed()
        compose.onNodeWithText("Complemento").assertIsDisplayed()
        compose.onNodeWithContentDescription("Excluir").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals(emptyList<String>(), calls) }
        compose.onNodeWithContentDescription("Excluir").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("+milionária").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("remove:1"), calls) }
    }

    @Test fun savingExistingFavoriteWaitsForOverwriteConsent() {
        show(listOf(individual), duplicateFavorite = true)
        compose.onNodeWithContentDescription("Salvar carrinho favorito").performClick()
        compose.onNodeWithContentDescription("Nome do carrinho favorito").performTextInput("Minha sorte")
        compose.onNodeWithText("Salvar").performClick()
        compose.onNodeWithText("Já existe um carrinho favorito com este nome. Deseja substituí-lo?").assertIsDisplayed()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Carrinho salvo nos favoritos.").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(listOf("save:Minha sorte"), calls) }
    }

    @Test fun recentPurchaseCanBeReviewedOrExplicitlyContinued() {
        show(listOf(individual), recentPurchase = true)
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle { assertEquals(emptyList<String>(), calls) }
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals(listOf("payment"), calls) }
    }

    @Test fun loginRequiredPromptsBeforeOpeningAuthorization() {
        show(listOf(individual), account = CartPaymentAccountDecision.LoginRequired)
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle { assertEquals(listOf("login"), calls) }
    }

    @Test fun suspendedAccountCannotProceedToPayment() {
        show(listOf(individual), account = CartPaymentAccountDecision.Suspended)
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(emptyList<String>(), calls) }
    }

    @Test fun unavailableLocationBlocksCheckoutUntilAcknowledgment() {
        show(listOf(individual), location = CheckoutLocationStatus.OUTSIDE_BRAZIL)
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.onNodeWithText(CART_LOCATION_BLOCKED).assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(listOf("home"), calls) }
    }

    @Test fun locationConsentCanBeDeclinedWithoutOpeningSettingsOrPaying() {
        show(listOf(individual), location = CheckoutLocationStatus.PERMISSION_REQUIRED)
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.onNodeWithText(CART_LOCATION_CONSENT).assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertEquals(emptyList<String>(), calls) }
    }

    @Test fun comboAndPoolDetailsNavigateWithTheirActualIdentifiers() {
        val combo = CartItem(8, CartItemKind.COMBO, "Combo", "Duas apostas", BigDecimal.TEN, comboBetCount = 2,
            gameRows = listOf("Mega-Sena", "Quina"))
        val pool = CartItem(9, CartItemKind.POOL_SHARE, "Bolão Mega", "", BigDecimal.TEN,
            poolShare = CartPoolShare("ABC123", 2, 10, ""))
        show(listOf(combo, pool))
        compose.onAllNodesWithText("mais detalhes").onFirst().performScrollTo().performClick()
        compose.onAllNodesWithText("mais detalhes").onLast().performScrollTo().performClick()
        compose.runOnIdle { assertEquals(setOf("combo:8", "pool:ABC123"), calls.toSet()) }
    }

    @Test fun failedLoadShowsRetryAndOnlyDisplaysTheCartAfterRecovery() {
        show(listOf(individual), failFirst = true)
        compose.onNodeWithText("Tentar novamente").assertIsDisplayed().performClick()
        compose.onNodeWithText("+milionária").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
    }

    @Test fun incompleteAccountIsSentToTheRequiredRegistrationStep() {
        show(listOf(individual), account = CartPaymentAccountDecision.CompleteAccount(
            br.gov.caixa.loterias.core.domain.account.PostLoginDestination.REGISTRATION))
        compose.onNodeWithText("Ir para pagamento").performClick()
        compose.runOnIdle { assertEquals(listOf("account"), calls) }
    }

    private fun show(items: List<CartItem>, failFirst: Boolean = false, duplicateFavorite: Boolean = false, recentPurchase: Boolean = false,
                     account: CartPaymentAccountDecision = CartPaymentAccountDecision.Allowed,
                     location: CheckoutLocationStatus = CheckoutLocationStatus.IN_BRAZIL) {
        var cart = ShoppingCart(1, BigDecimal.TEN, items)
        var first = true
        compose.setContent {
            val vm = remember {
                CartViewModel(
                    getCurrentCart = { if (failFirst && first) { first = false; AppResult.Failure(AppError.Network) } else AppResult.Success(cart) },
                    removeCartItem = { item -> calls.add("remove:${item.id}"); cart = cart.copy(items = cart.items.filterNot { it.id == item.id }); AppResult.Success(cart) },
                    clearCart = { cart = cart.copy(items = emptyList()); AppResult.Success(cart) },
                    serverTime = ServerTimeSource { Instant.parse("2026-09-09T12:00:00Z") },
                    verifyPaymentAccount = { AppResult.Success(account) },
                    validateCart = { AppResult.Success(CartValidationResult.Accepted) },
                    hasRecentPurchase = { AppResult.Success(recentPurchase) },
                    checkCheckoutLocation = { AppResult.Success(location) },
                    recordLocationRequest = { AppResult.Success(Unit) },
                    validateFavoriteCartName = { if (duplicateFavorite) AppResult.Failure(AppError.Validation("Já existe")) else AppResult.Success(Unit) },
                    saveCurrentCartFavorite = { calls.add("save:$it"); AppResult.Success(Unit) },
                    authenticated = true,
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                )
            }
            CaixaDSTheme(darkTheme = false) {
                CurrentCartRoute(vm, onBack = {}, onContinueBetting = { calls.add("home") }, onPayment = { calls.add("payment") },
                    onLogin = { calls.add("login") }, onAccountRequired = { calls.add("account") },
                    onLocationBlocked = { calls.add("home") }, onValidationRedirect = { calls.add("redirect") }, onPoolDetail = { calls.add("pool:$it") }, onComboDetail = { calls.add("combo:$it") })
            }
        }
    }
}
