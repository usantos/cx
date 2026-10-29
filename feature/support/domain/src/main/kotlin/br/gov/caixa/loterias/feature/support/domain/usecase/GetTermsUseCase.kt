package br.gov.caixa.loterias.feature.support.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface GetTermsUseCase {
    suspend operator fun invoke(): AppResult<String>
}
