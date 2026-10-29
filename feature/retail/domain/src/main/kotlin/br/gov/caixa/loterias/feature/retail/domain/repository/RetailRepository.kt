package br.gov.caixa.loterias.feature.retail.domain.repository

import br.gov.caixa.loterias.feature.retail.domain.model.Retailer

interface RetailRepository {
    suspend fun search(query: String): List<Retailer>
    suspend fun favorites(): List<Retailer>
    suspend fun setFavorite(retailerId: Long, favorite: Boolean)
}
