package br.gov.caixa.loterias.core.domain.service

import br.gov.caixa.loterias.core.common.AppError

/** A service can return a business decision on either successful or failed HTTP status. */
sealed interface ServiceResult<out T> {
    data class Success<T>(val value: T) : ServiceResult<T>
    data class Rejected(val failure: ServiceFailure) : ServiceResult<Nothing>
    data class Failed(val error: AppError) : ServiceResult<Nothing>
}
