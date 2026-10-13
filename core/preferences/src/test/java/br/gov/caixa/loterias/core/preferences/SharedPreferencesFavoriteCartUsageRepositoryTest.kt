package br.gov.caixa.loterias.core.preferences

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SharedPreferencesFavoriteCartUsageRepositoryTest {
    @Test
    fun `persists every favorite cart transformed by the user`() {
        val store = InMemoryLongSetStore()
        val repository = SharedPreferencesFavoriteCartUsageRepository(store)

        assertFalse(repository.wasAddedToCurrentCart(42))
        repository.markAsAddedToCurrentCart(42)
        repository.markAsAddedToCurrentCart(77)

        assertTrue(repository.wasAddedToCurrentCart(42))
        assertTrue(repository.wasAddedToCurrentCart(77))
        assertFalse(repository.wasAddedToCurrentCart(99))
    }

    private class InMemoryLongSetStore : LongSetPreferenceStore {
        private val values = mutableMapOf<String, Set<Long>>()
        override fun read(key: String): Set<Long> = values[key].orEmpty()
        override fun write(key: String, value: Set<Long>) { values[key] = value }
    }
}
