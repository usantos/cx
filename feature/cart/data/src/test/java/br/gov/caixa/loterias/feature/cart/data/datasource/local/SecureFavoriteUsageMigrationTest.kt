package br.gov.caixa.loterias.feature.cart.data.datasource.local

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureFavoriteUsageMigrationTest {
    @Test fun `unbound favorite usage is encrypted in quarantine and never assigned to active account`() = runTest {
        val disk = CartMemorySecureStore()
        val source = FavoriteSource(setOf("4", "9"))
        SecureFavoriteUsageMigration(disk, source).migrate()
        assertNull(source.value)
        val quarantine = SecureCartDocument(disk)
        assertTrue(quarantine.requiresLegacyReview())
        assertFalse(quarantine.requiresCartRecovery())
        assertEquals(setOf(FIRST_FAVORITE_ID, SECOND_FAVORITE_ID), quarantine.read().favorites)
        assertTrue(SecureCartDocument(CartMemorySecureStore()).read().favorites.isEmpty())
    }

    @Test fun `write failure or malformed IDs preserves legacy favorite data`() = runTest {
        val disk = CartMemorySecureStore().apply { failWrite = true }
        val source = FavoriteSource(setOf("4"))
        assertTrue(runCatching { SecureFavoriteUsageMigration(disk, source).migrate() }.isFailure)
        assertEquals(setOf("4"), source.value)
        disk.failWrite = false
        source.value = setOf("4", "invalid")
        assertTrue(runCatching { SecureFavoriteUsageMigration(disk, source).migrate() }.isFailure)
        assertEquals(setOf("4", "invalid"), source.value)
        assertTrue(disk.snapshot.values.isEmpty())
    }

    private class FavoriteSource(var value: Set<String>?) : FavoriteUsageMigrationSource {
        override fun read() = value
        override fun cleanup(expected: Set<String>) {
            check(value == expected)
            value = null
        }
    }
}

private const val FIRST_FAVORITE_ID = 4L
private const val SECOND_FAVORITE_ID = 9L
