package br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState

fun initialMarketplaceFilterState(parameters: MarketplaceFilterParameters) = MarketplaceFilterUiState(
    betOptions = parameters.betOptions,
    numberOptions = allNumberQuantityOptions,
    configurationError = if (parameters.isValid) null else messageText("configuracao_do_filtro_indisponi_ecb11f")
)
