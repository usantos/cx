package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.core.testing.repository.FakeSessionContentRepository
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.model.AccountStatus
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccess
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccess
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.data.dto.AccountSessionDto
import com.google.gson.Gson
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import okhttp3.OkHttpClient
import java.lang.reflect.Proxy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSource
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationRemoteData
import br.gov.caixa.loterias.feature.account.data.repository.AccountRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.repository.BlockedAccountVerificationRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.repository.PostLoginAccountRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.data.datasource.remote.PostLoginAccountDataSourceImpl

class AccountAccessResponseTest {
    @Test fun `absent registration payload and unknown redirects never grant home access`() = runTest {
        for (response in listOf(
            AccountVerificationRemoteData(null, null),
            AccountVerificationRemoteData(AccountSessionDto(), ""),
            AccountVerificationRemoteData(AccountSessionDto(), "unknown"),
            AccountVerificationRemoteData(AccountSessionDto(), "login"),
            AccountVerificationRemoteData(AccountSessionDto(), "https://untrusted.invalid"),
        )) {
            val result = VerifyAccountAccess(repository(response))()
            assertEquals(AppResult.Failure(AppError.Unexpected), result)
        }
    }

    @Test fun `recognized registration redirects can omit the payload but home requires it`() = runTest {
        for ((redirect, expected) in mapOf(
            "cadastrar-apostador" to PostLoginDestination.REGISTRATION,
            " ATIVAR-CADASTRO " to PostLoginDestination.REGISTRATION,
            "aceitar-termo-de-uso" to PostLoginDestination.TERMS,
        )) {
            assertEquals(expected, repository(AccountVerificationRemoteData(null, redirect)).verifyAccess().destination)
        }
        assertEquals(PostLoginDestination.HOME,
            repository(AccountVerificationRemoteData(AccountSessionDto(), null)).verifyAccess().destination)
        val assessment = repository(AccountVerificationRemoteData(AccountSessionDto(requiresSelfAssessment = true, temporarySuspension = true), null)).verifyAccess()
        assertEquals(PostLoginDestination.SELF_ASSESSMENT, assessment.destination)
        assertTrue(assessment.temporarySuspension)
    }

    @Test fun `legacy nested account status is mapped without treating absent or unknown status as active`() = runTest {
        for ((kind, expected) in mapOf(
            "ATIVO" to AccountStatus.ACTIVE,
            "BLOQUEADO" to AccountStatus.BLOCKED,
            "BLOQUEADO_TOTAL" to AccountStatus.TOTALLY_BLOCKED,
            "BLOQUEADO_PARCIAL" to AccountStatus.PARTIALLY_BLOCKED,
            "NEW_BACKEND_STATUS" to AccountStatus.UNKNOWN,
            "" to AccountStatus.UNKNOWN,
        )) {
            val session = Gson().fromJson("""{"situacaoApostador":{"id":7,"descricao":"ignored","enum":"$kind"}}""", AccountSessionDto::class.java)
            assertEquals(kind, session.status?.kind)
            for (redirect in listOf(null, "cadastrar-apostador", "aceitar-termo-de-uso")) {
                assertEquals(expected, repository(AccountVerificationRemoteData(session, redirect)).verifyAccess().status)
            }
        }
        for (json in listOf("{}", "{\"situacaoApostador\":null}", "{\"situacaoApostador\":{}}")) {
            val session = Gson().fromJson(json, AccountSessionDto::class.java)
            assertEquals(null, repository(AccountVerificationRemoteData(session, null)).verifyAccess().status)
        }
    }

    @Test fun `partial account fixture travels through both real Retrofit contracts in legacy order`() = runTest {
        val requests = java.util.concurrent.CopyOnWriteArrayList<String>()
        fun builder() = OkHttpClient.Builder().addInterceptor { chain ->
            requests += "${chain.request().method} ${chain.request().url.encodedPath}"
            chain.proceed(chain.request()).also { response ->
                if (response.request.method == "POST") {
                    assertEquals("Bearer fixture", response.request.header("Authorization"))
                    assertTrue(response.request.body!!.isOneShot())
                    assertTrue(response.request.tag(AccountVerificationSession::class.java) != null)
                }
            }
        }
        val mock = MockBackendOptions(enabled = true, scenario = MockBackendScenario.ACCOUNT_PARTIALLY_BLOCKED)
        val api = NetworkClientFactory.createAccountVerification("https://example.invalid/rest/v1/", builder(), mockOptions = mock)
        val legacy = NetworkClientFactory.createLegacy("https://example.invalid/rest/v1/", AuthTokenProvider { null }, builder(), mockOptions = mock)
        val identity = AuthenticatedSessionIdentity("a".repeat(64))
        val sessions = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = identity
            override fun isCurrent(candidate: AuthenticatedSessionIdentity) = candidate === identity
        }
        val remote = PostLoginAccountDataSourceImpl(AccountVerificationBackendProvider().apply { replace(api) },
            AccountVerificationSessionBinding { captured ->
                assertTrue(captured === identity)
                AccountVerificationSession({ "fixture" }, { sessions.isCurrent(captured) })
            })
        val verify = VerifyPostLoginAccess(
            BlockedAccountVerificationRepositoryImpl(BackendApiProvider().apply { replace(legacy) }),
            PostLoginAccountRepositoryImpl(remote, FakeSessionContentRepository()),
            sessions,
        )
        val result = verify() as PostLoginAccessResult.Verified
        assertEquals(AccountStatus.PARTIALLY_BLOCKED, result.access.status)
        assertEquals(PostLoginDestination.HOME, result.access.destination)
        assertEquals(listOf(
            "GET /rest/v1/usuarios/verificar-apostador-bloqueado",
            "POST /rest/v1/logado/usuarios/verificar-cadastro",
        ), requests)
    }

    private fun repository(response: AccountVerificationRemoteData): AccountRepositoryImpl {
        val source = Proxy.newProxyInstance(AccountRemoteDataSource::class.java.classLoader,
            arrayOf(AccountRemoteDataSource::class.java)) { _, method, _ ->
            check(method.name == "verifyAccount")
            response
        } as AccountRemoteDataSource
        return AccountRepositoryImpl(source)
    }
}
