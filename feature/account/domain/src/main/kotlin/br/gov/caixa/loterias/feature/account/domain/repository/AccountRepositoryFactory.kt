package br.gov.caixa.loterias.feature.account.domain.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

/** Vincula o formulário ao proprietário capturado; ausência de sessão nunca autoriza escrita. */
fun interface AccountRepositoryFactory {
    fun forSession(identity: AuthenticatedSessionIdentity?): AccountRepository
}
