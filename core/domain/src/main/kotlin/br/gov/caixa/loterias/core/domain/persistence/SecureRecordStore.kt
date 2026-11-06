package br.gov.caixa.loterias.core.domain.persistence

import java.io.IOException
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.Collections

data class SecureStoreScope(val environmentKey: String, val ownerKey: String, val record: String) {
    override fun toString(): String = "SecureStoreScope(redacted)"

    init {
        require(environmentKey == "installation" || environmentKey.matches(OPAQUE))
        require(ownerKey in setOf("guest", "installation") || ownerKey.matches(OPAQUE))
        require(record.matches(Regex("[a-z][a-z0-9_-]{0,63}")))
    }

    private companion object {
        val OPAQUE = Regex("[a-f0-9]{64}")
    }
}

class SecureSnapshot(val revision: Long, values: Map<String, String>) {
    val values: Map<String, String> = Collections.unmodifiableMap(LinkedHashMap(values))

    override fun equals(other: Any?): Boolean = other is SecureSnapshot && revision == other.revision && values == other.values
    override fun hashCode(): Int = 31 * revision.hashCode() + values.hashCode()
    override fun toString(): String = "SecureSnapshot(revision=$revision, entries=${values.size})"
}

interface SecureRecordStore {
    suspend fun read(): SecureSnapshot
    suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot
}

fun interface SecureStoreFactory {
    fun open(scope: SecureStoreScope): SecureRecordStore
}

sealed class SecureStorageException(message: String, cause: Throwable? = null) : IOException(message, cause) {
    class KeyUnavailable(cause: Throwable? = null) : SecureStorageException("Secure storage key unavailable", cause)
    class CorruptRecord(cause: Throwable? = null) : SecureStorageException("Secure storage authentication or format failure", cause)
    class InvalidRecord(cause: Throwable? = null) : SecureStorageException("Secure storage bounds exceeded", cause)
    class ReadFailure(cause: Throwable? = null) : SecureStorageException("Secure storage read failed", cause)
    class WriteFailure(cause: Throwable? = null) : SecureStorageException("Secure storage write failed", cause)
}

fun secureEnvironmentKey(namespace: String, backendUrl: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    listOf(namespace, backendUrl).forEach { value ->
        val bytes = value.toByteArray(Charsets.UTF_8)
        digest.update(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(bytes.size).array())
        digest.update(bytes)
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}
