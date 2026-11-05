package br.gov.caixa.loterias.feature.account.domain.repository

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity

fun interface UnreadNotificationCountRepository {
    suspend fun getCount(identity: AuthenticatedSessionIdentity): Int
}
