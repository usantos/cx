package br.gov.caixa.loterias.feature.marketplace.navigation

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer

data class MarketplaceFilterEntry(
    val initialFilter: MarketplaceFilter,
    val clearFilter: MarketplaceFilter,
    val initialRetailer: Retailer?,
    val retailer: Retailer?,
    val resultDestination: String,
)

fun marketplaceFilterEntry(filter: MarketplaceFilter, baseline: MarketplaceFilter,
    appliedRetailer: Retailer?, scopedRetailer: Retailer?,
): MarketplaceFilterEntry = MarketplaceFilterEntry(
    initialFilter = scopedRetailer?.let { filter.withLottery(it.id.toString()) } ?: filter,
    clearFilter = scopedRetailer?.let { baseline.withLottery(it.id.toString()) } ?: baseline,
    initialRetailer = scopedRetailer ?: appliedRetailer,
    retailer = scopedRetailer,
    resultDestination = if (scopedRetailer != null) "betting/pool-view" else "betting/pools"
)
