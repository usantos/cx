package br.gov.caixa.loterias.feature.notifications.data.repository

import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.NotificationInboxApi
import br.gov.caixa.loterias.core.network.api.NotificationInboxApiProvider
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.notifications.data.datasource.remote.NotificationInboxRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.notifications.data.datasource.remote.NotificationInboxSessionBinding
import br.gov.caixa.loterias.feature.notifications.data.dto.NotificationInboxEnvelopeDto
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class NotificationInboxRepositoryImplTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val session = AccountVerificationSession({ "captured-token" }, { true })
    private val api = FakeApi()
    private val provider = NotificationInboxApiProvider().apply { replace(api) }
    private val repository = NotificationInboxRepositoryImpl(
        NotificationInboxRemoteDataSourceImpl(
            provider,
            NotificationInboxSessionBinding {
                assertSame(identity, it)
                session
            }
        )
    )

    @Test fun `maps ordered server histories and read date`() = runTest {
        api.all = listResponse(
            """{"payload":[
                {"id":7,"titulo":"private-title","conteudo":"private-content","dataVisualizacao":"2026-10-25"},
                {"id":3,"titulo":"second","conteudo":"body","dataVisualizacao":null}
            ]}"""
        )
        api.unread = listResponse("""{"payload":[{"id":3,"titulo":"second","conteudo":"body"}]}""")
        val inbox = repository.load(identity)
        assertEquals(listOf(FIRST_ID, SECOND_ID), inbox.all.map { it.id })
        assertTrue(inbox.all.first().read)
        assertFalse(inbox.unread.single().read)
        assertEquals("private-title", inbox.all.first().title)
        assertEquals("private-content", inbox.all.first().content)
        assertFalse(inbox.toString().contains("private"))
        assertFalse(inbox.all.first().toString().contains("private"))
        assertEquals("NotificationInboxEnvelopeDto(redacted)", api.all.body().toString())
        assertEquals(listOf("all", "unread"), api.calls)
    }

    @Test fun `missing malformed and business payloads never become an empty inbox`() = runTest {
        for (json in listOf(
            "{}", "null", "{\"payload\":null}", "{\"payload\":{}}", "{\"payload\":[{}]}",
            """{"payload":[],"codigo":"E1"}""", """{"payload":[],"redirect":"login"}""",
            """{"payload":[],"errosValidacao":[{}]}""", """{"payload":[{"id":1.2,"titulo":"t","conteudo":"c"}]}""",
            """{"payload":[{"id":3,"titulo":"t","conteudo":"c"},{"id":3,"titulo":"duplicate","conteudo":"c"}]}"""
        )) {
            api.all = listResponse(json)
            assertTrue(json, runCatching { repository.load(identity) }.isFailure)
        }
        api.all = Response.error(HTTP_UNAVAILABLE, "private-error".toResponseBody())
        assertTrue(runCatching { repository.load(identity) }.isFailure)
        api.all = listResponse("{\"payload\":[]}")
        api.unread = listResponse("{}")
        assertTrue(runCatching { repository.load(identity) }.isFailure)
    }

    @Test fun `mutations accept only 200 or 204 without business failure`() = runTest {
        repository.markRead(identity, MUTATION_ID)
        api.mutation = Response.success("".toResponseBody())
        repository.markRead(identity, NEXT_MUTATION_ID)
        api.mutation = Response.success<ResponseBody>(HTTP_NO_CONTENT, null)
        repository.remove(identity, MUTATION_ID)
        repository.clear(identity)
        assertEquals(listOf("read:9", "read:10", "remove:9", "clear"), api.calls)
        for (json in listOf("""{"codigo":"E"}""", """{"redirect":"login"}""", """{"errosValidacao":[{}]}""", "malformed")) {
            api.mutation = Response.success(json.toResponseBody())
            assertTrue(runCatching { repository.clear(identity) }.isFailure)
        }
        api.mutation = Response.success(HTTP_ACCEPTED, "{}".toResponseBody())
        assertTrue(runCatching { repository.clear(identity) }.isFailure)
    }

    @Test fun `load keeps captured client across provider replacement and later mutation rotates`() = runTest {
        val replacement = FakeApi()
        api.afterAll = { provider.replace(replacement) }
        repository.load(identity)
        assertEquals(listOf("all", "unread"), api.calls)
        assertTrue(replacement.calls.isEmpty())
        repository.clear(identity)
        assertEquals(listOf("clear"), replacement.calls)
    }

    private fun listResponse(json: String) = Response.success(Gson().fromJson(json, NotificationInboxEnvelopeDto::class.java))

    private inner class FakeApi : NotificationInboxApi {
        var all = listResponse("{\"payload\":[]}")
        var unread = listResponse("{\"payload\":[]}")
        var mutation: Response<ResponseBody> = Response.success("{}".toResponseBody())
        val calls = mutableListOf<String>()
        var afterAll: () -> Unit = {}
        override suspend fun all(session: AccountVerificationSession) = all.also {
            assertSame(this@NotificationInboxRepositoryImplTest.session, session)
            calls += "all"
            afterAll()
        }
        override suspend fun unread(session: AccountVerificationSession) = unread.also {
            assertSame(this@NotificationInboxRepositoryImplTest.session, session)
            calls += "unread"
        }
        override suspend fun markRead(id: Long, session: AccountVerificationSession) = mutation.also {
            assertSame(this@NotificationInboxRepositoryImplTest.session, session)
            calls += "read:$id"
        }
        override suspend fun remove(id: Long, session: AccountVerificationSession) = mutation.also {
            assertSame(this@NotificationInboxRepositoryImplTest.session, session)
            calls += "remove:$id"
        }
        override suspend fun clear(session: AccountVerificationSession) = mutation.also {
            assertSame(this@NotificationInboxRepositoryImplTest.session, session)
            calls += "clear"
        }
    }
    private companion object {
        const val FIRST_ID = 7L
        const val SECOND_ID = 3L
        const val MUTATION_ID = 9L
        const val NEXT_MUTATION_ID = 10L
        const val HTTP_UNAVAILABLE = 503
        const val HTTP_NO_CONTENT = 204
        const val HTTP_ACCEPTED = 202
    }
}
