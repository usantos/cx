package br.gov.caixa.loterias.core.keymanagement

import com.google.gson.Gson
import java.security.KeyPairGenerator
import java.security.Signature
import java.util.Base64
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.keymanagement.model.HostPublicKeyPins
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.keymanagement.model.KeySetSignature
import br.gov.caixa.loterias.core.keymanagement.model.SignedKeySetEnvelope
import br.gov.caixa.loterias.core.keymanagement.model.TrustedConfigurationSigners
import br.gov.caixa.loterias.core.keymanagement.model.KeySetSource
import br.gov.caixa.loterias.core.keymanagement.model.SignedKeySetStore
import br.gov.caixa.loterias.core.keymanagement.repository.PublicKeySetRepository
import br.gov.caixa.loterias.core.keymanagement.verification.SignedKeySetVerifier

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PublicKeySetRepositoryTest {
    @Test fun `cache expires at the boundary and does not revive after clock rollback`() {
        val fixture = Fixture()
        fixture.saved = fixture.signed(4, expires = fixture.now + 10)
        val repository = fixture.repository()
        assertEquals(KeySetSource.CACHE, repository.current().source)
        fixture.now += 9
        assertEquals(4L, repository.current().keySet.version)
        fixture.now++
        assertEquals(fixture.fallback, repository.current().keySet)
        fixture.now -= 20
        assertEquals(KeySetSource.LOCAL_FALLBACK, repository.current().source)
    }

    @Test fun `offline absent or rejected remote never preserves expired pins`() = runTest {
        for (outcome in 0..2) {
            val fixture = Fixture()
            fixture.saved = fixture.signed(5, expires = fixture.now + 1)
            val repository = fixture.repository()
            fixture.now++
            fixture.fetch = {
                when (outcome) {
                    0 -> null
                    1 -> error("offline")
                    else -> SignedKeySetEnvelope("corrupt", emptyList())
                }
            }
            assertEquals(KeySetSource.LOCAL_FALLBACK, repository.refresh().source)
        }
    }

    @Test fun `expired signed cache preserves rollback floor after repository recreation`() = runTest {
        val fixture = Fixture()
        fixture.saved = fixture.signed(10, expires = fixture.now - 1)
        val repository = fixture.repository()
        assertEquals(KeySetSource.LOCAL_FALLBACK, repository.current().source)
        for (version in listOf(9L, 10L)) {
            fixture.fetch = { fixture.signed(version) }
            assertEquals(KeySetSource.LOCAL_FALLBACK, repository.refresh().source)
            assertEquals(0, fixture.writes)
        }
        fixture.fetch = { fixture.signed(11) }
        assertEquals(11L, repository.refresh().keySet.version)
        assertEquals(1, fixture.writes)
    }

    @Test fun `unauthenticated cache cannot raise the rollback floor`() = runTest {
        val fixture = Fixture()
        fixture.saved = fixture.signed(100).copy(signatures = emptyList())
        fixture.fetch = { fixture.signed(2) }
        assertEquals(2L, fixture.repository().refresh().keySet.version)
        assertNull(fixture.verifier.authenticatedVersion(SignedKeySetEnvelope("not-json", emptyList())))
    }

    @Test fun `refreshes are serialized and a later older response cannot roll back state or disk`() = runTest {
        val fixture = Fixture()
        val gate = CompletableDeferred<Unit>()
        var calls = 0
        fixture.fetch = {
            calls++
            if (calls == 1) { gate.await(); fixture.signed(10) } else fixture.signed(9)
        }
        val repository = fixture.repository()
        val first = async { repository.refresh() }
        runCurrent()
        val second = async { repository.refresh() }
        runCurrent()
        assertEquals(1, calls)
        gate.complete(Unit)
        assertEquals(10L, first.await().keySet.version)
        assertEquals(10L, second.await().keySet.version)
        assertEquals(1, fixture.writes)
        assertEquals(10L, fixture.repository().current().keySet.version)
    }

    @Test fun `cancellation is propagated and refresh lock is released`() = runTest {
        val fixture = Fixture()
        val repository = fixture.repository()
        fixture.fetch = { throw CancellationException("cancel fetch") }
        expectCancellation { repository.refresh() }
        fixture.fetch = { fixture.signed(2) }
        fixture.cancelWrite = true
        expectCancellation { repository.refresh() }
        assertEquals(KeySetSource.LOCAL_FALLBACK, repository.current().source)
        fixture.cancelWrite = false
        assertEquals(2L, repository.refresh().keySet.version)
    }

    @Test fun `expiration while storage is writing is rechecked without losing version floor`() = runTest {
        val fixture = Fixture()
        fixture.fetch = { fixture.signed(7, expires = fixture.now + 1) }
        fixture.onWrite = { fixture.now += 2 }
        val repository = fixture.repository()
        assertEquals(KeySetSource.LOCAL_FALLBACK, repository.refresh().source)
        fixture.fetch = { fixture.signed(6) }
        assertEquals(KeySetSource.LOCAL_FALLBACK, repository.refresh().source)
        assertEquals(1, fixture.writes)
    }

    @Test fun `expiry during remote wait falls back and valid newer response restores service`() = runTest {
        val fixture = Fixture()
        fixture.saved = fixture.signed(3, expires = fixture.now + 1)
        val repository = fixture.repository()
        val stale = fixture.signed(4, expires = fixture.now + 1)
        fixture.fetch = { fixture.now += 2; stale }
        assertEquals(KeySetSource.LOCAL_FALLBACK, repository.refresh().source)
        fixture.fetch = { fixture.signed(4) }
        assertEquals(KeySetSource.REMOTE, repository.refresh().source)
    }

    private suspend fun expectCancellation(block: suspend () -> Unit) {
        try { block(); fail("Cancellation must propagate") } catch (_: CancellationException) { }
    }

    private class Fixture {
        var now = 2_000_000_000L
        private val signer = KeyPairGenerator.getInstance("EC").apply { initialize(256) }.generateKeyPair()
        private val encoder = Base64.getUrlEncoder().withoutPadding()
        private val pin = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
        val fallback = PublicKeySet(0, 0, Long.MAX_VALUE, listOf(HostPublicKeyPins("api.caixa.gov.br", listOf(pin))))
        val verifier = SignedKeySetVerifier(TrustedConfigurationSigners(mapOf("one" to encoder.encodeToString(signer.public.encoded)), 1),
            setOf("api.caixa.gov.br"), nowEpochSeconds = { now })
        var saved: SignedKeySetEnvelope? = null
        var fetch: suspend () -> SignedKeySetEnvelope? = { null }
        var writes = 0
        var cancelWrite = false
        var onWrite: () -> Unit = {}

        fun repository() = PublicKeySetRepository({ fetch() }, object : SignedKeySetStore {
            override fun read() = saved
            override fun write(envelope: SignedKeySetEnvelope) {
                if (cancelWrite) throw CancellationException("cancel write")
                writes++
                saved = envelope
                onWrite()
            }
        }, verifier, fallback)

        fun signed(version: Long, expires: Long = now + 100): SignedKeySetEnvelope {
            val bytes = Gson().toJson(fallback.copy(version = version, issuedAtEpochSeconds = now - 60,
                expiresAtEpochSeconds = expires)).toByteArray(Charsets.UTF_8)
            val signature = Signature.getInstance("SHA256withECDSA").run {
                initSign(signer.private); update(bytes); sign()
            }
            return SignedKeySetEnvelope(encoder.encodeToString(bytes), listOf(KeySetSignature("one", encoder.encodeToString(signature))))
        }
    }
}
