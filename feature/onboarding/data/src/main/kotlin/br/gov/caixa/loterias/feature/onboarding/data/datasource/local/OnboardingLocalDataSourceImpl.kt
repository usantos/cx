package br.gov.caixa.loterias.feature.onboarding.data.datasource.local

import android.content.Context
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.core.persistence.SecurePreferenceMigration
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class OnboardingLocalDataSourceImpl(context: Context, factory: SecureStoreFactory = AndroidSecureStores(context)) :
    OnboardingLocalDataSource {
    private val application = context.applicationContext
    private val keys = listOf("INTRODUCAO_KEY") + BettingTutorial.entries.map { it.legacyPreferenceKey }
    private val store = SecurePreferenceMigration(
        factory.open(SecureStoreScope("installation", "installation", "onboarding")),
        readLegacy = {
            withContext(Dispatchers.IO) {
                keys.mapNotNull { key ->
                    val preferences = application.getSharedPreferences(key, Context.MODE_PRIVATE)
                    if (preferences.contains(key)) key to preferences.getBoolean(key, false).toString() else null
                }.toMap()
            }
        },
        clearLegacy = {
            withContext(Dispatchers.IO) {
                keys.forEach { key ->
                    val preferences = application.getSharedPreferences(key, Context.MODE_PRIVATE)
                    if (preferences.contains(key) && !preferences.edit().remove(key).commit()) {
                        throw SecureStorageException.WriteFailure()
                    }
                }
            }
        }
    )

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val value = store.read().values[key] ?: return defaultValue
        return value.toBooleanStrictOrNull() ?: throw SecureStorageException.InvalidRecord()
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        require(key in keys)
        store.update { it + (key to value.toString()) }
    }
}
