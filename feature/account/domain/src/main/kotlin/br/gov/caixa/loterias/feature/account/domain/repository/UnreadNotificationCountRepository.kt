package br.gov.caixa.loterias.feature.account.domain.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

fun interface UnreadNotificationCountRepository {
    suspend fun getCount(identity: AuthenticatedSessionIdentity): Int
}
