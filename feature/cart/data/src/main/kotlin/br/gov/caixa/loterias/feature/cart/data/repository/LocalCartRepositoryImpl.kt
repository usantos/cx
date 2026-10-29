package br.gov.caixa.loterias.feature.cart.data.repository

import android.content.Context
import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartBet
import br.gov.caixa.loterias.feature.cart.domain.repository.LocalCartRepository
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartSnapshotLocalDataSource
import br.gov.caixa.loterias.feature.cart.data.mapper.decodeLegacyCart
import br.gov.caixa.loterias.feature.cart.data.datasource.local.CartSnapshotLocalDataSourceImpl

/** Preserva o grafo do banco legado sem abri-lo para escrita nem invocar seu auxiliar destrutivo. */
class LocalCartRepositoryImpl internal constructor(private val store: CartSnapshotLocalDataSource) : LocalCartRepository {
    override suspend fun snapshot(): List<LocalCartBet> = decodeLegacyCart(store.read())

    companion object {
        fun fromContext(context: Context, scope: br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope): LocalCartRepositoryImpl =
            br.gov.caixa.loterias.feature.cart.data.datasource.local.AndroidCartPersistence(context, scope).let { persistence ->
                LocalCartRepositoryImpl {
                    persistence.requireReady()
                    persistence.document.read().tables
                }
            }
    }
}
