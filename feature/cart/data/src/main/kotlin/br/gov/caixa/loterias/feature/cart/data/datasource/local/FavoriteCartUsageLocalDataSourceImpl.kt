package br.gov.caixa.loterias.feature.cart.data.datasource.local

internal class FavoriteCartUsageLocalDataSourceImpl(private val access: SecureCartAccess) : FavoriteCartUsageLocalDataSource {
    override suspend fun read(key: String): Set<Long> = access.use { it.read().favorites }
    override suspend fun update(key: String, transform: (Set<Long>) -> Set<Long>) = access.use { document ->
        document.transaction { updateFavorites(transform) }
    }
}
