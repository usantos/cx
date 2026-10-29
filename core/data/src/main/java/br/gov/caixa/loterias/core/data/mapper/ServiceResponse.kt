package br.gov.caixa.loterias.core.data.mapper

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.error.BackendErrorMetadata
import br.gov.caixa.loterias.core.network.error.BackendHttpFailure
import br.gov.caixa.loterias.core.network.error.toBackendHttpFailure
import kotlinx.coroutines.CancellationException

fun <T> ApiEnvelope<T>.checkedPayload(): T? {
    if (redirect != null) throw BackendHttpFailure(200, BackendErrorMetadata(redirect = redirect, message = message))
    return payload
}

suspend fun <T> serviceResultOf(block: suspend () -> T): ServiceResult<T> = try {
    ServiceResult.Success(block())
} catch (cancelled: CancellationException) {
    throw cancelled
} catch (failure: BackendHttpFailure) {
    ServiceResult.Rejected(failure.toServiceFailure(allowRegistration = false))
} catch (failure: retrofit2.HttpException) {
    ServiceResult.Rejected(failure.toBackendHttpFailure().toServiceFailure(allowRegistration = false))
} catch (failure: java.io.IOException) {
    ServiceResult.Rejected(ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()))
} catch (failure: IllegalArgumentException) {
    ServiceResult.Failed(AppError.Validation(failure.message.orEmpty()))
} catch (_: Exception) {
    ServiceResult.Failed(AppError.Unexpected)
}
