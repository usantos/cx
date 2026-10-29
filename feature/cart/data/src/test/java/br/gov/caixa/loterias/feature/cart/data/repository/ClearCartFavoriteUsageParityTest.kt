package br.gov.caixa.loterias.feature.cart.data.repository

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.cart.data.datasource.local.FavoriteCartUsageLocalDataSource
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartValidationResult
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository
import br.gov.caixa.loterias.feature.cart.domain.usecase.ClearCart
import java.io.IOException
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClearCartFavoriteUsageParityTest {
    @Test fun `successful clear allows previously imported favorites to be added again`() = runTest {
        val usage = usageWithImportedFavorites()
        val result = ClearCart(CartStub { emptyCart() }, usage)()
        assertTrue(result is AppResult.Success)
        assertFalse(usage.wasAddedToCurrentCart(42))
        assertFalse(usage.wasAddedToCurrentCart(77))
    }

    @Test fun `failed clear preserves previously imported favorite warnings`() = runTest {
        val usage = usageWithImportedFavorites()
        val result = ClearCart(CartStub { throw IOException("clear failed") }, usage)()
        assertTrue(result is AppResult.Failure)
        assertTrue(usage.wasAddedToCurrentCart(42))
        assertTrue(usage.wasAddedToCurrentCart(77))
    }

    @Test fun `cancelled clear preserves previously imported favorite warnings`() = runTest {
        val usage = usageWithImportedFavorites()
        val request = launch { ClearCart(CartStub { throw CancellationException("ended session") }, usage)() }
        request.join()
        assertTrue(request.isCancelled)
        assertTrue(usage.wasAddedToCurrentCart(42))
        assertTrue(usage.wasAddedToCurrentCart(77))
    }

    @Test fun `late success after cancellation cannot reset imported favorite warnings`() = runTest {
        val usage = usageWithImportedFavorites()
        val request = launch {
            ClearCart(CartStub {
                currentCoroutineContext().cancel()
                emptyCart()
            }, usage)()
        }
        request.join()
        assertTrue(request.isCancelled)
        assertTrue(usage.wasAddedToCurrentCart(42))
        assertTrue(usage.wasAddedToCurrentCart(77))
    }

    private suspend fun usageWithImportedFavorites() = FavoriteCartUsageRepositoryImpl(MemoryStore()).apply {
        markAsAddedToCurrentCart(42)
        markAsAddedToCurrentCart(77)
    }

    private class MemoryStore : FavoriteCartUsageLocalDataSource {
        private val values = mutableMapOf<String, Set<Long>>()
        override suspend fun read(key: String): Set<Long> = values[key].orEmpty()
        override suspend fun update(key: String, transform: (Set<Long>) -> Set<Long>) {
            values[key] = transform(values[key].orEmpty())
        }
    }

    private class CartStub(private val clearResult: suspend () -> ShoppingCart) : CartRepository {
        override suspend fun clear() = clearResult()
        override suspend fun current(): ShoppingCart = error("No reload after clear")
        override suspend fun remove(item: CartItem): ShoppingCart = error("No individual removal")
        override suspend fun validate(): CartValidationResult = error("No checkout")
        override suspend fun validateFavoriteName(name: String): Unit = error("No favorite mutation")
        override suspend fun saveFavorite(name: String): Unit = error("No favorite mutation")
    }

    companion object {
        private fun emptyCart() = ShoppingCart(5, BigDecimal.ZERO, emptyList())
    }
}
