package br.gov.caixa.loterias.core.auth.refresh

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.FutureTask
import br.gov.caixa.loterias.core.auth.token.accessTokenExpiration
import br.gov.caixa.loterias.core.auth.gateway.OidcConfiguration
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore

enum class RegularSessionIdentityMode { OAUTH_ACCESS, OIDC }

/** Contexto volátil de validação da troca original e confiável com o endpoint de tokens. */
class RegularSessionRefreshContext(
    val configuration: OidcConfiguration,
    val originalNonce: String,
    val mode: RegularSessionIdentityMode,
    /** Somente a confirmação biométrica vincula uma sessão OAuth a uma credencial já lembrada. */
    internal val rememberedBiometric: Boolean = false,
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

/** O executor valida a resposta do endpoint de tokens e o titular antes de retornar estas credenciais. */
class RegularSessionRefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val ownerKey: String,
    val idToken: String? = null,
) {
    override fun toString() = "RegularSessionRefreshResponse(redacted)"
}

fun interface RegularSessionRefreshExecutor {
    /** Operação bloqueante para uma thread de trabalho; nunca deve ser chamada na thread principal. */
    fun execute(request: RegularSessionRefreshRequest): RegularSessionRefreshResponse
    /** O gateway protege primeiro o estado opcional das credenciais, preservando a ordem de bloqueio Gateway -> Store. */
    fun withCommitGuard(commit: () -> RegularSessionRefreshResult): RegularSessionRefreshResult = commit()
    /** Chamado somente depois de esta sessão específica aceitar a rotação, enquanto ambas as proteções estão mantidas. */
    fun afterCommit(request: RegularSessionRefreshRequest, response: RegularSessionRefreshResponse) = Unit
}

/** Não contém credenciais. A identidade e a geração do token são comparadas por referência. */
class RegularSessionRefreshTicket internal constructor(
    val identity: AuthenticatedSessionIdentity,
    internal val generation: Any,
) {
    override fun toString() = "RegularSessionRefreshTicket(redacted)"
}

enum class RegularSessionRefreshResult { NOT_REQUIRED, UPDATED, ALREADY_UPDATED, STALE, FAILED }

/** Agrupa renovações concorrentes sem manter o bloqueio do armazenamento de sessões durante operações de entrada e saída. */
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
        // O ServiceConnection do CX renova somente dentro de sua janela estrita de 30 segundos.
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
                    // Uma falha nunca destrói uma sessão substituta nem expõe a entrada do provedor.
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
