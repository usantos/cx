package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrizeDeliveryAdmissionTest : PrizeRedemptionTestFixture() {
    @Test
    fun `disabled entry does not load or generate new redemption`() = runTest(dispatcher) {
        entryEnabled = false
        val vm = viewModel()
        vm.load()
        vm.select(PrizeRedemptionChannel.LOTTERY)
        vm.refreshLotteryCode()
        advanceUntilIdle()
        assertEquals(0, detailsCalls)
        assertEquals(0, codeCalls)
        assertEquals(0, redemptionCalls)
    }

    @Test
    fun `disabled module preserves draft and blocks a new submission`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.changeEmail("draft@example.test")
        entryEnabled = false
        vm.confirmMercadoPago()
        vm.select(PrizeRedemptionChannel.LOTTERY)
        vm.refreshLotteryCode()
        advanceUntilIdle()
        assertEquals(0, redemptionCalls)
        assertEquals(0, codeCalls)
        assertEquals("draft@example.test", vm.state.value.email)
        assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
    }

    @Test
    fun `disabling after enqueue prevents redemption and releases the owner without losing draft`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.changeEmail("draft@example.test")
        vm.confirmMercadoPago()
        entryEnabled = false
        advanceUntilIdle()
        assertEquals(0, redemptionCalls)
        assertFalse(vm.state.value.loading)
        assertEquals("draft@example.test", vm.state.value.email)
        assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
        entryEnabled = true
        vm.confirmMercadoPago()
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
    }

    @Test
    fun `disabling during submission preserves owner and completes once`() = runTest(dispatcher) {
        redemptionPending = CompletableDeferred()
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.changeEmail("draft@example.test")
        vm.confirmMercadoPago()
        runCurrent()
        assertEquals(1, redemptionCalls)
        entryEnabled = false
        vm.confirmMercadoPago()
        assertFalse(vm.back())
        redemptionPending!!.complete(Unit)
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        assertEquals(details, vm.state.value.details)
    }
}
