package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.feature.betting.domain.repository.RapidBettingRepository

class GetRapidBetConfiguration(private val repository: RapidBettingRepository) : GetRapidBetConfigurationUseCase {
    override suspend fun invoke() = repository.configuration()
}
