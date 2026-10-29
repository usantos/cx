package br.gov.caixa.loterias.feature.cart.data.repository

import android.content.Context
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartUsageRepository
import br.gov.caixa.loterias.feature.cart.data.datasource.local.FavoriteCartUsageLocalDataSource
import br.gov.caixa.loterias.feature.cart.data.datasource.local.FavoriteCartUsageLocalDataSourceImpl

class FavoriteCartUsageRepositoryImpl internal constructor(
    private val store: FavoriteCartUsageLocalDataSource,
) : FavoriteCartUsageRepository {
    override suspend fun wasAddedToCurrentCart(cartId: Long): Boolean = cartId in store.read(ADDED_CART_IDS)

    override suspend fun markAsAddedToCurrentCart(cartId: Long) {
        require(cartId > 0)
        store.update(ADDED_CART_IDS) { it + cartId }
    }

    override suspend fun clear() {
        store.update(ADDED_CART_IDS) { emptySet() }
    }

    companion object {
        private const val ADDED_CART_IDS = "added_cart_ids"

        fun fromContext(
            context: Context,
            captureLease: () -> (() -> Boolean) = {
                { true }
            },
            scope: () -> br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope,
        ): FavoriteCartUsageRepositoryImpl {
            val legacy = br.gov.caixa.loterias.feature.cart.data.datasource.local.AndroidCartPersistence(
                context,
                br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope("installation", "installation", "cart-legacy-v1")
            )
            return FavoriteCartUsageRepositoryImpl(
                FavoriteCartUsageLocalDataSourceImpl(
                    br.gov.caixa.loterias.feature.cart.data.datasource.local.SecureCartAccess(
                        br.gov.caixa.loterias.core.persistence.AndroidSecureStores(context.applicationContext),
                        scope,
                        legacy::migrateLegacy,
                        captureLease
                    )
                )
            )
        }
    }
}
