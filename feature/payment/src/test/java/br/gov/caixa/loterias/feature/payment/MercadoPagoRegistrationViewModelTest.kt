package br.gov.caixa.loterias.feature.payment

import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.AccountProfile
import br.gov.caixa.loterias.core.domain.account.Gender
import br.gov.caixa.loterias.core.domain.account.MunicipalityId
import br.gov.caixa.loterias.core.domain.account.GetAccountProfileUseCase
import br.gov.caixa.loterias.core.domain.payment.MercadoPagoRegistration
import br.gov.caixa.loterias.core.domain.payment.MercadoPagoRegistrationResult
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.ServiceRedirect
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import br.gov.caixa.loterias.core.domain.payment.PrepareMercadoPagoRegistrationUseCase
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MercadoPagoRegistrationViewModelTest {
    private var connection = br.gov.caixa.loterias.core.common.ConnectionAvailability.CONNECTED
    private var connectionReads = 0

    @Test fun `token transport failures use current connection without starting a browser or another request`() = runTest {
        for (reason in br.gov.caixa.loterias.core.domain.service.ServiceConnectionFailure.entries) {
            for (availability in br.gov.caixa.loterias.core.common.ConnectionAvailability.entries) {
                connection = br.gov.caixa.loterias.core.common.ConnectionAvailability.CONNECTED
                result = AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.ConnectionFailed(reason)))
                pending = CompletableDeferred()
                val vm = viewModel(); vm.start("selected@example.com")
                val reads = connectionReads
                connection = availability
                pending!!.complete(Unit); advanceUntilIdle()
                assertEquals(reads + 1, connectionReads)
                assertEquals(MercadoPagoRegistrationStage.NOTICE, vm.state.value.stage)
                assertNull(vm.state.value.request)
                val notice = vm.state.value.notice!!
                assertEquals(br.gov.caixa.loterias.core.presentation.connectionFailureNotice(reason, availability), notice)
                val calls = emails.size
                vm.start(); assertEquals(calls, emails.size)
                assertFalse(vm.navigationReturned()); assertFalse(vm.browserReturned())
                assertTrue(vm.dismissNotice(notice)); assertFalse(vm.dismissNotice(notice))
                assertEquals(calls, emails.size)
            }
        }
    }

    @Test fun `token login redirects and positive login notices do not navigate when connection was lost`() = runTest {
        for (direct in listOf(false, true)) {
            connection = br.gov.caixa.loterias.core.common.ConnectionAvailability.CONNECTED
            result = AppResult.Success(MercadoPagoRegistrationResult.Rejected(if (direct) ServiceFailure.RedirectRequired(ServiceRedirect.LOGIN)
                else ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED))))
            val vm = viewModel(); vm.start(); advanceUntilIdle()
            val original = vm.state.value.notice
            val reads = connectionReads
            connection = br.gov.caixa.loterias.core.common.ConnectionAvailability.DISCONNECTED
            if (direct) assertFalse(vm.consumeRedirect(ServiceRedirect.LOGIN)) else assertNull(vm.consumeNoticeAction(original!!))
            assertEquals(reads + 1, connectionReads)
            val offline = vm.state.value.notice!!
            assertEquals(br.gov.caixa.loterias.core.presentation.LEGACY_OFFLINE, offline.message)
            assertNull(vm.state.value.redirect); assertEquals(MercadoPagoRegistrationStage.NOTICE, vm.state.value.stage)
            if (original != null) assertNull(vm.consumeNoticeAction(original))
            assertFalse(vm.consumeRedirect(ServiceRedirect.LOGIN)); assertEquals(reads + 1, connectionReads)
            val calls = emails.size
            assertTrue(vm.dismissNotice(offline)); assertFalse(vm.navigationReturned())
            assertEquals(calls, emails.size)
            connection = br.gov.caixa.loterias.core.common.ConnectionAvailability.CONNECTED
            vm.start(); advanceUntilIdle()
            if (direct) assertTrue(vm.consumeRedirect(ServiceRedirect.LOGIN)) else assertEquals(ServiceRedirect.LOGIN, vm.consumeNoticeAction(vm.state.value.notice!!))
            assertTrue(vm.navigationReturned()); assertEquals(calls + 1, emails.size)
        }
    }
    private val profile = AccountProfile("1", "Pessoa", "profile@example.com", "", Gender.MALE, MunicipalityId(0, 0, 0), "", "", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false)
    private var profileResult: AppResult<AccountProfile> = AppResult.Success(profile)
    private val request = MercadoPagoRegistration.fromToken("private-token")
    private var result: AppResult<MercadoPagoRegistrationResult> = AppResult.Success(MercadoPagoRegistrationResult.Ready(request))
    private var profileCalls = 0
    private val emails = mutableListOf<String>()
    private var pending: CompletableDeferred<Unit>? = null
    @Before fun setup() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun cleanup() = Dispatchers.resetMain()

    @Test fun `no work before consent and browser request is transient and consumed once`() = runTest {
        val vm = viewModel()
        assertEquals(MercadoPagoRegistrationStage.IDLE, vm.state.value.stage)
        assertEquals(0, profileCalls); assertTrue(emails.isEmpty())
        assertFalse(vm.browserReturned()); assertFalse(vm.dismissError())
        vm.browserUnavailable()
        assertNull(vm.takeBrowserRequest(request))
        vm.start(); advanceUntilIdle()
        assertEquals(listOf("profile@example.com"), emails)
        assertEquals(1, profileCalls)
        assertFalse(vm.state.value.toString().contains("private-token"))
        assertNull(vm.takeBrowserRequest(MercadoPagoRegistration.fromToken("private-token")))
        vm.start(); assertEquals(1, emails.size)
        assertEquals(request.url, vm.takeBrowserRequest(request))
        assertNull(vm.takeBrowserRequest(request))
        assertNull(vm.state.value.request)
        assertEquals(MercadoPagoRegistrationStage.OPENED, vm.state.value.stage)
        vm.start(); assertEquals(1, emails.size)
        assertTrue(vm.browserReturned()); assertFalse(vm.browserReturned())
        assertEquals(MercadoPagoRegistrationStage.IDLE, vm.state.value.stage)
        assertEquals(1, emails.size)
    }

    @Test fun `explicit email bypasses profile and duplicate loading actions are ignored`() = runTest {
        pending = CompletableDeferred()
        val vm = viewModel()
        vm.start("selected@example.com"); vm.start("different@example.com")
        assertEquals(MercadoPagoRegistrationStage.LOADING, vm.state.value.stage)
        assertEquals(0, profileCalls)
        assertFalse(vm.dismissError()); assertFalse(vm.browserReturned())
        pending!!.complete(Unit); advanceUntilIdle()
        assertEquals(listOf("selected@example.com"), emails)
        assertEquals(MercadoPagoRegistrationStage.READY, vm.state.value.stage)
    }

    @Test fun `profile and preparation failures are dismissible and never leak backend details`() = runTest {
        for (error in listOf(AppError.Network, AppError.Validation("private-token"), AppError.Unexpected)) {
            for (failProfile in listOf(true, false)) {
                profileResult = if (failProfile) AppResult.Failure(error) else AppResult.Success(profile)
                result = AppResult.Failure(error)
                val vm = viewModel()
                vm.start(); advanceUntilIdle()
                assertEquals(MercadoPagoRegistrationStage.ERROR, vm.state.value.stage)
                assertNull(vm.state.value.request)
                assertFalse(vm.state.value.toString().contains("private-token"))
                val count = emails.size
                vm.start(); assertEquals(count, emails.size)
                assertTrue(vm.dismissError()); assertFalse(vm.dismissError())
                assertEquals(MercadoPagoRegistrationStage.IDLE, vm.state.value.stage)
            }
        }
    }

    @Test fun `missing browser reports failure and explicit new consent may request a fresh token`() = runTest {
        val vm = viewModel()
        vm.start(); advanceUntilIdle()
        vm.takeBrowserRequest(request)
        vm.browserUnavailable()
        assertEquals("Não foi possível abrir o cadastro Mercado Pago no navegador.", vm.state.value.errorMessage)
        assertNull(vm.state.value.request)
        assertFalse(vm.browserReturned())
        assertTrue(vm.dismissError())
        vm.start(); advanceUntilIdle()
        assertEquals(2, emails.size)
    }

    @Test fun `destroyed scope cannot publish a token for a pending request`() = runTest {
        pending = CompletableDeferred()
        val vm = viewModel()
        vm.start()
        vm.viewModelScope.cancel()
        pending!!.complete(Unit); advanceUntilIdle()
        assertNull(vm.state.value.request)
        assertNull(vm.takeBrowserRequest(request))
        assertEquals(1, emails.size)
    }

    @Test fun `all service notices pause preparation and reject stale or duplicate actions`() = runTest {
        for (reason in ServiceNoticeReason.entries) {
            result = AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.NoticeRequired(ServiceNotice(reason, "Mensagem", "E1"))))
            val vm = viewModel()
            vm.start("selected@example.com"); advanceUntilIdle()
            val notice = requireNotNull(vm.state.value.notice)
            assertEquals(MercadoPagoRegistrationStage.NOTICE, vm.state.value.stage)
            assertNull(vm.state.value.request)
            val calls = emails.size
            vm.start(); assertEquals(calls, emails.size)
            assertFalse(vm.dismissNotice(notice.copy()))
            assertNull(vm.consumeNoticeAction(notice.copy()))
            assertFalse(vm.navigationReturned()); assertFalse(vm.finishWithoutAction())
            if (notice.destination != null) {
                assertEquals(notice.destination, vm.consumeNoticeAction(notice))
                assertNull(vm.consumeNoticeAction(notice)); assertFalse(vm.dismissNotice(notice))
                assertEquals(MercadoPagoRegistrationStage.NAVIGATING, vm.state.value.stage)
                vm.start(); assertEquals(calls, emails.size)
                assertTrue(vm.navigationReturned()); assertFalse(vm.navigationReturned())
            } else {
                assertNull(vm.consumeNoticeAction(notice))
                assertTrue(vm.dismissNotice(notice)); assertFalse(vm.dismissNotice(notice))
            }
            assertEquals(MercadoPagoRegistrationStage.IDLE, vm.state.value.stage)
            assertEquals(calls, emails.size)
        }
    }

    @Test fun `diagnostic time is a fixed snapshot and declining login never starts navigation`() = runTest {
        result = AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Mensagem", "E1"))))
        val vm = viewModel(); vm.start(); advanceUntilIdle()
        assertEquals("Mensagem\n\nE1\n07/09/2026 12:30:00", vm.state.value.notice!!.message)
        vm.dismissNotice(vm.state.value.notice!!)
        result = AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED))))
        vm.start(); advanceUntilIdle()
        val prompt = vm.state.value.notice!!
        assertEquals("Entendi", prompt.positiveLabel); assertNull(prompt.negativeLabel)
        assertTrue(vm.dismissNotice(prompt))
        assertFalse(vm.navigationReturned()); assertEquals(2, emails.size)
    }

    @Test fun `redirects are consumed once and returning never repeats preparation`() = runTest {
        for (destination in ServiceRedirect.entries) {
            result = AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.RedirectRequired(destination)))
            val vm = viewModel(); vm.start(); advanceUntilIdle()
            assertEquals(destination, vm.state.value.redirect)
            val wrong = ServiceRedirect.entries.first { it != destination }
            assertFalse(vm.consumeRedirect(wrong))
            assertFalse(vm.navigationReturned())
            assertTrue(vm.consumeRedirect(destination)); assertFalse(vm.consumeRedirect(destination))
            val calls = emails.size
            vm.start(); assertEquals(calls, emails.size)
            assertNull(vm.state.value.redirect); assertNull(vm.state.value.request)
            assertFalse(vm.browserReturned())
            assertTrue(vm.navigationReturned()); assertFalse(vm.navigationReturned())
            assertEquals(calls, emails.size)
        }
    }

    @Test fun `recognized ignored directives finish once without opening browser or showing error`() = runTest {
        result = AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.Unchanged))
        val vm = viewModel(); vm.start(); advanceUntilIdle()
        assertEquals(MercadoPagoRegistrationStage.FINISHED, vm.state.value.stage)
        assertNull(vm.state.value.request); assertNull(vm.state.value.notice); assertNull(vm.state.value.errorMessage)
        vm.start(); assertEquals(1, emails.size)
        assertTrue(vm.finishWithoutAction()); assertFalse(vm.finishWithoutAction())
        assertEquals(MercadoPagoRegistrationStage.IDLE, vm.state.value.stage)
    }

    @Test fun `018003 requires fresh consent and retries only the selected email once`() = runTest {
        val decision = ServiceFailure.RegistrationRequired("Deseja se cadastrar?")
        result = AppResult.Success(MercadoPagoRegistrationResult.Rejected(decision))
        val vm = viewModel(); vm.start("entered@example.com"); advanceUntilIdle()
        val consent = requireNotNull(vm.state.value.repeatConsent)
        assertEquals(MercadoPagoRegistrationStage.REPEAT_CONSENT, vm.state.value.stage)
        assertEquals(decision.message, consent.message)
        vm.start("another@example.com"); assertEquals(1, emails.size)
        vm.repeatRegistration(MercadoPagoRegistrationConsent(consent.message))
        assertFalse(vm.declineRepeatRegistration(MercadoPagoRegistrationConsent(consent.message)))
        assertEquals(1, emails.size); assertEquals(0, profileCalls)
        result = AppResult.Success(MercadoPagoRegistrationResult.Ready(request))
        pending = CompletableDeferred()
        vm.repeatRegistration(consent); vm.repeatRegistration(consent)
        assertFalse(vm.declineRepeatRegistration(consent)); assertFalse(vm.navigationReturned())
        assertEquals(MercadoPagoRegistrationStage.LOADING, vm.state.value.stage)
        pending!!.complete(Unit); advanceUntilIdle()
        assertEquals(listOf("entered@example.com", "entered@example.com"), emails)
        assertEquals(0, profileCalls)
        assertEquals(MercadoPagoRegistrationStage.READY, vm.state.value.stage)
        assertEquals(request.url, vm.takeBrowserRequest(request))
    }

    @Test fun `declining repeat consent clears state and a later flow cannot reuse the old email`() = runTest {
        result = AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.RegistrationRequired("Deseja se cadastrar?")))
        val vm = viewModel(); vm.start("first@example.com"); advanceUntilIdle()
        val consent = requireNotNull(vm.state.value.repeatConsent)
        assertTrue(vm.declineRepeatRegistration(consent)); assertFalse(vm.declineRepeatRegistration(consent))
        vm.repeatRegistration(consent); assertEquals(1, emails.size)
        result = AppResult.Success(MercadoPagoRegistrationResult.Ready(request))
        vm.start(); advanceUntilIdle()
        assertEquals(listOf("first@example.com", "profile@example.com"), emails)
        assertEquals(1, profileCalls)
    }

    @Test fun `a repeated 018003 creates a new consent snapshot even if repository reuses the same decision`() = runTest {
        result = AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.RegistrationRequired("Cadastrar?")))
        val vm = viewModel(); vm.start(); advanceUntilIdle()
        val firstState = vm.state.value
        val first = vm.state.value.repeatConsent!!
        vm.repeatRegistration(first); advanceUntilIdle()
        val second = vm.state.value.repeatConsent!!
        assertEquals(first.message, second.message); assertNotSame(first, second)
        assertNotEquals(firstState, vm.state.value) // StateFlow must not conflate equal consecutive error messages.
        assertEquals("MercadoPagoRegistrationConsent(redacted)", second.toString())
        vm.repeatRegistration(first); assertFalse(vm.declineRepeatRegistration(first))
        assertEquals(2, emails.size); assertEquals(1, profileCalls)
        assertTrue(vm.declineRepeatRegistration(second))
    }

    private fun viewModel() = MercadoPagoRegistrationViewModel(
        GetAccountProfileUseCase { profileCalls++; profileResult },
        PrepareMercadoPagoRegistrationUseCase { emails += it; pending?.await(); result },
        FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
        Clock.fixed(Instant.parse("2026-09-07T15:30:00Z"), ZoneId.of("America/Sao_Paulo")),
        br.gov.caixa.loterias.core.common.NetworkAvailability { connectionReads++; connection },
    )
}
