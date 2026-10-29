package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplaceUseCase
import br.gov.caixa.loterias.feature.marketplace.presentation.coordinator.MarketplaceFavoriteRetailersCoordinator
import br.gov.caixa.loterias.feature.marketplace.presentation.coordinator.MarketplaceLoadingCoordinator
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceStateAction
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import br.gov.caixa.loterias.feature.marketplace.presentation.state.reduceMarketplaceState
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import java.math.BigDecimal
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pausa a resposta pendente enquanto um resultado de busca mais recente é reduzido, sem pausas temporizadas. */
class MarketplaceConcurrentStateTest {
    private val searchError = "Não foram encontrados bolões disponíveis para os filtros informados. Limpe os filtros e tente novamente."

    @Test fun `favorite result cannot overwrite a newer search failure read concurrently`() {
        withPausedWorker("favorite-state-worker") { state, scope, dispatchers ->
            val coordinator = MarketplaceFavoriteRetailersCoordinator(
                state.store,
                scope,
                dispatchers,
                GetFavoriteRetailersUseCase {
                    state.pauseWorker()
                    AppResult.Success(emptyList())
                },
                SetRetailerFavoriteUseCase { _, _ -> error("No favorite mutation") }
            )
            coordinator.loadFavoriteRetailers()
            state.awaitCapturedSnapshot()
            state.publishSearchFailure(searchError)
            state.releaseSnapshot()
            scope.awaitChildren()

            assertFalse(state.value.isLoading)
            assertEquals(searchError, state.value.errorMessage)
            assertFalse(state.value.favoriteRetailersLoading)
        }
    }

    @Test fun `header result cannot overwrite a newer search failure read concurrently`() {
        withPausedWorker("header-state-worker") { state, scope, dispatchers ->
            val game = LotteryGameParameters(
                LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
                3000, "2026-12-31", 60, 6, 20, 0, emptyList(), emptyList()
            )
            val coordinator = MarketplaceLoadingCoordinator(
                state.store,
                scope,
                dispatchers,
                SearchMarketplaceUseCase { _, _ -> error("Only the header request is started") },
                MarketplaceFilter.forModality(2, 1),
                GetBettingParametersUseCase {
                    state.pauseWorker()
                    AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, games = listOf(game)))
                },
                filteredSearch = false
            )
            coordinator.loadContestHeader()
            state.awaitCapturedSnapshot()
            state.publishSearchFailure(searchError)
            state.releaseSnapshot()
            scope.awaitChildren()

            assertFalse(state.value.isLoading)
            assertEquals(searchError, state.value.errorMessage)
            assertEquals(game, state.value.contestHeader)
        }
    }

    private fun withPausedWorker(workerName: String, assertions: (PausingReduxWorker, CoroutineScope, FixedCoroutineDispatchers) -> Unit) {
        Executors.newSingleThreadExecutor { runnable -> Thread(runnable, workerName) }
            .asCoroutineDispatcher().use { dispatcher ->
                val workerErrors = CopyOnWriteArrayList<Throwable>()
                val errors = CoroutineExceptionHandler { _, error -> workerErrors += error }
                val scope = CoroutineScope(SupervisorJob() + dispatcher + errors)
                val state = PausingReduxWorker(workerName, workerErrors)
                try {
                    assertions(state, scope, FixedCoroutineDispatchers(dispatcher))
                    assertTrue("Worker failed: ${workerErrors.joinToString { it.stackTraceToString() }}", workerErrors.isEmpty())
                } finally {
                    state.releaseSnapshot()
                    scope.cancel()
                    state.store.close()
                }
            }
    }

    private fun CoroutineScope.awaitChildren() {
        val workers = requireNotNull(coroutineContext[Job]).children.toList()
        runBlocking { withTimeout(5_000) { workers.joinAll() } }
    }

    /** A conclusão da consulta é bloqueada antes do despacho; o Redux serializa a redução real do estado. */
    private class PausingReduxWorker(private val workerName: String, private val workerErrors: List<Throwable>) {
        val store = reduxStateStore<MarketplaceUiState, MarketplaceStateAction>(
            MarketplaceUiState(isLoading = true),
            ::reduceMarketplaceState
        )
        val value: MarketplaceUiState get() = store.value
        private val captureNextRead = AtomicBoolean(true)
        private val readThreads = CopyOnWriteArrayList<String>()
        private val captured = CountDownLatch(1)
        private val released = CountDownLatch(1)

        fun pauseWorker() {
            val threadName = Thread.currentThread().name
            readThreads.addIfAbsent(threadName)
            if (threadName.startsWith(workerName) && captureNextRead.compareAndSet(true, false)) {
                captured.countDown()
                check(released.await(WORKER_TIMEOUT_SECONDS, TimeUnit.SECONDS)) { "Test did not release the captured state" }
            }
        }

        fun awaitCapturedSnapshot() {
            val snapshotCaptured = captured.await(WORKER_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            assertTrue(
                "Coordinator did not read its result state; threads=$readThreads; " +
                    "workerErrors=${workerErrors.joinToString { it.stackTraceToString() }}",
                snapshotCaptured
            )
        }
        fun releaseSnapshot() = released.countDown()
        fun publishSearchFailure(message: String) {
            store.dispatch(MarketplaceStateAction.SearchFailed(stale = false, filteredSearch = false))
            assertEquals(message, store.value.errorMessage)
        }
    }
}

private const val WORKER_TIMEOUT_SECONDS = 5L
