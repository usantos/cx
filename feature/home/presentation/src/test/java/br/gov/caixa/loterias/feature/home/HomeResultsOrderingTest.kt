package br.gov.caixa.loterias.feature.home

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeResultsCoordinator
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.home.presentation.state.reduceHomeState
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeResultsOrderingTest {
    @Test fun nonCooperativeOldResponseCannotReplaceRefreshedResult() = runTest {
        val oldReply = CompletableDeferred<AppResult<DrawResult>>()
        var requests = 0
        val store = reduxStateStore<HomeUiState, HomeStateAction>(HomeUiState(), ::reduceHomeState)
        val owner = HomeResultsCoordinator(
            store,
            this,
            FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
            GetLatestResultUseCase {
                requests++
                if (requests == 1) {
                    withContext(NonCancellable) { oldReply.await() }
                } else {
                    AppResult.Success(result(2))
                }
            }
        )
        try {
            owner.toggle(0, "megasena")
            runCurrent()
            owner.invalidate()
            owner.toggle(1, "megasena")
            runCurrent()
            assertEquals(2, store.value.results.cache["megasena"]?.contest)
            oldReply.complete(AppResult.Success(result(1)))
            advanceUntilIdle()
            assertEquals(2, store.value.results.cache["megasena"]?.contest)
            assertEquals(1, store.value.results.expandedPage)
            assertNull(store.value.results.pendingPage)
        } finally {
            oldReply.complete(AppResult.Success(result(1)))
            store.close()
        }
    }

    @Test fun closingAndReopeningUsesTheHomeOwnedCache() = runTest {
        var calls = 0
        val store = reduxStateStore<HomeUiState, HomeStateAction>(HomeUiState(), ::reduceHomeState)
        val owner = HomeResultsCoordinator(
            store,
            this,
            FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
            GetLatestResultUseCase {
                calls++
                AppResult.Success(result(1))
            }
        )
        try {
            owner.toggle(0, "megasena")
            advanceUntilIdle()
            assertEquals(0, store.value.results.expandedPage)
            owner.toggle(0, "megasena")
            assertNull(store.value.results.expandedPage)
            owner.toggle(2, "megasena")
            assertEquals(2, store.value.results.expandedPage)
            assertEquals(1, calls)
        } finally {
            store.close()
        }
    }

    private fun result(contest: Int) = DrawResult(
        "megasena", contest, "27/10/2026", "", listOf(1),
        emptyList(), emptyList(), false, "", "", "", emptyList()
    )
}
