package br.gov.caixa.loterias.feature.marketplace.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests

fun interface GetMarketplaceSpecialContestsUseCase {
    suspend operator fun invoke(): AppResult<MarketplaceSpecialContests>
}
