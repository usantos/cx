package br.gov.caixa.loterias.core.presentation

import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.domain.service.*
import java.time.LocalDateTime
import org.junit.Assert.*
import org.junit.Test

class ServiceFailurePresentationTest {
    private val now = LocalDateTime.of(2026, 9, 7, 12, 0)
    private val online = NetworkAvailability { ConnectionAvailability.CONNECTED }

    @Test fun `redirects preserve terms acknowledgement and direct destinations`() {
        for (destination in ServiceRedirect.entries) {
            val state = serviceFailurePresentation(ServiceFailure.RedirectRequired(destination), online, now)
            assertTrue(state.pending)
            if (destination == ServiceRedirect.ACCEPT_TERMS) {
                assertEquals(LEGACY_UPDATED_TERMS, state.notice!!.message)
                assertNull(state.redirect)
                val accepted = state.accept(state.notice!!, online)
                assertNull(accepted.notice); assertEquals(destination, accepted.redirect!!.destination)
            } else {
                assertNull(state.notice); assertEquals(destination, state.redirect!!.destination)
            }
        }
    }

    @Test fun `connection notice uses current connectivity and unknown does not imply offline`() {
        for (availability in ConnectionAvailability.entries) for (reason in ServiceConnectionFailure.entries) {
            val network = NetworkAvailability { availability }
            val state = serviceFailurePresentation(ServiceFailure.ConnectionFailed(reason), network, now)
            assertEquals(connectionFailureNotice(reason, availability), state.notice)
            assertNull(state.redirect)
        }
        val state = serviceFailurePresentation(ServiceFailure.RedirectRequired(ServiceRedirect.LOGIN),
            NetworkAvailability { ConnectionAvailability.DISCONNECTED }, now)
        assertEquals(LEGACY_OFFLINE, state.notice!!.message); assertNull(state.redirect)
    }

    @Test fun `notice diagnostics actions registration fallback and unchanged are explicit`() {
        for (reason in ServiceNoticeReason.entries) {
            val notice = ServiceNotice(reason, "Mensagem", "123")
            val state = serviceFailurePresentation(ServiceFailure.NoticeRequired(notice), online, now)
            assertEquals(serviceNoticeUi(notice, now), state.notice)
            assertNull(state.redirect)
        }
        assertEquals(LEGACY_OPERATION_FAILED,
            serviceFailurePresentation(ServiceFailure.RegistrationRequired("Cadastro"), online, now).notice!!.message)
        assertEquals(ServiceFailurePresentation(), serviceFailurePresentation(ServiceFailure.Unchanged, online, now))
        assertFalse(ServiceFailurePresentation().pending)
    }

    @Test fun `stale callbacks cannot dismiss or accept a newer identical notice`() {
        val old = ServiceNoticeUiState("Mensagem", ServiceRedirect.HOME)
        val current = old.copy()
        val state = ServiceFailurePresentation(notice = current)
        assertSame(state, state.dismiss(old))
        assertSame(state, state.accept(old, online))
        assertFalse(state.dismiss(current).pending)
        val accepted = state.accept(current, online)
        assertEquals(ServiceRedirect.HOME, accepted.redirect!!.destination)
        assertNull(accepted.notice)
        assertNotSame(accepted.redirect, state.accept(current, online).redirect)
    }

    @Test fun `login is rechecked at consent without a retry and plain acknowledgement only clears`() {
        val notice = ServiceNoticeUiState(LEGACY_LOGIN_REQUIRED, ServiceRedirect.LOGIN)
        val state = ServiceFailurePresentation(notice = notice)
        val offline = state.accept(notice, NetworkAvailability { ConnectionAvailability.DISCONNECTED })
        assertEquals(LEGACY_OFFLINE, offline.notice!!.message)
        assertNull(offline.redirect)
        assertFalse(offline.accept(offline.notice!!, online).pending)
        assertEquals(ServiceRedirect.LOGIN, state.accept(notice, online).redirect!!.destination)
        assertFalse(state.dismiss(notice).pending)
    }
}
