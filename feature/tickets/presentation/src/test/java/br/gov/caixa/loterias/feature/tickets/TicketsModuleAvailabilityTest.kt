package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseDetails
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.RecoverConfirmedBetUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TicketsModuleAvailabilityTest {
    private val dispatcher = StandardTestDispatcher()
    private val bet = ConfirmedBet(1, "Mega-Sena", 1, "hoje", "Ativa", listOf(1))

    private val purchase = Purchase(9, "01/09/2026", "Aprovada", "Pix", BigDecimal.TEN)

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `disabled tickets keeps favorite draft while allowing an existing bet receipt`() = runTest {
        var enabled = true
        var saves = 0
        var receipts = 0
        val vm = BetDetailViewModel(
            bet.copy(modalityId = 2),
            RecoverConfirmedBetUseCase { AppResult.Success(it) },
            DownloadBetReceiptUseCase {
                receipts++
                AppResult.Success(byteArrayOf(1))
            },
            ValidateFavoriteBetUseCase { AppResult.Success(Unit) },
            SaveFavoriteBetUseCase {
                saves++
                AppResult.Success(Unit)
            },
            cartUseCase(),
            FixedCoroutineDispatchers(dispatcher),
            canStartOperation = { enabled }
        )
        advanceUntilIdle()
        vm.toggleFavoriteEditor()
        vm.changeFavoriteName("Minha aposta")
        enabled = false
        vm.submitFavorite()
        vm.downloadReceipt()
        advanceUntilIdle()
        assertEquals(0, saves)
        assertEquals(1, receipts)
        assertEquals("Minha aposta", vm.state.value.favoriteName)
    }

    @Test fun `disabled purchase favorites keeps its name without saving the purchase`() = runTest(dispatcher) {
        var enabled = true
        var writes = 0
        val detail = purchase.copy(status = "Cancelada", statusId = 5, paymentMethodId = 6)
        val vm = TicketsViewModel(
            getConfirmedBets = GetConfirmedBetsUseCase { AppResult.Success(emptyList()) },
            getPurchases = GetPurchasesUseCase { AppResult.Success(emptyList()) },
            getPurchaseBets = GetPurchaseBetsUseCase { AppResult.Success(emptyList()) },
            checkTicket = CheckTicketUseCase { AppResult.Success(emptyList()) },
            dispatchers = FixedCoroutineDispatchers(dispatcher),
            canStartOperation = { enabled },
            getPurchaseDetails = GetPurchaseDetailsUseCase { AppResult.Success(PurchaseDetails(detail, listOf(bet))) },
            validateFavoriteName = ValidateFavoriteCartNameUseCase { AppResult.Success(Unit) },
            savePurchaseAsFavorite = SavePurchaseAsFavoriteUseCase { _, _, _ ->
                writes++
                AppResult.Success(Unit)
            }
        )
        vm.loadPurchase(purchase)
        advanceUntilIdle()
        vm.openFavoriteDialog()
        vm.changeFavoriteName("Minha compra")
        enabled = false
        vm.validateAndSaveFavorite()
        advanceUntilIdle()
        assertEquals(0, writes)
        assertEquals("Minha compra", vm.state.value.favoriteName)
    }

    private fun cartUseCase() = AddBetToCartUseCase {
        AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
    }
}
