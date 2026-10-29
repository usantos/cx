package br.gov.caixa.loterias.core.persistence

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.domain.persistence.secureEnvironmentKey
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first

internal class FileSecureStores(
    private val directory: File,
    private val keys: SecureKeyProvider,
    private val coroutineScope: CoroutineScope,
) : SecureStoreFactory {
    override fun open(scope: SecureStoreScope): SecureRecordStore = synchronized(REGISTRY) {
        val identity = "${scope.environmentKey}/${scope.ownerKey}/${scope.record}"
        val filename = secureEnvironmentKey("secure_datastore", identity) + ".bin"
        val file = File(directory, filename).canonicalFile
        REGISTRY[file.path] ?: create(file, scope).also { store ->
            REGISTRY[file.path] = store
            coroutineScope.coroutineContext[Job]?.invokeOnCompletion {
                synchronized(REGISTRY) {
                    if (REGISTRY[file.path] === store) REGISTRY.remove(file.path)
                }
            }
        }
    }

    private fun create(file: File, scope: SecureStoreScope): SecureRecordStore {
        val serializer = EncryptedSnapshotSerializer(scope, keys, file::exists)
        val dataStore = DataStoreFactory.create(serializer = serializer, scope = coroutineScope) {
            file
        }
        return DataStoreRecord(dataStore)
    }

    private companion object {
        val REGISTRY = mutableMapOf<String, SecureRecordStore>()
    }
}

private class DataStoreRecord(private val dataStore: DataStore<SecureSnapshot>) : SecureRecordStore {
    override suspend fun read(): SecureSnapshot = try {
        dataStore.data.first()
    } catch (failure: SecureStorageException) {
        throw failure
    } catch (failure: IOException) {
        throw SecureStorageException.ReadFailure(failure)
    }

    override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot = try {
        dataStore.updateData { previous ->
            if (previous.revision == Long.MAX_VALUE) throw SecureStorageException.InvalidRecord()
            SecureSnapshot(previous.revision + 1, transform(previous.values))
        }
    } catch (failure: SecureStorageException) {
        throw failure
    } catch (failure: IOException) {
        throw SecureStorageException.WriteFailure(failure)
    }
}
