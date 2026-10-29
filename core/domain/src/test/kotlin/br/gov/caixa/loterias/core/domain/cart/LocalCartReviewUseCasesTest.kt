package br.gov.caixa.loterias.core.domain.cart

import org.junit.Assert.*
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionContent
import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartNumbers
import br.gov.caixa.loterias.feature.cart.domain.model.CartCode
import br.gov.caixa.loterias.feature.cart.domain.model.CartModality
import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartBet
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.usecase.DiscardLocalCartReviewUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetLocalCartReviewUseCase
import br.gov.caixa.loterias.feature.cart.domain.model.CartCleanupResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartReview
import br.gov.caixa.loterias.feature.cart.domain.model.CartTransferPreparation
import br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LocalCartReviewUseCasesTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private var active = true
    private var review: CartReview? = CartReview("receipt", emptyList())
    private var discardAccepted = true
    private var discards = 0
    private var published = 0
    private var afterDiscard: () -> Unit = {}
    private val empty = ShoppingCart(1, BigDecimal.ZERO, emptyList())
    private val contents = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(null, null, false, true))
    private val sessions = object : AuthenticatedSessionIdentityProvider {
        override fun captureIdentity() = identity
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = active && identity === this@LocalCartReviewUseCasesTest.identity
    }
    private val content = object : AuthenticatedSessionContentRepository {
        override fun observe() = contents
        override fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean {
            published++; contents.value = content; return true
        }
    }
    private val transfer = object : CartTransferRepository {
        override suspend fun prepare(ownerKey: String): CartTransferPreparation = error("Must not send")
        override suspend fun confirmReceived(ownerKey: String, transferId: String) = error("Must not confirm")
        override suspend fun finishConfirmed(ownerKey: String, transferId: String): CartCleanupResult = error("Must not finish")
        override suspend fun review(ownerKey: String) = review
        override suspend fun discardReviewed(ownerKey: String, review: CartReview): Boolean { discards++; afterDiscard(); return discardAccepted }
    }
    private val local = object : VisitorCartRepository {
        override suspend fun current() = empty.copy(items = listOf(CartItem(10, CartItemKind.INDIVIDUAL_BET, "", "", BigDecimal.TEN), CartItem(11, CartItemKind.INDIVIDUAL_BET, "", "", BigDecimal.ONE)))
        override suspend fun add(draft: BetDraft, parameters: BettingParameters): ShoppingCart = error("Must not add")
        override suspend fun remove(item: CartItem): ShoppingCart = error("Must not remove")
        override suspend fun clear(): ShoppingCart = error("Must not clear")
    }
    private fun load(query: GetCurrentCartUseCase = GetCurrentCartUseCase { AppResult.Success(empty) }) =
        GetLocalCartReviewUseCase(sessions, transfer, local, query)

    @Test fun `missing receipt excludes every local item and recomputes totals`() = runTest {
        review = null
        val result = load()(identity)
        assertNull(result.review)
        assertTrue(result.local.items.isEmpty())
        assertEquals(BigDecimal.ZERO, result.local.total)
        assertEquals(0, result.local.totalBetCount)
        assertEquals(empty, result.remote)
    }

    @Test fun `receipt keeps only its own pending ids and recomputes amounts and bet count`() = runTest {
        val bet = LocalCartBet(
            10, null, CartModality.MEGA_SENA, CartCode("NORMAL", null),
            CartNumbers.Flat(listOf(1, 2, 3, 4, 5, 6)), null, CartCode(0, null),
            BigDecimal.TEN, 1, 0, 1, false, false, 1, CartCode(0L, null), false,
            null, null, 0, 6, 0, false, 0, null, null, emptyList(), emptyList(),
        )
        review = CartReview("receipt", listOf(bet))
        val result = load()(identity)
        assertEquals(listOf(10L), result.local.items.map { it.id })
        assertEquals(BigDecimal.TEN, result.local.total)
        assertEquals(1, result.local.totalBetCount)
        assertSame(review, result.review)
        assertEquals(0, discards)
    }

    @Test fun `session replaced after deletion cannot receive publication in its new content`() = runTest {
        afterDiscard = { active = false }
        DiscardLocalCartReviewUseCase(sessions, transfer, content)(identity, requireNotNull(review))
        assertEquals(1, discards)
        assertEquals(0, published)
        assertTrue(contents.value!!.localCartReviewRequired)
    }

    @Test fun `expired identity before or after query never returns another sessions data`() = runTest {
        active = false
        assertFails { load()(identity) }
        active = true
        assertFails { load(GetCurrentCartUseCase { active = false; AppResult.Success(empty) })(identity) }
        assertFails { load()(null) }
    }

    @Test fun `query failure and cancellation propagate without sending or deleting`() = runTest {
        assertFails { load(GetCurrentCartUseCase { AppResult.Failure(AppError.Network) })(identity) }
        try { load(GetCurrentCartUseCase { throw CancellationException("cancel") })(identity); fail() }
        catch (expected: CancellationException) { assertEquals("cancel", expected.message) }
        assertEquals(0, discards)
    }

    @Test fun `explicit discard clears only the current content notice`() = runTest {
        DiscardLocalCartReviewUseCase(sessions, transfer, content)(identity, requireNotNull(review))
        assertEquals(1, discards)
        assertEquals(1, published)
        assertFalse(contents.value!!.localCartReviewRequired)
    }

    @Test fun `expired session or changed review refuses deletion and publication`() = runTest {
        active = false
        assertFails { DiscardLocalCartReviewUseCase(sessions, transfer, content)(identity, requireNotNull(review)) }
        assertEquals(0, discards)
        active = true; discardAccepted = false
        assertFails { DiscardLocalCartReviewUseCase(sessions, transfer, content)(identity, requireNotNull(review)) }
        assertEquals(1, discards)
        assertEquals(0, published)
        assertTrue(contents.value!!.localCartReviewRequired)
    }

    private suspend fun assertFails(block: suspend () -> Unit) {
        try { block(); fail("Expected failed identity/review/query guard") }
        catch (_: IllegalStateException) { }
    }
}
