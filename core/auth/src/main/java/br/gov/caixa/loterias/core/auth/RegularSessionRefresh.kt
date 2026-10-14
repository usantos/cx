package br.gov.caixa.loterias.core.auth

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.FutureTask

enum class RegularSessionIdentityMode { OAUTH_ACCESS, OIDC }

/** Volatile validation context for the original, trusted token-endpoint exchange. */
class RegularSessionRefreshContext(
    val configuration: OidcConfiguration,
    val originalNonce: String,
    val mode: RegularSessionIdentityMode,
) {
    override fun toString() = "RegularSessionRefreshContext(redacted)"
}

class RegularSessionRefreshRequest internal constructor(
    val refreshToken: String,
    val ownerKey: String,
    val context: RegularSessionRefreshContext,
    val previousIdToken: String?,
) {
    override fun toString() = "RegularSessionRefreshRequest(redacted)"
}

/** Executor validates the token-endpoint response and owner before returning these credentials. */
class RegularSessionRefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val ownerKey: String,
    val idToken: String? = null,
) {
    override fun toString() = "RegularSessionRefreshResponse(redacted)"
}

fun interface RegularSessionRefreshExecutor {
    /** Blocking worker-thread operation; never call from the main thread. */
    fun execute(request: RegularSessionRefreshRequest): RegularSessionRefreshResponse
    /** Gateway guards opt-in credential state first, preserving Gateway -> Store lock order. */
    fun withCommitGuard(commit: () -> RegularSessionRefreshResult): RegularSessionRefreshResult = commit()
    /** Called only after this exact session accepted the rotation, while both guards are held. */
    fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) = Unit
}

/** Contains no credentials. Identity and token generation are compared by reference. */
class RegularSessionRefreshTicket internal constructor(
    val identity: AuthenticatedSessionIdentity,
    internal val generation: Any,
) {
    override fun toString() = "RegularSessionRefreshTicket(redacted)"
}

enum class RegularSessionRefreshResult { NOT_REQUIRED, UPDATED, ALREADY_UPDATED, STALE, FAILED }

/** Coalesces concurrent refreshes without holding the session store lock across I/O. */
class RegularSessionRefreshCoordinator(
    private val store: SessionTokenStore,
    private val executor: RegularSessionRefreshExecutor,
    private val nowSeconds: () -> Long = { System.currentTimeMillis() / 1000 },
) {
    private val flights = ConcurrentHashMap<Any, FutureTask<RegularSessionRefreshResult>>()

    fun prepare(ticket: RegularSessionRefreshTicket): RegularSessionRefreshResult {
        store.refreshTicketStatus(ticket)?.let { return it }
        val token = store.tokenFor(ticket.identity) ?: return RegularSessionRefreshResult.STALE
        val expiration = accessTokenExpiration(token) ?: return RegularSessionRefreshResult.NOT_REQUIRED
        // CX ServiceConnection refreshes only within its strict 30-second window.
        if (expiration - nowSeconds() >= 30) return RegularSessionRefreshResult.NOT_REQUIRED
        return refreshAfterUnauthorized(ticket)
    }

    fun refreshAfterUnauthorized(ticket: RegularSessionRefreshTicket): RegularSessionRefreshResult {
        store.refreshTicketStatus(ticket)?.let { return it }
        val candidate = FutureTask {
            store.refreshTicketStatus(ticket) ?: run {
                val request = store.refreshRequest(ticket) ?: return@run RegularSessionRefreshResult.STALE
                try {
                    val response = executor.execute(request)
                    executor.withCommitGuard {
                        store.commitRefresh(ticket, response) { executor.afterCommit(request, response) }
                    }
                } catch (_: Exception) {
                    // A failure never destroys a replacement session or leaks provider input.
                    store.refreshTicketStatus(ticket) ?: RegularSessionRefreshResult.FAILED
                }
            }
        }
        val flight = flights.putIfAbsent(ticket.generation, candidate) ?: candidate
        try {
            if (flight === candidate) flight.run()
            return flight.get()
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            return RegularSessionRefreshResult.FAILED
        } finally {
            if (flight === candidate) flights.remove(ticket.generation, candidate)
        }
    }
}
