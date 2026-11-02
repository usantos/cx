package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrizeRedemptionOwnershipTest : PrizeRedemptionTestFixture() {
    @Test fun `back cannot release a pending pix redemption or allow another channel`() = runTest(dispatcher) {
        val pending = CompletableDeferred<Unit>()
        redemptionPending = pending
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix()
        advanceUntilIdle()
        vm.confirmPix()
        runCurrent()

        assertFalse(vm.back())
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)
        assertTrue(vm.state.value.loading)
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.confirmMercadoPago()
        vm.confirmPix()
        vm.cancelPixAndExit()
        runCurrent()
        assertEquals(1, redemptionCalls)
        assertEquals(0, cancelCalls)

        pending.complete(Unit)
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        assertEquals(PrizeRedemptionChannel.PIX, vm.state.value.selectedChannel)
    }

    @Test fun `failed pix redemption releases ownership for explicit retry`() = runTest(dispatcher) {
        redeemResult = AppResult.Failure(AppError.Network)
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix()
        advanceUntilIdle()
        vm.confirmPix()
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        vm.dismissError()
        vm.confirmPix()
        advanceUntilIdle()
        assertEquals(2, redemptionCalls)
    }
}
