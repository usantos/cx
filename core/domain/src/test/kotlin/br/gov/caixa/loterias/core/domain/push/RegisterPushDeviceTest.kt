package br.gov.caixa.loterias.core.domain.push

import br.gov.caixa.loterias.core.domain.account.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RegisterPushDeviceTest {
    private var identity: AuthenticatedSessionIdentity? = AuthenticatedSessionIdentity("a".repeat(64))
    private val sessions = object : AuthenticatedSessionIdentityProvider {
        override fun captureIdentity() = identity
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = this@RegisterPushDeviceTest.identity === identity
    }
    private val markers = mutableSetOf<String>()
    private val store = object : PushRegistrationStore {
        override fun contains(marker: String) = marker in markers
        override fun remember(marker: String) { markers += marker }
    }

    @Test fun `failed registration retries and only successful CPF token pairs are remembered`() = runTest {
        var accepted = false
        val requests = mutableListOf<Pair<Long, String>>()
        var cpf = "01234567890"
        val register = RegisterPushDevice(sessions, { cpf }, PushRegistrationRepository { _, number, token ->
            requests += number to token; accepted
        }, store)
        assertFalse(register("fcm-one"))
        assertTrue(markers.isEmpty())
        accepted = true
        assertTrue(register("fcm-one"))
        assertTrue(register("fcm-one"))
        assertEquals(2, requests.size)
        assertEquals(1234567890L to "fcm-one", requests.first())
        assertTrue(register("fcm-two"))
        cpf = "12345678901"
        assertTrue(register("fcm-two"))
        assertEquals(4, requests.size)
        assertFalse(markers.any { "fcm" in it || cpf in it })
    }

    @Test fun `logout or replaced session while backend responds does not acknowledge registration`() = runTest {
        for (replacement in listOf(null, AuthenticatedSessionIdentity("b".repeat(64)))) {
            identity = AuthenticatedSessionIdentity("a".repeat(64))
            val entered = CompletableDeferred<Unit>()
            val response = CompletableDeferred<Boolean>()
            val register = RegisterPushDevice(sessions, { "01234567890" }, PushRegistrationRepository { _, _, _ ->
                entered.complete(Unit); response.await()
            }, store)
            val pending = async { register("fcm-one") }
            entered.await()
            identity = replacement
            response.complete(true)
            assertFalse(pending.await())
            assertTrue(markers.isEmpty())
        }
    }

    @Test fun `guest invalid CPF and empty token never reach backend`() = runTest {
        var requests = 0
        var cpf: String? = null
        val register = RegisterPushDevice(sessions, { cpf }, PushRegistrationRepository { _, _, _ -> requests++; true }, store)
        assertFalse(register("fcm"))
        cpf = "invalid"
        assertFalse(register("fcm"))
        cpf = "12345678901"
        assertFalse(register(" "))
        identity = null
        assertFalse(register("fcm"))
        assertEquals(0, requests)
    }

    @Test fun `transport exceptions keep registration retryable`() = runTest {
        val register = RegisterPushDevice(sessions, { "01234567890" }, PushRegistrationRepository { _, _, _ ->
            throw java.io.IOException("unavailable")
        }, store)
        assertFalse(register("fcm"))
        assertTrue(markers.isEmpty())
    }

    @Test fun `cancellation never becomes an acknowledged registration`() = runTest {
        val cancelled = kotlinx.coroutines.CancellationException("cancelled")
        val register = RegisterPushDevice(sessions, { "01234567890" }, PushRegistrationRepository { _, _, _ ->
            throw cancelled
        }, store)
        assertSame(cancelled, runCatching { register("fcm") }.exceptionOrNull())
        assertTrue(markers.isEmpty())
    }
}
