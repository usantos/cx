package br.gov.caixa.loterias.core.auth

import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationDeviceInfoProvider
import br.gov.caixa.loterias.core.auth.authorization.WeakBiometricAvailability
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationRequestPolicy

class AuthorizationRequestPolicyTest {
    @Test fun `device digest replaces every pipe and sends exact CX parameter names`() {
        val options = AuthorizationRequestPolicy(AuthorizationDeviceInfoProvider { "synthetic|device||info" }, WeakBiometricAvailability { false }).capture()
        assertEquals(mapOf("deviceid" to "synthetic-device--info", "nivel" to "10",
            "app" to "br.gov.caixa.loterias.apostas", "origem" to "tofd"), options.additionalParameters)
        assertNull(options.scope)
        assertFalse(options.toString().contains("synthetic"))
    }

    @Test fun `null empty and SDK exceptions preserve provider defaults without fabricated identifiers`() {
        for (deviceInfo in listOf(AuthorizationDeviceInfoProvider { null }, AuthorizationDeviceInfoProvider { "" }, AuthorizationDeviceInfoProvider { error("SDK failure") })) {
            val options = AuthorizationRequestPolicy(deviceInfo, WeakBiometricAvailability { false }).capture()
            assertTrue(options.additionalParameters.isEmpty())
            assertNull(options.scope)
        }
    }

    @Test fun `only weak biometric success adds legacy extended scopes independently of device info`() {
        for (available in listOf(false, true)) {
            val options = AuthorizationRequestPolicy(AuthorizationDeviceInfoProvider { null }, WeakBiometricAvailability { available }).capture()
            assertEquals(if (available) "openid profile offline_access" else null, options.scope)
            assertTrue(options.additionalParameters.isEmpty())
        }
        assertNull(AuthorizationRequestPolicy(AuthorizationDeviceInfoProvider { "device" }, WeakBiometricAvailability { error("permission denied") }).capture().scope)
    }

    @Test fun `each explicit authorization obtains fresh SDK info and biometric availability once`() {
        var digests = 0
        var biometrics = 0
        val policy = AuthorizationRequestPolicy(AuthorizationDeviceInfoProvider { "synthetic-${++digests}" }, WeakBiometricAvailability { ++biometrics; biometrics == 1 })
        val first = policy.capture()
        val second = policy.capture()
        assertEquals("synthetic-1", first.additionalParameters["deviceid"])
        assertEquals("synthetic-2", second.additionalParameters["deviceid"])
        assertEquals("openid profile offline_access", first.scope)
        assertNull(second.scope)
        assertEquals(2, digests)
        assertEquals(2, biometrics)
    }
}
