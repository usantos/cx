package br.gov.caixa.loterias.feature.payment.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.feature.account.data.dto.UserProfileDto
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface PaymentLoteriasApi {
    @GET("usuarios/recuperar-dados")
    suspend fun currentUserProfile(): ApiEnvelope<UserProfileDto>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun LoteriasApi.featurePaymentApi(): PaymentLoteriasApi = PaymentLoteriasApiProjection(this)

private class PaymentLoteriasApiProjection(private val transport: LoteriasApi) : PaymentLoteriasApi {
    override suspend fun currentUserProfile(): ApiEnvelope<UserProfileDto> = transport.currentUserProfile()
}
