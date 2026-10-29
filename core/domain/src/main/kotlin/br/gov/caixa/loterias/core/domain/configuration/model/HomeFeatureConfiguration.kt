package br.gov.caixa.loterias.core.domain.configuration.model

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters

data class HomeFeatureConfiguration(
    val poolSalesEnabled: Boolean = false,
    val comboBettingEnabled: Boolean = false,
    val rapidBettingEnabled: Boolean = false,
    val myBetsVisible: Boolean = true,
    val campaigns: CommemorativeCampaignSettings = CommemorativeCampaignSettings(),
    val specialContests: HomeSpecialContests = HomeSpecialContests(),
)
