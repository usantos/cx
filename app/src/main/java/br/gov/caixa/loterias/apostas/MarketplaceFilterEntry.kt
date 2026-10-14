package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.retail.Retailer

internal data class MarketplaceFilterEntry(
    val initialFilter: MarketplaceFilter,
    val clearFilter: MarketplaceFilter,
    val initialRetailer: Retailer?,
    val retailer: Retailer?,
    val resultDestination: String,
)

internal fun marketplaceFilterEntry(filter: MarketplaceFilter, baseline: MarketplaceFilter,
    appliedRetailer: Retailer?, scopedRetailer: Retailer?): MarketplaceFilterEntry =
    MarketplaceFilterEntry(
        initialFilter = scopedRetailer?.let { filter.withLottery(it.id.toString()) } ?: filter,
        clearFilter = scopedRetailer?.let { baseline.withLottery(it.id.toString()) } ?: baseline,
        initialRetailer = scopedRetailer ?: appliedRetailer,
        retailer = scopedRetailer,
        resultDestination = if (scopedRetailer != null) "betting/pool-view" else "betting/pools",
    )
