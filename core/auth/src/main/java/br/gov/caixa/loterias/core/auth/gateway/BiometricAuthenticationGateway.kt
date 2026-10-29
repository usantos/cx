package br.gov.caixa.loterias.core.auth.gateway

import android.content.Intent
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.auth.biometric.BiometricCredential
import br.gov.caixa.loterias.core.auth.authorization.PendingAuthorization

/** Login lembrado opcional. Uma solicitação local, sozinha, nunca concede acesso ao servidor. */
interface BiometricAuthenticationGateway : br.gov.caixa.loterias.feature.account.domain.port.BiometricLoginPreferences {
    override val biometricEnabled: Boolean
    /** NONE_ENROLLED ainda oferece o cadastro; hardware ausente ou indisponível oculta a linha. */
    override val biometricHardwareAvailable: Boolean get() = true
    override val biometricAvailable: Boolean
    override val biometricCredentialAvailable: Boolean
    override fun setBiometricEnabled(enabled: Boolean)
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
