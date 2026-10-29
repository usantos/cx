package br.gov.caixa.loterias.feature.account.domain.repository

import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

/** O transporte após o login deve usar a sessão capturada, e não um token de acesso genérico posterior. */
fun interface PostLoginAccountRepository {
    suspend fun verifyAccess(identity: AuthenticatedSessionIdentity): AccountAccess
}
