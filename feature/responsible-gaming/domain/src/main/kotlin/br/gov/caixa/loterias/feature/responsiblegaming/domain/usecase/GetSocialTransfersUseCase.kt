package br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers

fun interface GetSocialTransfersUseCase {
    suspend operator fun invoke(): AppResult<SocialTransfers>
}
