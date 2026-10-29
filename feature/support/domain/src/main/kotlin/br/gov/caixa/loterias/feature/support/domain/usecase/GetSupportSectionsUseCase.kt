package br.gov.caixa.loterias.feature.support.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection

fun interface GetSupportSectionsUseCase {
    suspend operator fun invoke(): AppResult<List<SupportSection>>
}
