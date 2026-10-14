package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.domain.service.*
import br.gov.caixa.loterias.core.network.*
import kotlinx.coroutines.CancellationException

internal fun <T> ApiEnvelope<T>.checkedPayload(): T? {
    if (redirect != null) throw BackendHttpFailure(200, BackendErrorMetadata(redirect = redirect, message = message))
    return payload
}

internal suspend fun <T> serviceResultOf(block: suspend () -> T): ServiceResult<T> = try {
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
