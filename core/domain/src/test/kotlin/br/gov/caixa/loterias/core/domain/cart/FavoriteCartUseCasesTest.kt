package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.common.result.AppResult
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrent
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.DeleteFavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartRepository
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartUsageRepository
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBets
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCarts
import br.gov.caixa.loterias.feature.cart.domain.usecase.MarkFavoriteCartAdded
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.WasFavoriteCartAdded

class FavoriteCartUseCasesTest {
    @Test
    fun `favorite cart use cases delegate valid commands`() = runTest {
        val repository = FakeFavoriteCartRepository()
        assertEquals(repository.carts, (GetFavoriteCarts(repository)() as AppResult.Success).value)
        assertEquals(repository.favoriteBets, (GetFavoriteCartBets(repository)(1) as AppResult.Success).value)
        assertEquals(repository.carts, (DeleteFavoriteCart(repository)(1) as AppResult.Success).value)
        assertEquals(repository.favoriteBets, (DeleteFavoriteCartBet(repository)(1, 2) as AppResult.Success).value)
        assertEquals(repository.cart, (AddFavoriteCartToCurrent(repository)(1, 2) as AppResult.Success).value)
        assertEquals(1L to 2L, repository.deletedBet)
        assertEquals(1L to 2, repository.imported)
    }

    @Test
    fun `favorite cart use cases reject invalid ids and selections`() = runTest {
        val repository = FakeFavoriteCartRepository()
        assertTrue(GetFavoriteCartBets(repository)(0) is AppResult.Failure)
        assertTrue(DeleteFavoriteCart(repository)(0) is AppResult.Failure)
        assertTrue(DeleteFavoriteCartBet(repository)(1, 0) is AppResult.Failure)
        assertTrue(DeleteFavoriteCartBet(repository)(0, 1) is AppResult.Failure)
        assertTrue(AddFavoriteCartToCurrent(repository)(0, 0) is AppResult.Failure)
        assertTrue(AddFavoriteCartToCurrent(repository)(1, -1) is AppResult.Failure)
        assertTrue(AddFavoriteCartToCurrent(repository)(1, 3) is AppResult.Failure)
    }

    @Test
    fun `favorite cart usage use cases preserve the local legacy rule`() = runTest {
        val repository = FakeFavoriteCartUsageRepository()
        val wasAdded = WasFavoriteCartAdded(repository)
        val markAdded = MarkFavoriteCartAdded(repository)

        assertFalse(wasAdded(1))
        markAdded(1)
        assertTrue(wasAdded(1))
        assertFalse(wasAdded(0))
        markAdded(0)
        assertFalse(repository.ids.contains(0))
    }

    private class FakeFavoriteCartRepository : FavoriteCartRepository {
        val carts = listOf(FavoriteCart(1, "Favorito"))
        val favoriteBets = listOf(FavoriteCartBet(2, "Mega-Sena", listOf(1, 2)))
        val cart = ShoppingCart(3, BigDecimal.TEN, emptyList())
        var deletedBet: Pair<Long, Long>? = null
        var imported: Pair<Long, Int>? = null
        override suspend fun favorites() = carts
        override suspend fun bets(cartId: Long) = favoriteBets
        override suspend fun deleteCart(cartId: Long) = carts
        override suspend fun deleteBet(cartId: Long, betId: Long): List<FavoriteCartBet> {
            deletedBet = cartId to betId
            return favoriteBets
        }
        override suspend fun addToCurrentCart(cartId: Long, specialSelection: Int): ShoppingCart {
            imported = cartId to specialSelection
            return cart
        }
    }

    private class FakeFavoriteCartUsageRepository : FavoriteCartUsageRepository {
        val ids = mutableSetOf<Long>()
        override suspend fun wasAddedToCurrentCart(cartId: Long): Boolean = cartId in ids
        override suspend fun markAsAddedToCurrentCart(cartId: Long) {
            ids += cartId
        }
        override suspend fun clear() {
            ids.clear()
        }
    }
}
