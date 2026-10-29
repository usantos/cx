package br.gov.caixa.loterias.feature.payment

import androidx.compose.ui.test.*
import org.junit.Assert.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.lifecycle.SavedStateHandle
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RecordRecentPurchaseUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.payment.domain.model.DamValidation
import br.gov.caixa.loterias.feature.payment.domain.usecase.DeleteSavedCardUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GeneratePixChargeUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.usecase.ValidateDamUseCase
import java.math.BigDecimal
import java.time.Clock
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentDataRoute
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentViewModel

/** Rota/ViewModel reais apenas com casos de uso locais; nenhuma compra ou cartão salvo real é alterado. */
class PaymentDataCoordinationUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun cancellingPrePurchaseUsesBackWithoutValidationOrGeneration() {
        val fixture = Fixture()
        var backs = 0
        show(fixture, PaymentMode.METHODS, onBack = { backs++ })
        compose.onNodeWithText("Confirmar").assertIsDisplayed()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle {
            assertEquals(1, backs)
            assertEquals(0, fixture.validations)
            assertEquals(0, fixture.generations)
        }
    }

    @Test fun methodsKeepPixHelpAndEmptyProviderAdditionWithNoticeConsumedFirst() {
        val fixture = Fixture()
        var pix = 0
        var help = 0
        val added = mutableListOf<Long>()
        show(fixture, PaymentMode.METHODS, onPix = { pix++ }, onHelp = { help++ }, onAddCard = {
            assertNull(fixture.vm.state.value.pendingAddMethodId)
            added.add(it)
        })
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("Gerar código Pix Copia e cola").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.onNodeWithText("Novo Cartão de Crédito").performScrollTo().performClick()
        compose.onNodeWithText("O cartão de crédito adicionado deve ser de sua titularidade").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(listOf(1L), added)
            assertEquals(1, fixture.validations)
            assertEquals(1, pix)
            assertEquals(1, help)
            assertEquals(0, fixture.generations)
        }
    }

    @Test fun methodsKeepExpansionCardOpenDeleteCancellationAndPopulatedProviderAddition() {
        val cards = (1..3).map { card(it.toString(), "000$it") }
        val fixture = Fixture().apply {
            methods = listOf(PaymentMethod(1, "Mercado Pago"))
            saved[1] = cards
        }
        val opened = mutableListOf<SavedPaymentCard>()
        val added = mutableListOf<Long>()
        show(fixture, PaymentMode.METHODS, onOpenCard = { opened.add(it) }, onAddCard = { added.add(it) })
        compose.onNodeWithText("Confirmar").performClick()
        compose.onNodeWithText("**** 0003").assertDoesNotExist()
        compose.onNodeWithText("Listar Mais").performScrollTo().performClick()
        compose.onNodeWithText("**** 0003").performClick()
        compose.onNodeWithContentDescription("Excluir cartão final 0003").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.onNodeWithText("Adicionar Cartão").performScrollTo().performClick()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Listar Menos").performClick()
        compose.onNodeWithText("**** 0003").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(listOf(cards[2]), opened)
            assertEquals(listOf(1L), added)
            assertTrue(fixture.deletions.isEmpty())
            assertEquals(0, fixture.generations)
        }
    }

    @Test fun savedRecargaDeletionUsesItsTokenAndThenShowsTheEmptyState() {
        val recarga = card("CONTROLLED-RECARGA", "0005").copy(paymentMethodId = 5, customerToken = "CONTROLLED-TOKEN")
        val fixture = Fixture().apply {
            methods = listOf(PaymentMethod(5, "Recarga Pay"))
            saved[5] = listOf(recarga)
        }
        show(fixture, PaymentMode.CARDS)
        compose.onNodeWithContentDescription("Remover cartão").performClick()
        compose.runOnIdle { assertTrue(fixture.deletions.isEmpty()) }
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Você ainda não possui um cartão de crédito cadastrado.", substring = true).assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(listOf("CONTROLLED-TOKEN" to 5L), fixture.deletions)
            assertEquals(0, fixture.generations)
            assertEquals(0, fixture.validations)
        }
    }

    @Test fun visitorCardFlagChangesOnlyEmptyPresentationWithoutLoadingPrivateData() {
        val fixture = Fixture()
        val privateData = mutableStateOf(false)
        show(fixture, PaymentMode.CARDS, autoLoad = false, privateData = { privateData.value })
        compose.onNodeWithText("Cartões cadastrados").assertIsDisplayed()
        compose.onNodeWithText("Você ainda não possui um cartão de crédito cadastrado.", substring = true).assertDoesNotExist()
        compose.runOnIdle { privateData.value = true }
        compose.onNodeWithText("Você ainda não possui um cartão de crédito cadastrado.", substring = true).assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(0, fixture.methodReads)
            assertTrue(fixture.cardReads.isEmpty())
            assertEquals(0, fixture.generations)
        }
    }

    @Test fun interruptedPixShowsRecoveryAndOpensPurchasesWithoutAnotherGeneration() {
        val fixture = Fixture()
        var purchases = 0
        show(fixture, PaymentMode.PIX, savedState = SavedStateHandle(), onPurchases = { purchases++ })
        compose.onNodeWithText("Não foi possível acessar o pagamento. Verifique sua internet.").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Não foi possível recuperar o código Pix. Confira suas compras antes de gerar outro código.").assertIsDisplayed()
        compose.onNodeWithText("Verificar Pagamento").assertDoesNotExist()
        compose.onNodeWithText("Minhas Compras").performClick()
        compose.runOnIdle { fixture.vm.load() }
        compose.runOnIdle {
            assertTrue(fixture.vm.state.value.pixRecoveryRequired)
            assertEquals(1, fixture.generations)
            assertEquals(1, purchases)
        }
    }

    @Test fun replacingViewModelUpdatesTheSavedCardDeleteCallback() {
        val savedCard = card("CONTROLLED-SAVED", "0001")
        val first = Fixture().apply { saved[1] = listOf(savedCard) }
        val second = Fixture().apply { saved[1] = listOf(savedCard) }
        val original = first.model(PaymentMode.CARDS)
        val replacement = second.model(PaymentMode.CARDS)
        val current = mutableStateOf(original)
        compose.setContent { Render(PaymentMode.CARDS, current.value) }
        compose.onNodeWithText("Cartões cadastrados").assertIsDisplayed()
        compose.runOnIdle { current.value = replacement }
        compose.onNodeWithContentDescription("Remover cartão").performClick()
        compose.onNodeWithText("Excluir cartão?").assertIsDisplayed()
        compose.runOnIdle {
            assertNull(original.state.value.deleteCandidate)
            assertEquals(savedCard, replacement.state.value.deleteCandidate)
        }
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle { assertTrue(first.deletions.isEmpty()); assertTrue(second.deletions.isEmpty()) }
    }

    private fun show(
        fixture: Fixture,
        mode: PaymentMode,
        autoLoad: Boolean = true,
        savedState: SavedStateHandle? = null,
        onBack: () -> Unit = {}, onHelp: () -> Unit = {}, onPix: () -> Unit = {},
        onAddCard: (Long) -> Unit = {}, onOpenCard: (SavedPaymentCard) -> Unit = {},
        onPurchases: () -> Unit = {}, privateData: () -> Boolean = { true },
    ) {
        compose.setContent {
            val model = remember { fixture.model(mode, autoLoad, savedState) }
            Render(mode, model, onBack, onHelp, onPix, onAddCard, onOpenCard, onPurchases, privateData())
        }
    }

    @Composable private fun Render(
        mode: PaymentMode, model: PaymentViewModel,
        onBack: () -> Unit = {}, onHelp: () -> Unit = {}, onPix: () -> Unit = {},
        onAddCard: (Long) -> Unit = {}, onOpenCard: (SavedPaymentCard) -> Unit = {},
        onPurchases: () -> Unit = {}, privateData: Boolean = true,
    ) {
        CaixaDSTheme {
            PaymentDataRoute(mode, model, onBack = onBack, onHelp = onHelp, onPix = onPix,
                onAddCard = onAddCard, onOpenCard = onOpenCard, onApproved = {}, onHome = {},
                onPurchases = onPurchases, onRedirect = {}, onRegistration = {}, privateDataEnabled = privateData)
        }
    }

    private class Fixture {
        var methods = listOf(PaymentMethod(6, "Pix"), PaymentMethod(1, "Mercado Pago"))
        val saved = mutableMapOf<Long, List<SavedPaymentCard>>()
        var methodReads = 0
        val cardReads = mutableListOf<Long>()
        var validations = 0
        var generations = 0
        val deletions = mutableListOf<Pair<String, Long>>()
        lateinit var vm: PaymentViewModel

        fun model(mode: PaymentMode, autoLoad: Boolean = true, handle: SavedStateHandle? = null) = PaymentViewModel(
            mode,
            getMethods = GetPaymentMethodsUseCase { methodReads++; AppResult.Success(methods) },
            getCards = GetSavedCardsUseCase { cardReads.add(it); AppResult.Success(saved[it].orEmpty()) },
            deleteCard = DeleteSavedCardUseCase { id, method ->
                deletions.add(id to method)
                val remaining = saved[method].orEmpty().filterNot { it.deletionIdentifier == id }
                saved[method] = remaining
                AppResult.Success(remaining)
            },
            generatePixCharge = GeneratePixChargeUseCase { check(mode == PaymentMode.PIX); generations++; AppResult.Failure(AppError.Network) },
            getPurchaseProgress = GetPurchaseProgressUseCase { error("No status query expected in these fixtures") },
            validateDam = ValidateDamUseCase { validations++; AppResult.Success(DamValidation(false, "")) },
            getCurrentCart = GetCurrentCartUseCase { AppResult.Success(ShoppingCart(1, BigDecimal("12.50"), emptyList())) },
            recordRecentPurchase = RecordRecentPurchaseUseCase { error("No completion expected in these fixtures") },
            clock = Clock.systemUTC(), dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
            ownerKey = if (handle != null) "a".repeat(64) else null, savedStateHandle = handle, autoLoad = autoLoad,
        ).also { vm = it }
    }

    companion object {
        private fun card(id: String, digits: String) = SavedPaymentCard(id, 1, "000000", digits, 12, 2030, 3, "VISA", "CONTROLLED")
    }
}
