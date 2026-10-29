package br.gov.caixa.loterias.feature.marketplace.presentation.mapper

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool

internal fun MarketplacePool.totalFor(shares: Int) = lastSharePriceWithFeeValue +
    regularSharePriceWithFee.multiply((shares - 1).coerceAtLeast(0).toBigDecimal())

internal fun MarketplacePool.numberLabel() = if (modality.contains("LOTECA", ignoreCase = true)) "palpites" else "dezenas"
