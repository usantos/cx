package br.gov.caixa.loterias.feature.support.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.support.domain.repository.SupportRepository

class GetSupportSections(
    private val repository: SupportRepository,
) : GetSupportSectionsUseCase {
    override suspend fun invoke() = appResultOf { repository.sections() }
}
