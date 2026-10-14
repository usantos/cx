package br.gov.caixa.loterias.core.auth

import br.gov.caixa.loterias.core.auth.RegularSessionRefreshCoordinator
import br.gov.caixa.loterias.core.auth.RegularSessionRefreshResult
import br.gov.caixa.loterias.core.auth.SessionTokenStore
import br.gov.caixa.loterias.core.network.SessionRefreshHandler
import br.gov.caixa.loterias.core.network.SessionRefreshLease

/** One CAIXA request retains its original owner across preflight and HTTP 401 renewal. */
class BackendSessionRefresh(
    private val store: SessionTokenStore,
    private val coordinator: RegularSessionRefreshCoordinator,
) : SessionRefreshHandler {
    override fun bind(authorization: String): SessionRefreshLease? {
        if (!authorization.startsWith("Bearer ")) return null
        val token = authorization.removePrefix("Bearer ")
        val identity = store.captureIdentity() ?: return null
        if (store.tokenFor(identity) != token) return null
        val ticket = store.captureRefreshTicketForToken(token)
        if (ticket != null && ticket.identity !== identity) return null
        return object : SessionRefreshLease {
            override fun prepare(): String? {
                if (ticket != null && !accepted(coordinator.prepare(ticket))) return null
                return header()
            }
            override fun refreshAfterUnauthorized(): String? {
                if (ticket == null || !accepted(coordinator.refreshAfterUnauthorized(ticket))) return null
                return header()
            }
            private fun header() = store.tokenFor(identity)?.let { "Bearer $it" }
        }
    }
    private fun accepted(result: RegularSessionRefreshResult) = result == RegularSessionRefreshResult.NOT_REQUIRED ||
        result == RegularSessionRefreshResult.UPDATED || result == RegularSessionRefreshResult.ALREADY_UPDATED
}
