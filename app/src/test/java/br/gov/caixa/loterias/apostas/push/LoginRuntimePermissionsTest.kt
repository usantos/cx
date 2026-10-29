package br.gov.caixa.loterias.apostas.push

import android.Manifest.permission.*
import br.gov.caixa.loterias.feature.account.presentation.permission.loginRuntimePermissions
import org.junit.Assert.*
import org.junit.Test

class LoginRuntimePermissionsTest {
    private val declared = setOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, READ_PHONE_STATE,
        READ_PHONE_NUMBERS, READ_EXTERNAL_STORAGE, GET_ACCOUNTS, READ_CONTACTS, BLUETOOTH_CONNECT, POST_NOTIFICATIONS,
        INTERNET, ACCESS_WIFI_STATE, RECEIVE_BOOT_COMPLETED, BIND_NOTIFICATION_LISTENER_SERVICE)

    @Test fun `API guards exclude obsolete storage future Bluetooth notifications and capped contacts`() {
        val old = loginRuntimePermissions(23, declared, emptySet(), false)
        assertTrue(old.containsAll(listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, READ_PHONE_STATE,
            READ_EXTERNAL_STORAGE, GET_ACCOUNTS, READ_CONTACTS)))
        assertFalse(old.contains(READ_PHONE_NUMBERS))
        assertFalse(old.contains(BLUETOOTH_CONNECT))
        assertFalse(old.contains(POST_NOTIFICATIONS))
        val modern = loginRuntimePermissions(37, declared, emptySet(), false)
        assertTrue(modern.containsAll(listOf(READ_PHONE_NUMBERS, BLUETOOTH_CONNECT, POST_NOTIFICATIONS)))
        assertFalse(modern.contains(READ_EXTERNAL_STORAGE))
        assertFalse(modern.contains(READ_CONTACTS))
        for (normal in listOf(INTERNET, ACCESS_WIFI_STATE, RECEIVE_BOOT_COMPLETED, BIND_NOTIFICATION_LISTENER_SERVICE)) assertFalse(modern.contains(normal))
    }

    @Test fun `only declared ungranted runtime permissions are requested with location pair on Android12`() {
        assertEquals(listOf(POST_NOTIFICATIONS), loginRuntimePermissions(33, setOf(POST_NOTIFICATIONS), emptySet(), false))
        assertTrue(loginRuntimePermissions(33, declared, declared, false).isEmpty())
        assertEquals(listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
            loginRuntimePermissions(31, setOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), setOf(ACCESS_COARSE_LOCATION), false))
        assertTrue(loginRuntimePermissions(22, declared, emptySet(), false).isEmpty())
        assertTrue(loginRuntimePermissions(37, declared, emptySet(), true).isEmpty())
    }

    @Test fun `storage contacts and phone boundary versions follow SDK manifest`() {
        assertTrue(loginRuntimePermissions(32, declared, emptySet(), false).contains(READ_EXTERNAL_STORAGE))
        assertFalse(loginRuntimePermissions(33, declared, emptySet(), false).contains(READ_EXTERNAL_STORAGE))
        assertTrue(loginRuntimePermissions(36, declared, emptySet(), false).contains(READ_CONTACTS))
        assertTrue(loginRuntimePermissions(26, declared, emptySet(), false).contains(READ_PHONE_NUMBERS))
        assertTrue(loginRuntimePermissions(31, declared, emptySet(), false).contains(BLUETOOTH_CONNECT))
    }
}
