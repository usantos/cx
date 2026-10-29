package br.gov.caixa.loterias.core.domain.service.model

import br.gov.caixa.loterias.core.common.result.AppError

/** Um serviço pode retornar uma decisão de negócio tanto em respostas HTTP de sucesso quanto de falha. */
sealed interface ServiceResult<out T> {
    data class Success<T>(val value: T) : ServiceResult<T>
    data class Rejected(val failure: ServiceFailure) : ServiceResult<Nothing>
    data class Failed(val error: AppError) : ServiceResult<Nothing>
}
