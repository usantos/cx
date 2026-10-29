package br.gov.caixa.loterias.feature.marketplace.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter

fun interface SearchMarketplaceUseCase {
    suspend operator fun invoke(filter: MarketplaceFilter, page: Int): AppResult<MarketplacePage>
}
