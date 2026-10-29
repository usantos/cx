package br.gov.caixa.loterias.feature.account.domain.port

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

interface AuthenticatedSessionIdentityProvider {
    fun captureIdentity(): AuthenticatedSessionIdentity?
    fun isCurrent(identity: AuthenticatedSessionIdentity): Boolean
}
