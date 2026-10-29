package br.gov.caixa.loterias.core.data.datasource.remote

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

/** O transporte informa ao repositório apenas uma confirmação de negócio verificada. */
fun interface PushRegistrationRemoteDataSource {
    suspend fun register(identity: AuthenticatedSessionIdentity, cpf: Long, token: String): Boolean
}

