package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AccountStatus
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.network.*
import com.google.gson.Gson
import java.io.IOException
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class RemotePostLoginAccountRepositoryTest {
    @Test fun `business error code or validation failures cannot be erased by HTTP 200 or redirect`() = runTest {
        for (metadata in listOf("\"codigo\":\"E1\"", "\"codigo\":\"\"", "\"codigo\":\"000000\"",
            "\"errosValidacao\":[\"Falha privada\"]")) {
            for (redirect in listOf("null", "\"cadastrar-apostador\"", "\"aceitar-termo-de-uso\"")) {
                val error = runCatching { repository("""{$metadata,"redirect":$redirect,"payload":{}}""")
                    .verifyAccess(identity) }.exceptionOrNull()
                assertTrue(error is IllegalStateException)
                assertFalse(error.toString().contains("Falha privada"))
            }
        }
    }

    @Test fun `accepted payload maps access and nullable validation list without losing suspension or status`() = runTest {
        for (validation in listOf("null", "[]")) {
            val access = repository("""{"errosValidacao":$validation,"payload":{
                "limiteDiario":123.45,"suspensaoTemporariaApostador":true,"responderAutoavaliacao":true,
                "situacaoApostador":{"enum":"BLOQUEADO_PARCIAL"}}}""").verifyAccess(identity)
            assertEquals(PostLoginDestination.SELF_ASSESSMENT, access.destination)
            assertEquals(AccountStatus.PARTIALLY_BLOCKED, access.status)
            assertEquals(BigDecimal("123.45"), access.dailyLimit)
            assertTrue(access.temporarySuspension)
        }
        assertEquals(PostLoginDestination.HOME, repository("""{"payload":{}}""").verifyAccess(identity).destination)
    }

    @Test fun `known redirects may omit payload while empty payload and unknown redirects never grant access`() = runTest {
        for ((redirect, expected) in mapOf("cadastrar-apostador" to PostLoginDestination.REGISTRATION,
            " ATIVAR-CADASTRO " to PostLoginDestination.REGISTRATION,
            "aceitar-termo-de-uso" to PostLoginDestination.TERMS)) {
            assertEquals(expected, repository("""{"redirect":"$redirect"}""").verifyAccess(identity).destination)
        }
        for (json in listOf("{}", "{\"payload\":null}", "{\"redirect\":\"\"}",
            "{\"redirect\":\"https://untrusted.invalid\",\"payload\":{}}")) {
            assertTrue(runCatching { repository(json).verifyAccess(identity) }.exceptionOrNull() is IllegalStateException)
        }
    }

    @Test fun `network failure cancellation and expired identity are not hidden or retried`() = runTest {
        for (failure in listOf(IOException("lost"), CancellationException("cancelled"))) {
            var calls = 0
            val provider = AccountVerificationBackendProvider().apply {
                replace(AccountVerificationBackendClient(object : AccountVerificationBackendApi {
                    override suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession): Response<ResponseBody> {
                        calls++
                        throw failure
                    }
                }, Gson()))
            }
            val remote = RetrofitPostLoginAccountDataSource(provider, binding)
            assertSame(failure, runCatching { RemotePostLoginAccountRepository(remote, FakeSessionContentRepository()).verifyAccess(identity) }.exceptionOrNull())
            assertEquals(1, calls)
        }
        val repo = repository("""{"payload":{}}""")
        val forged = AuthenticatedSessionIdentity(identity.ownerKey)
        assertTrue(runCatching { repo.verifyAccess(forged) }.exceptionOrNull() is AccountVerificationSessionExpiredException)
    }

    @Test fun `post login confirms and cleans transfer only after accepted cart for current identity`() = runTest {
        val calls = mutableListOf<String>()
        val transfer = object : br.gov.caixa.loterias.core.domain.cart.LegacyCartTransferRepository {
            override suspend fun prepare(ownerKey: String) = br.gov.caixa.loterias.core.domain.cart.LegacyCartTransferPreparation.CleanupPending("receipt")
            override suspend fun confirmReceived(ownerKey: String, transferId: String): Boolean { error("Already confirmed") }
            override suspend fun finishConfirmed(ownerKey: String, transferId: String): br.gov.caixa.loterias.core.domain.cart.LegacyCartCleanupResult {
                calls += "cleanup"
                return br.gov.caixa.loterias.core.domain.cart.LegacyCartCleanupResult.Complete
            }
        }
        val provider = AccountVerificationBackendProvider().apply {
            replace(AccountVerificationBackendClient(object : AccountVerificationBackendApi {
                override suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession): Response<ResponseBody> {
                    calls += "verify"
                    assertTrue(request.bets.isEmpty())
                    return Response.success("{\"payload\":{}}".toResponseBody("application/json".toMediaType()))
                }
            }, Gson()))
        }
        RemotePostLoginAccountRepository(RetrofitPostLoginAccountDataSource(provider, binding), FakeSessionContentRepository(), transfer, { it === identity }).verifyAccess(identity)
        assertEquals(listOf("cleanup", "verify"), calls)
    }

    private val identity = AuthenticatedSessionIdentity("f".repeat(64))
    private val binding = AccountVerificationSessionBinding { captured ->
        AccountVerificationSession({ if (captured === identity) "fixture" else null }, { captured === identity })
    }

    private fun repository(json: String, content: FakeSessionContentRepository = FakeSessionContentRepository()): RemotePostLoginAccountRepository {
        val provider = AccountVerificationBackendProvider().apply {
            replace(AccountVerificationBackendClient(object : AccountVerificationBackendApi {
                override suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession): Response<ResponseBody> {
                    // Local transfer is deliberately not enabled merely by connecting this repository.
                    assertTrue(request.bets.isEmpty())
                    return Response.success(json.toResponseBody("application/json".toMediaType()))
                }
            }, Gson()))
        }
        return RemotePostLoginAccountRepository(RetrofitPostLoginAccountDataSource(provider, binding), content)
    }

    @Test fun `post login applies cart notification and payment flag together to the captured owner`() = runTest {
        val store = FakeSessionContentRepository()
        repository("""{"payload":{"carrinho":{"id":21,"valorTotal":12.5,
            "apostasIndividuais":[{"id":31,"modalidade":"MEGA_SENA","valor":12.5,"numerosSelecionados":[1,2,3,4,5,6]}]},
            "ultimaNotificacao":{"id":7,"titulo":"Título","conteudo":"Corpo","imagem":"YmFzZTY0","link":"https://example.invalid/"},
            "pagamentoNaoIdentificado":true}}""", store).verifyAccess(identity)
        assertSame(identity, store.owner)
        assertEquals(1, store.publications)
        val snapshot = store.content.value!!
        val restoredCart = snapshot.cart!!
        assertEquals(21L, restoredCart.id)
        assertEquals(BigDecimal("12.5"), restoredCart.total)
        assertEquals(31L, restoredCart.items.single().id)
        assertTrue(snapshot.unidentifiedPayment)
        with(snapshot.latestNotification!!) {
            assertEquals(7L, id); assertEquals("Título", title); assertEquals("Corpo", content)
            assertEquals("YmFzZTY0", imageBase64); assertEquals("https://example.invalid/", link)
            assertEquals("AccountNotification(redacted)", toString())
            assertEquals(this, copy()); assertEquals(hashCode(), copy().hashCode())
            val (a,b,c,d,e) = this
            assertEquals(listOf(id,title,content,imageBase64,link), listOf(a,b,c,d,e))
        }
        assertEquals(snapshot, snapshot.copy()); assertEquals(snapshot.hashCode(), snapshot.copy().hashCode())
        assertEquals("AuthenticatedSessionContent(redacted)", snapshot.toString())
        val (cart, notification, payment) = snapshot
        assertEquals(snapshot.cart, cart); assertEquals(snapshot.latestNotification, notification); assertTrue(payment)
    }

    @Test fun `absent fields clear stale metadata redirects publish no content and rejected publication denies access`() = runTest {
        val store = FakeSessionContentRepository()
        for (json in listOf("""{"payload":{}}""", """{"payload":{"pagamentoNaoIdentificado":false}}""")) {
            repository(json, store).verifyAccess(identity)
            assertNull(store.content.value!!.cart)
            assertNull(store.content.value!!.latestNotification)
            assertFalse(store.content.value!!.unidentifiedPayment)
        }
        repository("""{"redirect":"aceitar-termo-de-uso","payload":{"pagamentoNaoIdentificado":true}}""", store).verifyAccess(identity)
        assertNull(store.content.value)
        store.accepts = false
        assertTrue(runCatching { repository("""{"payload":{}}""", store).verifyAccess(identity) }.exceptionOrNull()
            is AccountVerificationSessionExpiredException)
        assertNull(store.content.value)
        val before = store.publications
        assertTrue(runCatching { repository("""{"codigo":"E1","payload":{}}""", store).verifyAccess(identity) }.isFailure)
        assertEquals(before, store.publications)
    }

    @Test fun `post login publishes flat cart summary and refuses incomplete reservation without replacing session content`() = runTest {
        val store = FakeSessionContentRepository()
        repository("""{"payload":{"carrinho":{"id":21,"valorTotal":999,
            "apostas":[{"id":3,"valor":4.25},{"id":4,"valor":5}],
            "combos":[{"id":8,"apostas":[{"id":3,"valor":4.25},{"id":4,"valor":5}]}]}}}""", store).verifyAccess(identity)
        val accepted = store.content.value
        assertEquals(BigDecimal("9.25"), accepted!!.cart!!.total)
        assertEquals(2, accepted.cart!!.betCount); assertEquals(1, accepted.cart!!.items.size)
        val calls = store.publications
        assertTrue(runCatching { repository("""{"payload":{"carrinho":{"apostas":[{"reservaCotaBolao":{}}]}}}""", store).verifyAccess(identity) }.isFailure)
        assertSame(accepted, store.content.value); assertEquals(calls, store.publications)
    }
}
