package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.*
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Tag

class RemoteUnreadNotificationCountRepositoryTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val session = AccountVerificationSession({ "synthetic-token" }, { true })

    @Test fun `GET uses installed endpoint with only bound session tag`() {
        val method = NotificationReadApi::class.java.methods.single { it.name == "unread" }
        assertEquals("historico-notificacoes/nao-visualizadas", method.getAnnotation(GET::class.java).value)
        assertEquals(2, method.parameterCount) // session and coroutine continuation
        assertTrue(method.parameterAnnotations[0].single() is Tag)
    }

    @Test fun `payload count comes from list and binding receives captured identity`() = runTest {
        for ((json, count) in listOf("{\"payload\":[]}" to 0,
            "{\"payload\":[{\"id\":1},{\"id\":2}]}" to 2, "{}" to 0)) {
            var calls = 0
            val repository = repository(Response.success(Gson().fromJson(json, UnreadNotificationsEnvelopeDto::class.java))) { calls++ }
            assertEquals(count, repository.getCount(identity))
            assertEquals(1, calls)
        }
        assertEquals("UnreadNotificationsEnvelopeDto(redacted)", UnreadNotificationsEnvelopeDto().toString())
    }

    @Test fun `HTTP and business failure do not count a supplied list`() = runTest {
        assertTrue(runCatching { repository(Response.error(500, "{}".toResponseBody())).getCount(identity) }.isFailure)
        for (json in listOf("{\"payload\":[{}],\"codigo\":\"E1\"}",
            "{\"payload\":[{}],\"redirect\":\"login\"}", "{\"payload\":[{}],\"errosValidacao\":[{}]}")) {
            assertTrue(runCatching { repository(Response.success(Gson().fromJson(json, UnreadNotificationsEnvelopeDto::class.java))).getCount(identity) }.isFailure)
        }
        assertEquals(0, repository(Response.success(null)).getCount(identity))
    }

    private fun repository(response: Response<UnreadNotificationsEnvelopeDto>, onCall: () -> Unit = {}): RemoteUnreadNotificationCountRepository {
        val provider = NotificationReadApiProvider().apply { replace(object : NotificationReadApi {
            override suspend fun registerPushDevice(body: PushRegistrationRequestDto, session: AccountVerificationSession): PushRegistrationEnvelopeDto = error("Not used by this test")
            override suspend fun unread(session: AccountVerificationSession): Response<UnreadNotificationsEnvelopeDto> {
                assertSame(this@RemoteUnreadNotificationCountRepositoryTest.session, session)
                onCall(); return response
            }
            override suspend fun markRead(notificationId: Long, session: AccountVerificationSession): Response<NotificationReadEnvelopeDto> = error("Unused PUT")
        }) }
        return RemoteUnreadNotificationCountRepository(provider, AccountVerificationSessionBinding {
            assertSame(identity, it); session
        })
    }
}
