package br.gov.caixa.loterias.core.auth

import java.util.Base64
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.auth.session.ForegroundSessionStatus

/** Textos sintéticos sem assinatura exercitam apenas a expiração; nunca autenticam nem contatam um provedor. */
class ForegroundSessionCheckTest {
    @Test fun missingExpiredMalformedAndFutureTokensAreClassifiedWithoutClearing() {
        val store = SessionTokenStore()
        assertEquals(ForegroundSessionStatus.MISSING, store.foregroundCheck(100).status)
        for (token in listOf(jwt("{\"exp\":99}"), jwt("{\"exp\":100}"), "malformed", jwt("{}"))) {
            store.replace(token)
            assertEquals(ForegroundSessionStatus.EXPIRED, store.foregroundCheck(100).status)
            assertEquals(token, store.token())
        }
        store.replace(jwt("{\"exp\":101}"))
        assertEquals(ForegroundSessionStatus.ACTIVE, store.foregroundCheck(100).status)
    }

    @Test fun pendingBrowserOrBiometricAttemptSuppressesMissingAndExpiredClassification() {
        val store = SessionTokenStore()
        for (token in listOf(null, jwt("{\"exp\":1}"))) {
            store.replace(token)
            val attempt = store.beginAuthorization()
            assertEquals(ForegroundSessionStatus.AUTHORIZATION_PENDING, store.foregroundCheck(100).status)
            store.cancelAuthorization(attempt)
        }
    }

    @Test fun acknowledgmentClearsOnlyTheCapturedSessionAndInvalidatesItsLease() {
        val store = SessionTokenStore()
        val token = jwt("{\"exp\":1}")
        store.completeAuthorization(store.beginAuthorization(), token, "synthetic-id", "a".repeat(64))
        val identity = store.captureIdentity()!!
        val check = store.foregroundCheck(100)
        assertTrue(store.clearForegroundSession(check))
        assertNull(store.token())
        assertFalse(store.isCurrent(identity))
        assertFalse(store.clearForegroundSession(check))
    }

    @Test fun staleAcknowledgmentCannotClearReplacementOrNewPendingAuthorization() {
        val store = SessionTokenStore()
        store.replace(jwt("{\"exp\":1}"))
        val replaced = store.foregroundCheck(100)
        store.replace(jwt("{\"exp\":200}"))
        assertFalse(store.clearForegroundSession(replaced))
        val expired = jwt("{\"exp\":1}")
        store.replace(expired)
        val beforeLogin = store.foregroundCheck(100)
        val pending = store.beginAuthorization()
        assertFalse(store.clearForegroundSession(beforeLogin))
        assertTrue(store.completeAuthorization(pending, "new-synthetic-token"))
        assertEquals("new-synthetic-token", store.token())
    }

    @Test fun activeChecksCannotClearAndContentRefreshDoesNotInvalidateTheSameExpiredLease() {
        val store = SessionTokenStore()
        store.replace(jwt("{\"exp\":200}"))
        assertFalse(store.clearForegroundSession(store.foregroundCheck(100)))
        store.completeAuthorization(store.beginAuthorization(), jwt("{\"exp\":1}"), ownerKey = "a".repeat(64))
        val identity = store.captureIdentity()!!
        val check = store.foregroundCheck(100)
        assertTrue(store.publish(identity, null))
        assertTrue(store.clearForegroundSession(check))
    }

    private fun jwt(claims: String) = "e30.${Base64.getUrlEncoder().withoutPadding().encodeToString(claims.toByteArray())}.c2ln"
}
