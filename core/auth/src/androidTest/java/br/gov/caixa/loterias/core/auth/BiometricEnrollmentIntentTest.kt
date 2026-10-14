package br.gov.caixa.loterias.core.auth

import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.provider.Settings
import org.junit.Assert.*
import org.junit.Test

class BiometricEnrollmentIntentTest {
    @Test fun enrollmentOffersWeakBiometricAndDeviceCredential() {
        val launched = mutableListOf<Intent>()
        launchBiometricEnrollment { launched += it }
        assertEquals(1, launched.size)
        assertEquals(Settings.ACTION_BIOMETRIC_ENROLL, launched.single().action)
        assertEquals(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL,
            launched.single().getIntExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, 0))
    }
    @Test fun unavailableEnrollmentActionFallsBackToSettings() {
        val launched = mutableListOf<Intent>()
        launchBiometricEnrollment { launched += it; if (launched.size == 1) throw android.content.ActivityNotFoundException() }
        assertEquals(listOf(Settings.ACTION_BIOMETRIC_ENROLL, Settings.ACTION_SETTINGS), launched.map { it.action })
    }
}
