package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.auth.SessionTokenStore
import java.util.Base64
import org.junit.Assert.*
import org.junit.Test

class ForegroundSessionNoticeControllerTest {
    @Test fun recreatedControllerRebuildsEntendiActionFromOnlySavedUiMarker() {
        val store = SessionTokenStore().apply { replace(jwt(99)) }
        var logins = 0
        val original = ForegroundSessionNoticeController(store, { 100 }, { logins++ })
        val savedMarker = original.show(store.foregroundCheck(100))
        assertNotNull("Warning marker must survive configuration recreation", savedMarker)
        val recreated = ForegroundSessionNoticeController(store, { 100 }, { logins++ })
        val accept = recreated.restore(savedMarker!!)
        assertNotNull("Restored marker must rebuild the Entendi callback", accept)
        assertEquals(0, logins)
        accept!!.invoke()
        assertNull(store.token())
        assertEquals(1, logins)
        assertNull(recreated.restore(savedMarker))
    }

    @Test fun reconstructionCannotBindOldWarningToReplacementExpiredSession() {
        val store = SessionTokenStore().apply { replace(jwt(99)) }
        var logins = 0
        val original = ForegroundSessionNoticeController(store, { 100 }, { logins++ })
        val marker = original.show(store.foregroundCheck(100))
        assertNotNull(marker)
        val replacement = jwt(98)
        store.replace(replacement)
        val recreated = ForegroundSessionNoticeController(store, { 100 }, { logins++ })
        assertNull(recreated.restore(marker!!))
        assertEquals(replacement, store.token())
        assertEquals(0, logins)
    }
    private fun jwt(exp: Long) = "e30.${Base64.getUrlEncoder().withoutPadding().encodeToString("{\"exp\":$exp}".toByteArray())}.c2ln"
}
