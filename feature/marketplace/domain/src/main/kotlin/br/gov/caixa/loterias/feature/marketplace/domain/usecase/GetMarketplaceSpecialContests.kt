package br.gov.caixa.loterias.feature.marketplace.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests

class GetMarketplaceSpecialContests(private val configuration: AppConfigurationRepository) : GetMarketplaceSpecialContestsUseCase {
    override suspend fun invoke(): AppResult<MarketplaceSpecialContests> {
        val result = appResultOf {
            val configured = configuration.homeFeatures().specialContests
            MarketplaceSpecialContests(configured.megaThirtyYearsContest, configured.lotecaFathersDayContest)
        }
        return when (result) {
            is AppResult.Success -> result
            is AppResult.Failure -> AppResult.Failure(AppError.Unexpected)
        }
    }
}
