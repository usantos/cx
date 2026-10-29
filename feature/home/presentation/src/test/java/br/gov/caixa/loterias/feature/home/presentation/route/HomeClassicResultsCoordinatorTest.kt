package br.gov.caixa.loterias.feature.home.presentation.route

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeResultsCoordinator
import br.gov.caixa.loterias.feature.home.presentation.mapper.toHomeLotteries
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.home.presentation.state.reduceHomeState
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.usecase.GetLatestResultUseCase
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeClassicResultsCoordinatorTest {
    private val lotteries = BettingParameters(
        BigDecimal.TEN,
        BigDecimal.TEN,
        10,
        listOf("MEGA_SENA", "QUINA").mapIndexed { index, code ->
            LotteryGameParameters(
                LotteryModality(index + 1, code, code), 3073,
                "22/10/2026 21:00:00", 60, 6, 20, 10, emptyList(), emptyList()
            )
        }
    ).toHomeLotteries(false)
    private fun result(code: String) = DrawResult(
        code, 3072, "21/10/2026", "São Paulo",
        listOf(1, 2, 3, 4, 5, 6), emptyList(), emptyList(), false, "", "", "", emptyList()
    )

    private inner class Fixture(scope: TestScope) {
        val store = reduxStateStore<HomeUiState, HomeStateAction>(HomeUiState(), ::reduceHomeState)
        val requests = mutableListOf<String>()
        val reply = mutableMapOf<String, CompletableDeferred<AppResult<DrawResult>>>()
        val coordinator = HomeResultsCoordinator(
            store,
            scope,
            FixedCoroutineDispatchers(StandardTestDispatcher(scope.testScheduler)),
            GetLatestResultUseCase { code ->
                requests += code
                reply.getOrPut(code) { CompletableDeferred() }.await()
            }
        )
        val state get() = store.value.results
        fun toggle(page: Int) = coordinator.toggle(page, lotteries[page % lotteries.size].resultCode)
        fun receive(code: String, value: AppResult<DrawResult>) {
            reply.getValue(code).complete(value)
            reply.remove(code)
        }
    }

    @Test fun `loading blocks repeated requests and leaves card closed until success`() = runTest {
        val f = Fixture(this)
        try {
            f.toggle(0)
            runCurrent()
            assertEquals(listOf("megasena"), f.requests)
            assertNull(f.state.expandedPage)
            assertEquals(0, f.state.pendingPage)
            f.toggle(1)
            runCurrent()
            assertEquals(1, f.requests.size)
            f.receive("megasena", AppResult.Success(result("MEGA_SENA")))
            runCurrent()
            assertEquals(0, f.state.expandedPage)
            assertNull(f.state.pendingPage)
            assertNull(f.state.error)
        } finally {
            f.store.close()
        }
    }

    @Test fun `failure dismisses loading leaves closed and permits retry`() = runTest {
        val f = Fixture(this)
        try {
            f.toggle(0)
            runCurrent()
            f.receive("megasena", AppResult.Failure(AppError.Validation("Falha na consulta")))
            runCurrent()
            assertNull(f.state.expandedPage)
            assertNull(f.state.pendingPage)
            assertEquals("Falha na consulta", f.state.error)
            f.coordinator.dismissError()
            assertNull(f.state.error)
            f.toggle(0)
            runCurrent()
            assertEquals(2, f.requests.size)
            f.receive("megasena", AppResult.Success(result("megasena")))
            runCurrent()
        } finally {
            f.store.close()
        }
    }

    @Test fun `each modality keeps its own result when closed and reopened`() = runTest {
        val f = Fixture(this)
        try {
            f.toggle(0)
            runCurrent()
            val mega = result("megasena")
            f.receive("megasena", AppResult.Success(mega))
            runCurrent()
            f.toggle(0)
            assertNull(f.state.expandedPage)
            f.toggle(1)
            runCurrent()
            f.receive("quina", AppResult.Success(result("quina")))
            runCurrent()
            f.toggle(1)
            f.toggle(0)
            assertEquals(listOf("megasena", "quina"), f.requests)
            assertEquals(mega, f.state.cache[f.state.expandedModality])
        } finally {
            f.store.close()
        }
    }

    @Test fun `stale response from another modality cannot expand pending card`() = runTest {
        val f = Fixture(this)
        try {
            f.toggle(1)
            runCurrent()
            assertNull(f.state.expandedPage)
            assertEquals(1, f.state.pendingPage)
            f.receive("quina", AppResult.Success(result("megasena")))
            runCurrent()
            assertNull(f.state.expandedPage)
            assertNotNull(f.state.error)
        } finally {
            f.store.close()
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun `resale expiry refreshes each deadline once and ignores expired data`() = kotlinx.coroutines.test.runTest {
        val initial = java.time.Instant.parse("2026-10-21T21:00:00Z")
        val fixtures = listOf(
            lotteries[0].copy(poolResaleClosingDateTime = "21/10/2026 18:00:01"),
            lotteries[1].copy(poolResaleClosingDateTime = "21/10/2026 18:00:02"),
            lotteries[0].copy(poolResaleClosingDateTime = "21/10/2026 17:00:00")
        )
        val calls = mutableListOf<Long>()
        refreshHomeAtPoolResaleExpirations(
            fixtures,
            now = { initial.plusMillis(testScheduler.currentTime) },
            refresh = { calls += testScheduler.currentTime }
        )
        assertEquals(listOf(1001L, 2001L), calls)
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun `resale exactly at server closing refreshes after inclusive boundary`() = kotlinx.coroutines.test.runTest {
        val serverTime = java.time.Instant.parse("2026-10-21T21:00:00Z")
        val calls = mutableListOf<Long>()
        refreshHomeAtPoolResaleExpirations(
            listOf(lotteries[0].copy(poolResaleClosingDateTime = "21/10/2026 18:00:00")),
            now = { serverTime.plusMillis(testScheduler.currentTime) },
            refresh = { calls += testScheduler.currentTime }
        )
        assertEquals(listOf(1L), calls)
    }

    @Test fun `looped pages share cache by modality`() = runTest {
        val f = Fixture(this)
        try {
            f.toggle(0)
            runCurrent()
            f.receive("megasena", AppResult.Success(result("megasena")))
            runCurrent()
            f.toggle(0)
            f.toggle(2)
            assertEquals(2, f.state.expandedPage)
            assertEquals(1, f.requests.size)
        } finally {
            f.store.close()
        }
    }
}
