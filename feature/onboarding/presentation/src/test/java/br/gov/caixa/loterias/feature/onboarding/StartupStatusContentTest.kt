package br.gov.caixa.loterias.feature.onboarding

import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.StartupStatusKind
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.legacyStatusContentMiddle
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.startupStatusForConnection
import org.junit.Assert.assertEquals
import org.junit.Test

class StartupStatusContentTest {
    @Test fun `connection hints affect unavailable text but never bypass mandatory update`() {
        val connected = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.CONNECTED
        val disconnected = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.DISCONNECTED
        val unknown = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.UNKNOWN
        for (kind in StartupStatusKind.entries) {
            assertEquals(kind, startupStatusForConnection(kind, unknown))
            assertEquals(
                if (kind == StartupStatusKind.UPDATE_REQUIRED) kind else StartupStatusKind.UNAVAILABLE,
                startupStatusForConnection(kind, connected)
            )
            assertEquals(
                if (kind == StartupStatusKind.UPDATE_REQUIRED) kind else StartupStatusKind.OFFLINE,
                startupStatusForConnection(kind, disconnected)
            )
        }
    }

    @Test fun `connection failure and service unavailability have distinct resources and codes`() {
        assertEquals(R.string.startup_unavailable_message, StartupStatusKind.UNAVAILABLE.message)
        assertEquals(R.string.startup_offline_message, StartupStatusKind.OFFLINE.message)
        assertEquals("LTR-001", StartupStatusKind.OFFLINE.errorCode)
        assertEquals("LTR-503", StartupStatusKind.UNAVAILABLE.errorCode)
        assertEquals(R.string.startup_retry, StartupStatusKind.OFFLINE.actionLabel)
        assertEquals(R.string.startup_update, StartupStatusKind.UPDATE_REQUIRED.actionLabel)
        assertEquals(null, StartupStatusKind.UPDATE_REQUIRED.errorCode)
    }

    @Test fun `content midpoint matches full screen legacy constraint layout`() {
        assertEquals(200.dp, legacyStatusContentMiddle(400.dp))
    }
}
