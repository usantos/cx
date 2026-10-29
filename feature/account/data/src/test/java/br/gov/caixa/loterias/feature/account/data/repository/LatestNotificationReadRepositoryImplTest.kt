package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.NotificationReadResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.feature.account.data.dto.AccountVerificationRequestDto
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.core.network.client.NetworkClientFactory
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions
import br.gov.caixa.loterias.core.network.interceptor.NetworkLogSink
import br.gov.caixa.loterias.core.network.api.NotificationReadApi
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import br.gov.caixa.loterias.core.network.api.NotificationReadEnvelopeDto
import br.gov.caixa.loterias.feature.account.data.dto.PushRegistrationEnvelopeDto
import br.gov.caixa.loterias.feature.account.data.dto.PushRegistrationRequestDto
import br.gov.caixa.loterias.core.network.api.UnreadNotificationsEnvelopeDto
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response
import br.gov.caixa.loterias.feature.account.data.datasource.remote.LatestNotificationReadRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.account.data.repository.LatestNotificationReadRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding

class LatestNotificationReadRepositoryImplTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val session = AccountVerificationSession({ "fixture" }, { true })
    private val binding = AccountVerificationSessionBinding { assertSame(identity, it); session }

    @Test fun `read endpoint is session bound one shot redacted and preserves pin rotation provider policy`() = runTest {
        val requests = mutableListOf<okhttp3.Request>()
        val logs = mutableListOf<String>()
        val builder = OkHttpClient.Builder().addInterceptor { chain -> chain.proceed(chain.request()).also { requests += it.request } }
        val api = NetworkClientFactory.createNotificationRead("https://mock.caixa.gov.br/rest/v1/", builder,
            debugOptions = NetworkDebugOptions(true, NetworkLogSink { logs += it }), mockOptions = MockBackendOptions(true))
        val provider = NotificationReadApiProvider()
        assertTrue(runCatching { provider.current() }.isFailure)
        provider.replace(api); assertSame(api, provider.current())
        assertEquals(NotificationReadResult.Confirmed, LatestNotificationReadRepositoryImpl(LatestNotificationReadRemoteDataSourceImpl(provider, binding)).markRead(identity, 7))
        val request = requests.single()
        assertEquals("PUT", request.method)
        assertEquals("/rest/v1/historico-notificacoes/7/visualiza", request.url.encodedPath)
        assertEquals("Bearer fixture", request.header("Authorization"))
        assertSame(session, request.tag(AccountVerificationSession::class.java))
        assertTrue(request.body!!.isOneShot())
        assertTrue(logs.any { "curl" in it }); assertTrue(logs.none { "fixture" in it })
        assertFalse(builder.build().followRedirects); assertFalse(builder.build().followSslRedirects)
        assertFalse(builder.build().retryOnConnectionFailure)
        val dto = NotificationReadEnvelopeDto("code", JsonParser.parseString("\"redirect\""), emptyList())
        assertEquals(dto, dto.copy()); assertEquals(dto.hashCode(), dto.copy().hashCode())
        val (code, redirect, errors) = dto
        assertEquals("code", code); assertEquals(dto.redirect, redirect); assertEquals(emptyList<Any>(), errors)
        assertEquals("NotificationReadEnvelopeDto(redacted)", dto.toString())
    }

    @Test fun `only confirmed response clears errors cannot be hidden by 200 and 204 is accepted`() = runTest {
        for (json in listOf("{}", "{\"redirect\":null,\"errosValidacao\":[]}")) {
            repository(Response.success(Gson().fromJson(json, NotificationReadEnvelopeDto::class.java))).markRead(identity, 7)
        }
        repository(Response.success<NotificationReadEnvelopeDto>(204, null)).markRead(identity, 7)
        for (json in listOf("{\"codigo\":\"E1\"}", "{\"codigo\":\"\"}", "{\"redirect\":\"login\"}", "{\"errosValidacao\":[{}]}")) {
            assertTrue(runCatching { repository(Response.success(Gson().fromJson(json, NotificationReadEnvelopeDto::class.java))).markRead(identity, 7) }.isFailure)
        }
        assertTrue(runCatching { repository(Response.success(null)).markRead(identity, 7) }.isFailure)
    }

    @Test fun `http errors missing setup and invalid id never imply successful reading`() = runTest {
        for (code in listOf(400, 401, 404, 500, 503)) {
            val result = repository(Response.error(code, "private".toResponseBody())).markRead(identity, 7)
            assertTrue(result is NotificationReadResult.Rejected)
            assertFalse(result.toString().contains("private"))
        }
        assertTrue(runCatching { repository(Response.success(NotificationReadEnvelopeDto())).markRead(identity, 0) }.isFailure)
        assertTrue(runCatching { LatestNotificationReadRepositoryImpl(LatestNotificationReadRemoteDataSourceImpl(NotificationReadApiProvider(), binding)).markRead(identity, 7) }.isFailure)
    }

    @Test fun `http decisions preserve redirects login consent business messages and server diagnostics`() = runTest {
        suspend fun outcome(status: Int, json: String): ServiceFailure =
            (repository(Response.error(status, json.toResponseBody())).markRead(identity, 7) as NotificationReadResult.Rejected).failure
        val routes = mapOf("login" to ServiceRedirect.LOGIN, "aceitar-termo-de-uso" to ServiceRedirect.ACCEPT_TERMS,
            "cadastrar-apostador" to ServiceRedirect.REGISTER_ACCOUNT, "indisponibilidade" to ServiceRedirect.UNAVAILABLE,
            "carrinho" to ServiceRedirect.CART, "carrinhos-favoritos" to ServiceRedirect.FAVORITE_CARTS)
        for ((directive, destination) in routes) {
            assertEquals(ServiceFailure.RedirectRequired(destination), outcome(401, "{\"redirect\":\"$directive\"}"))
        }
        assertEquals(ServiceFailure.Unchanged, outcome(500, "{\"redirect\":\"/carrinhos/verifica-compra-processamento\"}"))
        assertEquals(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED)), outcome(400, "{\"redirect\":\"unknown\"}"))
        assertEquals(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED)), outcome(401, "{}"))
        for (code in listOf("011010", "011011", "011012")) {
            assertEquals(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.MORE_BETS_REQUIRED, "Mais apostas", code)),
                outcome(400, "{\"codigo\":\"$code\",\"mensagem\":\"Mais apostas\"}"))
            assertEquals(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Mais apostas", code)),
                outcome(404, "{\"codigo\":\"$code\",\"mensagem\":\"Mais apostas\"}"))
        }
        assertEquals(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.INVALID_IDENTIFICATION, null, "2067")),
            outcome(500, "{\"codigo\":\"2067\"}"))
        assertEquals(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.SERVER_MESSAGE, "Mensagem", "X")),
            outcome(500, "{\"codigo\":\"X\",\"tipo\":1,\"mensagem\":\"Mensagem\"}"))
        assertEquals(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.SERVER_FAILURE)), outcome(500, "{}"))
        assertEquals(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Tempo de resposta esgotou")), outcome(408, "{}"))
        assertEquals(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED)), outcome(400, "{\"codigo\":\"018003\"}"))
    }

    @Test fun `connection failures are typed and cancellation is never converted to a notice`() = runTest {
        for ((failure, expected) in listOf(java.net.SocketTimeoutException() to ServiceConnectionFailure.TIMEOUT,
            java.io.IOException() to ServiceConnectionFailure.CONNECTION)) {
            val provider = NotificationReadApiProvider().apply { replace(object : NotificationReadApi {
            override suspend fun registerPushDevice(body: PushRegistrationRequestDto, session: AccountVerificationSession): PushRegistrationEnvelopeDto = error("Not used by this test")
                override suspend fun unread(session: AccountVerificationSession): Response<UnreadNotificationsEnvelopeDto> = error("Unused GET in PUT fixture")
                override suspend fun markRead(notificationId: Long, session: AccountVerificationSession): Response<NotificationReadEnvelopeDto> = throw failure
            }) }
            assertEquals(NotificationReadResult.Rejected(ServiceFailure.ConnectionFailed(expected)),
                LatestNotificationReadRepositoryImpl(LatestNotificationReadRemoteDataSourceImpl(provider, binding)).markRead(identity, 7))
        }
        val cancellation = kotlinx.coroutines.CancellationException("cancelled")
        val provider = NotificationReadApiProvider().apply { replace(object : NotificationReadApi {
            override suspend fun registerPushDevice(body: PushRegistrationRequestDto, session: AccountVerificationSession): PushRegistrationEnvelopeDto = error("Not used by this test")
                override suspend fun unread(session: AccountVerificationSession): Response<UnreadNotificationsEnvelopeDto> = error("Unused GET in PUT fixture")
            override suspend fun markRead(notificationId: Long, session: AccountVerificationSession): Response<NotificationReadEnvelopeDto> = throw cancellation
        }) }
        assertSame(cancellation, runCatching { LatestNotificationReadRepositoryImpl(LatestNotificationReadRemoteDataSourceImpl(provider, binding)).markRead(identity, 7) }.exceptionOrNull())
    }

    private fun repository(response: Response<NotificationReadEnvelopeDto>): LatestNotificationReadRepositoryImpl {
        val provider = NotificationReadApiProvider().apply { replace(object : NotificationReadApi {
            override suspend fun registerPushDevice(body: PushRegistrationRequestDto, session: AccountVerificationSession): PushRegistrationEnvelopeDto = error("Not used by this test")
                override suspend fun unread(session: AccountVerificationSession): Response<UnreadNotificationsEnvelopeDto> = error("Unused GET in PUT fixture")
            override suspend fun markRead(notificationId: Long, session: AccountVerificationSession) = response
        }) }
        return LatestNotificationReadRepositoryImpl(LatestNotificationReadRemoteDataSourceImpl(provider, binding))
    }

    @Test fun `notification mocks preserve post login and affect only the read endpoint without retries`() = runTest {
        val cases = mapOf(
            MockBackendScenario.NOTIFICATION_READ_LOGIN_REQUIRED to ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED)),
            MockBackendScenario.NOTIFICATION_READ_TERMS_REQUIRED to ServiceFailure.RedirectRequired(ServiceRedirect.ACCEPT_TERMS),
            MockBackendScenario.NOTIFICATION_READ_CART_REDIRECT to ServiceFailure.RedirectRequired(ServiceRedirect.CART),
            MockBackendScenario.NOTIFICATION_READ_BUSINESS_RULE to ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Não foi possível confirmar a leitura.", "mock-notification")),
            MockBackendScenario.NOTIFICATION_READ_SERVER_MESSAGE to ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.SERVER_MESSAGE, "Leitura temporariamente indisponível.", "mock-notification")),
            MockBackendScenario.NOTIFICATION_READ_TIMEOUT to ServiceFailure.ConnectionFailed(ServiceConnectionFailure.TIMEOUT),
            MockBackendScenario.NOTIFICATION_READ_CONNECTION_FAILURE to ServiceFailure.ConnectionFailed(ServiceConnectionFailure.CONNECTION),
        )
        for ((scenario, expected) in cases) {
            val options = MockBackendOptions(true, scenario)
            val base = "https://mock.caixa.gov.br/rest/v1/"
            val account = NetworkClientFactory.createAccountVerification(base, mockOptions = options)
                .verify(AccountVerificationRequestDto(), session)
            assertEquals(91L, account.payload!!.latestNotification!!.id)
            assertFalse(account.payload!!.unidentifiedPayment!!)
            var calls = 0
            val builder = OkHttpClient.Builder().addInterceptor { chain -> calls++; chain.proceed(chain.request()) }
            val provider = NotificationReadApiProvider().apply { replace(NetworkClientFactory.createNotificationRead(base, builder, mockOptions = options)) }
            assertEquals(NotificationReadResult.Rejected(expected), LatestNotificationReadRepositoryImpl(LatestNotificationReadRemoteDataSourceImpl(provider, binding)).markRead(identity, 91))
            assertEquals(1, calls)
        }
    }
}
