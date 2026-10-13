package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteBettingUseCasesTest {
    private val favorite = FavoriteBet(4, 2, "Mega-Sena", "Minha Mega", listOf(listOf(1, 2, 3, 4, 5, 6)))

    @Test fun `favorite derived properties and valid operations preserve parameters`() = runTest {
        val superSeven = favorite.copy(numberColumns = listOf(listOf(1), listOf(2)))
        assertEquals(listOf(1, 2), superSeven.numbers)
        assertTrue(superSeven.isSuperSeven)
        assertFalse(favorite.isSuperSeven)

        val repository = FakeRepository()
        assertEquals(listOf(favorite), (GetFavoriteBets(repository)(null) as AppResult.Success).value)
        assertTrue(DeleteFavoriteBet(repository)(4) is AppResult.Success)
        val options = FavoriteBetCartOptions(2, 3, true)
        assertEquals(9L, (AddFavoriteBetToCart(repository)(4, options) as AppResult.Success).value.id)
        assertEquals(4L, repository.deleted)
        assertEquals(options, repository.options)

        val draft = FavoriteBetDraft(2, "Mega-Sena", "Minha aposta", listOf(listOf(1, 2, 3, 4, 5, 6)))
        assertTrue(ValidateFavoriteBet(repository)(draft) is AppResult.Success)
        assertTrue(SaveFavoriteBet(repository)(draft) is AppResult.Success)
    }

    @Test fun `invalid identifiers contest and recurring values fail before repository`() = runTest {
        val repository = FakeRepository()
        assertFailure(GetFavoriteBets(repository)(0))
        assertFailure(DeleteFavoriteBet(repository)(0))
        assertFailure(AddFavoriteBetToCart(repository)(0, FavoriteBetCartOptions()))
        assertFailure(AddFavoriteBetToCart(repository)(1, FavoriteBetCartOptions(contestType = 3)))
        assertFailure(AddFavoriteBetToCart(repository)(1, FavoriteBetCartOptions(recurringDraws = -1)))
        assertFailure(ValidateFavoriteBet(repository)(FavoriteBetDraft(0, "", "", emptyList())))
        assertFailure(SaveFavoriteBet(repository)(FavoriteBetDraft(2, "Mega-Sena", "", listOf(listOf(1)))))
    }

    @Test fun `repository errors are mapped`() = runTest {
        val repository = FakeRepository(fail = true)
        assertFailure(GetFavoriteBets(repository)(2))
        assertFailure(DeleteFavoriteBet(repository)(4))
        assertFailure(AddFavoriteBetToCart(repository)(4, FavoriteBetCartOptions()))
        val draft = FavoriteBetDraft(2, "Mega-Sena", "Favorita", listOf(listOf(1)))
        assertFailure(ValidateFavoriteBet(repository)(draft))
        assertFailure(SaveFavoriteBet(repository)(draft))
    }

    private fun assertFailure(result: AppResult<*>) = assertTrue(result is AppResult.Failure)

    private inner class FakeRepository(private val fail: Boolean = false) : FavoriteBetRepository {
        var deleted: Long? = null
        var options: FavoriteBetCartOptions? = null
        private fun check() { if (fail) error("failure") }
        override suspend fun favorites(modalityId: Int?, offset: Int) = listOf(favorite).also { check() }
        override suspend fun delete(favoriteBetId: Long) { check(); deleted = favoriteBetId }
        override suspend fun addToCart(favoriteBetId: Long, options: FavoriteBetCartOptions) =
            ShoppingCart(9, BigDecimal.ZERO, emptyList()).also { check(); this.options = options }
        override suspend fun validate(draft: FavoriteBetDraft) { check() }
        override suspend fun save(draft: FavoriteBetDraft) { check() }
    }
}
