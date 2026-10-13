package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.domain.betting.*
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class SessionCartRepositoryTest {
    private var authenticated = false
    private val localCart = ShoppingCart(0, BigDecimal.TEN, emptyList())
    private val remoteCart = ShoppingCart(5, BigDecimal.ONE, emptyList())
    private val calls = mutableListOf<String>()
    private val local = object : VisitorCartRepository {
        override suspend fun current() = localCart.also { calls += "local-current" }
        override suspend fun add(draft: BetDraft, parameters: BettingParameters) = localCart.also { calls += "local-add" }
        override suspend fun remove(item: CartItem) = localCart.also { calls += "local-remove" }
        override suspend fun clear() = localCart.also { calls += "local-clear" }
    }
    private val remote = object : CartRepository {
        override suspend fun current() = remoteCart.also { calls += "remote-current" }
        override suspend fun remove(item: CartItem) = remoteCart.also { calls += "remote-remove" }
        override suspend fun clear() = remoteCart.also { calls += "remote-clear" }
        override suspend fun validate() = CartValidationResult.Accepted.also { calls += "remote-validate" }
        override suspend fun validateFavoriteName(name: String) { calls += "remote-name" }
        override suspend fun saveFavorite(name: String) { calls += "remote-favorite" }
    }
    @Test fun `visitor reads removes and clears local cart while login switches all operations`() = runTest {
        val repository = SessionCartRepository(remote, local) { authenticated }
        val item = CartItem(1, CartItemKind.INDIVIDUAL_BET, "", "", BigDecimal.TEN)
        assertSame(localCart, repository.current())
        repository.remove(item); repository.clear()
        assertTrue(repository.validate() is CartValidationResult.Rejected)
        assertTrue(runCatching { repository.saveFavorite("Teste") }.exceptionOrNull() is IllegalArgumentException)
        assertEquals(listOf("local-current", "local-remove", "local-clear"), calls)
        authenticated = true
        assertSame(remoteCart, repository.current())
        repository.remove(item); repository.clear(); repository.validate(); repository.saveFavorite("Teste")
        assertEquals(listOf("remote-current", "remote-remove", "remote-clear", "remote-validate", "remote-favorite"), calls.drop(3))
    }
    @Test fun `visitor inclusion uses public parameters and login uses remote without local fallback`() = runTest {
        var failRemote = false
        val betting = object : BettingRepository {
            override suspend fun modalities() = emptyList<LotteryModality>()
            override suspend fun parameters() = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 10, emptyList()).also { calls += "parameters" }
            override suspend fun combos() = emptyList<BetCombo>()
            override suspend fun addBet(draft: BetDraft): ShoppingCart { calls += "remote-add"; if (failRemote) error("Failure"); return remoteCart }
            override suspend fun addCombo(comboId: Int, quantity: Int, surprise: Boolean) = remoteCart
        }
        val repository = SessionBettingRepository(betting, local) { authenticated }
        val draft = BetDraft(2, numbers = listOf(1, 2, 3, 4, 5, 6))
        assertSame(localCart, repository.addBet(draft))
        assertEquals(listOf("parameters", "local-add"), calls)
        authenticated = true
        assertSame(remoteCart, repository.addBet(draft))
        failRemote = true
        assertTrue(runCatching { repository.addBet(draft) }.isFailure)
        assertEquals(listOf("parameters", "local-add", "remote-add", "remote-add"), calls)
    }
}
