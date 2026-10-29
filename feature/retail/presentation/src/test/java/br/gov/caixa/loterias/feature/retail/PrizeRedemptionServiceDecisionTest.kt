package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrizeRedemptionServiceDecisionTest : PrizeRedemptionTestFixture() {
    @Test fun `connection failures dismiss loading preserve channel and use connectivity at response time`() = runTest(dispatcher) {
        for (reason in br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure.entries) {
            for (availability in br.gov.caixa.loterias.core.common.network.ConnectionAvailability.entries) {
                for (channel in listOf(PrizeRedemptionChannel.PIX, PrizeRedemptionChannel.MERCADO_PAGO)) {
                    connection = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.CONNECTED
                    redeemResult = AppResult.Success(PrizeRedemptionResult.ConnectionFailed(reason))
                    val vm = viewModel()
                    advanceUntilIdle()
                    vm.select(channel)
                    if (channel == PrizeRedemptionChannel.PIX) {
                        vm.consultPix()
                        advanceUntilIdle()
                    }
                    val reads = connectionReads
                    if (channel == PrizeRedemptionChannel.PIX) vm.confirmPix() else vm.confirmMercadoPago()
                    assertTrue(vm.state.value.loading)
                    connection = availability
                    advanceUntilIdle()
                    assertEquals(reads + 1, connectionReads)
                    val notice = requireNotNull(vm.state.value.notice)
                    assertEquals(br.gov.caixa.loterias.core.presentation.rule.connectionFailureNotice(reason, availability), notice)
                    assertFalse(vm.state.value.loading)
                    assertEquals(channel, vm.state.value.selectedChannel)
                    assertNotEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
                    val calls = redemptionCalls
                    vm.resumeAfterRedirect()
                    if (channel == PrizeRedemptionChannel.PIX) vm.confirmPix() else vm.confirmMercadoPago()
                    advanceUntilIdle()
                    assertEquals(calls, redemptionCalls)
                    assertTrue(vm.dismissNotice(notice))
                    assertFalse(vm.dismissNotice(notice))
                    vm.resumeAfterRedirect()
                    advanceUntilIdle()
                    assertEquals(calls, redemptionCalls)
                }
            }
        }
    }

    @Test fun `login decisions recheck connectivity at click and an offline notice never navigates`() = runTest(dispatcher) {
        for (direct in listOf(false, true)) {
            connection = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.CONNECTED
            redeemResult = AppResult.Success(
                if (direct) {
                    PrizeRedemptionResult.RedirectRequired(ServiceRedirect.LOGIN)
                } else {
                    PrizeRedemptionResult.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED))
                }
            )
            val vm = viewModel()
            advanceUntilIdle()
            vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
            vm.confirmMercadoPago()
            advanceUntilIdle()
            val reads = connectionReads
            val original = vm.state.value.notice
            connection = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.DISCONNECTED
            if (direct) assertFalse(vm.consumeRedirect(ServiceRedirect.LOGIN)) else assertNull(vm.consumeNoticeAction(original!!))
            assertEquals(reads + 1, connectionReads)
            val offline = vm.state.value.notice!!
            assertEquals(br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OFFLINE, offline.message)
            assertNull(vm.state.value.redirect)
            assertFalse(vm.state.value.redirectPaused)
            if (original != null) assertNull(vm.consumeNoticeAction(original))
            assertFalse(vm.consumeRedirect(ServiceRedirect.LOGIN))
            assertEquals(reads + 1, connectionReads)
            val calls = redemptionCalls
            vm.resumeAfterRedirect()
            advanceUntilIdle()
            assertEquals(calls, redemptionCalls)
            assertTrue(vm.dismissNotice(offline))
            connection = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.CONNECTED
            vm.confirmMercadoPago()
            advanceUntilIdle()
            if (direct) {
                assertTrue(
                    vm.consumeRedirect(ServiceRedirect.LOGIN)
                )
            } else {
                assertEquals(ServiceRedirect.LOGIN, vm.consumeNoticeAction(vm.state.value.notice!!))
            }
            assertTrue(vm.state.value.redirectPaused)
            vm.resumeAfterRedirect()
            advanceUntilIdle()
            assertEquals(calls + 1, redemptionCalls)
        }
    }

    @Test fun `business notices pause redemption and consume navigation or dismissal only once`() = runTest(dispatcher) {
        for (reason in ServiceNoticeReason.entries) {
            val before = redemptionCalls
            redeemResult = AppResult.Success(PrizeRedemptionResult.NoticeRequired(ServiceNotice(reason, "Mensagem", "E1")))
            val vm = viewModel()
            advanceUntilIdle()
            vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
            vm.confirmMercadoPago()
            advanceUntilIdle()
            val notice = vm.state.value.notice!!
            assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
            assertNull(vm.state.value.successNoticeMessage)
            assertFalse(vm.dismissNotice(notice.copy()))
            assertNull(vm.consumeNoticeAction(notice.copy()))
            vm.confirmMercadoPago()
            vm.load()
            vm.select(PrizeRedemptionChannel.PIX)
            assertFalse(vm.back())
            advanceUntilIdle()
            assertEquals(before + 1, redemptionCalls)
            if (notice.destination == null) {
                assertNull(vm.consumeNoticeAction(notice))
                assertTrue(vm.dismissNotice(notice))
                assertFalse(vm.dismissNotice(notice))
            } else {
                assertEquals(notice.destination, vm.consumeNoticeAction(notice))
                assertNull(vm.consumeNoticeAction(notice))
                assertFalse(vm.dismissNotice(notice))
                assertTrue(vm.state.value.redirectPaused)
                vm.registrationFinished()
                vm.confirmMercadoPago()
                advanceUntilIdle()
                assertEquals(before + 1, redemptionCalls)
                vm.resumeAfterRedirect()
                vm.resumeAfterRedirect()
            }
            advanceUntilIdle()
            assertNull(vm.state.value.notice)
            assertFalse(vm.state.value.redirectPaused)
            assertEquals(before + 1, redemptionCalls)
        }
    }

    @Test fun `each redirect preserves prize form and only explicit retry redeems after return`() = runTest(dispatcher) {
        for (destination in ServiceRedirect.entries) {
            val before = redemptionCalls
            redeemResult = AppResult.Success(PrizeRedemptionResult.RedirectRequired(destination))
            val vm = viewModel()
            advanceUntilIdle()
            vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
            vm.confirmMercadoPago()
            advanceUntilIdle()
            assertEquals(destination, vm.state.value.redirect)
            assertFalse(vm.consumeRedirect(ServiceRedirect.entries.first { it != destination }))
            assertTrue(vm.consumeRedirect(destination))
            assertFalse(vm.consumeRedirect(destination))
            assertTrue(vm.state.value.redirectPaused)
            assertNull(vm.state.value.redirect)
            vm.confirmMercadoPago()
            vm.load()
            advanceUntilIdle()
            assertEquals(before + 1, redemptionCalls)
            vm.resumeAfterRedirect()
            advanceUntilIdle()
            assertEquals(before + 1, redemptionCalls)
            assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
            assertEquals("initial@caixa.gov.br", vm.state.value.email)
            redeemResult = AppResult.Success(PrizeRedemptionResult.Redeemed())
            vm.confirmMercadoPago()
            advanceUntilIdle()
            assertEquals(before + 2, redemptionCalls)
            assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        }
    }

    @Test fun `No on unauthorized prompt stays on redemption without authentication or retry`() = runTest(dispatcher) {
        redeemResult = AppResult.Success(PrizeRedemptionResult.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED)))
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.confirmMercadoPago()
        advanceUntilIdle()
        val notice = vm.state.value.notice!!
        assertEquals("Entendi", notice.positiveLabel)
        assertNull(notice.negativeLabel)
        assertTrue(vm.dismissNotice(notice))
        assertNull(vm.consumeNoticeAction(notice))
        vm.resumeAfterRedirect()
        advanceUntilIdle()
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
            val vm = viewModel()
            advanceUntilIdle()
            vm.select(channel)
            val stage = vm.state.value.stage
            if (channel == PrizeRedemptionChannel.MERCADO_PAGO) vm.confirmMercadoPago() else vm.confirmPix()
            advanceUntilIdle()
            assertEquals(before + 1, redemptionCalls)
            assertEquals(stage, vm.state.value.stage)
            assertFalse(vm.state.value.loading)
            assertNull(vm.state.value.errorMessage)
            assertNull(vm.state.value.notice)
            assertNull(vm.state.value.redirect)
            assertNull(vm.state.value.registrationNotice)
            assertNull(vm.state.value.successNoticeMessage)
            vm.resumeAfterRedirect()
            advanceUntilIdle()
            assertEquals(before + 1, redemptionCalls)
        }
    }

    @Test fun `registration retains entered email and pauses redemption until browser flow finishes`() = runTest(dispatcher) {
        redeemResult = AppResult.Success(PrizeRedemptionResult.RegistrationRequired("Cadastrar?"))
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.changeEmail("selected@example.test")
        vm.confirmMercadoPago()
        vm.confirmMercadoPago()
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
        vm.confirmMercadoPago()
        vm.changeEmail("changed@example.test")
        assertEquals("selected@example.test", vm.consumeRegistrationNotice(notice))
        assertNull(vm.consumeRegistrationNotice(notice))
        assertFalse(vm.dismissRegistrationNotice(notice))
        assertTrue(vm.state.value.registrationPending)
        assertNull(vm.state.value.registrationNotice)
        vm.confirmMercadoPago()
        vm.confirmPix()
        vm.load()
        vm.consultPix()
        vm.cancelPixAndExit()
        vm.refreshLotteryCode()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.betNow()
        vm.downloadBetReceipt()
        vm.downloadPrizeReceipt()
        assertFalse(vm.back())
        advanceUntilIdle()
        assertEquals(1, redemptionCalls)
        assertEquals(0, cancelCalls)
        assertEquals(0, codeCalls)
        assertEquals(0, entryCalls)
        assertEquals(PrizeRedemptionStage.MERCADO_PAGO, vm.state.value.stage)
        vm.registrationFinished()
        vm.registrationFinished()
        advanceUntilIdle()
        assertFalse(vm.state.value.registrationPending)
        assertEquals(1, redemptionCalls)
        redeemResult = AppResult.Success(PrizeRedemptionResult.Redeemed("MI053"))
        vm.confirmMercadoPago()
        advanceUntilIdle()
        assertEquals(2, redemptionCalls)
        assertEquals(PrizeRedemptionStage.SUCCESS, vm.state.value.stage)
        vm.confirmMercadoPago()
        advanceUntilIdle()
        assertEquals(2, redemptionCalls)
    }

    @Test fun `No dismisses registration without redeeming or changing the entered email`() = runTest(dispatcher) {
        redeemResult = AppResult.Success(PrizeRedemptionResult.RegistrationRequired("Cadastrar?"))
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.MERCADO_PAGO)
        vm.confirmMercadoPago()
        advanceUntilIdle()
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
        val vm = viewModel()
        advanceUntilIdle()
        vm.select(PrizeRedemptionChannel.PIX)
        vm.confirmPix()
        advanceUntilIdle()
        assertEquals(PrizeRedemptionStage.PIX_ENTRY, vm.state.value.stage)
        assertNull(vm.state.value.registrationNotice)
        assertNotNull(vm.state.value.errorMessage)
        assertFalse(vm.state.value.loading)
    }
}
