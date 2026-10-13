package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.*
import br.gov.caixa.loterias.core.domain.cart.*
import br.gov.caixa.loterias.core.network.*
import com.google.gson.Gson
import java.io.IOException
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class PostLoginCartTransferTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val bet = LegacyLocalCartBet(1, null, LegacyCartModality.MEGA_SENA, LegacyCartCode("N", "NORMAL"), LegacyCartNumbers.Flat(listOf(1,2,3,4,5,6)), emptyList(), LegacyCartCode(0, "Não"), BigDecimal("6.00"), 3000, 0, 1, false, false, 3000, LegacyCartCode(0L, ""), false, null, null, 0, 6, 0, false, 0, null, null, emptyList(), emptyList())
    private var current = true
    private val calls = mutableListOf<String>()
    private var published: AuthenticatedSessionContent? = null
    private val content = object : AuthenticatedSessionContentRepository {
        override fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean { calls += "publish"; if (current) published = content; return current }
        override fun observe() = MutableStateFlow<AuthenticatedSessionContent?>(null)
    }
    private inner class Transfer : LegacyCartTransferRepository {
        var pending: LegacyCartTransferPreparation = LegacyCartTransferPreparation.Ready("receipt", listOf(bet))
        override suspend fun prepare(ownerKey: String): LegacyCartTransferPreparation { assertEquals(identity.ownerKey, ownerKey); calls += "prepare"; return pending }
        override suspend fun confirmReceived(ownerKey: String, transferId: String): Boolean { assertEquals("receipt", transferId); calls += "confirm"; return true }
        override suspend fun finishConfirmed(ownerKey: String, transferId: String): LegacyCartCleanupResult { calls += "cleanup"; return LegacyCartCleanupResult.Complete }
    }
    private fun repository(transfer: Transfer, response: suspend () -> String): RemotePostLoginAccountRepository {
        val provider = AccountVerificationBackendProvider().apply {
            replace(AccountVerificationBackendClient(object : AccountVerificationBackendApi {
                override suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession): Response<ResponseBody> {
                    calls += "send"
                    if (transfer.pending is LegacyCartTransferPreparation.Ready) {
                        assertEquals(1, request.bets.size)
                        assertEquals("MEGA_SENA", request.bets.single().modality)
                        assertNull(request.bets.single().id)
                    } else assertTrue(request.bets.isEmpty())
                    return Response.success(response().toResponseBody("application/json".toMediaType()))
                }
            }, Gson()))
        }
        return RemotePostLoginAccountRepository(RetrofitPostLoginAccountDataSource(provider, AccountVerificationSessionBinding { AccountVerificationSession({ if (current) "token" else null }, { current }) }), content, transfer, { current })
    }
    @Test fun `accepted cart is sent published confirmed and cleaned once`() = runTest {
        repository(Transfer()) { "{\"payload\":{\"carrinho\":{\"id\":4,\"apostas\":[]}}}" }.verifyAccess(identity)
        assertEquals(listOf("prepare", "send", "publish", "confirm", "cleanup"), calls)
    }
    @Test fun `metadata errors never clean and account redirects keep the batch for review`() = runTest {
        assertTrue(runCatching { repository(Transfer()) { "{\"codigo\":\"E1\",\"payload\":{}}" }.verifyAccess(identity) }.isFailure)
        assertFalse("confirm" in calls); assertFalse("cleanup" in calls)
        calls.clear()
        val access = repository(Transfer()) { "{\"redirect\":\"aceitar-termo-de-uso\"}" }.verifyAccess(identity)
        assertEquals(PostLoginDestination.TERMS, access.destination)
        assertFalse("confirm" in calls); assertFalse("cleanup" in calls)
        calls.clear()
        repository(Transfer()) { "{\"payload\":{}}" }.verifyAccess(identity)
        assertTrue(published!!.localCartReviewRequired)
        assertFalse("confirm" in calls); assertFalse("cleanup" in calls)
    }
    @Test fun `lost response allows subsequent account consultation without resending any bet`() = runTest {
        val transfer = Transfer()
        var lost = true
        val repository = repository(transfer) {
            if (lost) throw IOException("lost response")
            "{\"payload\":{\"carrinho\":{\"id\":4,\"apostas\":[]}}}"
        }
        assertTrue(runCatching { repository.verifyAccess(identity) }.isFailure)
        transfer.pending = LegacyCartTransferPreparation.RequiresReview
        lost = false
        assertEquals(PostLoginDestination.HOME, repository.verifyAccess(identity).destination)
        assertTrue(published!!.localCartReviewRequired)
        assertEquals(listOf("prepare", "send", "prepare", "send", "publish"), calls)
        assertFalse("confirm" in calls); assertFalse("cleanup" in calls)
    }
    @Test fun `replacement session does not acknowledge or delete an old callback`() = runTest {
        val repository = repository(Transfer()) { current = false; "{\"payload\":{\"carrinho\":{\"id\":4}}}" }
        assertTrue(runCatching { repository.verifyAccess(identity) }.exceptionOrNull() is AccountVerificationSessionExpiredException)
        assertFalse("confirm" in calls); assertFalse("cleanup" in calls)
    }
}
