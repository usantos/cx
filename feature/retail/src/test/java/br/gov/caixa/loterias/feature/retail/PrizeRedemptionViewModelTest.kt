package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.retail.*
import br.gov.caixa.loterias.core.domain.betting.*
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrizeRedemptionViewModelTest {
    private var connection = br.gov.caixa.loterias.core.common.ConnectionAvailability.CONNECTED
    private var connectionReads = 0

    @Test fun `connection failures dismiss loading preserve channel and use connectivity at response time`() = runTest(dispatcher) {
        for (reason in br.gov.caixa.loterias.core.domain.service.ServiceConnectionFailure.entries) {
            for (availability in br.gov.caixa.loterias.core.common.ConnectionAvailability.entries) {
                for (channel in listOf(PrizeRedemptionChannel.PIX, PrizeRedemptionChannel.MERCADO_PAGO)) {
                    connection = br.gov.caixa.loterias.core.common.ConnectionAvailability.CONNECTED
                    redeemResult = AppResult.Success(PrizeRedemptionResult.ConnectionFailed(reason))
                    val vm = viewModel(); advanceUntilIdle(); vm.select(channel)
                    if (channel == PrizeRedemptionChannel.PIX) { vm.consultPix(); advanceUntilIdle() }
                    val reads = connectionReads
                    if (channel == PrizeRedemptionChannel.PIX) vm.confirmPix() else vm.confirmMercadoPago()
                    assertTrue(vm.state.value.loading)
                    connection = availability
                    advanceUntilIdle()
                    assertEquals(reads + 1, connectionReads)
                    val notice = requireNotNull(vm.state.value.notice)
                    assertEquals(br.gov.caixa.loterias.core.presentation.connectionFailureNotice(reason, availability), notice)
                    assertFalse(vm.state.value.loading)
                    assertEquals(channel, vm.state.value.selectedChannel)
                    assertNotEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
                    val calls = redemptionCalls
                    vm.resumeAfterRedirect()
                    if (channel == PrizeRedemptionChannel.PIX) vm.confirmPix() else vm.confirmMercadoPago()
                    advanceUntilIdle(); assertEquals(calls, redemptionCalls)
                    assertTrue(vm.dismissNotice(notice)); assertFalse(vm.dismissNotice(notice))
                    vm.resumeAfterRedirect(); advanceUntilIdle(); assertEquals(calls, redemptionCalls)
                }
            }
        }
    }

    @Test fun `login decisions recheck connectivity at click and an offline notice never navigates`() = runTest(dispatcher) {
        for (direct in listOf(false, true)) {
            connection = br.gov.caixa.loterias.core.common.ConnectionAvailability.CONNECTED
            redeemResult = AppResult.Success(if (direct) PrizeRedemptionResult.RedirectRequired(PrizeRedirect.LOGIN)
                else PrizeRedemptionResult.NoticeRequired(PrizeNotice(PrizeNoticeReason.LOGIN_REQUIRED)))
            val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
            vm.confirmMercadoPago(); advanceUntilIdle()
            val reads = connectionReads
            val original = vm.state.value.notice
            connection = br.gov.caixa.loterias.core.common.ConnectionAvailability.DISCONNECTED
            if (direct) assertFalse(vm.consumeRedirect(PrizeRedirect.LOGIN)) else assertNull(vm.consumeNoticeAction(original!!))
            assertEquals(reads + 1, connectionReads)
            val offline = vm.state.value.notice!!
            assertEquals(br.gov.caixa.loterias.core.presentation.LEGACY_OFFLINE, offline.message)
            assertNull(vm.state.value.redirect); assertFalse(vm.state.value.redirectPaused)
            if (original != null) assertNull(vm.consumeNoticeAction(original))
            assertFalse(vm.consumeRedirect(PrizeRedirect.LOGIN)); assertEquals(reads + 1, connectionReads)
            val calls = redemptionCalls
            vm.resumeAfterRedirect(); advanceUntilIdle(); assertEquals(calls, redemptionCalls)
            assertTrue(vm.dismissNotice(offline))
            connection = br.gov.caixa.loterias.core.common.ConnectionAvailability.CONNECTED
            vm.confirmMercadoPago(); advanceUntilIdle()
            if (direct) assertTrue(vm.consumeRedirect(PrizeRedirect.LOGIN)) else assertEquals(PrizeRedirect.LOGIN, vm.consumeNoticeAction(vm.state.value.notice!!))
            assertTrue(vm.state.value.redirectPaused)
            vm.resumeAfterRedirect(); advanceUntilIdle(); assertEquals(calls + 1, redemptionCalls)
        }
    }
    private val dispatcher = StandardTestDispatcher()
    private val details = PrizeDetails(7, BigDecimal.TEN, "nsbi", PrizeRedemptionChannel.entries.map {
        PrizeRedemptionOption(it, it.name, "description")
    })
    private var detailsResult: AppResult<PrizeDetails> = AppResult.Success(details)
    private var codeResult: AppResult<LotteryRedemptionCode> = AppResult.Success(LotteryRedemptionCode("qr", "code", "nsb", 1))
    private var consultResult: AppResult<PixBeneficiary> = AppResult.Success(PixBeneficiary("cpf", "Pessoa", "key", "Banco"))
    private var redeemResult: AppResult<PrizeRedemptionResult> = AppResult.Success(PrizeRedemptionResult.Redeemed())
    private var cancelResult: AppResult<Unit> = AppResult.Success(Unit)
    private var betPdfResult: AppResult<ByteArray> = AppResult.Success(byteArrayOf(1))
    private var prizePdfResult: AppResult<ByteArray> = AppResult.Success(byteArrayOf(2))
    private var redemption: Triple<Long, PrizeRedemptionChannel, String>? = null
    private var redemptionCalls = 0
    private var cancelCalls = 0
    private var codeCalls = 0
    private val game = LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA"), 3000, "", 60, 6, 20, 10, emptyList(), emptyList(), hasNextContest = true)
    private var parametersResult: AppResult<BettingParameters> = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, listOf(game)))
    private var entryResult: AppResult<BetEntryDecision> = AppResult.Success(BetEntryDecision.Ready(game))
    private var entryCalls = 0
    private var requestedModality = 0

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `business notices pause redemption and consume navigation or dismissal only once`() = runTest(dispatcher) {
        for (reason in PrizeNoticeReason.entries) {
            val before = redemptionCalls
            redeemResult = AppResult.Success(PrizeRedemptionResult.NoticeRequired(PrizeNotice(reason, "Mensagem", "E1")))
            val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
            vm.confirmMercadoPago(); advanceUntilIdle()
            val notice = vm.state.value.notice!!
            assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
            assertNull(vm.state.value.successNoticeMessage)
            assertFalse(vm.dismissNotice(notice.copy()))
            assertNull(vm.consumeNoticeAction(notice.copy()))
            vm.confirmMercadoPago(); vm.load(); vm.select(PrizeRedemptionChannel.PIX)
            assertFalse(vm.back()); advanceUntilIdle()
            assertEquals(before + 1, redemptionCalls)
            if (notice.destination == null) {
                assertNull(vm.consumeNoticeAction(notice))
                assertTrue(vm.dismissNotice(notice)); assertFalse(vm.dismissNotice(notice))
            } else {
                assertEquals(notice.destination, vm.consumeNoticeAction(notice))
                assertNull(vm.consumeNoticeAction(notice)); assertFalse(vm.dismissNotice(notice))
                assertTrue(vm.state.value.redirectPaused)
                vm.registrationFinished(); vm.confirmMercadoPago(); advanceUntilIdle()
                assertEquals(before + 1, redemptionCalls)
                vm.resumeAfterRedirect(); vm.resumeAfterRedirect()
            }
            advanceUntilIdle()
            assertNull(vm.state.value.notice)
            assertFalse(vm.state.value.redirectPaused)
            assertEquals(before + 1, redemptionCalls)
        }
    }

    @Test fun `each redirect preserves prize form and only explicit retry redeems after return`() = runTest(dispatcher) {
        for (destination in PrizeRedirect.entries) {
            val before = redemptionCalls
            redeemResult = AppResult.Success(PrizeRedemptionResult.RedirectRequired(destination))
            val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
            vm.confirmMercadoPago(); advanceUntilIdle()
            assertEquals(destination, vm.state.value.redirect)
            assertFalse(vm.consumeRedirect(PrizeRedirect.entries.first { it != destination }))
            assertTrue(vm.consumeRedirect(destination)); assertFalse(vm.consumeRedirect(destination))
            assertTrue(vm.state.value.redirectPaused); assertNull(vm.state.value.redirect)
            vm.confirmMercadoPago(); vm.load(); advanceUntilIdle()
            assertEquals(before + 1, redemptionCalls)
            vm.resumeAfterRedirect(); advanceUntilIdle()
            assertEquals(before + 1, redemptionCalls)
            assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
            assertEquals("initial@caixa.gov.br", vm.state.value.email)
            redeemResult = AppResult.Success(PrizeRedemptionResult.Redeemed())
            vm.confirmMercadoPago(); advanceUntilIdle()
            assertEquals(before + 2, redemptionCalls)
            assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        }
    }

    @Test fun `No on unauthorized prompt stays on redemption without authentication or retry`() = runTest(dispatcher) {
        redeemResult = AppResult.Success(PrizeRedemptionResult.NoticeRequired(PrizeNotice(PrizeNoticeReason.LOGIN_REQUIRED)))
        val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.confirmMercadoPago(); advanceUntilIdle()
        val notice = vm.state.value.notice!!
        assertEquals("Entendi", notice.positiveLabel)
        assertNull(notice.negativeLabel)
        assertTrue(vm.dismissNotice(notice))
        assertNull(vm.consumeNoticeAction(notice))
        vm.resumeAfterRedirect(); advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertNull(vm.state.value.redirect)
        assertFalse(vm.state.value.redirectPaused)
        assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
        assertEquals("initial@caixa.gov.br", vm.state.value.email)
    }

    @Test fun `processing redirect ends loading without changing form or claiming redemption`() = runTest(dispatcher) {
        redeemResult = AppResult.Success(PrizeRedemptionResult.Unchanged)
        for (channel in listOf(PrizeRedemptionChannel.MERCADO_PAGO, PrizeRedemptionChannel.PIX)) {
            val before = redemptionCalls
            val vm = viewModel(); advanceUntilIdle(); vm.select(channel)
            val stage = vm.state.value.stage
            if (channel == PrizeRedemptionChannel.MERCADO_PAGO) vm.confirmMercadoPago() else vm.confirmPix()
            advanceUntilIdle()
            assertEquals(before + 1, redemptionCalls)
            assertEquals(stage, vm.state.value.stage)
            assertFalse(vm.state.value.loading)
            assertNull(vm.state.value.errorMessage); assertNull(vm.state.value.notice)
            assertNull(vm.state.value.redirect); assertNull(vm.state.value.registrationNotice)
            assertNull(vm.state.value.successNoticeMessage)
            vm.resumeAfterRedirect(); advanceUntilIdle()
            assertEquals(before + 1, redemptionCalls)
        }
    }

    @Test fun `registration retains entered email and pauses redemption until browser flow finishes`() = runTest(dispatcher) {
        redeemResult = AppResult.Success(PrizeRedemptionResult.RegistrationRequired("Cadastrar?"))
        val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.changeEmail("selected@example.test")
        vm.confirmMercadoPago(); vm.confirmMercadoPago()
        assertFalse(vm.back())
        vm.changeEmail("changed@example.test")
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertEquals("selected@example.test", redemption?.third)
        assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
        assertNull(vm.state.value.successNoticeMessage)
        val notice = vm.state.value.registrationNotice!!
        assertEquals("Cadastrar?", notice.message)
        assertFalse(notice.toString().contains("selected@example.test"))
        assertFalse(vm.dismissRegistrationNotice(notice.copy()))
        assertNull(vm.consumeRegistrationNotice(notice.copy()))
        assertFalse(vm.back())
        vm.confirmMercadoPago(); vm.changeEmail("changed@example.test")
        assertEquals("selected@example.test", vm.consumeRegistrationNotice(notice))
        assertNull(vm.consumeRegistrationNotice(notice))
        assertFalse(vm.dismissRegistrationNotice(notice))
        assertTrue(vm.state.value.registrationPending)
        assertNull(vm.state.value.registrationNotice)
        vm.confirmMercadoPago(); vm.confirmPix(); vm.load(); vm.consultPix(); vm.cancelPixAndExit()
        vm.refreshLotteryCode(); vm.select(PrizeRedemptionChannel.PIX); vm.betNow()
        vm.downloadBetReceipt(); vm.downloadPrizeReceipt()
        assertFalse(vm.back())
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertEquals(0, cancelCalls); assertEquals(0, codeCalls); assertEquals(0, entryCalls)
        assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
        vm.registrationFinished(); vm.registrationFinished(); advanceUntilIdle()
        assertFalse(vm.state.value.registrationPending)
        assertEquals(1, redemptionCalls)
        redeemResult = AppResult.Success(PrizeRedemptionResult.Redeemed("MI053"))
        vm.confirmMercadoPago(); advanceUntilIdle()
        assertEquals(2, redemptionCalls)
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        vm.confirmMercadoPago(); advanceUntilIdle()
        assertEquals(2, redemptionCalls)
    }

    @Test fun `No dismisses registration without redeeming or changing the entered email`() = runTest(dispatcher) {
        redeemResult = AppResult.Success(PrizeRedemptionResult.RegistrationRequired("Cadastrar?"))
        val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.confirmMercadoPago(); advanceUntilIdle()
        val notice = vm.state.value.registrationNotice!!
        assertTrue(vm.dismissRegistrationNotice(notice))
        assertFalse(vm.dismissRegistrationNotice(notice))
        assertNull(vm.consumeRegistrationNotice(notice))
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertFalse(vm.state.value.registrationPending)
        assertEquals("initial@caixa.gov.br", vm.state.value.email)
        assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
        assertFalse(vm.back())
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
    }

    @Test fun `unexpected registration response on Pix cannot approve redemption or open registration`() = runTest(dispatcher) {
        redeemResult = AppResult.Success(PrizeRedemptionResult.RegistrationRequired("Cadastrar?"))
        val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.PIX)
        vm.confirmPix(); advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_ENTRY, vm.state.value.stage)
        assertNull(vm.state.value.registrationNotice)
        assertNotNull(vm.state.value.errorMessage)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `loads options and navigates agency and internal back`() = runTest(dispatcher) {
        val vm = viewModel(); advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        vm.select(PrizeRedemptionChannel.AGENCY)
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        runCurrent()
        assertEquals(PrizeRedemptionStage.AGENCY, vm.state.value.stage)
        assertEquals("code", vm.state.value.lotteryCode?.code)
        assertFalse(vm.back())
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertTrue(vm.back())
        vm.load(); advanceUntilIdle()
        assertEquals(details, vm.state.value.details)
    }

    @Test fun `current contest load failure does not hide redemption and supports retry`() = runTest(dispatcher) {
        parametersResult = AppResult.Failure(AppError.Network)
        val vm = viewModel()
        vm.loadCurrentContest() // Details not loaded yet.
        vm.betNow()
        advanceUntilIdle()
        assertEquals(0, entryCalls)
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertEquals(details, vm.state.value.details)
        assertNotNull(vm.state.value.currentContestError)
        assertFalse(vm.state.value.currentContestLoading)
        parametersResult = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
            listOf(game.copy(hasNextContest = false), game.copy(contestType = "ESPECIAL"))))
        vm.loadCurrentContest(); vm.loadCurrentContest(); advanceUntilIdle()
        assertEquals(game, vm.state.value.currentContest)
        assertNull(vm.state.value.currentContestError)
        parametersResult = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, emptyList()))
        vm.loadCurrentContest(); advanceUntilIdle()
        assertNull(vm.state.value.currentContest)
    }

    @Test fun `bet now waits for eligibility and emits one decision until consumed`() = runTest(dispatcher) {
        val vm = viewModel(); advanceUntilIdle()
        vm.betNow(); vm.betNow()
        assertTrue(vm.state.value.loading)
        assertNull(vm.state.value.betEntryDecision)
        advanceUntilIdle()
        assertEquals(1, entryCalls)
        assertEquals(2, requestedModality)
        assertEquals(BetEntryDecision.Ready(game), vm.state.value.betEntryDecision)
        vm.betNow(); advanceUntilIdle()
        assertEquals(1, entryCalls)
        vm.consumeBetEntryDecision()
        assertNull(vm.state.value.betEntryDecision)
        entryResult = AppResult.Failure(AppError.Validation("Suspensão ativa"))
        vm.betNow(); advanceUntilIdle()
        assertNull(vm.state.value.betEntryDecision)
        assertEquals("Suspensão ativa", vm.state.value.errorMessage)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `agency guidance requires successful redemption code and permits retry`() = runTest(dispatcher) {
        codeResult = AppResult.Failure(AppError.Validation("Código indisponível"))
        val vm = viewModel(); advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.AGENCY); runCurrent()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertFalse(vm.state.value.loading)
        assertNotNull(vm.state.value.errorMessage)
        assertNull(vm.state.value.lotteryCode)
        codeResult = AppResult.Success(LotteryRedemptionCode("qr", "code", "nsb", 1))
        vm.select(PrizeRedemptionChannel.AGENCY); runCurrent()
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
        val vm = viewModel(); advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.LOTTERY); runCurrent()
        assertEquals(60, vm.state.value.remainingSeconds)
        advanceTimeBy(1_001); runCurrent()
        assertEquals(59, vm.state.value.remainingSeconds)
        vm.refreshLotteryCode(); runCurrent()
        assertEquals(60, vm.state.value.remainingSeconds)
        assertFalse(vm.back())
    }

    @Test fun `missing qr prevents lottery rendering but not agency guidance`() = runTest(dispatcher) {
        codeResult = AppResult.Success(LotteryRedemptionCode("", "code", "nsb", 1))
        val vm = viewModel(); advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.LOTTERY); runCurrent()
        assertNull(vm.state.value.lotteryCode)
        assertEquals(0, vm.state.value.remainingSeconds)
        assertEquals("Código de resgate indisponível", vm.state.value.errorMessage)
        vm.back()
        vm.select(PrizeRedemptionChannel.AGENCY); runCurrent()
        assertEquals(PrizeRedemptionStage.AGENCY, vm.state.value.stage)
        assertNull(vm.state.value.errorMessage)
        assertFalse(vm.state.value.loading)
    }

    @Test fun `pix validates consults cancels and redeems`() = runTest(dispatcher) {
        val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.PIX)
        assertEquals(PrizePixKeySource.CPF, vm.state.value.pixKeySource)
        assertTrue(vm.state.value.canContinuePix())
        vm.selectOtherPixKey()
        vm.changePixKey("bad"); vm.consultPix(); runCurrent()
        assertTrue(vm.state.value.errorMessage!!.contains("válida"))
        vm.dismissError(); assertNull(vm.state.value.errorMessage)
        vm.changePixKey("cliente@caixa.gov.br")
        assertFalse(vm.state.value.canContinuePix())
        vm.togglePixOwnership()
        assertTrue(vm.state.value.canContinuePix())
        vm.consultPix(); advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)
        assertFalse(vm.back()); advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertEquals(0, cancelCalls)
        vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix(); advanceUntilIdle(); vm.confirmPix(); advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        assertEquals(PrizeRedemptionChannel.PIX, redemption!!.second)
    }

    @Test fun `mercado pago validates and submits email`() = runTest(dispatcher) {
        val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.changeEmail("bad"); vm.confirmMercadoPago()
        assertEquals("E-mail inválido.", vm.state.value.errorMessage)
        runCurrent()
        assertNull(redemption)
        assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
        vm.changeEmail("cliente@caixa.gov.br"); vm.confirmMercadoPago(); advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        assertEquals("cliente@caixa.gov.br", redemption!!.third)
    }

    @Test fun `pix cancel button cancels backend reservation and exits channel`() = runTest(dispatcher) {
        val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix(); advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)

        vm.cancelPixAndExit(); advanceUntilIdle()

        assertEquals(1, cancelCalls)
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertNull(vm.state.value.selectedChannel)
        assertNull(vm.state.value.pixBeneficiary)
    }

    @Test fun `mercado pago preserves legacy recurring bet notice`() = runTest(dispatcher) {
        redeemResult = AppResult.Success(PrizeRedemptionResult.Redeemed("MI053"))
        val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.confirmMercadoPago(); advanceUntilIdle()
        assertTrue(vm.state.value.successNoticeMessage!!.contains("recibo de troca"))
        vm.dismissSuccessNotice()
        assertNull(vm.state.value.successNoticeMessage)
    }

    @Test fun `downloads both receipts and exposes save effects`() = runTest(dispatcher) {
        val vm = viewModel(); advanceUntilIdle()
        val receipts = mutableListOf<PrizeReceipt>()
        val collector = launch { vm.receipts.take(2).collect(receipts::add) }
        runCurrent()
        vm.downloadBetReceipt(); advanceUntilIdle()
        vm.downloadPrizeReceipt(); advanceUntilIdle()
        assertEquals(listOf("comprovante-aposta-7.pdf", "comprovante-premio-7.pdf"), receipts.map { it.fileName })
        collector.cancel()
    }

    @Test fun `maps network validation and unexpected failures`() = runTest(dispatcher) {
        detailsResult = AppResult.Failure(AppError.Network)
        var vm = viewModel(); advanceUntilIdle(); assertTrue(vm.state.value.errorMessage!!.contains("conexão"))
        detailsResult = AppResult.Success(details)
        codeResult = AppResult.Failure(AppError.Validation("sem qr"))
        vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.LOTTERY); advanceUntilIdle()
        assertEquals("sem qr", vm.state.value.errorMessage)
        consultResult = AppResult.Failure(AppError.Unexpected)
        vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.PIX); vm.changePixKey("11999999999"); vm.consultPix(); advanceUntilIdle()
        assertTrue(vm.state.value.errorMessage!!.contains("Tente novamente"))
        cancelResult = AppResult.Failure(AppError.Network)
        consultResult = AppResult.Success(PixBeneficiary("", "", "", ""))
        vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.PIX); vm.changePixKey("11999999999"); vm.consultPix(); advanceUntilIdle(); vm.cancelPixAndExit(); advanceUntilIdle()
        assertTrue(vm.state.value.errorMessage!!.contains("conexão"))
        redeemResult = AppResult.Failure(AppError.Unexpected); vm.confirmPix(); advanceUntilIdle()
        assertTrue(vm.state.value.errorMessage!!.contains("Tente novamente"))
        betPdfResult = AppResult.Failure(AppError.Network); vm.downloadBetReceipt(); advanceUntilIdle()
        prizePdfResult = AppResult.Failure(AppError.Validation("pdf")); vm.downloadPrizeReceipt(); advanceUntilIdle()
        assertEquals("pdf", vm.state.value.errorMessage)
    }

    @Test fun `server with no eligible redemption channels exposes no actionable destination`() = runTest(dispatcher) {
        detailsResult = AppResult.Success(details.copy(options = emptyList()))
        val vm = viewModel(); advanceUntilIdle()
        for (channel in PrizeRedemptionChannel.entries) vm.select(channel)
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertNull(vm.state.value.selectedChannel)
        assertEquals(0, redemptionCalls)
        assertEquals(0, codeCalls)
    }

    @Test fun `only server eligible channels can be selected and explicit redemption is sent once`() = runTest(dispatcher) {
        detailsResult = AppResult.Success(details.copy(options = listOf(
            PrizeRedemptionOption(PrizeRedemptionChannel.MERCADO_PAGO, "Mercado Pago", "Receba na hora"),
        )))
        val vm = viewModel(); advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        assertEquals(PrizeRedemptionStage.OPTIONS, vm.state.value.stage)
        assertNull(vm.state.value.selectedChannel)
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.changeEmail("fixture@example.test")
        vm.confirmMercadoPago(); vm.confirmMercadoPago()
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertEquals(Triple(7L, PrizeRedemptionChannel.MERCADO_PAGO, "fixture@example.test"), redemption)
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        vm.confirmMercadoPago(); vm.resumeAfterRedirect(); vm.load()
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
    }

    @Test fun `Pix confirmation submits the consulted beneficiary key once without reload resubmission`() = runTest(dispatcher) {
        consultResult = AppResult.Success(PixBeneficiary("CONTROLLED", "Fixture", "fixture@example.test", "Fixture Bank"))
        val vm = viewModel(); advanceUntilIdle(); vm.select(PrizeRedemptionChannel.PIX)
        vm.consultPix(); advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_CONFIRM, vm.state.value.stage)
        vm.confirmPix(); vm.confirmPix()
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertEquals(Triple(7L, PrizeRedemptionChannel.PIX, "fixture@example.test"), redemption)
        vm.confirmPix(); vm.resumeAfterRedirect(); vm.load()
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
    }

    private fun viewModel() = PrizeRedemptionViewModel(
        7, "initial@caixa.gov.br", "12345678901",
        GetPrizeDetailsUseCase { detailsResult },
        GenerateLotteryRedemptionCodeUseCase { codeCalls++; codeResult },
        ConsultPixRedemptionKeyUseCase { _, _ -> consultResult },
        RedeemPrizeUseCase { id, channel, value -> redemptionCalls++; redemption = Triple(id, channel, value); redeemResult },
        CancelPixRedemptionUseCase { cancelCalls++; cancelResult },
        DownloadBetReceiptUseCase { betPdfResult },
        DownloadPrizeReceiptUseCase { prizePdfResult },
        GetBettingParametersUseCase { parametersResult },
        PrepareBetEntryUseCase { request -> entryCalls++; requestedModality = request.modalityId; entryResult },
        java.time.Clock.fixed(java.time.Instant.parse("2026-09-07T15:30:00Z"), java.time.ZoneId.of("America/Sao_Paulo")),
        br.gov.caixa.loterias.core.common.NetworkAvailability { connectionReads++; connection },
        FixedCoroutineDispatchers(dispatcher),
    )
}
