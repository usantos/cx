package br.gov.caixa.loterias.core.auth

import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.auth.gateway.biometricHardwareAvailable
import br.gov.caixa.loterias.core.auth.gateway.sameBiometricCredential
import br.gov.caixa.loterias.core.auth.biometric.BiometricCredential

class BiometricCredentialContextTest {
    @Test fun `no hardware and unavailable hide row while no enrollment permits enrollment`() {
        assertFalse(biometricHardwareAvailable(12)) // BIOMETRIC_ERROR_NO_HARDWARE
        assertFalse(biometricHardwareAvailable(1)) // BIOMETRIC_ERROR_HW_UNAVAILABLE
        assertTrue(biometricHardwareAvailable(11)) // BIOMETRIC_ERROR_NONE_ENROLLED
        assertTrue(biometricHardwareAvailable(0))
    }
    @Test fun `same owner never authorizes a rotated or replaced remembered credential`() {
        val original = BiometricCredential("refresh", "a".repeat(64), "nonce")
        assertTrue(sameBiometricCredential(original, BiometricCredential("refresh", "a".repeat(64), "nonce")))
        assertFalse(sameBiometricCredential(original, BiometricCredential("new-refresh", "a".repeat(64), "nonce")))
        assertFalse(sameBiometricCredential(original, BiometricCredential("refresh", "a".repeat(64), "new-nonce")))
        assertFalse(sameBiometricCredential(original, BiometricCredential("refresh", "b".repeat(64), "nonce")))
        assertFalse(sameBiometricCredential(original, null))
    }
}
