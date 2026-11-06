package br.gov.caixa.loterias.core.auth.refresh

import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshHandler
import br.gov.caixa.loterias.core.network.interceptor.SessionRefreshLease
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore

/** Uma requisição CAIXA mantém seu titular original durante a verificação prévia e a renovação por HTTP 401. */
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
