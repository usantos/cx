package br.gov.caixa.loterias.feature.retail.data.datasource.remote

import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.retail.data.mapper.retailLong
import br.gov.caixa.loterias.feature.retail.data.mapper.retailObjects
import br.gov.caixa.loterias.feature.retail.data.mapper.toRetailer

internal class RetailFavorites(private val remote: RetailRemoteDataSource) {
    suspend fun favoriteIds(): Set<Long> =
        remote.favorites().retailObjects().mapNotNull { it.retailLong("codigo") ?: it.retailLong("id") }.toSet()

    suspend fun favorites(): List<Retailer> = remote.favorites().retailObjects().map { it.toRetailer(true) }

    suspend fun setFavorite(retailerId: Long, favorite: Boolean) {
        if (favorite) remote.addFavorite(retailerId) else remote.deleteFavorite(retailerId)
    }
}
