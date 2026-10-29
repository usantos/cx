package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.feature.cart.presentation.mapper.label
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionKind
import br.gov.caixa.loterias.feature.cart.presentation.state.CountdownTicker
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartAdditionViewModel
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.formatRemainingTime
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.reservationRemainingSeconds
import br.gov.caixa.loterias.feature.cart.presentation.mapper.drawDateLabel

@OptIn(ExperimentalCoroutinesApi::class)
class CartAdditionViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test
    fun `regular bet does not start reservation countdown`() = runTest(dispatcherRule.dispatcher) {
        var ticks = 0
        val viewModel = CartAdditionViewModel(CartAdditionKind.BET,
            serverTime = ServerTimeSource { error("An individual bet has no reservation") },
            ticker = CountdownTicker { ticks++ })
        viewModel.refreshReservation()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, ticks)
        assertEquals(0, viewModel.state.value.remainingSeconds)
        assertFalse(viewModel.state.value.expired)
    }

    @Test
    fun `pool reservation counts down and expires at zero`() = runTest(dispatcherRule.dispatcher) {
        var ticks = 0
        var now = Instant.parse("2026-09-03T12:00:00Z")
        val viewModel = CartAdditionViewModel(
            CartAdditionKind.POOL_SHARE,
            serverTime = ServerTimeSource { now },
            expiresAt = "2026-09-03T12:00:02Z",
            ticker = CountdownTicker { ticks++; now = now.plusSeconds(1) },
        )
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, ticks)
        assertEquals(0, viewModel.state.value.remainingSeconds)
        assertTrue(viewModel.state.value.expired)
    }

    @Test
    fun `invalid reservation is immediately expired without a fabricated duration`() {
        val viewModel = CartAdditionViewModel(
            CartAdditionKind.POOL_SHARE,
            serverTime = ServerTimeSource { Instant.parse("2026-09-03T12:00:00Z") },
            expiresAt = "invalid",
            modality = "MEGA_SENA",
            contest = 2800,
            drawDate = "2026-09-10",
            amount = BigDecimal("10.00"),
        )
        assertEquals(0, viewModel.state.value.remainingSeconds)
        assertTrue(viewModel.state.value.expired)
        assertEquals("MEGA_SENA", viewModel.state.value.modality)
        assertEquals(2800, viewModel.state.value.contest)
        assertEquals("2026-09-10", viewModel.state.value.drawDate)
        assertEquals(BigDecimal("10.00"), viewModel.state.value.amount)
    }

    @Test
    fun `remaining time formatter clamps negatives and pads values`() {
        assertEquals("00:00", formatRemainingTime(-1))
        assertEquals("00:09", formatRemainingTime(9))
        assertEquals("15:00", formatRemainingTime(900))
        assertEquals("61:01", formatRemainingTime(3661))
    }

    @Test
    fun `reservation duration accepts backend offset and local Sao Paulo formats`() {
        val now = Instant.parse("2026-09-03T12:00:00Z")
        assertEquals(900, reservationRemainingSeconds("2026-09-03T12:15:00Z", now))
        assertEquals(900, reservationRemainingSeconds("2026-09-03T09:15:00", now))
        assertEquals(0, reservationRemainingSeconds("", now))
        assertEquals(0, reservationRemainingSeconds("invalid", now))
        assertEquals(0, reservationRemainingSeconds("2026-09-03T11:59:00Z", now))
    }

    @Test
    fun `draw date label supports backend iso and legacy formats`() {
        assertEquals("10/09", drawDateLabel("2026-09-10T20:00:00-03:00"))
        assertEquals("10/09", drawDateLabel("10/09/2026"))
        assertEquals("", drawDateLabel("invalid"))
    }

    @Test fun `delayed tick accounts for all elapsed seconds instead of decrementing once`() = runTest(dispatcherRule.dispatcher) {
        var now = Instant.parse("2026-09-03T12:00:00Z")
        var ticks = 0
        val vm = CartAdditionViewModel(CartAdditionKind.POOL_SHARE,
            serverTime = ServerTimeSource { now }, expiresAt = "2026-09-03T12:01:00Z",
            ticker = CountdownTicker { ticks++; now = now.plusSeconds(120) })
        assertEquals(60, vm.state.value.remainingSeconds)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, ticks)
        assertEquals(0, vm.state.value.remainingSeconds)
        assertTrue(vm.state.value.expired)
        now = now.minusSeconds(3600)
        vm.refreshReservation()
        assertTrue(vm.state.value.expired)
        assertEquals(0, vm.state.value.remainingSeconds)
    }

    @Test fun `resume refresh expires a reservation before the pending next tick`() = runTest(dispatcherRule.dispatcher) {
        val origin = Instant.parse("2026-09-03T12:00:00Z")
        var elapsedElsewhere = 0L
        val vm = CartAdditionViewModel(CartAdditionKind.POOL_SHARE,
            serverTime = ServerTimeSource { origin.plusMillis(testScheduler.currentTime + elapsedElsewhere) },
            expiresAt = "2026-09-03T12:15:00Z")
        runCurrent()
        elapsedElsewhere = 30_000L
        vm.refreshReservation()
        assertEquals(870, vm.state.value.remainingSeconds)
        assertFalse(vm.state.value.expired)
        elapsedElsewhere = 901_000L
        vm.refreshReservation()
        assertEquals(0, vm.state.value.remainingSeconds)
        assertTrue(vm.state.value.expired)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, vm.state.value.remainingSeconds)
    }

    @Test fun `reservation deadline survives viewmodel recreation without restarting duration`() = runTest(dispatcherRule.dispatcher) {
        var now = Instant.parse("2026-09-03T12:00:00Z")
        val clock = ServerTimeSource { now }
        val original = CartAdditionViewModel(CartAdditionKind.POOL_SHARE, clock, "2026-09-03T12:15:00Z")
        assertEquals(900, original.state.value.remainingSeconds)
        val store = androidx.lifecycle.ViewModelStore()
        store.put("reservation", original)
        store.clear()
        now = now.plusSeconds(1200)
        val vm = CartAdditionViewModel(CartAdditionKind.POOL_SHARE, clock, "2026-09-03T12:15:00Z")
        vm.refreshReservation()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.expired)
        assertEquals(0, vm.state.value.remainingSeconds)
    }
}
