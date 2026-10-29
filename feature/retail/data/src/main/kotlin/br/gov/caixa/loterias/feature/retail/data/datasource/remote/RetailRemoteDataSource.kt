package br.gov.caixa.loterias.feature.retail.data.datasource.remote

import com.google.gson.JsonElement

interface RetailRemoteDataSource {
    suspend fun search(query: String): JsonElement?
    suspend fun favorites(): JsonElement?
    suspend fun addFavorite(retailerId: Long)
    suspend fun deleteFavorite(retailerId: Long)
}
