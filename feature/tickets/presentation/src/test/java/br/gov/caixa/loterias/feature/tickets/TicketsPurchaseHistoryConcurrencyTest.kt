package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseMonthsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private const val PURCHASE_PAGE_SIZE = 10
private const val SINGLE_SELECTED_PURCHASE_ID = 100L
private const val STALE_APPEND_PURCHASE_ID = 11L
private const val NEXT_SELECTED_PURCHASE_ID = 111L
private const val FIRST_SELECTED_PURCHASE_ID = 101
private const val LAST_SELECTED_PURCHASE_ID = 110
private const val SELECTED_STATUS_ID = 7L

@OptIn(ExperimentalCoroutinesApi::class)
class TicketsPurchaseHistoryConcurrencyTest {
    private val dispatcher = StandardTestDispatcher()
    private val purchase = Purchase(9, "01/09/2026", "Aprovada", "Pix", BigDecimal.TEN)
    private val september = PurchaseMonth(9, 2026, "Setembro de 2026")
    private val october = PurchaseMonth(10, 2026, "Outubro de 2026")

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `month change rejects pending append from previous query`() = runTest(dispatcher) {
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<Purchase>>>()
        val oldPage = (1..PURCHASE_PAGE_SIZE).map { purchase.copy(id = it.toLong()) }
        val selectedPage = listOf(purchase.copy(id = SINGLE_SELECTED_PURCHASE_ID))
        val model = viewModel(purchasesLoader = { query ->
            when {
                query["offset"] == "10" -> pending.await()
                query["mes"] == "10" -> AppResult.Success(selectedPage)
                else -> AppResult.Success(oldPage)
            }
        })
        model.loadPurchases()
        advanceUntilIdle()
        model.loadMorePurchases()
        advanceUntilIdle()
        model.selectPurchaseMonth(october)
        advanceUntilIdle()
        pending.complete(AppResult.Success(listOf(purchase.copy(id = STALE_APPEND_PURCHASE_ID))))
        advanceUntilIdle()
        assertEquals(october, model.state.value.purchaseFilters.month)
        assertEquals(selectedPage, model.state.value.purchases)
        assertFalse(model.state.value.canLoadMore)
        assertFalse(model.state.value.isLoadingMore)
    }

    @Test fun `month change during first page starts selected query and ignores old failure`() = runTest(dispatcher) {
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<Purchase>>>()
        val selectedPage = listOf(purchase.copy(id = SINGLE_SELECTED_PURCHASE_ID))
        val model = viewModel(purchasesLoader = { query ->
            if (query["mes"] == "10") AppResult.Success(selectedPage) else pending.await()
        })
        model.loadPurchases()
        advanceUntilIdle()
        model.selectPurchaseMonth(october)
        advanceUntilIdle()
        pending.complete(AppResult.Failure(AppError.Network))
        advanceUntilIdle()
        assertEquals(october, model.state.value.purchaseFilters.month)
        assertEquals(selectedPage, model.state.value.purchases)
        assertFalse(model.state.value.isLoading)
        assertNull(model.state.value.errorMessage)
    }

    @Test fun `applied filters supersede pending page and reject stale failure`() = runTest(dispatcher) {
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<Purchase>>>()
        val selectedPage = listOf(purchase.copy(id = SINGLE_SELECTED_PURCHASE_ID))
        val model = viewModel(purchasesLoader = { query ->
            if (query["situacao"] == "7") AppResult.Success(selectedPage) else pending.await()
        })
        model.loadPurchases()
        advanceUntilIdle()
        model.selectPurchaseStatus(SELECTED_STATUS_ID)
        model.applyPurchaseFilters()
        advanceUntilIdle()
        pending.complete(AppResult.Failure(AppError.Network))
        advanceUntilIdle()
        assertEquals(selectedPage, model.state.value.purchases)
        assertFalse(model.state.value.isLoading)
        assertNull(model.state.value.errorMessage)
    }

    @Test fun `failed replacement page blocks append until current query has a first page`() = runTest(dispatcher) {
        val queries = mutableListOf<Map<String, String>>()
        var replacementFails = true
        val oldPage = (1..PURCHASE_PAGE_SIZE).map { purchase.copy(id = it.toLong()) }
        val selectedPage = (FIRST_SELECTED_PURCHASE_ID..LAST_SELECTED_PURCHASE_ID).map { purchase.copy(id = it.toLong()) }
        val model = viewModel(purchasesLoader = { query ->
            queries += query
            when {
                query["mes"] != "10" -> AppResult.Success(oldPage)
                replacementFails -> AppResult.Failure(AppError.Network)
                query["offset"] == "0" -> AppResult.Success(selectedPage)
                else -> AppResult.Success(listOf(purchase.copy(id = NEXT_SELECTED_PURCHASE_ID)))
            }
        })
        model.loadPurchases()
        advanceUntilIdle()
        model.selectPurchaseMonth(october)
        advanceUntilIdle()
        assertTrue(model.state.value.errorMessage != null)
        model.loadMorePurchases()
        advanceUntilIdle()
        assertEquals(listOf("0", "0"), queries.map { it["offset"] })
        replacementFails = false
        model.retryPurchases()
        advanceUntilIdle()
        assertEquals(selectedPage, model.state.value.purchases)
        model.loadMorePurchases()
        advanceUntilIdle()
        assertEquals(listOf("0", "0", "0", "10"), queries.map { it["offset"] })
        assertEquals(selectedPage + purchase.copy(id = NEXT_SELECTED_PURCHASE_ID), model.state.value.purchases)
        assertFalse(model.state.value.canLoadMore)
        assertNull(model.state.value.errorMessage)
    }

    private fun viewModel(purchasesLoader: suspend (Map<String, String>) -> AppResult<List<Purchase>>) = TicketsViewModel(
        getConfirmedBets = GetConfirmedBetsUseCase { AppResult.Success(emptyList()) },
        getPurchases = GetPurchasesUseCase { purchasesLoader(it) },
        getPurchaseBets = GetPurchaseBetsUseCase { AppResult.Success(emptyList()) },
        checkTicket = CheckTicketUseCase { AppResult.Success(emptyList()) },
        dispatchers = FixedCoroutineDispatchers(dispatcher),
        getPurchaseMonths = GetPurchaseMonthsUseCase { AppResult.Success(listOf(september, october)) }
    )
}
