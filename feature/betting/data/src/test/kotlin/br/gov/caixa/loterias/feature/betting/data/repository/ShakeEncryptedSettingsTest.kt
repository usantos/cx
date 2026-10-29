package br.gov.caixa.loterias.feature.betting.data.repository

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ShakeEncryptedSettingsTest {
    @Test fun tutorialAndTooltipCommitTogetherAndAreIsolatedByOwnerAndEnvironment() = runTest {
        val stores = mutableMapOf<SecureStoreScope, MemoryStore>()
        val factory = SecureStoreFactory { stores.getOrPut(it) { MemoryStore() } }
        var environment = "a".repeat(DIGEST_WIDTH)
        val owner = "c".repeat(DIGEST_WIDTH)
        val repository = ShakeSettingsRepositoryImpl(RuntimeEnvironment.getApplication(), factory) { environment }
        assertFalse(repository.tutorialCompleted(owner))
        val store = stores.getValue(SecureStoreScope(environment, owner, "shake_tutorial"))
        val before = store.snapshot.revision
        repository.completeTutorial(owner)
        assertEquals(before + 1, store.snapshot.revision)
        assertTrue(repository.tooltipPending(owner))
        assertTrue(repository.tutorialCompleted(owner))
        assertFalse(repository.tutorialCompleted("d".repeat(DIGEST_WIDTH)))
        assertFalse(repository.tutorialCompleted(null))
        environment = "b".repeat(DIGEST_WIDTH)
        assertFalse(repository.tutorialCompleted(owner))
        repository.setEnabled(true)
        environment = "a".repeat(DIGEST_WIDTH)
        assertTrue(repository.enabled())
        store.fail = true
        try {
            repository.consumeTooltip(owner)
            throw AssertionError("Expected write failure")
        } catch (_: SecureStorageException.WriteFailure) {
            assertTrue(repository.tooltipPending(owner))
        }
    }

    @Test fun ambiguousOwnerTutorialIsPreservedInsteadOfImportedIntoAnEnvironment() = runTest {
        val context = RuntimeEnvironment.getApplication()
        val key = "aposta_shake_tutorial_v1_concluido_usuario_" + "c".repeat(DIGEST_WIDTH)
        val preferences = context.getSharedPreferences(key, 0)
        preferences.edit().putBoolean(key, true).commit()
        val store = MemoryStore()
        val repository = ShakeSettingsRepositoryImpl(context, SecureStoreFactory { store }) { "a".repeat(DIGEST_WIDTH) }
        assertFalse(repository.tutorialCompleted("c".repeat(DIGEST_WIDTH)))
        assertTrue(preferences.getBoolean(key, false))
    }

    private class MemoryStore : SecureRecordStore {
        var snapshot = SecureSnapshot(0, emptyMap())
        var fail = false
        override suspend fun read() = snapshot
        override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot {
            if (fail) throw SecureStorageException.WriteFailure()
            snapshot = SecureSnapshot(snapshot.revision + 1, transform(snapshot.values))
            return snapshot
        }
    }
    private companion object {
        const val DIGEST_WIDTH = 64
    }
}
