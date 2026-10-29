package br.gov.caixa.loterias.feature.cart.data.datasource.local

import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import java.security.MessageDigest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal data class CartMigrationSnapshot(
    val tables: Map<String, List<CartSnapshotRow>>,
    val receipt: CartReceipt?,
    val lastId: Long,
    val purchases: Map<String, List<String>>,
) {
    fun document() = CartDocumentState(tables, receipt, lastId, purchases)
    override fun toString() = "CartMigrationSnapshot(redacted)"
}

/** Lê somente tabelas conhecidas pertencentes ao aplicativo. A limpeza deve comparar o original antes de excluir qualquer linha. */
internal interface CartMigrationSource {
    fun read(): CartMigrationSnapshot?
    fun cleanup(expected: CartMigrationSnapshot)
}

/** Registros antigos não possuem vínculo confiável de ambiente e são colocados em quarentena em vez de adotados. */
internal class CartDocumentMigration(private val quarantine: SecureCartDocument, private val source: CartMigrationSource) {
    suspend fun migrate() = withContext(Dispatchers.IO) {
        val original = source.read() ?: return@withContext
        original.document().validate()
        val encoded = encodeCartDocument(original.document())
        val digest = MessageDigest.getInstance("SHA-256").digest(encoded.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        var imported: CartDocumentState? = null
        quarantine.transaction {
            if (state.legacyDigest == null) {
                check(state.tables.isEmpty() && state.receipt == null && state.lastId == 0L)
                state = original.document().copy(favorites = state.favorites, legacyReview = true, legacyDigest = digest)
                imported = state
            } else if (state.legacyDigest != digest) {
                throw SecureStorageException.InvalidRecord()
            }
        }
        // Relê o texto criptografado confirmado antes da limpeza; uma falha de leitura mantém toda a origem intacta.
        val verified = quarantine.read()
        check(verified.legacyDigest == digest && verified.legacyReview)
        imported?.let { check(verified == it) }
        source.cleanup(original)
    }
}
