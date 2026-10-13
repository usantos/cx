package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.common.AppResult
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartUseCasesTest {
    @Test
    fun `cart use cases delegate load removal and clear`() = runTest {
        val repository = FakeCartRepository()
        assertEquals(cart(), (GetCurrentCart(repository)() as AppResult.Success).value)
        val item = cart().items.single()
        assertEquals(cart(), (RemoveCartItem(repository)(item) as AppResult.Success).value)
        assertEquals(item, repository.removed)
        assertEquals(cart(), (ClearCart(repository)() as AppResult.Success).value)
        assertTrue(repository.cleared)
        assertTrue(ValidateCart(repository)() is AppResult.Success)
        assertTrue(ValidateFavoriteCartName(repository)(" Favorito ") is AppResult.Success)
        assertEquals("Favorito", repository.validatedFavorite)
        assertTrue(SaveCurrentCartFavorite(repository)(" Favorito ") is AppResult.Success)
        assertEquals("Favorito", repository.savedFavorite)
    }

    @Test
    fun `remove rejects item without backend id`() = runTest {
        val invalid = CartItem(0, CartItemKind.COMBO, "Combo", "", BigDecimal.ZERO)
        assertTrue(RemoveCartItem(FakeCartRepository())(invalid) is AppResult.Failure)
        assertTrue(ValidateFavoriteCartName(FakeCartRepository())(" ") is AppResult.Failure)
        assertTrue(SaveCurrentCartFavorite(FakeCartRepository())("") is AppResult.Failure)
    }

    private class FakeCartRepository : CartRepository {
        var removed: CartItem? = null
        var cleared = false
        var validatedFavorite: String? = null
        var savedFavorite: String? = null
        override suspend fun current() = cart()
        override suspend fun remove(item: CartItem): ShoppingCart { removed = item; return cart() }
        override suspend fun clear(): ShoppingCart { cleared = true; return cart() }
        override suspend fun validate() = CartValidationResult.Accepted
        override suspend fun validateFavoriteName(name: String) { validatedFavorite = name }
        override suspend fun saveFavorite(name: String) { savedFavorite = name }
    }

    companion object {
        private fun cart() = ShoppingCart(
            1,
            BigDecimal("5"),
            listOf(CartItem(2, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "01  02", BigDecimal("5"))),
        )
    }
}
