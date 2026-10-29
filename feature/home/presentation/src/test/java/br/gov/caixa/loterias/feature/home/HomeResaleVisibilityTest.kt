package br.gov.caixa.loterias.feature.home

import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.feature.home.presentation.coordinator.HomeResaleRefreshCoordinator
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeResaleVisibilityTest {
    @Test fun hiddenHomeCancelsDeadlineAndResumingDoesNotRepeatExpiredRequest() = runTest {
        val initial = Instant.parse("2026-10-27T15:00:00Z")
        val clock = ServerTimeSource { initial.plusMillis(testScheduler.currentTime) }
        var calls = 0
        val owner = HomeResaleRefreshCoordinator(this) { calls++ }
        owner.update(true, listOf("2026-10-27T15:00:01Z"), clock)
        runCurrent()
        advanceTimeBy(HIDE_BEFORE_DEADLINE_MILLIS)
        owner.update(false, emptyList(), clock)
        advanceTimeBy(PASS_HIDDEN_DEADLINE_MILLIS)
        runCurrent()
        assertEquals(0, calls)
        owner.update(true, listOf("2026-10-27T15:00:01Z", "2026-10-27T15:00:03Z"), clock)
        advanceTimeBy(NEXT_INCLUSIVE_DEADLINE_MILLIS)
        runCurrent()
        assertEquals(1, calls)
    }
}

private const val HIDE_BEFORE_DEADLINE_MILLIS = 500L
private const val PASS_HIDDEN_DEADLINE_MILLIS = 1500L
private const val NEXT_INCLUSIVE_DEADLINE_MILLIS = 1001L
