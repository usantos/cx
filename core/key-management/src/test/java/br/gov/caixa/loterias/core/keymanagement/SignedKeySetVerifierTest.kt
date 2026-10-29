package br.gov.caixa.loterias.core.keymanagement

import com.google.gson.Gson
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import java.util.Base64
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.core.keymanagement.model.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.keymanagement.model.KeySetSignature
import br.gov.caixa.loterias.core.keymanagement.model.SignedKeySetEnvelope
import br.gov.caixa.loterias.core.keymanagement.model.TrustedConfigurationSigners
import br.gov.caixa.loterias.core.keymanagement.model.KeySetRejectionReason
import br.gov.caixa.loterias.core.keymanagement.model.KeySetVerificationResult
import br.gov.caixa.loterias.core.keymanagement.model.KeySetSource
import br.gov.caixa.loterias.core.keymanagement.model.SignedKeySetStore
import br.gov.caixa.loterias.core.keymanagement.repository.PublicKeySetRepository
import br.gov.caixa.loterias.core.keymanagement.verification.SignedKeySetVerifier

class SignedKeySetVerifierTest {
    private val now = 2_000_000_000L
    private val signer = ecKeyPair()
    private val secondSigner = ecKeyPair()
    private val validPin = "sha256/${Base64.getEncoder().encodeToString(ByteArray(32) { 7 })}"

    @Test
    fun `accepts valid threshold-signed key set`() {
        val verifier = verifier(mapOf("one" to signer, "two" to secondSigner), threshold = 2)
        val envelope = envelope(validSet(), listOf("one" to signer, "two" to secondSigner))

        val result = verifier.verify(envelope)

        assertTrue(result is KeySetVerificationResult.Accepted)
        assertEquals(4L, (result as KeySetVerificationResult.Accepted).keySet.version)
    }

    @Test
    fun `rejects malformed envelope and json`() {
        val verifier = verifier()
        assertRejected(verifier.verify(SignedKeySetEnvelope("%%%", emptyList())), KeySetRejectionReason.MALFORMED_ENVELOPE)
        assertRejected(verifier.verify(SignedKeySetEnvelope("", emptyList())), KeySetRejectionReason.MALFORMED_ENVELOPE)
        val oversized = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(65 * 1024))
        assertRejected(verifier.verify(SignedKeySetEnvelope(oversized, emptyList())), KeySetRejectionReason.MALFORMED_ENVELOPE)
        assertRejected(verifier.verify(signedPayload("not-json")), KeySetRejectionReason.MALFORMED_ENVELOPE)
        val missingHosts = """{"version":4,"issuedAtEpochSeconds":${now - 1},"expiresAtEpochSeconds":${now + 1}}"""
        assertRejected(verifier.verify(signedPayload(missingHosts)), KeySetRejectionReason.MALFORMED_ENVELOPE)
    }

    @Test
    fun `rejects missing unknown duplicated or corrupt signatures`() {
        val verifier = verifier()
        val payload = payload(validSet())
        assertRejected(verifier.verify(SignedKeySetEnvelope(payload, emptyList())), KeySetRejectionReason.INVALID_SIGNATURE)
        assertRejected(
            verifier.verify(SignedKeySetEnvelope(payload, listOf(KeySetSignature("unknown", "AA")))),
            KeySetRejectionReason.INVALID_SIGNATURE,
        )
        val valid = sign("one", signer, payload)
        assertRejected(
            verifier(mapOf("one" to signer, "two" to secondSigner), 2).verify(SignedKeySetEnvelope(payload, listOf(valid, valid))),
            KeySetRejectionReason.INVALID_SIGNATURE,
        )
        assertRejected(
            verifier().verify(SignedKeySetEnvelope(payload, listOf(KeySetSignature("one", "invalid")))),
            KeySetRejectionReason.INVALID_SIGNATURE,
        )
    }

    @Test
    fun `rejects rollback and invalid version`() {
        val verifier = verifier()
        assertRejected(verifier.verify(envelope(validSet(version = 0))), KeySetRejectionReason.INVALID_VERSION)
        assertRejected(verifier.verify(envelope(validSet(version = 3)), minimumVersion = 4), KeySetRejectionReason.INVALID_VERSION)
    }

    @Test
    fun `rejects invalid time windows`() {
        val verifier = verifier()
        assertRejected(verifier.verify(envelope(validSet(issued = now + 301))), KeySetRejectionReason.INVALID_TIME_WINDOW)
        assertRejected(verifier.verify(envelope(validSet(expires = now))), KeySetRejectionReason.INVALID_TIME_WINDOW)
        assertRejected(verifier.verify(envelope(validSet(issued = now + 1, expires = now))), KeySetRejectionReason.INVALID_TIME_WINDOW)
        assertRejected(
            verifier.verify(envelope(validSet(issued = now - 1, expires = now + 181L * 24 * 60 * 60))),
            KeySetRejectionReason.INVALID_TIME_WINDOW,
        )
    }

    @Test
    fun `rejects empty excessive or unauthorized hosts`() {
        val verifier = verifier()
        assertRejected(verifier.verify(envelope(validSet(hosts = emptyList()))), KeySetRejectionReason.EMPTY_KEY_SET)
        val tooMany = List(11) { HostPublicKeyPins("api.caixa.gov.br", listOf(validPin)) }
        assertRejected(verifier.verify(envelope(validSet(hosts = tooMany))), KeySetRejectionReason.EMPTY_KEY_SET)
        assertRejected(
            verifier.verify(envelope(validSet(hosts = listOf(HostPublicKeyPins("evil.test", listOf(validPin)))))),
            KeySetRejectionReason.INVALID_HOST,
        )
        assertRejected(
            verifier(setOf("bad_host"), mapOf("one" to signer)).verify(
                envelope(validSet(hosts = listOf(HostPublicKeyPins("bad_host", listOf(validPin))))),
            ),
            KeySetRejectionReason.INVALID_HOST,
        )
        val duplicateHosts = listOf(
            HostPublicKeyPins("api.caixa.gov.br", listOf(validPin)),
            HostPublicKeyPins("api.caixa.gov.br", listOf(validPin)),
        )
        assertRejected(verifier.verify(envelope(validSet(hosts = duplicateHosts))), KeySetRejectionReason.INVALID_HOST)
    }

    @Test
    fun `accepts an owned subdomain covered by an explicit wildcard allowlist`() {
        val verifier = verifier(setOf("*.caixa.gov.br"), mapOf("one" to signer))
        val set = validSet(hosts = listOf(HostPublicKeyPins("apostas.caixa.gov.br", listOf(validPin))))

        assertTrue(verifier.verify(envelope(set)) is KeySetVerificationResult.Accepted)
        assertRejected(
            verifier.verify(envelope(validSet(hosts = listOf(HostPublicKeyPins("caixa.gov.br", listOf(validPin)))))),
            KeySetRejectionReason.INVALID_HOST,
        )
    }

    @Test
    fun `rejects missing malformed and excessive pins`() {
        val verifier = verifier()
        fun withPins(pins: List<String>) = validSet(hosts = listOf(HostPublicKeyPins("api.caixa.gov.br", pins)))
        assertRejected(verifier.verify(envelope(withPins(emptyList()))), KeySetRejectionReason.INVALID_PIN)
        assertRejected(verifier.verify(envelope(withPins(List(6) { validPin }))), KeySetRejectionReason.INVALID_PIN)
        assertRejected(verifier.verify(envelope(withPins(listOf("md5/value")))), KeySetRejectionReason.INVALID_PIN)
        assertRejected(verifier.verify(envelope(withPins(listOf("sha256/not-base64")))), KeySetRejectionReason.INVALID_PIN)
        val short = Base64.getEncoder().encodeToString(ByteArray(8))
        assertRejected(verifier.verify(envelope(withPins(listOf("sha256/$short")))), KeySetRejectionReason.INVALID_PIN)
        assertRejected(verifier.verify(envelope(withPins(listOf(validPin, validPin)))), KeySetRejectionReason.INVALID_PIN)
    }

    @Test
    fun `signer threshold validates constructor invariants`() {
        assertThrows(IllegalArgumentException::class.java) { TrustedConfigurationSigners(emptyMap(), 0) }
        assertThrows(IllegalArgumentException::class.java) { TrustedConfigurationSigners(mapOf("one" to "key"), 2) }
        assertThrows(IllegalArgumentException::class.java) { TrustedConfigurationSigners(mapOf("" to "key"), 1) }
        assertThrows(IllegalArgumentException::class.java) { TrustedConfigurationSigners(mapOf("one" to ""), 1) }
        assertThrows(IllegalArgumentException::class.java) {
            SignedKeySetVerifier(TrustedConfigurationSigners(mapOf("one" to "key"), 1), emptySet())
        }
    }

    @Test
    fun `repository prefers cache then applies newer remote`() = runTest {
        val cached = envelope(validSet(version = 3))
        val remote = envelope(validSet(version = 4))
        val store = FakeStore(cached)
        val repository = PublicKeySetRepository({ remote }, store, verifier(), fallback())

        assertEquals(KeySetSource.CACHE, repository.current().source)
        val refreshed = repository.refresh()

        assertEquals(KeySetSource.REMOTE, refreshed.source)
        assertEquals(4, refreshed.keySet.version)
        assertEquals(remote, store.value)
    }

    @Test
    fun `repository keeps current set for absent failed rejected or older remote`() = runTest {
        val verifier = verifier()
        val fallback = fallback()
        assertEquals(
            KeySetSource.LOCAL_FALLBACK,
            PublicKeySetRepository({ null }, FakeStore(null), verifier, fallback).refresh().source,
        )
        assertEquals(
            KeySetSource.LOCAL_FALLBACK,
            PublicKeySetRepository({ error("offline") }, FakeStore(null), verifier, fallback).refresh().source,
        )
        val cached = envelope(validSet(version = 4))
        val older = envelope(validSet(version = 3))
        val repository = PublicKeySetRepository({ older }, FakeStore(cached), verifier, fallback)
        assertEquals(KeySetSource.CACHE, repository.refresh().source)
        assertEquals(4, repository.current().keySet.version)

        val sameVersion = PublicKeySetRepository({ cached }, FakeStore(cached), verifier, fallback)
        assertEquals(KeySetSource.CACHE, sameVersion.refresh().source)
    }

    @Test
    fun `repository tolerates corrupt storage and write failures`() = runTest {
        val readFailure = object : SignedKeySetStore {
            override fun read(): SignedKeySetEnvelope = error("corrupt")
            override fun write(envelope: SignedKeySetEnvelope) = Unit
        }
        val repository = PublicKeySetRepository({ envelope(validSet()) }, readFailure, verifier(), fallback())
        assertEquals(KeySetSource.LOCAL_FALLBACK, repository.current().source)

        val writeFailure = object : SignedKeySetStore {
            override fun read(): SignedKeySetEnvelope? = null
            override fun write(envelope: SignedKeySetEnvelope) = error("disk full")
        }
        val accepted = PublicKeySetRepository({ envelope(validSet()) }, writeFailure, verifier(), fallback()).refresh()
        assertEquals(KeySetSource.REMOTE, accepted.source)
    }

    @Test
    fun `repository ignores invalid cached signature`() {
        val invalid = envelope(validSet()).copy(signatures = listOf(KeySetSignature("one", "AA")))
        val repository = PublicKeySetRepository({ null }, FakeStore(invalid), verifier(), fallback())

        assertEquals(KeySetSource.LOCAL_FALLBACK, repository.current().source)
    }

    private fun verifier(
        keys: Map<String, KeyPair> = mapOf("one" to signer),
        threshold: Int = 1,
    ) = verifier(setOf("api.caixa.gov.br"), keys, threshold)

    private fun verifier(
        hosts: Set<String>,
        keys: Map<String, KeyPair>,
        threshold: Int = 1,
    ) = SignedKeySetVerifier(
        trustedSigners = TrustedConfigurationSigners(
            keys.mapValues { (_, pair) -> Base64.getUrlEncoder().withoutPadding().encodeToString(pair.public.encoded) },
            threshold,
        ),
        allowedHosts = hosts,
        nowEpochSeconds = { now },
    )

    private fun validSet(
        version: Long = 4,
        issued: Long = now - 60,
        expires: Long = now + 86_400,
        hosts: List<HostPublicKeyPins> = listOf(HostPublicKeyPins("api.caixa.gov.br", listOf(validPin))),
    ) = PublicKeySet(version, issued, expires, hosts)

    private fun fallback() = validSet(version = 0, issued = 0, expires = Long.MAX_VALUE)

    private fun envelope(
        keySet: PublicKeySet,
        signers: List<Pair<String, KeyPair>> = listOf("one" to signer),
    ): SignedKeySetEnvelope {
        val payload = payload(keySet)
        return SignedKeySetEnvelope(payload, signers.map { (id, key) -> sign(id, key, payload) })
    }

    private fun signedPayload(value: String): SignedKeySetEnvelope {
        val payload = Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray())
        return SignedKeySetEnvelope(payload, listOf(sign("one", signer, payload)))
    }

    private fun payload(keySet: PublicKeySet) = Base64.getUrlEncoder().withoutPadding()
        .encodeToString(Gson().toJson(keySet).toByteArray())

    private fun sign(id: String, keyPair: KeyPair, encodedPayload: String): KeySetSignature {
        val bytes = Base64.getUrlDecoder().decode(encodedPayload)
        val signature = Signature.getInstance("SHA256withECDSA").run {
            initSign(keyPair.private)
            update(bytes)
            sign()
        }
        return KeySetSignature(id, Base64.getUrlEncoder().withoutPadding().encodeToString(signature))
    }

    private fun ecKeyPair(): KeyPair = KeyPairGenerator.getInstance("EC").apply { initialize(256) }.generateKeyPair()

    private fun assertRejected(result: KeySetVerificationResult, expected: KeySetRejectionReason) {
        assertEquals(expected, (result as KeySetVerificationResult.Rejected).reason)
    }

    private class FakeStore(var value: SignedKeySetEnvelope?) : SignedKeySetStore {
        override fun read() = value
        override fun write(envelope: SignedKeySetEnvelope) { value = envelope }
    }
}
