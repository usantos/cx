package br.gov.caixa.loterias.core.keymanagement

data class HostPublicKeyPins(
    val hostname: String,
    val pins: List<String>,
)

data class PublicKeySet(
    val version: Long,
    val issuedAtEpochSeconds: Long,
    val expiresAtEpochSeconds: Long,
    val hosts: List<HostPublicKeyPins>,
)

data class KeySetSignature(
    val keyId: String,
    val value: String,
)

/** `payload` and signature values use unpadded Base64 URL encoding. */
data class SignedKeySetEnvelope(
    val payload: String,
    val signatures: List<KeySetSignature>,
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
