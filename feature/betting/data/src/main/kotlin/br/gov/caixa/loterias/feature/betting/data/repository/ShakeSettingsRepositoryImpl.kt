package br.gov.caixa.loterias.feature.betting.data.repository

import android.content.Context
import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.core.persistence.SecurePreferenceMigration
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShakeSettingsRepositoryImpl(
    context: Context,
    private val factory: SecureStoreFactory = AndroidSecureStores(context),
    private val environmentKey: () -> String = { "installation" },
) : ShakeSettingsRepository {
    private val application = context.applicationContext
    private val global = migratedStore(
        SecureStoreScope("installation", "installation", "shake_settings"),
        mapOf(ENABLED to ENABLED, WARNING to WARNING)
    )

    override suspend fun enabled() = read(global, ENABLED)
    override suspend fun setEnabled(enabled: Boolean) {
        global.update { it + (ENABLED to enabled.toString()) }
    }
    override suspend fun warningHidden() = read(global, WARNING)
    override suspend fun setWarningHidden(hidden: Boolean) {
        global.update { it + (WARNING to hidden.toString()) }
    }
    override suspend fun tutorialCompleted(ownerKey: String?) = read(tutorialStore(ownerKey), COMPLETED)
    override suspend fun tooltipPending(ownerKey: String?) = read(tutorialStore(ownerKey), TOOLTIP)
    override suspend fun completeTutorial(ownerKey: String?) {
        tutorialStore(ownerKey).update { it + mapOf(COMPLETED to "true", TOOLTIP to "true") }
    }
    override suspend fun consumeTooltip(ownerKey: String?) {
        tutorialStore(ownerKey).update { it + (TOOLTIP to "false") }
    }

    private fun tutorialStore(ownerKey: String?): SecureRecordStore {
        val scope = SecureStoreScope(environmentKey(), ownerKey ?: "guest", "shake_tutorial")
        val suffix = if (ownerKey == null) "visitante" else "usuario_$ownerKey"
        // Indicadores históricos do titular não possuem atribuição de ambiente; preserve-os quando ela for desconhecida.
        val sources = if (scope.environmentKey == "installation") {
            mapOf(COMPLETED to "aposta_shake_tutorial_v1_concluido_$suffix", TOOLTIP to "aposta_shake_tooltip_pendente_$suffix")
        } else {
            emptyMap()
        }
        return migratedStore(scope, sources)
    }

    private fun migratedStore(scope: SecureStoreScope, sources: Map<String, String>): SecureRecordStore = SecurePreferenceMigration(
        factory.open(scope),
        readLegacy = {
            withContext(Dispatchers.IO) {
                sources.mapNotNull { (target, key) ->
                    val preferences = application.getSharedPreferences(key, Context.MODE_PRIVATE)
                    if (preferences.contains(key)) target to preferences.getBoolean(key, false).toString() else null
                }.toMap()
            }
        },
        clearLegacy = {
            withContext(Dispatchers.IO) {
                sources.values.forEach { key ->
                    val preferences = application.getSharedPreferences(key, Context.MODE_PRIVATE)
                    if (preferences.contains(key) && !preferences.edit().remove(key).commit()) {
                        throw SecureStorageException.WriteFailure()
                    }
                }
            }
        }
    )

    private suspend fun read(store: SecureRecordStore, key: String): Boolean {
        val value = store.read().values[key] ?: return false
        return value.toBooleanStrictOrNull() ?: throw SecureStorageException.InvalidRecord()
    }

    private companion object {
        const val ENABLED = "aposta_shake_ativa"
        const val WARNING = "aposta_shake_ocultar_aviso"
        const val COMPLETED = "completed"
        const val TOOLTIP = "tooltip_pending"
    }
}
