package br.gov.caixa.loterias.feature.responsiblegaming.data.repository

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.AccountSuspension
import br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase.SuspensionUseCasesFactory
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** Rede sintética local; nenhuma conta real é consultada ou suspensa. */
class SuspensionSessionTransportTest {
    @Test fun `suspension acquired before lease replacement never uses the replacement bearer`() = runTest {
        MockWebServer().use { server ->
            var current = true
            var captures = 0
            val factory = SuspensionUseCasesFactory(
                SessionSuspensionRepositoryFactory(provider(server)) {
                    captures++
                    AccountVerificationSession({ if (current) "captured-A" else null }, { current })
                }
            )
            val suspendAccount = factory.forCurrentSession()
            assertEquals(1, captures)
            current = false
            assertTrue(suspendAccount(SUSPENSION_HOURS) is AppResult.Failure)
            assertEquals(0, server.requestCount)
            assertEquals(1, captures)
        }
    }

    @Test fun `replacement after profile GET but before POST prevents suspension`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(profile())
            var current = true
            val builder = OkHttpClient.Builder().addInterceptor { chain ->
                if (chain.request().method == "POST") current = false
                chain.proceed(chain.request())
            }
            val operation = SuspensionUseCasesFactory(
                SessionSuspensionRepositoryFactory(provider(server, builder)) {
                    AccountVerificationSession({ if (current) "captured-A" else null }, { current })
                }
            ).forCurrentSession()
            assertTrue(operation(SUSPENSION_HOURS) is AppResult.Failure)
            assertEquals(1, server.requestCount)
            val read = server.takeRequest()
            assertEquals("GET", read.method)
            assertEquals("/rest/v1/usuarios/recuperar-dados", read.path)
            assertEquals("Bearer captured-A", read.getHeader("Authorization"))
        }
    }

    @Test fun `replacement during profile response rejects the result before POST`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(profile())
            var current = true
            val builder = OkHttpClient.Builder().addNetworkInterceptor { chain ->
                chain.proceed(chain.request()).also { current = false }
            }
            val operation = SuspensionUseCasesFactory(
                SessionSuspensionRepositoryFactory(provider(server, builder)) {
                    AccountVerificationSession({ "captured-A" }, { current })
                }
            ).forCurrentSession()
            assertTrue(operation(SUSPENSION_HOURS) is AppResult.Failure)
            assertEquals(1, server.requestCount)
            assertEquals("Bearer captured-A", server.takeRequest().getHeader("Authorization"))
        }
    }

    @Test fun `profile and suspension retain one captured bearer and the original HTTP contract`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(profile())
            server.enqueue(
                MockResponse().setHeader("Content-Type", "application/json")
                    .setBody("""{"payload":{"apostadorSuspenso":true,"prazoHorasDias":"24 horas"}}""")
            )
            val session = AccountVerificationSession({ "captured-A" }, { true })
            val tags = mutableListOf<AccountVerificationSession?>()
            val builder = OkHttpClient.Builder().addInterceptor { chain ->
                tags += chain.request().tag(AccountVerificationSession::class.java)
                chain.proceed(chain.request())
            }
            val operation = SuspensionUseCasesFactory(SessionSuspensionRepositoryFactory(provider(server, builder)) { session })
                .forCurrentSession()
            assertEquals(AppResult.Success(AccountSuspension(true, "24 horas")), operation(SUSPENSION_HOURS))
            assertEquals(2, server.requestCount)
            val read = server.takeRequest()
            val write = server.takeRequest()
            assertEquals("GET", read.method)
            assertEquals("/rest/v1/usuarios/recuperar-dados", read.path)
            assertEquals("POST", write.method)
            assertEquals("/rest/v1/usuarios/suspender-conta-apostador", write.path)
            listOf(read, write).forEach { assertEquals("Bearer captured-A", it.getHeader("Authorization")) }
            assertEquals(2, tags.size)
            tags.forEach { assertSame(session, it) }
            val body = com.google.gson.JsonParser.parseString(write.body.readUtf8()).asJsonObject
            assertEquals("12345678901", body["cpfApostador"].asString)
            assertEquals(SUSPENSION_HOURS, body["periodoSuspensao"].asInt)
        }
    }

    private fun provider(server: MockWebServer, builder: OkHttpClient.Builder = OkHttpClient.Builder()): LoteriasApiProvider {
        val base = server.url("/rest/v1/").newBuilder().scheme("https").build().toString()
        builder.addInterceptor { chain ->
            val request = chain.request()
            chain.proceed(request.newBuilder().url(request.url.newBuilder().scheme("http").build()).build())
        }
        return LoteriasApiProvider().apply {
            replace(NetworkClientFactory.create(base, AuthTokenProvider { "replacement-generic-B" }, builder))
        }
    }

    private fun profile() = MockResponse().setHeader("Content-Type", "application/json")
        .setBody("""{"payload":{"cpf":"12345678901"}}""")
}

private const val SUSPENSION_HOURS = 24
