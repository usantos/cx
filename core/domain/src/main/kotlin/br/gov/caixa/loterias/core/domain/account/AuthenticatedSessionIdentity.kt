package br.gov.caixa.loterias.core.domain.account

/** Identity-only lease. Equality is referential; recreating the hash does not recreate a session. */
class AuthenticatedSessionIdentity(val ownerKey: String) {
    init { require(ownerKey.matches(Regex("[0-9a-f]{64}"))) }
    override fun toString() = "AuthenticatedSessionIdentity(redacted)"
}

interface AuthenticatedSessionIdentityProvider {
    fun captureIdentity(): AuthenticatedSessionIdentity?
    fun isCurrent(identity: AuthenticatedSessionIdentity): Boolean
}
