package br.gov.caixa.loterias.core.persistence

import androidx.datastore.core.Serializer
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.domain.persistence.secureEnvironmentKey
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

internal class EncryptedSnapshotSerializer(
    scope: SecureStoreScope,
    private val keys: SecureKeyProvider,
    private val recordExists: () -> Boolean,
) : Serializer<SecureSnapshot> {
    private val binding = "${scope.environmentKey}/${scope.ownerKey}/${scope.record}/$SCHEMA".toByteArray(Charsets.UTF_8)
    private val alias = "secure_record_${secureEnvironmentKey("secure_datastore", binding.toString(Charsets.UTF_8))}"
    override val defaultValue = SecureSnapshot(0, emptyMap())

    override suspend fun readFrom(input: InputStream): SecureSnapshot {
        try {
            val envelope = input.readBytesBounded()
            if (envelope.size < HEADER.size + IV_BYTES + TAG_BYTES || !envelope.take(HEADER.size).toByteArray().contentEquals(HEADER)) {
                throw SecureStorageException.CorruptRecord()
            }
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val iv = envelope.copyOfRange(HEADER.size, HEADER.size + IV_BYTES)
            cipher.init(Cipher.DECRYPT_MODE, keys.get(alias, false), GCMParameterSpec(TAG_BITS, iv))
            cipher.updateAAD(binding)
            val plain = cipher.doFinal(envelope, HEADER.size + IV_BYTES, envelope.size - HEADER.size - IV_BYTES)
            return SnapshotBinaryCodec.decode(plain)
        } catch (failure: SecureStorageException) {
            throw failure
        } catch (failure: GeneralSecurityException) {
            throw SecureStorageException.CorruptRecord(failure)
        } catch (failure: IOException) {
            throw SecureStorageException.CorruptRecord(failure)
        }
    }

    override suspend fun writeTo(t: SecureSnapshot, output: OutputStream) {
        val plain = SnapshotBinaryCodec.encode(t)
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, keys.get(alias, !recordExists()))
            if (cipher.iv.size != IV_BYTES) throw SecureStorageException.WriteFailure()
            cipher.updateAAD(binding)
            val ciphertext = cipher.doFinal(plain)
            output.write(HEADER)
            output.write(cipher.iv)
            output.write(ciphertext)
        } catch (failure: GeneralSecurityException) {
            throw SecureStorageException.KeyUnavailable(failure)
        }
    }

    private fun InputStream.readBytesBounded(): ByteArray {
        val bytes = java.io.ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (true) {
            val count = read(buffer)
            if (count < 0) return bytes.toByteArray()
            if (bytes.size() + count > MAX_ENVELOPE_BYTES) throw SecureStorageException.InvalidRecord()
            bytes.write(buffer, 0, count)
        }
    }

    private companion object {
        const val SCHEMA = 1
        const val IV_BYTES = 12
        const val TAG_BYTES = 16
        const val TAG_BITS = 128
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val MAX_ENVELOPE_BYTES = SnapshotBinaryCodec.MAX_BYTES + 32
        val HEADER = byteArrayOf(0x53, 0x44, 0x53, SCHEMA.toByte())
    }
}
