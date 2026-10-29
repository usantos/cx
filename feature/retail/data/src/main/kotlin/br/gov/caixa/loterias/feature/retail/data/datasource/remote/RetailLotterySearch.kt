package br.gov.caixa.loterias.feature.retail.data.datasource.remote

import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.retail.data.mapper.retailLong
import br.gov.caixa.loterias.feature.retail.data.mapper.retailObjects
import br.gov.caixa.loterias.feature.retail.data.mapper.toRetailer

internal class RetailLotterySearch(
    private val remote: RetailRemoteDataSource,
    private val favorites: RetailFavorites,
) {
    suspend fun search(query: String): List<Retailer> {
        val favoriteIds = favorites.favoriteIds()
        return remote.search(query).retailObjects().map { it.toRetailer(it.retailLong("id") in favoriteIds) }
    }
}
