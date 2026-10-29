package br.gov.caixa.loterias.core.persistence

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Reinicia com segurança após uma falha entre a confirmação criptografada, a verificação e a limpeza da origem. */
class SecurePreferenceMigration(
    private val store: SecureRecordStore,
    private val readLegacy: suspend () -> Map<String, String>,
    private val clearLegacy: suspend () -> Unit,
) : SecureRecordStore {
    private val mutex = Mutex()
    private var ready = false

    override suspend fun read(): SecureSnapshot {
        ensureMigrated()
        return store.read()
    }

    override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot {
        ensureMigrated()
        return store.update { values -> transform(values) + (MARKER to "true") }
    }

    private suspend fun ensureMigrated() = mutex.withLock {
        if (ready) return@withLock
        val existing = store.read()
        if (existing.values.containsKey(MARKER) && existing.values[MARKER] != "true") {
            throw SecureStorageException.InvalidRecord()
        }
        if (existing.values[MARKER] != "true") {
            val source = readLegacy().toMap()
            if (source.containsKey(MARKER)) throw SecureStorageException.InvalidRecord()
            val committed = store.update { current ->
                if (current[MARKER] == "true") current else source + current + (MARKER to "true")
            }
            val verified = store.read()
            if (verified.revision < committed.revision || verified.values[MARKER] != "true" ||
                (verified.revision == committed.revision && verified != committed)
            ) {
                throw SecureStorageException.ReadFailure()
            }
        }
        clearLegacy()
        ready = true
    }

    private companion object {
        const val MARKER = "__secure_migration_v1"
    }
}
