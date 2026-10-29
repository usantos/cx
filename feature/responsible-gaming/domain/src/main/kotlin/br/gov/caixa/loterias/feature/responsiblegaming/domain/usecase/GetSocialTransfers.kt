package br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.ResponsibleGamingRepository

class GetSocialTransfers(
    private val repository: ResponsibleGamingRepository,
) : GetSocialTransfersUseCase {
    override suspend fun invoke() = appResultOf { repository.socialTransfers() }
}
