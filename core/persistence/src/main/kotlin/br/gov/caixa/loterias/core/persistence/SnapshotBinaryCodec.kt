package br.gov.caixa.loterias.core.persistence

import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction

internal object SnapshotBinaryCodec {
    const val MAX_BYTES = 1_048_576
    private const val MAX_ENTRIES = 256
    private const val MAX_KEY_BYTES = 128
    private const val MAX_VALUE_BYTES = 262_144

    fun encode(snapshot: SecureSnapshot): ByteArray {
        if (snapshot.revision < 0 || snapshot.values.size > MAX_ENTRIES) invalid()
        val output = ByteArrayOutputStream()
        DataOutputStream(output).use { stream ->
            stream.writeLong(snapshot.revision)
            stream.writeInt(snapshot.values.size)
            snapshot.values.toSortedMap().forEach { (key, value) ->
                writeString(stream, key, MAX_KEY_BYTES)
                writeString(stream, value, MAX_VALUE_BYTES)
                if (output.size() > MAX_BYTES) invalid()
            }
        }
        return output.toByteArray()
    }

    fun decode(bytes: ByteArray): SecureSnapshot {
        if (bytes.size > MAX_BYTES) invalid()
        return DataInputStream(ByteArrayInputStream(bytes)).use { stream ->
            val revision = stream.readLong()
            val count = stream.readInt()
            if (revision < 0 || count !in 0..MAX_ENTRIES) invalid()
            val values = linkedMapOf<String, String>()
            repeat(count) {
                val key = readString(stream, MAX_KEY_BYTES)
                if (values.containsKey(key)) invalid()
                values[key] = readString(stream, MAX_VALUE_BYTES)
            }
            if (stream.available() != 0) invalid()
            SecureSnapshot(revision, values)
        }
    }

    private fun writeString(stream: DataOutputStream, value: String, maximum: Int) {
        val bytes = value.toByteArray(Charsets.UTF_8)
        if (bytes.size > maximum) invalid()
        stream.writeInt(bytes.size)
        stream.write(bytes)
    }

    private fun readString(stream: DataInputStream, maximum: Int): String {
        val size = stream.readInt()
        if (size !in 0..maximum || size > stream.available()) invalid()
        val bytes = ByteArray(size).also(stream::readFully)
        return Charsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes)).toString()
    }

    private fun invalid(): Nothing = throw SecureStorageException.InvalidRecord()
}
