package br.gov.caixa.loterias.feature.onboarding

import org.junit.Assert.assertEquals
import org.junit.Test
import androidx.compose.ui.unit.dp

class StartupStatusContentTest {
    @Test fun `connection hints affect unavailable text but never bypass mandatory update`() {
        val connected = br.gov.caixa.loterias.core.common.ConnectionAvailability.CONNECTED
        val disconnected = br.gov.caixa.loterias.core.common.ConnectionAvailability.DISCONNECTED
        val unknown = br.gov.caixa.loterias.core.common.ConnectionAvailability.UNKNOWN
        for (kind in StartupStatusKind.entries) {
            assertEquals(kind, startupStatusForConnection(kind, unknown))
            assertEquals(if (kind == StartupStatusKind.UPDATE_REQUIRED) kind else StartupStatusKind.UNAVAILABLE,
                startupStatusForConnection(kind, connected))
            assertEquals(if (kind == StartupStatusKind.UPDATE_REQUIRED) kind else StartupStatusKind.OFFLINE,
                startupStatusForConnection(kind, disconnected))
        }
    }

    @Test fun `unavailable and offline retain different literal legacy messages`() {
        assertEquals("O aplicativo está\ntemporariamente indisponível",
            StartupStatusKind.UNAVAILABLE.message)
        assertEquals("Pedimos desculpas pelo transtorno.\nTente novamente em breve",
            StartupStatusKind.UNAVAILABLE.detailMessage)
        assertEquals("Parece que sua internet\nestá com alguma instabilidade",
            StartupStatusKind.OFFLINE.message)
        assertEquals("Verifique sua conexão\ne tente novamente", StartupStatusKind.OFFLINE.detailMessage)
        assertEquals("Tentar novamente", StartupStatusKind.UNAVAILABLE.actionLabel)
        assertEquals("Tentar novamente", StartupStatusKind.OFFLINE.actionLabel)
    }

    @Test fun `mandatory update requests store action not retry`() {
        assertEquals("O seu aplicativo está desatualizado. Para continuar será necessário efetuar a atualização.",
            StartupStatusKind.UPDATE_REQUIRED.message)
        assertEquals("Atualizar", StartupStatusKind.UPDATE_REQUIRED.actionLabel)
    }

    @Test fun `version uses build input instead of fixed example`() {
        assertEquals("v4.7.2", startupStatusVersion("4.7.2"))
        assertEquals("v4.7.2-TQS", startupStatusVersion("4.7.2-TQS"))
    }

    @Test fun `content midpoint matches full screen legacy constraint layout`() {
        assertEquals(200.dp, legacyStatusContentMiddle(400.dp))
    }
}
