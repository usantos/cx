package br.gov.caixa.loterias.feature.account.domain.repository

import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

fun interface SessionSignOutRepository {
    suspend fun signOut(identity: AuthenticatedSessionIdentity): SessionSignOutResult
}
