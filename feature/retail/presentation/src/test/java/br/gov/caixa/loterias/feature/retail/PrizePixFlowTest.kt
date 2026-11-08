package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixKeySource
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrizePixFlowTest : PrizeRedemptionTestFixture() {
    @Test fun `CPF redemption sends the selected key instead of the beneficiary display key`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix()
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)
        assertEquals("Pessoa", vm.state.value.pixBeneficiary?.name)
        vm.confirmPix()
        advanceUntilIdle()
        assertEquals(consultedKey, redemption?.third)
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
    }

    @Test fun `other key requires ownership and preserves the consulted key through confirmation`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.selectOtherPixKey()
        vm.changePixKey("fixture@example.test")
        vm.consultPix()
        advanceUntilIdle()
        assertEquals(0, consultCalls)
        vm.dismissError()
        vm.togglePixOwnership()
        vm.consultPix()
        advanceUntilIdle()
        vm.changePixKey("changed@example.test")
        vm.selectCpfPixKey()
        vm.togglePixOwnership()
        vm.confirmPix()
        advanceUntilIdle()
        assertEquals("fixture@example.test", consultedKey)
        assertEquals(consultedKey, redemption?.third)
        assertEquals(PrizePixKeySource.OTHER, vm.state.value.pixKeySource)
    }

    @Test fun `consultation accepts one request and blocks leaving or editing until it completes`() = runTest(dispatcher) {
        consultPending = CompletableDeferred()
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix()
        vm.consultPix()
        runCurrent()
        vm.selectOtherPixKey()
        vm.changePixKey("changed@example.test")
        assertFalse(vm.back())
        assertEquals(PrizeRedemptionStage.PIX_ENTRY, vm.state.value.stage)
        assertTrue(vm.state.value.loading)
        assertEquals(1, consultCalls)
        consultPending!!.complete(Unit)
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)
        assertEquals(PrizePixKeySource.CPF, vm.state.value.pixKeySource)
        assertEquals("", vm.state.value.pixKey)
    }

    @Test fun `cancel accepts one request and keeps confirmation until cancellation completes`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix()
        advanceUntilIdle()
        cancelPending = CompletableDeferred()
        vm.cancelPixAndExit()
        vm.cancelPixAndExit()
        runCurrent()
        vm.confirmPix()
        assertFalse(vm.back())
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)
        assertEquals(1, cancelCalls)
        assertEquals(0, redemptionCalls)
        cancelPending!!.complete(Unit)
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertNull(vm.state.value.pixBeneficiary)
    }

    @Test fun `reopening Pix starts a fresh key choice like the legacy activity`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.selectOtherPixKey()
        vm.changePixKey("fixture@example.test")
        vm.togglePixOwnership()
        vm.back()
        vm.select(PrizeRedemptionChannel.PIX)
        assertEquals(PrizePixKeySource.CPF, vm.state.value.pixKeySource)
        assertEquals("", vm.state.value.pixKey)
        assertFalse(vm.state.value.pixOwnershipAccepted)
        assertNull(vm.state.value.pixBeneficiary)
    }

    @Test fun `Pix operations cannot bypass key confirmation or run from another channel`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.consultPix()
        vm.cancelPixAndExit()
        vm.confirmPix()
        advanceUntilIdle()
        assertEquals(0, consultCalls)
        assertEquals(0, cancelCalls)
        assertEquals(0, redemptionCalls)
        vm.select(PrizeRedemptionChannel.PIX)
        vm.confirmPix()
        vm.cancelPixAndExit()
        advanceUntilIdle()
        assertEquals(0, redemptionCalls)
        assertEquals(0, cancelCalls)
    }

    @Test fun `failed cancellation preserves confirmation and allows explicit retry`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix()
        advanceUntilIdle()
        cancelResult = AppResult.Failure(AppError.Network)
        vm.cancelPixAndExit()
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)
        assertFalse(vm.state.value.loading)
        assertEquals(1, cancelCalls)
        vm.dismissError()
        cancelResult = AppResult.Success(Unit)
        vm.cancelPixAndExit()
        advanceUntilIdle()
        assertEquals(2, cancelCalls)
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
    }

    @Test fun `failed consultation stays in key choice and requires explicit retry`() = runTest(dispatcher) {
        consultResult = AppResult.Failure(AppError.Network)
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix()
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_ENTRY, vm.state.value.stage)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.pixBeneficiary)
        assertEquals(1, consultCalls)
        vm.dismissError()
        consultResult = AppResult.Success(
            br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary("***", "Pessoa", "key", "Banco")
        )
        vm.consultPix()
        advanceUntilIdle()
        assertEquals(2, consultCalls)
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)
    }

}
