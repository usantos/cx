package br.gov.caixa.loterias.feature.marketplacefilter

import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilterParameters

fun initialMarketplaceFilterState(parameters: MarketplaceFilterParameters) = MarketplaceFilterUiState(
    betOptions = parameters.betOptions,
    numberOptions = allNumberQuantityOptions,
    configurationError = if (parameters.isValid) null else "Configuração do filtro indisponível",
)
