package br.gov.caixa.loterias.core.keymanagement.verification

import com.google.gson.Gson
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import br.gov.caixa.loterias.core.keymanagement.model.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.keymanagement.model.KeySetSignature
import br.gov.caixa.loterias.core.keymanagement.model.SignedKeySetEnvelope
import br.gov.caixa.loterias.core.keymanagement.model.TrustedConfigurationSigners
import br.gov.caixa.loterias.core.keymanagement.model.KeySetRejectionReason
import br.gov.caixa.loterias.core.keymanagement.model.KeySetVerificationResult

class SignedKeySetVerifier(
    private val trustedSigners: TrustedConfigurationSigners,
    private val allowedHosts: Set<String>,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1_000 },
    private val maxClockSkewSeconds: Long = 300,
    private val maxValiditySeconds: Long = 180L * 24 * 60 * 60,
    private val gson: Gson = Gson(),
) {
    init {
        require(allowedHosts.isNotEmpty()) { "A lista de hosts permitidos não pode ser vazia" }
    }

    fun verify(envelope: SignedKeySetEnvelope, minimumVersion: Long = 0): KeySetVerificationResult =
        runCatching { verifySafely(envelope, minimumVersion) }
            .getOrElse { rejected(KeySetRejectionReason.MALFORMED_ENVELOPE) }

    /** Lê apenas a versão assinada para proteger contra reversão; nunca autoriza pins expirados. */
    internal fun authenticatedVersion(envelope: SignedKeySetEnvelope): Long? = runCatching {
        (verifySafely(envelope, 0, enforceTimeWindow = false) as? KeySetVerificationResult.Accepted)?.keySet?.version
    }.getOrNull()

    internal fun isTimeWindowValid(keySet: PublicKeySet): Boolean {
        val now = nowEpochSeconds()
        val validity = keySet.expiresAtEpochSeconds - keySet.issuedAtEpochSeconds
        return keySet.issuedAtEpochSeconds <= now + maxClockSkewSeconds &&
            keySet.expiresAtEpochSeconds > now && validity > 0 && validity <= maxValiditySeconds
    }

    private fun verifySafely(envelope: SignedKeySetEnvelope, minimumVersion: Long, enforceTimeWindow: Boolean = true): KeySetVerificationResult {
        val payload = decode(envelope.payload)
            ?.takeIf { it.isNotEmpty() && it.size <= MAX_PAYLOAD_BYTES }
            ?: return rejected(KeySetRejectionReason.MALFORMED_ENVELOPE)

        val validSignatures = envelope.signatures
            .distinctBy(KeySetSignature::keyId)
            .count { signature -> verifySignature(signature, payload) }
        if (validSignatures < trustedSigners.threshold) {
            return rejected(KeySetRejectionReason.INVALID_SIGNATURE)
        }

        val keySet = runCatching { gson.fromJson(payload.toString(Charsets.UTF_8), PublicKeySet::class.java) }
            .getOrNull() ?: return rejected(KeySetRejectionReason.MALFORMED_ENVELOPE)
        if (keySet.version <= 0 || keySet.version < minimumVersion) {
            return rejected(KeySetRejectionReason.INVALID_VERSION)
        }

        if (enforceTimeWindow && !isTimeWindowValid(keySet)) {
            return rejected(KeySetRejectionReason.INVALID_TIME_WINDOW)
        }
        if (keySet.hosts.isEmpty() || keySet.hosts.size > MAX_HOSTS) {
            return rejected(KeySetRejectionReason.EMPTY_KEY_SET)
        }
        if (keySet.hosts.map(HostPublicKeyPins::hostname).distinct().size != keySet.hosts.size ||
            keySet.hosts.any { !isAllowedHost(it.hostname) || !HOSTNAME.matches(it.hostname) }
        ) {
            return rejected(KeySetRejectionReason.INVALID_HOST)
        }
        if (keySet.hosts.any { host ->
                host.pins.isEmpty() ||
                    host.pins.size > MAX_PINS_PER_HOST ||
                    host.pins.distinct().size != host.pins.size ||
                    host.pins.any { !isValidPin(it) }
            }
        ) {
            return rejected(KeySetRejectionReason.INVALID_PIN)
        }
        return KeySetVerificationResult.Accepted(keySet)
    }

    private fun verifySignature(signature: KeySetSignature, payload: ByteArray): Boolean = runCatching {
        val encodedSigner = trustedSigners.subjectPublicKeyInfoById[signature.keyId] ?: return false
        val publicKey = KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(decode(encodedSigner) ?: return false))
        Signature.getInstance(SIGNATURE_ALGORITHM).run {
            initVerify(publicKey)
            update(payload)
            verify(decode(signature.value) ?: return false)
        }
    }.getOrDefault(false)

    private fun isValidPin(pin: String): Boolean {
        if (!pin.startsWith(PIN_PREFIX)) return false
        val digest = runCatching { Base64.getDecoder().decode(pin.removePrefix(PIN_PREFIX)) }.getOrNull()
        return digest?.size == SHA_256_BYTES
    }

    private fun isAllowedHost(hostname: String): Boolean = allowedHosts.any { allowed ->
        when {
            allowed.startsWith("*.") -> hostname.endsWith(allowed.removePrefix("*")) &&
                hostname.length > allowed.length - 1
            else -> hostname == allowed
        }
    }

    private fun decode(value: String): ByteArray? = runCatching {
        Base64.getUrlDecoder().decode(value)
    }.recoverCatching {
        Base64.getDecoder().decode(value)
    }.getOrNull()

    private fun rejected(reason: KeySetRejectionReason) = KeySetVerificationResult.Rejected(reason)

    private companion object {
        const val SIGNATURE_ALGORITHM = "SHA256withECDSA"
        const val PIN_PREFIX = "sha256/"
        const val SHA_256_BYTES = 32
        const val MAX_PAYLOAD_BYTES = 64 * 1024
        const val MAX_HOSTS = 10
        const val MAX_PINS_PER_HOST = 5
        val HOSTNAME = Regex("^[a-z0-9](?:[a-z0-9.-]{0,251}[a-z0-9])?$")
    }
}
