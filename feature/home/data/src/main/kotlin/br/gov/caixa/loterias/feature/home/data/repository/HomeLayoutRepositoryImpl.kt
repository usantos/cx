package br.gov.caixa.loterias.feature.home.data.repository

import android.content.Context
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.core.persistence.SecurePreferenceMigration
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeLayoutRepositoryImpl(context: Context, factory: SecureStoreFactory = AndroidSecureStores(context)) : HomeLayoutRepository {
    private val preferences = context.applicationContext.getSharedPreferences("home_v2_layout", Context.MODE_PRIVATE)
    private val store = SecurePreferenceMigration(
        factory.open(SecureStoreScope("installation", "installation", "home_layout")),
        readLegacy = {
            withContext(Dispatchers.IO) {
                preferences.getString(KEY, null)?.let { mapOf(KEY to it) } ?: emptyMap()
            }
        },
        clearLegacy = {
            withContext(Dispatchers.IO) {
                if (preferences.contains(KEY) && !preferences.edit().remove(KEY).commit()) {
                    throw SecureStorageException.WriteFailure()
                }
            }
        }
    )

    override suspend fun loadOrder(): String? = store.read().values[KEY]
    override suspend fun saveOrder(order: String) {
        store.update { it + (KEY to order) }
    }

    private companion object {
        const val KEY = "modality_order"
    }
}
