package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.feature.account.data.dto.BlockedAccountVerificationEnvelopeDto
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface AccountBackendApi {
    @GET("usuarios/verificar-apostador-bloqueado")
    suspend fun blockedUserStatus(): BlockedAccountVerificationEnvelopeDto
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun BackendApi.featureAccountApi(): AccountBackendApi = AccountBackendApiProjection(this)

private class AccountBackendApiProjection(private val transport: BackendApi) : AccountBackendApi {
    override suspend fun blockedUserStatus(): BlockedAccountVerificationEnvelopeDto = transport.blockedUserStatus()
}
