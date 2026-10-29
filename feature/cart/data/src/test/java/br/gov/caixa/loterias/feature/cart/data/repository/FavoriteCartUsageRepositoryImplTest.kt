package br.gov.caixa.loterias.feature.cart.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.coroutines.test.runTest
import br.gov.caixa.loterias.feature.cart.data.datasource.local.FavoriteCartUsageLocalDataSource
import br.gov.caixa.loterias.feature.cart.data.repository.FavoriteCartUsageRepositoryImpl

class FavoriteCartUsageRepositoryImplTest {
    @Test
    fun `persists every favorite cart transformed by the user`() = runTest {
        val store = InMemoryLongSetStore()
        val repository = FavoriteCartUsageRepositoryImpl(store)

        assertFalse(repository.wasAddedToCurrentCart(42))
        repository.markAsAddedToCurrentCart(42)
        repository.markAsAddedToCurrentCart(77)

        assertTrue(repository.wasAddedToCurrentCart(42))
        assertTrue(repository.wasAddedToCurrentCart(77))
        assertFalse(repository.wasAddedToCurrentCart(99))
    }

    @Test
    fun `clear resets persisted imported favorites for new repository instances`() = runTest {
        val store = InMemoryLongSetStore()
        val repository = FavoriteCartUsageRepositoryImpl(store)
        repository.markAsAddedToCurrentCart(42)
        repository.markAsAddedToCurrentCart(77)
        repository.clear()
        val recreated = FavoriteCartUsageRepositoryImpl(store)
        assertFalse(recreated.wasAddedToCurrentCart(42))
        assertFalse(recreated.wasAddedToCurrentCart(77))
    }

    private class InMemoryLongSetStore : FavoriteCartUsageLocalDataSource {
        private val values = mutableMapOf<String, Set<Long>>()
        override suspend fun read(key: String): Set<Long> = values[key].orEmpty()
        override suspend fun update(key: String, transform: (Set<Long>) -> Set<Long>) {
            values[key] = transform(values[key].orEmpty())
        }
    }
}
