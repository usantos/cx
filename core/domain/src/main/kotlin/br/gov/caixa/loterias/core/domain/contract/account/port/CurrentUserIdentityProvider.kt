package br.gov.caixa.loterias.core.domain.contract.account.port

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedUser

fun interface CurrentUserIdentityProvider {
    fun currentUser(): AuthenticatedUser?
}
