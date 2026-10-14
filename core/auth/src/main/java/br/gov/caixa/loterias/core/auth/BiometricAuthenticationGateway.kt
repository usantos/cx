package br.gov.caixa.loterias.core.auth

import android.content.Intent

/** Opt-in remembered login. A local prompt alone never grants backend access. */
interface BiometricAuthenticationGateway {
    val biometricEnabled: Boolean
    /** NONE_ENROLLED still exposes enrollment; absent/unavailable hardware hides the row. */
    val biometricHardwareAvailable: Boolean get() = true
    val biometricAvailable: Boolean
    val biometricCredentialAvailable: Boolean
    fun setBiometricEnabled(enabled: Boolean)
    fun createBiometricAuthorizationIntent(): Intent
    suspend fun completeBiometricAuthorization(result: Intent?)
}

internal class BiometricAttemptRegistry(private val tokens: SessionTokenStore) {
    private var pending: PendingAuthorization<String>? = null
    @Synchronized fun begin(state: String, owner: String): PendingAuthorization<String> =
        PendingAuthorization(state, owner, tokens.beginAuthorization()).also { pending = it }
    @Synchronized fun consume(state: String?): PendingAuthorization<String>? {
        val current = pending ?: return null
        if (state != current.state) return null
        pending = null
        return current
    }
    @Synchronized fun cancel() {
        pending?.let { tokens.cancelAuthorization(it.ticket) }
        pending = null
    }
}

internal fun biometricHardwareAvailable(status: Int): Boolean =
    status == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS ||
        status == android.hardware.biometrics.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

fun launchBiometricEnrollment(startActivity: (Intent) -> Unit) {
    try {
        check(android.os.Build.VERSION.SDK_INT >= 30)
        startActivity(Intent(android.provider.Settings.ACTION_BIOMETRIC_ENROLL).putExtra(
            android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
            android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK or
                android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL))
    } catch (_: Exception) { startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) }
}

internal fun sameBiometricCredential(first: BiometricCredential, second: BiometricCredential?): Boolean =
    second != null && first.ownerKey == second.ownerKey && first.nonce == second.nonce &&
        first.refreshToken == second.refreshToken
