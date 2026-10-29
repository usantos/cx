package br.gov.caixa.loterias.core.persistence

import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SecurePersistenceTest {
    @get:Rule val folder = TemporaryFolder()
    private val scope = SecureStoreScope("installation", "guest", "draft")

    @Test fun encryptedEnvelopeHidesSentinelAndUsesFreshIv() = runTest {
        val serializer = EncryptedSnapshotSerializer(scope, MemoryKeys(), { false })
        val snapshot = SecureSnapshot(1, mapOf("selection" to "SENSITIVE_SENTINEL_123"))
        val first = encode(serializer, snapshot)
        val second = encode(serializer, snapshot)
        assertFalse(first.toString(Charsets.ISO_8859_1).contains("SENSITIVE_SENTINEL_123"))
        assertNotEquals(first.toList(), second.toList())
        assertEquals(snapshot, serializer.readFrom(ByteArrayInputStream(first)))
    }

    @Test fun tamperingAndWrongScopeAreRejectedWithoutChangingCiphertext() = runTest {
        val keys = MemoryKeys()
        val serializer = EncryptedSnapshotSerializer(scope, keys, { false })
        val bytes = encode(serializer, SecureSnapshot(1, mapOf("x" to "y")))
        val damaged = bytes.clone().apply { this[lastIndex] = (last().toInt() xor 1).toByte() }
        failsSecurely { serializer.readFrom(ByteArrayInputStream(damaged)) }
        val wrong = EncryptedSnapshotSerializer(scope.copy(record = "other"), keys, { true })
        failsSecurely { wrong.readFrom(ByteArrayInputStream(bytes)) }
        assertTrue(bytes.contentEquals(encodeAndRestore(serializer, bytes)))
    }

    @Test fun lostKeyAndUnsupportedSchemaDoNotGenerateReplacementKey() = runTest {
        val keys = MemoryKeys()
        val serializer = EncryptedSnapshotSerializer(scope, keys, { false })
        val bytes = encode(serializer, SecureSnapshot(1, mapOf("x" to "y")))
        keys.key = null
        val reader = EncryptedSnapshotSerializer(scope, keys, { true })
        failsSecurely { reader.readFrom(ByteArrayInputStream(bytes)) }
        assertEquals(null, keys.key)
        val schema = bytes.clone().apply { this[SCHEMA_BYTE_INDEX] = UNSUPPORTED_SCHEMA }
        failsSecurely { reader.readFrom(ByteArrayInputStream(schema)) }
        assertEquals(null, keys.key)
    }

    @Test fun wrongEncryptionKeyRejectsAnOtherwiseValidEnvelope() = runTest {
        val keys = MemoryKeys()
        val serializer = EncryptedSnapshotSerializer(scope, keys, { false })
        val bytes = encode(serializer, SecureSnapshot(1, mapOf("x" to "y")))
        keys.key = KeyGenerator.getInstance("AES").apply { init(AES_KEY_BITS) }.generateKey()
        failsSecurely { serializer.readFrom(ByteArrayInputStream(bytes)) }
    }

    @Test fun invalidAndOversizedMapsAreRejected() = runTest {
        val serializer = EncryptedSnapshotSerializer(scope, MemoryKeys(), { false })
        failsSecurely { encode(serializer, SecureSnapshot(-1, emptyMap())) }
        failsSecurely { encode(serializer, SecureSnapshot(1, mapOf("x" to "a".repeat(OVERSIZED_VALUE_BYTES)))) }
        failsSecurely { encode(serializer, SecureSnapshot(1, (0..MAX_ENTRY_COUNT).associate { "key$it" to "value" })) }
        failsSecurely { encode(serializer, SecureSnapshot(1, mapOf("k".repeat(OVERSIZED_KEY_BYTES) to "value"))) }
        failsSecurely { serializer.readFrom(ByteArrayInputStream(ByteArray(OVERSIZED_ENVELOPE_BYTES))) }
        failsSecurely { serializer.readFrom(ByteArrayInputStream(byteArrayOf(1, 2))) }
    }

    @Test fun factoriesShareAtomicUpdatesAndReopenCommittedSnapshot() = runTest {
        val directory = folder.newFolder()
        val keys = MemoryKeys()
        val first = FileSecureStores(directory, keys, backgroundScope).open(scope)
        val second = FileSecureStores(directory, keys, backgroundScope).open(scope)
        (1..CONCURRENT_UPDATES).map {
            async { second.update { mapOf("count" to ((it["count"]?.toInt() ?: 0) + 1).toString()) } }
        }.awaitAll()
        assertEquals(SecureSnapshot(CONCURRENT_UPDATES.toLong(), mapOf("count" to CONCURRENT_UPDATES.toString())), first.read())
        assertEquals(first.read(), FileSecureStores(directory, keys, backgroundScope).open(scope).read())
        assertFalse(directory.walkTopDown().filter(File::isFile).any { it.readText().contains("count") })
    }

    @Test fun failedUpdatePreservesPriorCommitAndCallerMutationCannotChangeIt() = runTest {
        val store = FileSecureStores(folder.newFolder(), MemoryKeys(), backgroundScope).open(scope)
        val candidate = mutableMapOf("x" to "old")
        val confirmed = store.update { candidate }
        candidate["x"] = "changed"
        failsSecurely { store.update { mapOf("x" to "z".repeat(OVERSIZED_VALUE_BYTES)) } }
        assertEquals(SecureSnapshot(1, mapOf("x" to "old")), confirmed)
        assertEquals(confirmed, store.read())
    }

    @Test fun reopenAfterScopeCompletionReadsCommittedDiskRecord() = runTest {
        val directory = folder.newFolder()
        val keys = MemoryKeys()
        val job = SupervisorJob()
        val first = FileSecureStores(directory, keys, CoroutineScope(Dispatchers.IO + job)).open(scope)
        val committed = first.update { mapOf("draft" to "sentinel") }
        job.cancel()
        job.join()
        val reopened = FileSecureStores(directory, keys, backgroundScope).open(scope)
        assertEquals(committed, reopened.read())
    }

    @Test fun diskWriteFailurePreservesRecoverableCiphertext() = runTest {
        val directory = folder.newFolder()
        val store = FileSecureStores(directory, MemoryKeys(), backgroundScope).open(scope)
        store.update { mapOf("draft" to "old") }
        val saved = directory.walkTopDown().single { it.isFile }.readBytes()
        val displaced = File(directory.parentFile, directory.name + "-saved")
        assertTrue(directory.renameTo(displaced))
        assertTrue(directory.createNewFile())
        failsSecurely { store.update { mapOf("draft" to "new") } }
        assertTrue(saved.contentEquals(displaced.walkTopDown().single { it.isFile }.readBytes()))
        assertTrue(directory.delete())
        assertTrue(displaced.renameTo(directory))
        assertEquals("old", store.read().values["draft"])
    }

    private suspend fun encode(serializer: EncryptedSnapshotSerializer, value: SecureSnapshot): ByteArray =
        ByteArrayOutputStream().also { serializer.writeTo(value, it) }.toByteArray()

    private suspend fun encodeAndRestore(serializer: EncryptedSnapshotSerializer, bytes: ByteArray): ByteArray {
        serializer.readFrom(ByteArrayInputStream(bytes))
        return bytes
    }

    private suspend fun failsSecurely(block: suspend () -> Unit) {
        try {
            block()
            throw AssertionError("Expected a typed secure storage failure")
        } catch (_: SecureStorageException) {
            // A falha nunca deve ser convertida em um registro vazio.
        }
    }

    internal class MemoryKeys : SecureKeyProvider {
        var key: SecretKey? = null
        override fun get(alias: String, create: Boolean): SecretKey {
            if (key == null && create) key = KeyGenerator.getInstance("AES").apply { init(AES_KEY_BITS) }.generateKey()
            return key ?: throw SecureStorageException.KeyUnavailable()
        }
    }
    private companion object {
        const val SCHEMA_BYTE_INDEX = 3
        const val UNSUPPORTED_SCHEMA: Byte = 99
        const val AES_KEY_BITS = 256
        const val OVERSIZED_VALUE_BYTES = 300_000
        const val MAX_ENTRY_COUNT = 256
        const val OVERSIZED_KEY_BYTES = 129
        const val OVERSIZED_ENVELOPE_BYTES = 2_000_000
        const val CONCURRENT_UPDATES = 40
    }
}
