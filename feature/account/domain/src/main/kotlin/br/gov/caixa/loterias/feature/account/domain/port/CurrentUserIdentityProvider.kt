package br.gov.caixa.loterias.feature.account.domain.port

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedUser

fun interface CurrentUserIdentityProvider {
    fun currentUser(): AuthenticatedUser?
}
