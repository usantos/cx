package br.gov.caixa.loterias.core.keymanagement.model

import com.google.gson.annotations.SerializedName

data class HostPublicKeyPins(
    @field:SerializedName("hostname") val hostname: String,
    @field:SerializedName("pins") val pins: List<String>,
)

data class PublicKeySet(
    @field:SerializedName("version") val version: Long,
    @field:SerializedName("issuedAtEpochSeconds") val issuedAtEpochSeconds: Long,
    @field:SerializedName("expiresAtEpochSeconds") val expiresAtEpochSeconds: Long,
    @field:SerializedName("hosts") val hosts: List<HostPublicKeyPins>,
)

data class KeySetSignature(
    @field:SerializedName("keyId") val keyId: String,
    @field:SerializedName("value") val value: String,
)

/** Os valores de `payload` e assinatura usam codificação Base64 URL sem preenchimento. */
data class SignedKeySetEnvelope(
    @field:SerializedName("payload") val payload: String,
    @field:SerializedName("signatures") val signatures: List<KeySetSignature>,
)

data class TrustedConfigurationSigners(
    val subjectPublicKeyInfoById: Map<String, String>,
    val threshold: Int,
) {
    init {
        require(threshold > 0) { "O limiar de assinaturas deve ser positivo" }
        require(threshold <= subjectPublicKeyInfoById.size) { "O limiar excede a quantidade de signatários" }
        require(subjectPublicKeyInfoById.all { (id, key) -> id.isNotBlank() && key.isNotBlank() }) {
            "Identificador e chave pública dos signatários são obrigatórios"
        }
    }
}

enum class KeySetRejectionReason {
    MALFORMED_ENVELOPE,
    INVALID_SIGNATURE,
    INVALID_VERSION,
    INVALID_TIME_WINDOW,
    INVALID_HOST,
    INVALID_PIN,
    EMPTY_KEY_SET,
}

sealed interface KeySetVerificationResult {
    data class Accepted(val keySet: PublicKeySet) : KeySetVerificationResult
    data class Rejected(val reason: KeySetRejectionReason) : KeySetVerificationResult
}

enum class KeySetSource { REMOTE, CACHE, LOCAL_FALLBACK }

data class ResolvedPublicKeySet(
    val keySet: PublicKeySet,
    val source: KeySetSource,
)

fun interface RemotePublicKeySetSource {
    suspend fun fetch(): SignedKeySetEnvelope?
}

interface SignedKeySetStore {
    fun read(): SignedKeySetEnvelope?
    fun write(envelope: SignedKeySetEnvelope)
}
