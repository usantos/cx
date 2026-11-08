package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsPage
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsConfigurationUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.TicketsHistoryBets
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.reduceTicketsState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TicketsBetHistoryPaginationTest {
    private val recent = TicketPeriod(45, description = "Últimos 45 dias")
    private val ninetyDays = TicketPeriod(90, description = "Últimos 90 dias")
    private val bet = ConfirmedBet(1, "Mega-Sena", 1, "01/11/2026", "Ativa", emptyList())

    @Test fun `selecting ninety days during a pending page loads the new period and rejects the old response`() = runTest {
        val pending = CompletableDeferred<AppResult<ConfirmedBetsPage>>()
        val queries = mutableListOf<Map<String, String>>()
        val (state, history) = history { query ->
            queries += query
            if (query["mes"] == "90") AppResult.Success(ConfirmedBetsPage(listOf(bet.copy(id = 2)))) else pending.await()
        }
        history.loadBets()
        runCurrent()
        state.dispatch(br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsBetFilterAction.PeriodSelected(ninetyDays))
        history.applyBetFilters()
        runCurrent()
        assertEquals(listOf("45", "90"), queries.map { it["mes"] })
        assertEquals(listOf(2L), state.value.bets.map { it.id })
        assertFalse(state.value.isLoading)
        pending.complete(AppResult.Success(ConfirmedBetsPage(listOf(bet))))
        runCurrent()
        assertEquals(listOf(2L), state.value.bets.map { it.id })
    }

    @Test fun `ninety day history appends the next page once and stops at the service total`() = runTest {
        val queries = mutableListOf<Map<String, String>>()
        val (state, history) = history(ninetyDays) { query ->
            queries += query
            AppResult.Success(ConfirmedBetsPage(
                if (query["offset"] == "0") (1..PAGE_SIZE).map { bet.copy(id = it.toLong()) } else listOf(bet.copy(id = 31)),
                totalCount = 31
            ))
        }
        history.applyBetFilters()
        runCurrent()
        assertTrue(state.value.canLoadMoreBets)
        history.loadMoreBets()
        history.loadMoreBets()
        runCurrent()
        assertEquals(listOf("0", "30"), queries.map { it["offset"] })
        assertTrue(queries.all { it["mes"] == "90" && it["size"] == "30" && "ano" !in it })
        assertEquals((1L..31L).toList(), state.value.bets.map { it.id })
        assertFalse(state.value.canLoadMoreBets)
        assertFalse(state.value.isLoadingMoreBets)
        history.loadMoreBets()
        runCurrent()
        assertEquals(2, queries.size)
    }

    @Test fun `period change rejects an old appended page and restarts at zero`() = runTest {
        val pending = CompletableDeferred<AppResult<ConfirmedBetsPage>>()
        val queries = mutableListOf<Map<String, String>>()
        val (state, history) = history(ninetyDays) { query ->
            queries += query
            when {
                query["offset"] != "0" -> pending.await()
                query["mes"] == "45" -> AppResult.Success(ConfirmedBetsPage(listOf(bet.copy(id = 99))))
                else -> AppResult.Success(ConfirmedBetsPage((1..PAGE_SIZE).map { bet.copy(id = it.toLong()) }, totalCount = 31))
            }
        }
        history.applyBetFilters()
        runCurrent()
        history.loadMoreBets()
        runCurrent()
        state.dispatch(br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsBetFilterAction.PeriodSelected(recent))
        history.applyBetFilters()
        runCurrent()
        pending.complete(AppResult.Success(ConfirmedBetsPage(listOf(bet.copy(id = 31)), totalCount = 31)))
        runCurrent()
        assertEquals(listOf("0", "30", "0"), queries.map { it["offset"] })
        assertEquals(listOf(99L), state.value.bets.map { it.id })
        assertFalse(state.value.canLoadMoreBets)
        assertFalse(state.value.isLoadingMoreBets)
    }

    @Test fun `failed appended page preserves visible bets and retries the same offset only on request`() = runTest {
        var fail = true
        val offsets = mutableListOf<String?>()
        val (state, history) = history(ninetyDays) { query ->
            offsets += query["offset"]
            when {
                query["offset"] == "0" -> AppResult.Success(ConfirmedBetsPage((1..PAGE_SIZE).map { bet.copy(id = it.toLong()) }))
                fail -> AppResult.Failure(AppError.Network)
                else -> AppResult.Success(ConfirmedBetsPage(listOf(bet.copy(id = 31))))
            }
        }
        history.applyBetFilters()
        runCurrent()
        history.loadMoreBets()
        runCurrent()
        assertEquals(PAGE_SIZE, state.value.bets.size)
        assertNull(state.value.errorMessage)
        assertTrue(state.value.betPageErrorMessage != null)
        assertFalse(state.value.isLoadingMoreBets)
        fail = false
        history.loadMoreBets()
        runCurrent()
        assertEquals(listOf("0", "30", "30"), offsets)
        assertEquals(PAGE_SIZE + 1, state.value.bets.size)
        assertNull(state.value.betPageErrorMessage)
        assertFalse(state.value.canLoadMoreBets)
    }

    @Test fun `offset advances by raw page size even when overlapping bets are deduplicated`() = runTest {
        val offsets = mutableListOf<String?>()
        val (state, history) = history(ninetyDays) { query ->
            offsets += query["offset"]
            AppResult.Success(ConfirmedBetsPage(when (query["offset"]) {
                "0" -> (1..PAGE_SIZE).map { bet.copy(id = it.toLong()) }
                "30" -> listOf(bet.copy(id = 30), bet.copy(id = 31))
                else -> listOf(bet.copy(id = 32))
            }, totalCount = 33))
        }
        history.applyBetFilters()
        runCurrent()
        history.loadMoreBets()
        runCurrent()
        history.loadMoreBets()
        runCurrent()
        assertEquals(listOf("0", "30", "32"), offsets)
        assertEquals((1L..32L).toList(), state.value.bets.map { it.id })
        assertFalse(state.value.canLoadMoreBets)
    }

    @Test fun `history without total stops after an empty page and does not loop`() = runTest {
        var calls = 0
        val (state, history) = history(ninetyDays) { query ->
            calls++
            AppResult.Success(ConfirmedBetsPage(if (query["offset"] == "0") {
                (1..PAGE_SIZE).map { bet.copy(id = it.toLong()) }
            } else emptyList()))
        }
        history.applyBetFilters()
        runCurrent()
        history.loadMoreBets()
        runCurrent()
        history.loadMoreBets()
        runCurrent()
        assertEquals(2, calls)
        assertEquals(PAGE_SIZE, state.value.bets.size)
        assertFalse(state.value.canLoadMoreBets)
    }

    @Test fun `complete full page does not request another page when the total is already reached`() = runTest {
        var calls = 0
        val (state, history) = history(ninetyDays) {
            calls++
            AppResult.Success(ConfirmedBetsPage((1..PAGE_SIZE).map { bet.copy(id = it.toLong()) }, totalCount = PAGE_SIZE.toLong()))
        }
        history.applyBetFilters()
        runCurrent()
        history.loadMoreBets()
        runCurrent()
        assertEquals(1, calls)
        assertFalse(state.value.canLoadMoreBets)
    }

    private fun TestScope.history(initialPeriod: TicketPeriod = recent, loader: suspend (Map<String, String>) -> AppResult<ConfirmedBetsPage>): Pair<
        br.gov.caixa.loterias.core.presentation.state.ReduxStateStore<TicketsUiState, TicketsStateAction>, TicketsHistoryBets
        > {
        val configuration = ConfirmedBetsConfiguration(defaultPeriod = recent, periods = listOf(recent, ninetyDays))
        val state = reduxStateStore<TicketsUiState, TicketsStateAction>(
            TicketsUiState(betConfiguration = configuration, betFilters = BetFilters(period = initialPeriod)), ::reduceTicketsState
        )
        val source = object : GetConfirmedBetsUseCase {
            override suspend fun invoke(filters: Map<String, String>) = loader(filters).mapBets()
            override suspend fun page(filters: Map<String, String>) = loader(filters)
        }
        val coordinator = TicketsHistoryBets(
            state, backgroundScope, FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)), source,
            GetConfirmedBetsConfigurationUseCase { AppResult.Success(configuration) },
            GetLotteryModalitiesUseCase { AppResult.Success(emptyList()) },
            { true }, { state.dispatch(TicketsStateAction.OperationFailed(it)) }
        )
        return state to coordinator
    }
}

private fun AppResult<ConfirmedBetsPage>.mapBets(): AppResult<List<ConfirmedBet>> = when (this) {
    is AppResult.Success -> AppResult.Success(value.bets)
    is AppResult.Failure -> this
}

private const val PAGE_SIZE = 30
