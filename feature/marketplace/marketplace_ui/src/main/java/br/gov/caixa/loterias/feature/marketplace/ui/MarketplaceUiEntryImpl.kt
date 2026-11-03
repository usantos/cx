package br.gov.caixa.loterias.feature.marketplace.ui

import androidx.annotation.Keep
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceMarketplaceDetailScreenEntry
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceMarketplaceScreenEntry
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceUiEntry

@Keep
class MarketplaceUiEntryImpl :
    MarketplaceUiEntry,
    MarketplaceMarketplaceDetailScreenEntry by MarketplaceMarketplaceDetailScreenRenderer(),

    MarketplaceMarketplaceScreenEntry by MarketplaceMarketplaceScreenRenderer()
