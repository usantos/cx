package br.gov.caixa.loterias.feature.cart.data.datasource.local

import android.content.Context
import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal interface FavoriteUsageMigrationSource {
    fun read(): Set<String>?
    fun cleanup(expected: Set<String>)
}

internal class UnassignedFavoriteUsageSource(context: Context) : FavoriteUsageMigrationSource {
    private val preferences = context.applicationContext.getSharedPreferences("favorite_cart_usage", Context.MODE_PRIVATE)
    override fun read(): Set<String>? = if (preferences.contains(KEY)) {
        preferences.getStringSet(KEY, null)?.toSet()
            ?: throw SecureStorageException.InvalidRecord()
    } else {
        null
    }
    override fun cleanup(expected: Set<String>) {
        if (!preferences.contains(KEY)) return
        check(read() == expected) { "Legacy favorite usage changed after migration" }
        val editor = preferences.edit()
        editor.remove(KEY)
        if (!editor.commit()) error("Legacy favorite cleanup failed")
    }
    private companion object {
        const val KEY = "added_cart_ids"
    }
}

/** Marcadores legados globais não possuem titular nem ambiente e nunca devem se tornar marcadores de conta. */
internal class SecureFavoriteUsageMigration(private val store: SecureRecordStore, private val source: FavoriteUsageMigrationSource) {
    suspend fun migrate() = withContext(Dispatchers.IO) {
        val original = source.read() ?: return@withContext
        val ids = original.map { value -> value.toLongOrNull()?.takeIf { it > 0 } ?: throw SecureStorageException.InvalidRecord() }.toSet()
        val digest = MessageDigest.getInstance("SHA-256").digest(original.sorted().joinToString("\n").toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        store.update { values ->
            if (values[MARKER] != null) {
                check(values[MARKER] == digest) { "Legacy favorite usage requires review" }
                values
            } else {
                val document = decodeCartDocument(values)
                document.copy(favorites = ids, legacyReview = document.legacyReview || ids.isNotEmpty()).also { it.validate() }.let {
                    values + (CART_DOCUMENT_KEY to encodeCartDocument(it)) + (MARKER to digest)
                }
            }
        }
        val verified = store.read().values
        check(verified[MARKER] == digest && decodeCartDocument(verified).favorites == ids)
        source.cleanup(original)
    }
    private companion object {
        const val MARKER = "legacy-favorite-digest-v1"
    }
}
