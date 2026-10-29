package br.gov.caixa.loterias.feature.cart.data.datasource.local

internal interface FavoriteCartUsageLocalDataSource {
    suspend fun read(key: String): Set<Long>
    suspend fun update(key: String, transform: (Set<Long>) -> Set<Long>)
}
