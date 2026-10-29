package br.gov.caixa.loterias.core.security

import android.hardware.biometrics.BiometricManager
import br.gov.caixa.loterias.core.security.authentication.AndroidWeakBiometricAvailability
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(BiometricManager::class)
class WeakBiometricManagerShadow {
    @Implementation fun canAuthenticate(authenticators: Int): Int {
        requested = authenticators
        return status
    }
    companion object {
        var status = BiometricManager.BIOMETRIC_SUCCESS
        var requested = 0
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], shadows = [WeakBiometricManagerShadow::class])
class WeakBiometricPlatformTest {
    @Test fun `extended scopes require successful weak biometrics and reject enrollment and hardware errors`() {
        val availability = AndroidWeakBiometricAvailability(RuntimeEnvironment.getApplication())
        for (status in listOf(
            BiometricManager.BIOMETRIC_SUCCESS,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED,
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
        )) {
            WeakBiometricManagerShadow.status = status
            assertEquals(status == BiometricManager.BIOMETRIC_SUCCESS, availability.available())
            assertEquals(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK, WeakBiometricManagerShadow.requested)
        }
    }
}
