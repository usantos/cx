package br.gov.caixa.loterias.feature.account.domain.model

/** Vínculo de sessão somente por identidade. A igualdade é por referência; recriar o hash não recria uma sessão. */
class AuthenticatedSessionIdentity(val ownerKey: String) {
    init { require(ownerKey.matches(Regex("[0-9a-f]{64}"))) }
    override fun toString() = "AuthenticatedSessionIdentity(redacted)"
}
