package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.domain.account.*
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.*
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class LocalCartReviewViewModelTest {
    @get:Rule val rule = MainDispatcherRule()
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private var active = true
    private var discards = 0
    private var sends = 0
    private var discardAccepted = true
    private var reviewAvailable = true
    private var nextQuery: AppResult<ShoppingCart>? = null
    private val contents = MutableStateFlow<AuthenticatedSessionContent?>(AuthenticatedSessionContent(null, null, false, true))
    private val sessions = object : AuthenticatedSessionIdentityProvider {
        override fun captureIdentity() = identity
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = active && identity === this@LocalCartReviewViewModelTest.identity
    }
    private val content = object : AuthenticatedSessionContentRepository {
        override fun observe() = contents
        override fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean {
            if (!sessions.isCurrent(identity)) return false
            contents.value = content; return true
        }
    }
    private val transfer = object : LegacyCartTransferRepository {
        override suspend fun prepare(ownerKey: String): LegacyCartTransferPreparation { sends++; error("Must not upload") }
        override suspend fun confirmReceived(ownerKey: String, transferId: String) = false
        override suspend fun finishConfirmed(ownerKey: String, transferId: String): LegacyCartCleanupResult = error("Must not acknowledge")
        override suspend fun review(ownerKey: String) = if (reviewAvailable) LegacyCartReview("receipt", emptyList()) else null
        override suspend fun discardReviewed(ownerKey: String, review: LegacyCartReview): Boolean { discards++; return discardAccepted }
    }
    private val empty = ShoppingCart(0, BigDecimal.ZERO, emptyList())
    private val local = object : VisitorCartRepository {
        override suspend fun current() = empty
        override suspend fun add(draft: BetDraft, parameters: BettingParameters): ShoppingCart = error("No add")
        override suspend fun remove(item: CartItem): ShoppingCart = error("No remove")
        override suspend fun clear(): ShoppingCart = error("No clear")
    }
    private fun model(result: AppResult<ShoppingCart> = AppResult.Success(empty)) = LocalCartReviewViewModel(sessions, content, transfer, local, GetCurrentCartUseCase { nextQuery ?: result }, FixedCoroutineDispatchers(rule.dispatcher))
    @Test fun `consultation never resends and explicit discard clears notice only after successful review`() = runTest(rule.dispatcher) {
        val model = model(); rule.dispatcher.scheduler.advanceUntilIdle()
        assertNotNull(model.state.value.remote); assertEquals(0, sends)
        model.discard(); rule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(model.state.value.discarded); assertEquals(1, discards)
        assertFalse(contents.value!!.localCartReviewRequired); assertEquals(0, sends)
    }
    @Test fun `failed remote query prevents discarding preserved local bets`() = runTest(rule.dispatcher) {
        val model = model(AppResult.Failure(AppError.Network)); rule.dispatcher.scheduler.advanceUntilIdle()
        assertNotNull(model.state.value.error)
        model.discard(); rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, discards); assertTrue(contents.value!!.localCartReviewRequired)
    }
    @Test fun `replaced session cannot discard or publish into the new account`() = runTest(rule.dispatcher) {
        val model = model(); rule.dispatcher.scheduler.advanceUntilIdle(); active = false
        model.discard(); rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, discards); assertTrue(contents.value!!.localCartReviewRequired)
    }
    @Test fun `retry after query failure permits comparison without clearing pending notice`() = runTest(rule.dispatcher) {
        val model = model(AppResult.Failure(AppError.Network)); rule.dispatcher.scheduler.advanceUntilIdle()
        nextQuery = AppResult.Success(empty)
        model.load(); rule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(model.state.value.error); assertNotNull(model.state.value.remote)
        assertTrue(contents.value!!.localCartReviewRequired); assertEquals(0, discards); assertEquals(0, sends)
    }
    @Test fun `changed receipt refuses deletion and retains pending notice`() = runTest(rule.dispatcher) {
        val model = model(); rule.dispatcher.scheduler.advanceUntilIdle(); discardAccepted = false
        model.discard(); rule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(model.state.value.discarded); assertNotNull(model.state.value.error)
        assertTrue(contents.value!!.localCartReviewRequired); assertEquals(0, sends)
    }
    @Test fun `account without own receipt can consult but cannot discard another owners batch`() = runTest(rule.dispatcher) {
        reviewAvailable = false
        val model = model(); rule.dispatcher.scheduler.advanceUntilIdle()
        assertNotNull(model.state.value.remote); assertNull(model.state.value.review)
        model.discard(); rule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, discards); assertEquals(0, sends); assertTrue(contents.value!!.localCartReviewRequired)
    }

}
