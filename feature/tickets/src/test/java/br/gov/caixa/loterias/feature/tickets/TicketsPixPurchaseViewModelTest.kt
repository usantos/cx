package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.domain.tickets.*
import br.gov.caixa.loterias.core.domain.payment.*
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TicketsPixPurchaseViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val pending = Purchase(9003, "01/10/2026", "Aguardando pagamento", "Pix", BigDecimal.TEN,
        statusId = 10, paymentMethodId = 6, pixCopyPaste = "CONTROLLED-CODE-NOT-PAYABLE", paymentExpiresAt = "2099-01-01T00:00:00")
    private var detail = pending
    private var status: AppResult<PurchaseProgress> = AppResult.Success(PurchaseProgress(9003, 10))
    private val statusIds = mutableListOf<Long>()
    private var detailsCalls = 0
    private var detailsOverride: (suspend (Purchase) -> AppResult<PurchaseDetails>)? = null

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `pending Pix queries the selected existing purchase once without any new purchase operation`() = runTest(dispatcher) {
        val vm = show()
        vm.checkPendingPixStatus(); vm.checkPendingPixStatus()
        advanceUntilIdle()
        assertEquals(listOf(9003L), statusIds)
        assertFalse(vm.state.value.checkingPixPurchase)
        assertFalse(vm.state.value.pixPurchaseCompleted)
        assertEquals(pending, vm.state.value.selectedPurchase)
        assertEquals(1, detailsCalls)
    }

    @Test fun `approved existing purchase refreshes authoritative details but never sends a new charge`() = runTest(dispatcher) {
        val vm = show()
        detail = pending.copy(statusId = 8, status = "Finalizada")
        status = AppResult.Success(PurchaseProgress(9003, 8))
        vm.checkPendingPixStatus(); advanceUntilIdle()
        assertTrue(vm.state.value.pixPurchaseCompleted)
        assertEquals(detail, vm.state.value.selectedPurchase)
        assertEquals(2, detailsCalls)
        vm.checkPendingPixStatus(); vm.resumePixPurchaseAfterRedirect(); advanceUntilIdle()
        assertEquals(listOf(9003L), statusIds)
    }

    @Test fun `unknown and negative statuses never approve an existing Pix purchase`() = runTest(dispatcher) {
        for (id in listOf(0, 3, 4, 5, 10, 12)) {
            val before = statusIds.size
            status = AppResult.Success(PurchaseProgress(9003, id))
            val vm = show(); vm.checkPendingPixStatus(); advanceUntilIdle()
            assertFalse(vm.state.value.pixPurchaseCompleted)
            assertEquals(before + 1, statusIds.size)
        }
    }

    @Test fun `redirect pauses status queries and return does not automatically query or resend`() = runTest(dispatcher) {
        status = AppResult.Success(PurchaseProgress(9003, 0, redirect = PurchaseRedirect.CART))
        val vm = show(); vm.checkPendingPixStatus(); advanceUntilIdle()
        assertEquals(PurchaseRedirect.CART, vm.state.value.pixPurchaseRedirect)
        assertTrue(vm.consumePixPurchaseRedirect(PurchaseRedirect.CART))
        assertFalse(vm.consumePixPurchaseRedirect(PurchaseRedirect.CART))
        vm.checkPendingPixStatus(); advanceUntilIdle()
        assertEquals(1, statusIds.size)
        vm.resumePixPurchaseAfterRedirect(); advanceUntilIdle()
        assertEquals(1, statusIds.size)
        assertEquals(pending, vm.state.value.selectedPurchase)
    }

    @Test fun `failed query preserves existing code and does not fabricate approval`() = runTest(dispatcher) {
        status = AppResult.Failure(AppError.Network)
        val vm = show(); vm.checkPendingPixStatus(); advanceUntilIdle()
        assertEquals(1, statusIds.size)
        assertFalse(vm.state.value.pixPurchaseCompleted)
        assertFalse(vm.state.value.checkingPixPurchase)
        assertEquals(pending.pixCopyPaste, vm.state.value.selectedPurchase?.pixCopyPaste)
        assertNotNull(vm.state.value.errorMessage)
    }

    @Test fun `late details failure from a previously approved purchase cannot replace another purchase screen`() = runTest(dispatcher) {
        val refresh = CompletableDeferred<AppResult<PurchaseDetails>>()
        val other = pending.copy(id = 9005, statusId = 8, status = "Finalizada")
        detailsOverride = { purchase ->
            if (purchase.id == pending.id && detailsCalls == 2) refresh.await()
            else AppResult.Success(PurchaseDetails(purchase, emptyList()))
        }
        status = AppResult.Success(PurchaseProgress(pending.id, 8))
        val vm = show()
        vm.checkPendingPixStatus(); dispatcher.scheduler.runCurrent()
        vm.loadPurchase(other); dispatcher.scheduler.runCurrent()
        refresh.complete(AppResult.Failure(AppError.Network)); advanceUntilIdle()
        assertEquals(other, vm.state.value.selectedPurchase)
        assertNull(vm.state.value.errorMessage)
        assertFalse(vm.state.value.pixPurchaseCompleted)
    }

    @Test fun `non Pix and completed or cancelled purchase states cannot query pending payment`() = runTest(dispatcher) {
        for (id in listOf(3L, 4L, 5L, 8L, 12L)) {
            detail = pending.copy(statusId = id, status = "Controlled status $id")
            val vm = show(detail); vm.checkPendingPixStatus(); advanceUntilIdle()
        }
        detail = pending.copy(paymentMethodId = 1, paymentMethod = "Mercado Pago")
        val vm = show(detail); vm.checkPendingPixStatus(); advanceUntilIdle()
        assertTrue(statusIds.isEmpty())
    }

    private suspend fun show(purchase: Purchase = pending): TicketsViewModel {
        val vm = TicketsViewModel(
            getConfirmedBets = GetConfirmedBetsUseCase { AppResult.Success(emptyList()) },
            getPurchases = GetPurchasesUseCase { AppResult.Success(emptyList()) },
            getPurchaseBets = GetPurchaseBetsUseCase { AppResult.Success(emptyList()) },
            checkTicket = CheckTicketUseCase { AppResult.Success(emptyList()) },
            getPurchaseDetails = GetPurchaseDetailsUseCase { purchase -> detailsCalls++; detailsOverride?.invoke(purchase) ?: AppResult.Success(PurchaseDetails(detail, emptyList())) },
            getPixPurchaseStatus = GetPurchaseProgressUseCase { statusIds.add(it); status },
            dispatchers = FixedCoroutineDispatchers(dispatcher),
        )
        vm.loadPurchase(purchase)
        dispatcher.scheduler.runCurrent()
        return vm
    }
}
