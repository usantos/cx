package br.gov.caixa.loterias.feature.support.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.support.domain.repository.SupportRepository

class GetSupportQuestions(
    private val repository: SupportRepository,
) : GetSupportQuestionsUseCase {
    override suspend fun invoke(sectionId: Long) = appResultOf { repository.questions(sectionId) }
}
