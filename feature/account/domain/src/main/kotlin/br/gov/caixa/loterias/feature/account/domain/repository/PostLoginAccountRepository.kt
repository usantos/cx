package br.gov.caixa.loterias.feature.account.domain.repository

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess

/** O transporte após o login deve usar a sessão capturada, e não um token de acesso genérico posterior. */
fun interface PostLoginAccountRepository {
    suspend fun verifyAccess(identity: AuthenticatedSessionIdentity): AccountAccess
}
