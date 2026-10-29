package br.gov.caixa.loterias.feature.retail.data.datasource.remote

import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationToken
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.network.error.BackendHttpFailure
import br.gov.caixa.loterias.core.data.mapper.toServiceConnectionFailure
import br.gov.caixa.loterias.core.data.mapper.toServiceFailure

internal class MercadoPagoRegistrationLoader(
    private val prizeSource: () -> PrizeRedemptionRemoteDataSource,
) {
    suspend fun mercadoPagoRegistrationToken(email: String): MercadoPagoRegistrationToken = try {
        MercadoPagoRegistrationToken.Available(prizeSource().mercadoPagoToken(email))
    } catch (failure: BackendHttpFailure) {
        MercadoPagoRegistrationToken.Rejected(failure.toServiceFailure(allowRegistration = true))
    } catch (failure: java.io.IOException) {
        MercadoPagoRegistrationToken.Rejected(ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()))
    }
}
