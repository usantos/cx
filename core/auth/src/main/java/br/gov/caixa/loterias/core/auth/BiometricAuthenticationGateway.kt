package br.gov.caixa.loterias.core.auth

import android.content.Intent

/** Opt-in remembered login. A local prompt alone never grants backend access. */
interface BiometricAuthenticationGateway {
    val biometricEnabled: Boolean
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
