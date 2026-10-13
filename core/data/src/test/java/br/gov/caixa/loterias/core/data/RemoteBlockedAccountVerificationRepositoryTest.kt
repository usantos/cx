package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.network.AuthTokenProvider
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import br.gov.caixa.loterias.core.network.NetworkClientFactory
import br.gov.caixa.loterias.core.network.BlockedAccountVerificationEnvelopeDto
import br.gov.caixa.loterias.core.network.MockBackendOptions
import br.gov.caixa.loterias.core.network.MockBackendScenario
import com.google.gson.Gson
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteBlockedAccountVerificationRepositoryTest {
    @Test fun `uses the legacy endpoint and rejects http errors and successful envelopes containing error metadata`() = runTest {
        for ((status, body, accepted) in listOf(
            Triple(200, "{}", true),
            Triple(200, "{\"payload\":null}", true),
            Triple(200, "{\"codigo\":\"private\"}", false),
            Triple(200, "{\"redirect\":\"login\"}", false),
            Triple(200, "{\"redirect\":\"\"}", false),
            Triple(401, "{}", false),
            Triple(403, "{}", false),
            Triple(500, "{}", false),
        )) {
            var calls = 0
            val api = NetworkClientFactory.createLegacy("https://example.invalid/rest/v1/", AuthTokenProvider { null },
                OkHttpClient.Builder().addInterceptor { chain ->
                    calls++
                    assertEquals("GET", chain.request().method)
                    assertEquals("/rest/v1/usuarios/verificar-apostador-bloqueado", chain.request().url.encodedPath)
                    Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1)
                        .code(status).message("Test").body(body.toResponseBody()).build()
                })
            val repository = RemoteBlockedAccountVerificationRepository(LegacyBackendApiProvider().apply { replace(api) })
            assertEquals("$status/$body", accepted, runCatching { repository.verifyBlockedAccount() }.isSuccess)
            assertEquals(1, calls)
        }
    }

    @Test fun `provider replacement is respected and missing provider never grants access`() = runTest {
        val provider = LegacyBackendApiProvider()
        val repository = RemoteBlockedAccountVerificationRepository(provider)
        assertTrue(runCatching { repository.verifyBlockedAccount() }.isFailure)
        fun api(fail: Boolean) = NetworkClientFactory.createLegacy("https://example.invalid/", AuthTokenProvider { null },
            OkHttpClient.Builder().addInterceptor { chain ->
                if (fail) throw IOException("private")
                Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1)
                    .code(200).message("Test").body("{}".toResponseBody()).build()
            })
        provider.replace(api(false))
        repository.verifyBlockedAccount()
        provider.replace(api(true))
        assertTrue(runCatching { repository.verifyBlockedAccount() }.exceptionOrNull() is IOException)
    }

    @Test fun `metadata dto preserves serialized fields but never exposes their contents in toString`() {
        val dto = Gson().fromJson("{\"codigo\":\"private-code\",\"redirect\":\"private-route\"}", BlockedAccountVerificationEnvelopeDto::class.java)
        assertEquals("private-code", dto.code)
        assertEquals("private-route", dto.redirect)
        assertFalse(dto.toString().contains("private"))
    }

    @Test fun `targeted mocks fail only the blocked check and preserve other endpoints`() = runTest {
        for (scenario in listOf(MockBackendScenario.POST_LOGIN_BLOCKED_CHECK_HTTP_ERROR, MockBackendScenario.POST_LOGIN_BLOCKED_CHECK_REDIRECT)) {
            val api = NetworkClientFactory.createLegacy("https://example.invalid/rest/v1/", AuthTokenProvider { null },
                mockOptions = MockBackendOptions(enabled = true, scenario = scenario))
            val repository = RemoteBlockedAccountVerificationRepository(LegacyBackendApiProvider().apply { replace(api) })
            assertTrue(runCatching { repository.verifyBlockedAccount() }.isFailure)
            assertTrue(runCatching { api.blockedUserStatus() }.isFailure == (scenario == MockBackendScenario.POST_LOGIN_BLOCKED_CHECK_HTTP_ERROR))
            // The fixture remains usable for other steps; no registration error is simulated here.
            val generalApi = NetworkClientFactory.create("https://example.invalid/rest/v1/", AuthTokenProvider { null },
                mockOptions = MockBackendOptions(enabled = true, scenario = scenario))
            assertTrue(generalApi.verifyAccount().payload != null)
        }
    }
}
