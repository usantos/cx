package br.gov.caixa.loterias.feature.marketplacefilter.ui

import androidx.annotation.Keep
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.delivery.MarketplaceFilterMarketplaceFilterScreenEntry
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.delivery.MarketplaceFilterUiEntry

@Keep
class MarketplaceFilterUiEntryImpl :
    MarketplaceFilterUiEntry,
    MarketplaceFilterMarketplaceFilterScreenEntry by MarketplaceFilterMarketplaceFilterScreenRenderer()
