package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionExpiredException
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.dto.account.AccountRegistrationDto
import br.gov.caixa.loterias.core.network.dto.account.MunicipalityIdDto
import br.gov.caixa.loterias.core.network.dto.account.PersonalDataUpdateDto
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.AccountRegistration
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.usecase.AccountFormUseCasesFactory
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

private const val FORM_MUTATION_COUNT = 3
private const val SESSION_OWNER_KEY_LENGTH = 64
private const val SYNTHETIC_STATE_ID = 7L

/** Somente transporte HTTP local sintético; nenhum cadastro ou conta real é alterado. */
class AccountFormSessionTransportTest {
    @Test fun `form source acquired before login replacement cannot write with replacement bearer without a binding`() = runTest {
        MockWebServer().use { server ->
            repeat(FORM_MUTATION_COUNT) { server.enqueue(response()) }
            var genericToken = "form-login-A"
            val source = AccountRemoteDataSourceImpl(provider(server) { genericToken })
            genericToken = "replacement-login-B"
            val failures = listOf(
                runCatching { source.updatePersonalData(update()) }.exceptionOrNull(),
                runCatching { source.registerAccount(registration()) }.exceptionOrNull(),
                runCatching { source.recalculateAuthorizedLimit() }.exceptionOrNull()
            )
            assertTrue(
                "Every account form write must fail closed without its captured owner",
                failures.all {
                    it is AccountVerificationSessionExpiredException
                }
            )
            assertEquals("The replacement bearer must never reach account form endpoints", 0, server.requestCount)
        }
    }

    @Test fun `form factory captures identity before work and expired owner never falls back to replacement bearer`() = runTest {
        MockWebServer().use { server ->
            var current = true
            var genericToken = "form-login-A"
            val identity = AuthenticatedSessionIdentity("a".repeat(SESSION_OWNER_KEY_LENGTH))
            val acquisitions = mutableListOf<AuthenticatedSessionIdentity>()
            val factory = AccountFormUseCasesFactory(
                SessionAccountRepositoryFactory(
                    provider(server) { genericToken },
                    AccountVerificationSessionBinding {
                        acquisitions += it
                        AccountVerificationSession({ if (current) "captured-login-A" else null }, { current })
                    }
                )
            )
            val form = factory.forSession(identity)
            assertEquals(listOf(identity), acquisitions)
            current = false
            genericToken = "replacement-login-B"
            assertTrue(form.updatePersonalData(domainUpdate(), profile()) is AppResult.Failure)
            assertTrue(form.registerAccount(domainRegistration()) is AppResult.Failure)
            assertTrue(form.recalculateAuthorizedLimit() is AppResult.Failure)
            assertEquals(listOf(identity), acquisitions)
            assertEquals(0, server.requestCount)
        }
    }

    @Test fun `all three form operations deliver captured tag bearer and unchanged remote contracts`() = runTest {
        MockWebServer().use { server ->
            repeat(FORM_MUTATION_COUNT) { server.enqueue(response()) }
            val session = AccountVerificationSession({ "captured-login-A" }, { true })
            val tags = mutableListOf<AccountVerificationSession?>()
            val builder = OkHttpClient.Builder().addInterceptor { chain ->
                tags += chain.request().tag(AccountVerificationSession::class.java)
                chain.proceed(chain.request())
            }
            val factory = AccountFormUseCasesFactory(
                SessionAccountRepositoryFactory(
                    provider(server, builder) { "unrelated-generic-B" },
                    AccountVerificationSessionBinding { session }
                )
            )
            val form = factory.forSession(AuthenticatedSessionIdentity("a".repeat(SESSION_OWNER_KEY_LENGTH)))
            assertEquals(AppResult.Success(Unit), form.updatePersonalData(domainUpdate(), profile()))
            assertEquals(AppResult.Success(Unit), form.registerAccount(domainRegistration()))
            assertEquals(AppResult.Success(BigDecimal("200")), form.recalculateAuthorizedLimit())
            val requests = List(FORM_MUTATION_COUNT) { server.takeRequest() }
            assertEquals(listOf("PUT", "POST", "POST"), requests.map { it.method })
            assertEquals(
                listOf("/rest/v1/usuarios", "/rest/v1/usuarios", "/rest/v1/usuarios/recalcular-limite-autorizado"),
                requests.map { it.path }
            )
            requests.forEach { assertEquals("Bearer captured-login-A", it.getHeader("Authorization")) }
            tags.forEach { assertSame(session, it) }
            assertEquals(FORM_MUTATION_COUNT, tags.size)
            val updateBody = com.google.gson.JsonParser.parseString(requests[0].body.readUtf8()).asJsonObject
            assertEquals("70000000", updateBody["cep"].asString)
            assertEquals(BigDecimal.TEN, updateBody["limiteDiario"].asBigDecimal)
            val registrationBody = com.google.gson.JsonParser.parseString(requests[1].body.readUtf8()).asJsonObject
            assertTrue(registrationBody["aceitaTermosUso"].asBoolean)
            assertEquals(0, registrationBody["apostas"].asJsonArray.size())
        }
    }

    @Test fun `session replacement after domain validation but before interception prevents the request`() = runTest {
        MockWebServer().use { server ->
            var current = true
            val builder = OkHttpClient.Builder().addInterceptor { chain ->
                current = false
                chain.proceed(chain.request())
            }
            val factory = AccountFormUseCasesFactory(
                SessionAccountRepositoryFactory(
                    provider(server, builder) { "replacement-login-B" },
                    AccountVerificationSessionBinding { AccountVerificationSession({ "captured-login-A" }, { current }) }
                )
            )
            val form = factory.forSession(AuthenticatedSessionIdentity("a".repeat(SESSION_OWNER_KEY_LENGTH)))
            assertTrue(form.updatePersonalData(domainUpdate(), profile()) is AppResult.Failure)
            assertEquals(0, server.requestCount)
        }
    }

    @Test fun `anonymous form never binds a current login or emits any mutation`() = runTest {
        MockWebServer().use { server ->
            var bindings = 0
            val factory = AccountFormUseCasesFactory(
                SessionAccountRepositoryFactory(
                    provider(server) { "replacement-login-B" },
                    AccountVerificationSessionBinding {
                        bindings++
                        error("No identity authorizes binding")
                    }
                )
            )
            val form = factory.forSession(null)
            assertTrue(form.updatePersonalData(domainUpdate(), profile()) is AppResult.Failure)
            assertTrue(form.registerAccount(domainRegistration()) is AppResult.Failure)
            assertTrue(form.recalculateAuthorizedLimit() is AppResult.Failure)
            assertEquals(0, bindings)
            assertEquals(0, server.requestCount)
        }
    }

    @Test fun `replacement during response rejects result from the original form`() = runTest {
        MockWebServer().use { server ->
            server.enqueue(response())
            var current = true
            val builder = OkHttpClient.Builder().addNetworkInterceptor { chain ->
                chain.proceed(chain.request()).also { current = false }
            }
            val factory = AccountFormUseCasesFactory(
                SessionAccountRepositoryFactory(
                    provider(server, builder) { "replacement-login-B" },
                    AccountVerificationSessionBinding { AccountVerificationSession({ "captured-login-A" }, { current }) }
                )
            )
            val form = factory.forSession(AuthenticatedSessionIdentity("a".repeat(SESSION_OWNER_KEY_LENGTH)))
            assertTrue(form.recalculateAuthorizedLimit() is AppResult.Failure)
            assertEquals(1, server.requestCount)
            assertEquals("Bearer captured-login-A", server.takeRequest().getHeader("Authorization"))
        }
    }

    private fun provider(
        server: MockWebServer,
        builder: OkHttpClient.Builder = OkHttpClient.Builder(),
        token: () -> String,
    ): LoteriasApiProvider {
        val base = server.url("/rest/v1/").newBuilder().scheme("https").build().toString()
        builder.addInterceptor { chain ->
            val request = chain.request()
            chain.proceed(request.newBuilder().url(request.url.newBuilder().scheme("http").build()).build())
        }
        return LoteriasApiProvider().apply {
            replace(NetworkClientFactory.create(base, AuthTokenProvider(token), builder))
        }
    }

    private fun response() = MockResponse().setHeader("Content-Type", "application/json")
        .setBody("""{"payload":{"limiteDiarioAutorizado":200}}""")

    private fun update() = PersonalDataUpdateDto(
        "70000000",
        MunicipalityIdDto(SYNTHETIC_STATE_ID, 1, 0),
        "M",
        dailyLimit = BigDecimal.TEN,
        acceptsNotifications = false
    )

    private fun registration() = AccountRegistrationDto("70000000", true, false, MunicipalityIdDto(SYNTHETIC_STATE_ID, 1, 0), "M")

    private fun domainUpdate() =
        PersonalDataUpdate("70000000", Gender.MALE, MunicipalityId(SYNTHETIC_STATE_ID, 1, 0), BigDecimal.TEN, false)
    private fun domainRegistration() = AccountRegistration("70000000", Gender.MALE, MunicipalityId(SYNTHETIC_STATE_ID, 1, 0), true, false)
    private fun profile() = AccountProfile(
        "", "", "", "70000000", Gender.MALE, MunicipalityId(SYNTHETIC_STATE_ID, 1, 0), "DF", "Brasília",
        BigDecimal.TEN, BigDecimal.ZERO, BigDecimal("200"), false
    )
}
