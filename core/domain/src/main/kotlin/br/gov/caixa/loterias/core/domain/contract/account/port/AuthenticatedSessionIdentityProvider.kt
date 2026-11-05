package br.gov.caixa.loterias.core.domain.contract.account.port

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity

interface AuthenticatedSessionIdentityProvider {
    fun captureIdentity(): AuthenticatedSessionIdentity?
    fun isCurrent(identity: AuthenticatedSessionIdentity): Boolean
}
