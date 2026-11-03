package br.gov.caixa.loterias.feature.marketplace.presentation.mapper

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool

fun MarketplacePool.totalFor(shares: Int) = lastSharePriceWithFeeValue +
    regularSharePriceWithFee.multiply((shares - 1).coerceAtLeast(0).toBigDecimal())

fun MarketplacePool.numberLabel() = if (modality.contains("LOTECA", ignoreCase = true)) "palpites" else "dezenas"
