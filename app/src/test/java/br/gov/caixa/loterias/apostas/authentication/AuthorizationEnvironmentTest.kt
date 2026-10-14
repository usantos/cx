package br.gov.caixa.loterias.apostas.authentication

import br.gov.caixa.loterias.core.auth.AuthorizationDeviceInfoProvider
import br.gov.caixa.loterias.core.auth.WeakBiometricAvailability
import org.junit.Assert.*
import org.junit.Test

class AuthorizationEnvironmentTest {
    @Test fun `simulation skips all SDK and biometric collection without synthetic production identifiers`() {
        val policy = loginAuthorizationRequestPolicy(true,
            AuthorizationDeviceInfoProvider { error("Must not collect SDK info in simulation") },
            WeakBiometricAvailability { error("Must not inspect biometrics in simulation") })
        assertTrue(policy.capture().additionalParameters.isEmpty())
        assertNull(policy.capture().scope)
    }

    @Test fun `production obtains injected SDK digest and weak biometric policy`() {
        val options = loginAuthorizationRequestPolicy(false, AuthorizationDeviceInfoProvider { "test|digest" },
            WeakBiometricAvailability { true }).capture()
        assertEquals("test-digest", options.additionalParameters["deviceid"])
        assertEquals("openid profile offline_access", options.scope)
    }
}
