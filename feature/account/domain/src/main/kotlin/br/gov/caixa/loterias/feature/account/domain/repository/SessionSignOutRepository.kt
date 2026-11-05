package br.gov.caixa.loterias.feature.account.domain.repository

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult

fun interface SessionSignOutRepository {
    suspend fun signOut(identity: AuthenticatedSessionIdentity): SessionSignOutResult
}
