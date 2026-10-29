package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration

fun interface SaveRapidBetConfigurationUseCase {
    suspend operator fun invoke(configuration: RapidBetConfiguration): ServiceResult<RapidBetConfiguration>
}
