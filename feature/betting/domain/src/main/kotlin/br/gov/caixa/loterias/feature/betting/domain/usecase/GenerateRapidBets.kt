package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.domain.repository.RapidBettingRepository

class GenerateRapidBets(private val repository: RapidBettingRepository) : GenerateRapidBetsUseCase {
    override suspend fun invoke(quantity: Int) = if (quantity < 0) {
        ServiceResult.Failed(
            AppError.Validation(messageText("quantidade_invalida_2eede1"))
        )
    } else {
        repository.generate(quantity)
    }
}
