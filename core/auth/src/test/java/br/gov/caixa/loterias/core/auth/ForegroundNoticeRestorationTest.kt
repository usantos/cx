package br.gov.caixa.loterias.core.auth

import java.util.Base64
import org.junit.Assert.*
import org.junit.Test

class ForegroundNoticeRestorationTest {
    @Test fun savedUiMarkerRestoresTheSameExpiredSnapshotWithoutClearingUntilAccept() {
        val store = SessionTokenStore()
        val expired = jwt(99)
        store.replace(expired)
        val check = store.foregroundCheck(100)
        val marker = store.rememberForegroundNotice(check)
        assertNotNull("Expired warning must have a restorable UI marker", marker)
        assertFalse(marker!!.contains(expired))
        val restored = store.restoreForegroundNotice(marker, 100)
        assertNotNull(restored)
        assertEquals(expired, store.token())
        assertTrue(store.clearForegroundSession(restored!!))
        assertNull(store.restoreForegroundNotice(marker, 100))
    }

    @Test fun savedOldMarkerCannotCaptureAReplacementExpiredSessionOrPendingLogin() {
        val store = SessionTokenStore()
        store.replace(jwt(99))
        val marker = store.rememberForegroundNotice(store.foregroundCheck(100))
        assertNotNull(marker)
        val replacement = jwt(98)
        store.replace(replacement)
        assertNull(store.restoreForegroundNotice(marker!!, 100))
        assertEquals(replacement, store.token())
        val currentMarker = store.rememberForegroundNotice(store.foregroundCheck(100))!!
        val pending = store.beginAuthorization()
        assertNull(store.restoreForegroundNotice(currentMarker, 100))
        assertTrue(store.completeAuthorization(pending, "new-synthetic-token"))
    }

    @Test fun markerFromDeadProcessDoesNotRestoreCredentialsOrWarning() {
        val previous = SessionTokenStore().apply { replace(jwt(99)) }
        val marker = previous.rememberForegroundNotice(previous.foregroundCheck(100))
        assertNotNull(marker)
        val coldStore = SessionTokenStore()
        assertNull(coldStore.restoreForegroundNotice(marker!!, 100))
        assertNull(coldStore.token())
    }

    private fun jwt(exp: Long) = "e30.${Base64.getUrlEncoder().withoutPadding().encodeToString("{\"exp\":$exp}".toByteArray())}.c2ln"
}
