package br.gov.caixa.loterias.core.domain.push.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

interface PushRegistrationLocalRepository {
    suspend fun contains(identity: AuthenticatedSessionIdentity, marker: String): Boolean
    suspend fun remember(identity: AuthenticatedSessionIdentity, marker: String)
}
