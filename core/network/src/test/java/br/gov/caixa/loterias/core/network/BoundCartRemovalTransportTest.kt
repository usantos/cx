package br.gov.caixa.loterias.core.network

import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test

/** Synthetic local HTTP only; no real account or backend mutation. */
class BoundCartRemovalTransportTest {
    @Test fun `ended login tag prevents DELETE despite replacement generic bearer`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(cart())
            val api = api(server, "replacement-login")
            val ended = AccountVerificationSession({ null }, { false })
            val failure = runCatching { api.removeBoundCartBet(17, ended) }.exceptionOrNull()
            assertTrue("Ended login must fail closed, never use replacement bearer", failure is AccountVerificationSessionExpiredException)
            assertEquals(0, server.requestCount)
        }
    }

    @Test fun `active tag sends captured bearer and ordinary removal remains compatible`() = runTest {
        MockWebServer().use { server ->
            repeat(2) { server.enqueue(cart()) }
            val api = api(server, "other-login")
            api.removeBoundCartBet(17, AccountVerificationSession({ "captured-login" }, { true }))
            val bound = server.takeRequest()
            assertEquals("DELETE", bound.method)
            assertEquals("/rest/v1/logado/carrinhos/apostas/17", bound.path)
            assertEquals("Bearer captured-login", bound.getHeader("Authorization"))
            api.removeCartBet(18)
            assertEquals("Bearer other-login", server.takeRequest().getHeader("Authorization"))
        }
    }

    @Test fun `bound tag survives authenticated refresh of same login`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(401))
            server.enqueue(cart())
            var boundToken = "captured-login"
            val session = AccountVerificationSession({ boundToken }, { true })
            val observedTags = mutableListOf<AccountVerificationSession?>()
            val lease = object : SessionRefreshLease {
                override fun prepare() = "Bearer $boundToken"
                override fun refreshAfterUnauthorized(): String {
                    boundToken = "same-login-refreshed"
                    return "Bearer $boundToken"
                }
            }
            val builder = OkHttpClient.Builder().addInterceptor { chain ->
                chain.proceed(chain.request()).also { observedTags += it.request.tag(AccountVerificationSession::class.java) }
            }
            val api = api(server, "replacement-login", builder, SessionRefreshHandler { authorization ->
                    assertEquals("Bearer captured-login", authorization)
                    lease
                })
            api.removeBoundCartBet(19, session)
            assertEquals("Bearer captured-login", server.takeRequest().getHeader("Authorization"))
            assertEquals("Bearer same-login-refreshed", server.takeRequest().getHeader("Authorization"))
            assertEquals(listOf(session), observedTags)
            assertEquals(2, server.requestCount)
        }
    }

    @Test fun `login replacement during response discards bound deletion response`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(cart())
            var current = true
            val builder = OkHttpClient.Builder().addNetworkInterceptor { chain ->
                chain.proceed(chain.request()).also { current = false }
            }
            val api = api(server, "replacement-login", builder)
            val session = AccountVerificationSession({ if (current) "captured-login" else null }, { current })
            val failure = runCatching { api.removeBoundCartBet(20, session) }.exceptionOrNull()
            assertTrue(failure is AccountVerificationSessionExpiredException)
            assertEquals(1, server.requestCount)
        }
    }

    @Test fun `bound DELETE never follows 503 retry after zero while ordinary deletion keeps its contract`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(503).setHeader("Retry-After", "0"))
            server.enqueue(cart())
            val api = api(server, "generic-login")
            val failure = runCatching {
                api.removeBoundCartBet(21, AccountVerificationSession({ "captured-login" }, { true }))
            }.exceptionOrNull()
            assertTrue("Bound mutation cannot silently follow 503", failure is retrofit2.HttpException)
            assertEquals(1, server.requestCount)
            assertEquals("DELETE", server.takeRequest().method)
        }
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(503).setHeader("Retry-After", "0"))
            server.enqueue(cart())
            api(server, "generic-login").removeCartBet(22)
            assertEquals("Unbound legacy transport is unchanged", 2, server.requestCount)
        }
    }

    private fun api(server: MockWebServer, generic: String, builder: OkHttpClient.Builder = OkHttpClient.Builder(),
        refresh: SessionRefreshHandler? = null): LoteriasApi {
        // The factory still enforces HTTPS. Only this fixture rewrites its local wire to MockWebServer.
        val base = server.url("/rest/v1/").newBuilder().scheme("https").build().toString()
        builder.addInterceptor { chain ->
            val request = chain.request()
            chain.proceed(request.newBuilder().url(request.url.newBuilder().scheme("http").build()).build())
        }
        return NetworkClientFactory.create(base, AuthTokenProvider { generic }, builder, sessionRefreshHandler = refresh)
    }

    private fun cart() = MockResponse().setHeader("Content-Type", "application/json")
        .setBody("{\"payload\":{\"apostas\":[],\"combos\":[],\"boloes\":[]}}")
}
