package br.gov.caixa.loterias.feature.support.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface AcceptTermsUseCase {
    suspend operator fun invoke(): AppResult<Unit>
}
