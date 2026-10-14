package br.gov.caixa.loterias.core.auth

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.util.Base64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class RegularSessionRefreshTest {
    private val owner = "a".repeat(64)
    private val configuration = OidcConfiguration("https://s/auth", "https://s/token", "client", "app:/return",
        "https://s/logout", "app:/logout", "https://session")
    private val context = RegularSessionRefreshContext(configuration, "nonce", RegularSessionIdentityMode.OAUTH_ACCESS)
    private fun token(exp: Long) = "header." + Base64.getUrlEncoder().withoutPadding()
        .encodeToString("{\"exp\":$exp}".toByteArray()) + ".signature"
    private fun store(exp: Long = 129) = SessionTokenStore().also {
        it.completeAuthorization(it.beginAuthorization(), token(exp), "id", owner, "refresh", context)
    }
    private fun response(refresh: String = "rotated") = RegularSessionRefreshResponse(token(500), refresh, owner)

    @Test fun `refresh preserves identity content and logout hint while rotating credentials`() = runBlocking {
        val store = store()
        val identity = store.captureIdentity()!!
        val content = AuthenticatedSessionContent(null, null, true)
        store.publish(identity, content)
        val ticket = store.captureRefreshTicket()!!
        val coordinator = RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor { request ->
            assertEquals("refresh", request.refreshToken)
            assertEquals(owner, request.ownerKey)
            assertSame(context, request.context)
            response()
        }) { 100 }
        assertEquals(RegularSessionRefreshResult.UPDATED, coordinator.prepare(ticket))
        assertSame(identity, store.captureIdentity())
        assertSame(content, store.observe().first())
        assertEquals(token(500), store.tokenFor(identity))
        assertEquals("rotated", store.refreshRequest(store.captureRefreshTicket()!!)!!.refreshToken)
        assertEquals("id", store.clearForEndSession())
    }

    @Test fun `CX preflight threshold is strictly less than thirty seconds`() {
        for ((exp, expected) in listOf(130L to RegularSessionRefreshResult.NOT_REQUIRED,
            129L to RegularSessionRefreshResult.UPDATED, 99L to RegularSessionRefreshResult.UPDATED)) {
            val store = store(exp)
            val coordinator = RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor { response() }) { 100 }
            assertEquals(expected, coordinator.prepare(store.captureRefreshTicket()!!))
        }
    }

    @Test fun `malformed expiration does not add a preflight refresh policy absent from CX`() {
        val store = SessionTokenStore()
        store.completeAuthorization(store.beginAuthorization(), "opaque", "id", owner, "refresh", context)
        val calls = AtomicInteger()
        val coordinator = RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor {
            calls.incrementAndGet(); response()
        }) { 100 }
        assertEquals(RegularSessionRefreshResult.NOT_REQUIRED, coordinator.prepare(store.captureRefreshTicket()!!))
        assertEquals("opaque", store.token())
        assertEquals(0, calls.get())
        assertEquals(RegularSessionRefreshResult.UPDATED, coordinator.refreshAfterUnauthorized(store.captureRefreshTicket()!!))
    }

    @Test fun `missing refresh never becomes renewable and empty rotation never replaces session`() {
        val store = SessionTokenStore()
        store.completeAuthorization(store.beginAuthorization(), token(129), "id", owner)
        assertNull(store.captureRefreshTicket())
        val renewable = store()
        val identity = renewable.captureIdentity()!!
        val coordinator = RegularSessionRefreshCoordinator(renewable, RegularSessionRefreshExecutor { response("") }) { 100 }
        assertEquals(RegularSessionRefreshResult.FAILED, coordinator.refreshAfterUnauthorized(renewable.captureRefreshTicket()!!))
        assertEquals(token(129), renewable.tokenFor(identity))
    }

    @Test fun `new login cancellation and logout invalidate captured refresh tickets`() {
        val store = store()
        val ticket = store.captureRefreshTicket()!!
        val attempt = store.beginAuthorization()
        store.cancelAuthorization(attempt)
        val coordinator = RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor { response() }) { 100 }
        assertEquals(RegularSessionRefreshResult.STALE, coordinator.refreshAfterUnauthorized(ticket))
        val current = store.captureRefreshTicket()!!
        store.clear()
        assertEquals(RegularSessionRefreshResult.STALE, coordinator.refreshAfterUnauthorized(current))
    }

    @Test fun `old unauthorized request reuses published rotation without another token exchange`() {
        val store = store()
        val ticket = store.captureRefreshTicket()!!
        val calls = AtomicInteger()
        val coordinator = RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor {
            calls.incrementAndGet(); response()
        }) { 100 }
        assertEquals(RegularSessionRefreshResult.UPDATED, coordinator.refreshAfterUnauthorized(ticket))
        assertEquals(RegularSessionRefreshResult.ALREADY_UPDATED, coordinator.refreshAfterUnauthorized(ticket))
        assertEquals(1, calls.get())
    }

    @Test fun `concurrent unauthorized requests share one rotation and stale completion cannot overwrite replacement`() {
        val store = store()
        val ticket = store.captureRefreshTicket()!!
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val calls = AtomicInteger()
        val coordinator = RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor {
            calls.incrementAndGet(); entered.countDown()
            check(release.await(5, TimeUnit.SECONDS)); response()
        }) { 100 }
        val results = java.util.concurrent.CopyOnWriteArrayList<RegularSessionRefreshResult>()
        val first = Thread { results += coordinator.refreshAfterUnauthorized(ticket) }
        val second = Thread { results += coordinator.refreshAfterUnauthorized(ticket) }
        first.start(); assertTrue(entered.await(5, TimeUnit.SECONDS)); second.start()
        store.completeAuthorization(store.beginAuthorization(), "replacement", "replacement-id", "b".repeat(64), "new-refresh", context)
        release.countDown(); first.join(5000); second.join(5000)
        assertFalse(first.isAlive); assertFalse(second.isAlive)
        assertEquals(1, calls.get()); assertEquals(listOf(RegularSessionRefreshResult.STALE, RegularSessionRefreshResult.STALE), results.toList())
        assertEquals("replacement", store.token())
    }

    @Test fun `simultaneous requests wait for a single exchange and both observe rotated credentials`() {
        val store = store()
        val ticket = store.captureRefreshTicket()!!
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val calls = AtomicInteger()
        val coordinator = RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor {
            calls.incrementAndGet(); entered.countDown()
            check(release.await(5, TimeUnit.SECONDS)); response()
        }) { 100 }
        val results = java.util.concurrent.CopyOnWriteArrayList<RegularSessionRefreshResult>()
        val first = Thread { results += coordinator.refreshAfterUnauthorized(ticket) }
        val second = Thread { results += coordinator.refreshAfterUnauthorized(ticket) }
        first.start(); assertTrue(entered.await(5, TimeUnit.SECONDS)); second.start()
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while (second.state != Thread.State.WAITING && second.isAlive && System.nanoTime() < deadline) Thread.yield()
        val waiting = second.state == Thread.State.WAITING
        release.countDown(); first.join(5000); second.join(5000)
        assertTrue("second request must join the in-flight exchange", waiting)
        assertFalse(first.isAlive); assertFalse(second.isAlive)
        assertEquals(1, calls.get())
        assertEquals(2, results.size)
        assertTrue(results.all { it == RegularSessionRefreshResult.UPDATED })
        assertEquals(token(500), store.tokenFor(ticket.identity))
    }

    @Test fun `explicit old token cannot capture replacement owner`() {
        val store = store()
        val old = token(129)
        assertNotNull(store.captureRefreshTicketForToken(old))
        store.completeAuthorization(store.beginAuthorization(), "replacement", null, "b".repeat(64), "new-refresh", context)
        assertNull(store.captureRefreshTicketForToken(old))
        assertEquals("b".repeat(64), store.captureRefreshTicketForToken("replacement")!!.identity.ownerKey)
    }

    @Test fun `late exchange failure cannot clear replacement tokens`() {
        val store = store()
        val ticket = store.captureRefreshTicket()!!
        val coordinator = RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor {
            store.completeAuthorization(store.beginAuthorization(), "replacement", null, "b".repeat(64), "new-refresh", context)
            throw IllegalStateException("provider failure")
        }) { 100 }
        assertEquals(RegularSessionRefreshResult.STALE, coordinator.refreshAfterUnauthorized(ticket))
        assertEquals("replacement", store.token())
    }

    @Test fun `accepted rotation invokes persistence hook only after credentials are visible`() {
        val store = store()
        val ticket = store.captureRefreshTicket()!!
        var remembered: String? = null
        val executor = object : RegularSessionRefreshExecutor {
            override fun execute(request: RegularSessionRefreshRequest) = response()
            override fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) {
                assertEquals(token(500), store.tokenFor(ticket.identity))
                remembered = response.refreshToken
            }
        }
        val coordinator = RegularSessionRefreshCoordinator(store, executor) { 100 }
        assertEquals(RegularSessionRefreshResult.UPDATED, coordinator.refreshAfterUnauthorized(ticket))
        assertEquals("rotated", remembered)
        remembered = null
        assertEquals(RegularSessionRefreshResult.ALREADY_UPDATED, coordinator.refreshAfterUnauthorized(ticket))
        assertNull(remembered)
    }

    @Test fun `logout and new login wait for accepted rotation persistence and cannot be overwritten late`() {
        for (newLogin in listOf(false, true)) {
            val store = store()
            val ticket = store.captureRefreshTicket()!!
            val persisting = CountDownLatch(1)
            val release = CountDownLatch(1)
            val executor = object : RegularSessionRefreshExecutor {
                override fun execute(request: RegularSessionRefreshRequest) = response()
                override fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) {
                    persisting.countDown()
                    check(release.await(5, TimeUnit.SECONDS))
                    assertEquals(token(500), store.tokenFor(ticket.identity))
                }
            }
            val coordinator = RegularSessionRefreshCoordinator(store, executor) { 100 }
            val refresh = Thread { coordinator.refreshAfterUnauthorized(ticket) }
            refresh.start()
            val hookEntered = persisting.await(2, TimeUnit.SECONDS)
            val replace = Thread {
                if (newLogin) store.completeAuthorization(store.beginAuthorization(), "replacement", null, "b".repeat(64))
                else store.clear()
            }
            replace.start()
            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2)
            while (replace.state != Thread.State.BLOCKED && replace.isAlive && System.nanoTime() < deadline) Thread.yield()
            val blocked = replace.state == Thread.State.BLOCKED
            release.countDown(); refresh.join(5000); replace.join(5000)
            assertTrue("accepted refresh must call its persistence hook", hookEntered)
            assertTrue("replacement cannot race the guarded persistence", blocked)
            assertFalse(refresh.isAlive); assertFalse(replace.isAlive)
            assertEquals(if (newLogin) "replacement" else null, store.token())
            assertFalse(store.isCurrent(ticket.identity))
        }
    }

    @Test fun `disabled biometric state is checked inside shared gateway commit guard`() {
        val store = store()
        val ticket = store.captureRefreshTicket()!!
        val gatewayLock = Any()
        var enabled = true
        var persisted: String? = null
        val executor = object : RegularSessionRefreshExecutor {
            override fun execute(request: RegularSessionRefreshRequest): RegularSessionRefreshResponse {
                synchronized(gatewayLock) { enabled = false }
                return response()
            }
            override fun withCommitGuard(commit: () -> RegularSessionRefreshResult) = synchronized(gatewayLock) { commit() }
            override fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) {
                assertTrue(Thread.holdsLock(gatewayLock))
                if (enabled) persisted = response.refreshToken
            }
        }
        assertEquals(RegularSessionRefreshResult.UPDATED,
            RegularSessionRefreshCoordinator(store, executor) { 100 }.refreshAfterUnauthorized(ticket))
        assertNull(persisted)
        assertEquals(token(500), store.token())
    }

    @Test fun `credential persistence failure does not undo accepted session refresh`() {
        val store = store()
        val ticket = store.captureRefreshTicket()!!
        val executor = object : RegularSessionRefreshExecutor {
            override fun execute(request: RegularSessionRefreshRequest) = response()
            override fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) {
                throw IllegalStateException("credential storage unavailable")
            }
        }
        assertEquals(RegularSessionRefreshResult.UPDATED,
            RegularSessionRefreshCoordinator(store, executor) { 100 }.refreshAfterUnauthorized(ticket))
        assertEquals(token(500), store.tokenFor(ticket.identity))
    }

    @Test fun `stale and rejected rotations never invoke persisted credential hook`() {
        for (replacement in listOf(false, true)) {
            val store = store()
            val ticket = store.captureRefreshTicket()!!
            var called = false
            val executor = object : RegularSessionRefreshExecutor {
                override fun execute(request: RegularSessionRefreshRequest): RegularSessionRefreshResponse {
                    if (replacement) store.clear()
                    return response("")
                }
                override fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) { called = true }
            }
            assertEquals(if (replacement) RegularSessionRefreshResult.STALE else RegularSessionRefreshResult.FAILED,
                RegularSessionRefreshCoordinator(store, executor) { 100 }.refreshAfterUnauthorized(ticket))
            assertFalse(called)
        }
    }

    @Test fun `refresh response from a different owner is rejected without clearing current session`() {
        val store = store()
        val coordinator = RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor {
            RegularSessionRefreshResponse("foreign-access", "foreign-refresh", "b".repeat(64), "foreign-id")
        }) { 100 }
        assertEquals(RegularSessionRefreshResult.FAILED, coordinator.refreshAfterUnauthorized(store.captureRefreshTicket()!!))
        assertEquals(token(129), store.token())
        assertFalse(context.toString().contains("nonce"))
        assertFalse(response().toString().contains("rotated"))
    }
}
