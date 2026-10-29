package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionContent
import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.cart.domain.model.CartCleanupResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartCode
import br.gov.caixa.loterias.feature.cart.domain.model.CartModality
import br.gov.caixa.loterias.feature.cart.domain.model.CartNumbers
import br.gov.caixa.loterias.feature.cart.domain.model.CartTransferPreparation
import br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository
import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartBet
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendApi
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendClient
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider
import br.gov.caixa.loterias.feature.account.data.dto.AccountVerificationRequestDto
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionExpiredException
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
import br.gov.caixa.loterias.feature.account.data.repository.PostLoginAccountRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.data.datasource.remote.PostLoginAccountDataSourceImpl

class PostLoginCartTransferTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val bet = LocalCartBet(1, null, CartModality.MEGA_SENA, CartCode("N", "NORMAL"), CartNumbers.Flat(listOf(1,2,3,4,5,6)), emptyList(), CartCode(0, "Não"), BigDecimal("6.00"), 3000, 0, 1, false, false, 3000, CartCode(0L, ""), false, null, null, 0, 6, 0, false, 0, null, null, emptyList(), emptyList())
    private var current = true
    private val calls = mutableListOf<String>()
    private var published: AuthenticatedSessionContent? = null
    private val content = object : AuthenticatedSessionContentRepository {
        override fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean { calls += "publish"; if (current) published = content; return current }
        override fun observe() = MutableStateFlow<AuthenticatedSessionContent?>(null)
    }
    private inner class Transfer : CartTransferRepository {
        var pending: CartTransferPreparation = CartTransferPreparation.Ready("receipt", listOf(bet))
        override suspend fun prepare(ownerKey: String): CartTransferPreparation { assertEquals(identity.ownerKey, ownerKey); calls += "prepare"; return pending }
        override suspend fun confirmReceived(ownerKey: String, transferId: String): Boolean { assertEquals("receipt", transferId); calls += "confirm"; return true }
        override suspend fun finishConfirmed(ownerKey: String, transferId: String): CartCleanupResult { calls += "cleanup"; return CartCleanupResult.Complete }
    }
    private fun repository(transfer: Transfer, response: suspend () -> String): PostLoginAccountRepositoryImpl {
        val provider = AccountVerificationBackendProvider().apply {
            replace(AccountVerificationBackendClient(object : AccountVerificationBackendApi {
                override suspend fun verify(request: AccountVerificationRequestDto, session: AccountVerificationSession): Response<ResponseBody> {
                    calls += "send"
                    if (transfer.pending is CartTransferPreparation.Ready) {
                        assertEquals(1, request.bets.size)
                        assertEquals("MEGA_SENA", request.bets.single().modality)
                        assertNull(request.bets.single().id)
                    } else assertTrue(request.bets.isEmpty())
                    return Response.success(response().toResponseBody("application/json".toMediaType()))
                }
            }, Gson()))
        }
        return PostLoginAccountRepositoryImpl(PostLoginAccountDataSourceImpl(provider, AccountVerificationSessionBinding { AccountVerificationSession({ if (current) "token" else null }, { current }) }), content, transfer, { current })
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
        transfer.pending = CartTransferPreparation.RequiresReview
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
