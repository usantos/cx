package br.gov.caixa.loterias.feature.support.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion

fun interface GetSupportQuestionsUseCase {
    suspend operator fun invoke(sectionId: Long): AppResult<List<SupportQuestion>>
}
