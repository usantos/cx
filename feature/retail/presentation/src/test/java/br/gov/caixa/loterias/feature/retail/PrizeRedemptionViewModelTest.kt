package br.gov.caixa.loterias.feature.retail

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionOption
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.presentation.mapper.canContinuePix
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixKeySource
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeReceipt
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrizeRedemptionViewModelTest : PrizeRedemptionTestFixture() {
    @Test fun `loads options and navigates agency and internal back`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        vm.select(PrizeRedemptionChannel.AGENCY)
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        runCurrent()
        assertEquals(PrizeRedemptionStage.AGENCY, vm.state.value.stage)
        assertEquals("code", vm.state.value.lotteryCode?.code)
        assertFalse(vm.back())
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertTrue(vm.back())
        vm.load()
        advanceUntilIdle()
        assertEquals(details, vm.state.value.details)
    }

    @Test fun `current contest load failure does not hide redemption and supports retry`() = runTest(dispatcher) {
        parametersResult = AppResult.Failure(AppError.Network)
        val vm = viewModel()
        vm.loadCurrentContest() // Detalhes ainda não carregados.
        vm.betNow()
        advanceUntilIdle()
        assertEquals(0, entryCalls)
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertEquals(details, vm.state.value.details)
        assertNotNull(vm.state.value.currentContestError)
        assertFalse(vm.state.value.currentContestLoading)
        parametersResult = AppResult.Success(
            BettingParameters(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                listOf(game.copy(hasNextContest = false), game.copy(contestType = "ESPECIAL"))
            )
        )
        vm.loadCurrentContest()
        vm.loadCurrentContest()
        advanceUntilIdle()
        assertEquals(game, vm.state.value.currentContest)
        assertNull(vm.state.value.currentContestError)
        parametersResult = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, emptyList()))
        vm.loadCurrentContest()
        advanceUntilIdle()
        assertNull(vm.state.value.currentContest)
    }

    @Test fun `bet now waits for eligibility and emits one decision until consumed`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.betNow()
        vm.betNow()
        assertTrue(vm.state.value.loading)
        assertNull(vm.state.value.betEntryDecision)
        advanceUntilIdle()
        assertEquals(1, entryCalls)
        assertEquals(2, requestedModality)
        assertEquals(BetEntryDecision.Ready(game), vm.state.value.betEntryDecision)
        vm.betNow()
        advanceUntilIdle()
        assertEquals(1, entryCalls)
        vm.consumeBetEntryDecision()
        assertNull(vm.state.value.betEntryDecision)
        entryResult = AppResult.Failure(AppError.Validation("Suspensão ativa"))
        vm.betNow()
        advanceUntilIdle()
        assertNull(vm.state.value.betEntryDecision)
        assertEquals("Suspensão ativa", vm.state.value.errorMessage)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `agency guidance requires successful redemption code and permits retry`() = runTest(dispatcher) {
        codeResult = AppResult.Failure(AppError.Validation("Código indisponível"))
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.AGENCY)
        runCurrent()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertFalse(vm.state.value.loading)
        assertNotNull(vm.state.value.errorMessage)
        assertNull(vm.state.value.lotteryCode)
        codeResult = AppResult.Success(LotteryRedemptionCode("qr", "code", "nsb", 1))
        vm.select(PrizeRedemptionChannel.AGENCY)
        runCurrent()
        assertEquals(PrizeRedemptionStage.AGENCY, vm.state.value.stage)
        assertEquals(0, vm.state.value.remainingSeconds)
        assertNull(vm.state.value.errorMessage)
    }

    @Test fun `only loaded backend channels may be selected and agency does not request twice`() = runTest(dispatcher) {
        detailsResult = AppResult.Success(details.copy(options = details.options.filter { it.channel == PrizeRedemptionChannel.AGENCY }))
        val vm = viewModel()
        vm.select(PrizeRedemptionChannel.PIX)
        assertEquals(PrizeRedemptionStage.LOADING, vm.state.value.stage)
        assertNull(vm.state.value.selectedChannel)
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertNull(vm.state.value.selectedChannel)
        vm.select(PrizeRedemptionChannel.AGENCY)
        assertTrue(vm.state.value.loading)
        vm.select(PrizeRedemptionChannel.AGENCY)
        advanceUntilIdle()
        assertEquals(1, codeCalls)
        assertEquals(PrizeRedemptionStage.AGENCY, vm.state.value.stage)
    }

    @Test fun `lottery generates qr counts down refreshes and returns`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.LOTTERY)
        runCurrent()
        assertEquals(60, vm.state.value.remainingSeconds)
        advanceTimeBy(1_001)
        runCurrent()
        assertEquals(59, vm.state.value.remainingSeconds)
        vm.refreshLotteryCode()
        runCurrent()
        assertEquals(60, vm.state.value.remainingSeconds)
        assertFalse(vm.back())
    }

    @Test fun `missing qr prevents lottery rendering but not agency guidance`() = runTest(dispatcher) {
        codeResult = AppResult.Success(LotteryRedemptionCode("", "code", "nsb", 1))
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.LOTTERY)
        runCurrent()
        assertNull(vm.state.value.lotteryCode)
        assertEquals(0, vm.state.value.remainingSeconds)
        assertEquals("Código de resgate indisponível", vm.state.value.errorMessage)
        vm.back()
        vm.select(PrizeRedemptionChannel.AGENCY)
        runCurrent()
        assertEquals(PrizeRedemptionStage.AGENCY, vm.state.value.stage)
        assertNull(vm.state.value.errorMessage)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `pix validates consults cancels and redeems`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        assertEquals(PrizePixKeySource.CPF, vm.state.value.pixKeySource)
        assertTrue(vm.state.value.canContinuePix())
        vm.selectOtherPixKey()
        vm.changePixKey("bad")
        vm.consultPix()
        runCurrent()
        assertTrue(vm.state.value.errorMessage!!.contains("válida"))
        vm.dismissError()
        assertNull(vm.state.value.errorMessage)
        vm.changePixKey("cliente@caixa.gov.br")
        assertFalse(vm.state.value.canContinuePix())
        vm.togglePixOwnership()
        assertTrue(vm.state.value.canContinuePix())
        vm.consultPix()
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)
        assertFalse(vm.back())
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertEquals(0, cancelCalls)
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix()
        advanceUntilIdle()
        vm.confirmPix()
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        assertEquals(PrizeRedemptionChannel.PIX, redemption!!.second)
    }

    @Test fun `mercado pago validates and submits email`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.changeEmail("bad")
        vm.confirmMercadoPago()
        assertEquals("E-mail inválido.", vm.state.value.errorMessage)
        runCurrent()
        assertNull(redemption)
        assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
        vm.changeEmail("cliente@caixa.gov.br")
        vm.confirmMercadoPago()
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        assertEquals("cliente@caixa.gov.br", redemption!!.third)
    }

    @Test fun `pix cancel button cancels backend reservation and exits channel`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix()
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)

        vm.cancelPixAndExit()
        advanceUntilIdle()

        assertEquals(1, cancelCalls)
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertNull(vm.state.value.selectedChannel)
        assertNull(vm.state.value.pixBeneficiary)
    }

    @Test fun `mercado pago preserves legacy recurring bet notice`() = runTest(dispatcher) {
        redeemResult = AppResult.Success(PrizeRedemptionResult.Redeemed("MI053"))
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.confirmMercadoPago()
        advanceUntilIdle()
        assertTrue(vm.state.value.successNoticeMessage!!.contains("recibo de troca"))
        vm.dismissSuccessNotice()
        assertNull(vm.state.value.successNoticeMessage)
    }

    @Test fun `downloads both receipts and exposes save effects`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        val receipts = mutableListOf<PrizeReceipt>()
        val collector = launch { vm.receipts.take(2).collect(receipts::add) }
        runCurrent()
        vm.downloadBetReceipt()
        advanceUntilIdle()
        vm.downloadPrizeReceipt()
        advanceUntilIdle()
        assertEquals(listOf("comprovante-aposta-7.pdf", "comprovante-premio-7.pdf"), receipts.map { it.fileName })
        collector.cancel()
    }

    @Test fun `recovery accepts only original receipt names for this bet`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        val receipts = mutableListOf<PrizeReceipt>()
        val collector = launch { vm.receipts.take(2).collect(receipts::add) }
        runCurrent()
        assertFalse(vm.recoverReceipt("comprovante-aposta-8.pdf"))
        assertFalse(vm.recoverReceipt("untrusted.pdf"))
        assertTrue(vm.recoverReceipt("comprovante-aposta-7.pdf"))
        advanceUntilIdle()
        assertTrue(vm.recoverReceipt("comprovante-premio-7.pdf"))
        advanceUntilIdle()
        assertEquals(listOf("comprovante-aposta-7.pdf", "comprovante-premio-7.pdf"), receipts.map { it.fileName })
        assertArrayEquals(byteArrayOf(1), receipts[0].bytes)
        assertArrayEquals(byteArrayOf(2), receipts[1].bytes)
        collector.cancel()
    }

    @Test fun `failed metadata recovery stops loading without a duplicate screen error or redemption`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        prizePdfResult = AppResult.Failure(AppError.Network)
        assertTrue(vm.recoverReceipt("comprovante-premio-7.pdf"))
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.errorMessage)
        assertTrue(vm.receiptDelivery.state.value.recoveryFailure)
        assertFalse(vm.receiptDelivery.state.value.recovering)
        assertEquals(0, redemptionCalls)
        val receipt = async { vm.receipts.first() }
        runCurrent()
        assertFalse("Failed recovery must not emit a receipt", receipt.isCompleted)
        receipt.cancel()
    }

    @Test fun `maps network validation and unexpected failures`() = runTest(dispatcher) {
        detailsResult = AppResult.Failure(AppError.Network)
        var vm = viewModel()
        advanceUntilIdle()
        assertTrue(vm.state.value.errorMessage!!.contains("conexão"))
        detailsResult = AppResult.Success(details)
        codeResult = AppResult.Failure(AppError.Validation("sem qr"))
        vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.LOTTERY)
        advanceUntilIdle()
        assertEquals("sem qr", vm.state.value.errorMessage)
        consultResult = AppResult.Failure(AppError.Unexpected)
        vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.changePixKey("11999999999")
        vm.consultPix()
        advanceUntilIdle()
        assertTrue(vm.state.value.errorMessage!!.contains("Tente novamente"))
        cancelResult = AppResult.Failure(AppError.Network)
        consultResult = AppResult.Success(PixBeneficiary("", "", "", ""))
        vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.changePixKey("11999999999")
        vm.consultPix()
        advanceUntilIdle()
        vm.cancelPixAndExit()
        advanceUntilIdle()
        assertTrue(vm.state.value.errorMessage!!.contains("conexão"))
        redeemResult = AppResult.Failure(AppError.Unexpected)
        vm.confirmPix()
        advanceUntilIdle()
        assertTrue(vm.state.value.errorMessage!!.contains("Tente novamente"))
        betPdfResult = AppResult.Failure(AppError.Network)
        vm.downloadBetReceipt()
        advanceUntilIdle()
        prizePdfResult = AppResult.Failure(AppError.Validation("pdf"))
        vm.downloadPrizeReceipt()
        advanceUntilIdle()
        assertEquals("pdf", vm.state.value.errorMessage)
    }

    @Test fun `server with no eligible redemption channels exposes no actionable destination`() = runTest(dispatcher) {
        detailsResult = AppResult.Success(details.copy(options = emptyList()))
        val vm = viewModel()
        advanceUntilIdle()
        for (channel in PrizeRedemptionChannel.entries) vm.select(channel)
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertNull(vm.state.value.selectedChannel)
        assertEquals(0, redemptionCalls)
        assertEquals(0, codeCalls)
    }

    @Test fun `only server eligible channels can be selected and explicit redemption is sent once`() = runTest(dispatcher) {
        detailsResult = AppResult.Success(
            details.copy(
                options = listOf(
                    PrizeRedemptionOption(PrizeRedemptionChannel.MERCADO_PAGO, "Mercado Pago", "Receba na hora")
                )
            )
        )
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertNull(vm.state.value.selectedChannel)
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.changeEmail("fixture@example.test")
        vm.confirmMercadoPago()
        vm.confirmMercadoPago()
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertEquals(Triple(7L, PrizeRedemptionChannel.MERCADO_PAGO, "fixture@example.test"), redemption)
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        vm.confirmMercadoPago()
        vm.resumeAfterRedirect()
        vm.load()
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
    }

    @Test fun `Pix confirmation submits the consulted beneficiary key once without reload resubmission`() = runTest(dispatcher) {
        consultResult = AppResult.Success(PixBeneficiary("CONTROLLED", "Fixture", "fixture@example.test", "Fixture Bank"))
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix()
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)
        vm.confirmPix()
        vm.confirmPix()
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertEquals(Triple(7L, PrizeRedemptionChannel.PIX, "fixture@example.test"), redemption)
        vm.confirmPix()
        vm.resumeAfterRedirect()
        vm.load()
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
    }

    @Test fun `refresh replaces the previous timer without an early or double tick`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.LOTTERY)
        runCurrent()
        advanceTimeBy(1_001)
        runCurrent()
        assertEquals(59, vm.state.value.remainingSeconds)
        vm.refreshLotteryCode()
        runCurrent()
        assertEquals(60, vm.state.value.remainingSeconds)
        advanceTimeBy(999)
        runCurrent()
        assertEquals(60, vm.state.value.remainingSeconds)
        advanceTimeBy(1)
        runCurrent()
        assertEquals(59, vm.state.value.remainingSeconds)
        vm.back()
    }

    @Test fun `back stops countdown without resetting its last displayed value`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.LOTTERY)
        runCurrent()
        advanceTimeBy(1_001)
        runCurrent()
        assertEquals(59, vm.state.value.remainingSeconds)
        assertFalse(vm.back())
        advanceTimeBy(5_000)
        runCurrent()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertEquals(59, vm.state.value.remainingSeconds)
        assertNull(vm.state.value.selectedChannel)
        assertTrue(vm.back())
    }

    @Test fun `expired lottery code stays at zero without leaving its channel`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.LOTTERY)
        runCurrent()
        advanceTimeBy(60_000)
        runCurrent()
        assertEquals(0, vm.state.value.remainingSeconds)
        advanceTimeBy(2_000)
        runCurrent()
        assertEquals(0, vm.state.value.remainingSeconds)
        assertEquals(PrizeRedemptionStage.LOTTERY, vm.state.value.stage)
        assertEquals("qr", vm.state.value.lotteryCode?.qrCode)
    }

    @Test fun `receipts downloaded without collector retain bytes and order without replay`() = runTest(dispatcher) {
        val vm = viewModel()
        advanceUntilIdle()
        vm.downloadBetReceipt()
        vm.downloadPrizeReceipt()
        advanceUntilIdle()
        assertFalse(vm.state.value.loading)
        val bet = async { vm.receipts.first() }
        runCurrent()
        val prize = async { vm.receipts.first() }
        runCurrent()
        assertEquals("comprovante-aposta-7.pdf", bet.await().fileName)
        assertArrayEquals(byteArrayOf(1), bet.await().bytes)
        assertEquals("comprovante-premio-7.pdf", prize.await().fileName)
        assertArrayEquals(byteArrayOf(2), prize.await().bytes)
        val next = async { vm.receipts.first() }
        runCurrent()
        assertFalse("Received PDFs must not replay to a new collector", next.isCompleted)
        vm.downloadBetReceipt()
        advanceUntilIdle()
        assertEquals("comprovante-aposta-7.pdf", next.await().fileName)
        assertArrayEquals(byteArrayOf(1), next.await().bytes)
    }

    @Test fun `clearing viewmodel cancels its pending receipt and countdown`() = runTest(dispatcher) {
        val response = CompletableDeferred<AppResult<ByteArray>>()
        var downloadStarted = false
        var downloadCancelled = false
        val vm = viewModel(
            DownloadBetReceiptUseCase {
                downloadStarted = true
                try {
                    response.await()
                } finally {
                    downloadCancelled = true
                }
            }
        )
        val store = ViewModelStore().apply { put("prize", vm) }
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.LOTTERY)
        runCurrent()
        vm.downloadBetReceipt()
        runCurrent()
        assertTrue(downloadStarted)
        assertTrue(vm.state.value.loading)
        val beforeClear = vm.state.value
        store.clear()
        runCurrent()
        assertTrue(downloadCancelled)
        response.complete(AppResult.Success(byteArrayOf(3)))
        advanceTimeBy(2_000)
        runCurrent()
        assertEquals(beforeClear, vm.state.value)
        val receipt = async { vm.receipts.first() }
        runCurrent()
        assertFalse("A cancelled download must not emit a PDF", receipt.isCompleted)
        receipt.cancel()
    }
}
