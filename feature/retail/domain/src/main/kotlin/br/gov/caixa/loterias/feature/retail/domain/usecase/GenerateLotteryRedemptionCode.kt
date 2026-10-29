package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.retail.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.domain.repository.PrizeRedemptionRepository

class GenerateLotteryRedemptionCode(private val repository: PrizeRedemptionRepository) : GenerateLotteryRedemptionCodeUseCase {
    override suspend fun invoke(betId: Long) = appResultOf {
        require(betId > 0) { messageText("aposta_invalida_de9ad6") }
        repository.lotteryCode(betId)
    }
}
