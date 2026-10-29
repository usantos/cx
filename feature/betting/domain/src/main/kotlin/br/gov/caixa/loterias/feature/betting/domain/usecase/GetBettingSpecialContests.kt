package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository

fun interface GetBettingSpecialContestsUseCase {
    suspend operator fun invoke(): AppResult<HomeSpecialContests>
}

/** Usa a configuração compartilhada em cache, como o cabeçalho nativo usa as preferências da abertura. */
class GetBettingSpecialContests(private val configuration: AppConfigurationRepository) : GetBettingSpecialContestsUseCase {
    override suspend fun invoke() = appResultOf { configuration.homeFeatures().specialContests }
}
