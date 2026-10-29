package br.gov.caixa.loterias.feature.account.data.repository

import org.junit.Assert.*
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.NotificationReadApi
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import br.gov.caixa.loterias.core.network.api.NotificationReadEnvelopeDto
import br.gov.caixa.loterias.feature.account.data.dto.PushRegistrationEnvelopeDto
import br.gov.caixa.loterias.feature.account.data.dto.PushRegistrationPayloadDto
import br.gov.caixa.loterias.feature.account.data.dto.PushRegistrationRequestDto
import br.gov.caixa.loterias.core.network.api.UnreadNotificationsEnvelopeDto
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.test.runTest
import org.junit.Test
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Tag
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.data.datasource.remote.PushRegistrationRemoteDataSourceImpl
import br.gov.caixa.loterias.core.data.datasource.remote.PushRegistrationRemoteDataSource
import br.gov.caixa.loterias.core.data.repository.PushRegistrationRepositoryImpl

class PushRegistrationRepositoryImplTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val session = AccountVerificationSession({ "synthetic" }, { true })
    private var response = PushRegistrationEnvelopeDto(PushRegistrationPayloadDto("200"))
    private var request: PushRegistrationRequestDto? = null
    private val api = object : NotificationReadApi {
        override suspend fun registerPushDevice(body: PushRegistrationRequestDto, session: AccountVerificationSession): PushRegistrationEnvelopeDto {
            assertSame(this@PushRegistrationRepositoryImplTest.session, session)
            request = body
            return response
        }
        override suspend fun unread(session: AccountVerificationSession): Response<UnreadNotificationsEnvelopeDto> = error("unused")
        override suspend fun markRead(notificationId: Long, session: AccountVerificationSession): Response<NotificationReadEnvelopeDto> = error("unused")
    }
    private val repository = PushRegistrationRepositoryImpl(PushRegistrationRemoteDataSourceImpl(
        NotificationReadApiProvider().apply { replace(api) },
        AccountVerificationSessionBinding { assertSame(identity, it); session },
    ))

    @Test fun `repository forwards the captured user and preserves data source acknowledgement`() = runTest {
        var confirmed = false
        val repository = PushRegistrationRepositoryImpl(PushRegistrationRemoteDataSource { captured, cpf, token ->
            assertSame(identity, captured)
            assertEquals(1234567890L, cpf)
            assertEquals("synthetic-fcm", token)
            confirmed
        })
        assertFalse(repository.register(identity, 1234567890L, "synthetic-fcm"))
        confirmed = true
        assertTrue(repository.register(identity, 1234567890L, "synthetic-fcm"))
    }

    @Test fun `device registration serializes exact legacy contract with captured session`() = runTest {
        assertTrue(repository.register(identity, 1234567890L, "synthetic-fcm"))
        assertEquals(JsonParser.parseString("""{"identificadores":[1234567890],"token":"synthetic-fcm","plataforma":"android","canal":"loterias"}"""), Gson().toJsonTree(request))
        assertEquals("PushRegistrationRequestDto(redacted)", request.toString())
        val method = NotificationReadApi::class.java.methods.single { it.name == "registerPushDevice" }
        assertEquals("push/registros", method.getAnnotation(POST::class.java).value)
        assertTrue(method.parameterAnnotations[1].single() is Tag)
    }

    @Test fun `missing business acknowledgement and envelope failures never confirm submission`() = runTest {
        for (json in listOf("{}", "{\"payload\":{}}", "{\"payload\":{\"codigo\":\"500\"}}",
            "{\"payload\":{\"codigo\":\"200\"},\"codigo\":\"E1\"}",
            "{\"payload\":{\"codigo\":\"200\"},\"redirect\":\"login\"}",
            "{\"payload\":{\"codigo\":\"200\"},\"errosValidacao\":[{}]}")) {
            response = Gson().fromJson(json, PushRegistrationEnvelopeDto::class.java)
            assertFalse(repository.register(identity, 1234567890L, "synthetic-fcm"))
        }
        response = Gson().fromJson("{\"payload\":{\"codigo\":\"200\"},\"redirect\":null,\"errosValidacao\":[]}", PushRegistrationEnvelopeDto::class.java)
        assertTrue(repository.register(identity, 1234567890L, "synthetic-fcm"))
    }
}
