package br.gov.caixa.loterias.core.auth

import br.gov.caixa.loterias.core.auth.*
import org.junit.Assert.*
import org.junit.Test

class BackendSessionRefreshTest {
    private val context = RegularSessionRefreshContext(OidcConfiguration("https://s/auth", "https://s/token",
        "client", "app:/callback", "https://s/logout", "app:/logout", "backend"), "nonce", RegularSessionIdentityMode.OAUTH_ACCESS)
    private fun store() = SessionTokenStore().also {
        it.completeAuthorization(it.beginAuthorization(), "old", null, "a".repeat(64), "refresh", context)
    }
    @Test fun `captured lease rotates only its owner and cannot use replacement login`() {
        val store = store()
        val binding = BackendSessionRefresh(store, RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor {
            RegularSessionRefreshResponse("rotated", "new-refresh", "a".repeat(64))
        }))
        val lease = binding.bind("Bearer old")!!
        assertEquals("Bearer old", lease.prepare())
        assertEquals("Bearer rotated", lease.refreshAfterUnauthorized())
        assertNull(binding.bind("Bearer old"))
        store.completeAuthorization(store.beginAuthorization(), "other", null, "b".repeat(64), "other-refresh", context)
        assertNull(lease.prepare()); assertNull(lease.refreshAfterUnauthorized())
        assertNull(binding.bind("Basic other"))
    }
    @Test fun `session without refresh can send bound bearer but cannot renew unauthorized call`() {
        val store = SessionTokenStore()
        store.completeAuthorization(store.beginAuthorization(), "fixture", null, "a".repeat(64))
        val binding = BackendSessionRefresh(store, RegularSessionRefreshCoordinator(store, RegularSessionRefreshExecutor {
            error("must not exchange without credentials")
        }))
        val lease = binding.bind("Bearer fixture")!!
        assertEquals("Bearer fixture", lease.prepare()); assertNull(lease.refreshAfterUnauthorized())
        store.clear(); assertNull(lease.prepare()); assertNull(binding.bind("Bearer fixture"))
    }
}
