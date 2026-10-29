package br.gov.caixa.loterias.feature.notifications.domain.push

import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider

/** A captura da identidade ocorre antes da obtenção assíncrona do token da plataforma. */
class PushRegistrationCoordinator(
    private val identities: AuthenticatedSessionIdentityProvider,
    private val register: suspend (String) -> Boolean,
) {
    suspend fun refresh(token: suspend () -> String?): Boolean {
        val identity = identities.captureIdentity() ?: return false
        val value = token() ?: return false
        if (!identities.isCurrent(identity)) return false
        return register(value)
    }
    suspend fun onNewToken(token: String): Boolean = register(token)
}
