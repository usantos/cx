package br.gov.caixa.loterias.core.presentation

import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.presentation.rule.connectionFailureNotice
import br.gov.caixa.loterias.core.presentation.rule.loginConnectivityNotice
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_OFFLINE
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_CONNECTION_FAILED
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_RESPONSE_TIMEOUT
import br.gov.caixa.loterias.core.presentation.formatting.LEGACY_INVALID_RESPONSE

class ServiceConnectionRulesTest {
    @Test fun `offline takes priority and unknown connectivity does not impersonate an offline device`() {
        for (availability in ConnectionAvailability.entries) for (reason in ServiceConnectionFailure.entries) {
            val notice = connectionFailureNotice(reason, availability)
            assertEquals(when {
                availability == ConnectionAvailability.DISCONNECTED -> LEGACY_OFFLINE
                reason == ServiceConnectionFailure.TIMEOUT -> LEGACY_RESPONSE_TIMEOUT
                reason == ServiceConnectionFailure.INVALID_RESPONSE -> LEGACY_INVALID_RESPONSE
                else -> LEGACY_CONNECTION_FAILED
            }, notice.message)
            assertNull(notice.destination); assertNull(notice.negativeLabel); assertEquals("Entendi", notice.positiveLabel)
        }
    }

    @Test fun `login checks current connectivity only at that destination`() {
        var availability = ConnectionAvailability.DISCONNECTED
        var calls = 0
        val network = NetworkAvailability { calls++; availability }
        for (destination in ServiceRedirect.entries.filter { it != ServiceRedirect.LOGIN }) {
            assertNull(loginConnectivityNotice(destination, network))
        }
        assertEquals(0, calls)
        assertEquals(LEGACY_OFFLINE, loginConnectivityNotice(ServiceRedirect.LOGIN, network)!!.message)
        availability = ConnectionAvailability.CONNECTED
        assertNull(loginConnectivityNotice(ServiceRedirect.LOGIN, network))
        availability = ConnectionAvailability.UNKNOWN
        assertNull(loginConnectivityNotice(ServiceRedirect.LOGIN, network))
        assertEquals(3, calls)
    }
}
