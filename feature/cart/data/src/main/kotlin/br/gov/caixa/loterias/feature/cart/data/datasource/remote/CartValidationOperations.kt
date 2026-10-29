package br.gov.caixa.loterias.feature.cart.data.datasource.remote

import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.network.error.BackendHttpFailure
import java.io.IOException
import br.gov.caixa.loterias.core.data.mapper.toServiceConnectionFailure
import br.gov.caixa.loterias.core.data.mapper.toServiceFailure

internal class CartValidationOperations(private val remote: CartRemoteDataSource) {
    suspend fun validate(): CartValidationResult = try {
        try {
            remote.validateCart()
            CartValidationResult.Accepted
        } catch (failure: BackendHttpFailure) {
            CartValidationResult.Rejected(failure.toServiceFailure(allowRegistration = false))
        }
    } catch (_: com.google.gson.stream.MalformedJsonException) {
        CartValidationResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE))
    } catch (_: com.google.gson.JsonParseException) {
        CartValidationResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE))
    } catch (failure: IOException) {
        CartValidationResult.Rejected(ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()))
    }
}
