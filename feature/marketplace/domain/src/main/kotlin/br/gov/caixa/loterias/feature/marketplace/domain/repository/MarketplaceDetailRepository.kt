package br.gov.caixa.loterias.feature.marketplace.domain.repository

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail

fun interface MarketplaceDetailRepository {
    suspend fun detail(poolCode: String): MarketplacePoolDetail
}
