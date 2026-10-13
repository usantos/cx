package br.gov.caixa.loterias.core.preferences

import br.gov.caixa.loterias.core.domain.onboarding.BettingTutorial
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SharedPreferencesOnboardingRepositoryTest {
    @Test
    fun `reads legacy key and writes completion`() {
        val store = InMemoryStore()
        val repository = SharedPreferencesOnboardingRepository(store)

        assertFalse(repository.isIntroductionCompleted())
        repository.completeIntroduction()
        assertTrue(repository.isIntroductionCompleted())
        assertTrue(store.values.getValue("INTRODUCAO_KEY"))

        BettingTutorial.entries.forEach {
            assertFalse(repository.isTutorialDismissed(it))
            repository.setTutorialDismissed(it, true)
            assertTrue(repository.isTutorialDismissed(it))
        }
        assertEquals(
            setOf("INTRODUCAO_KEY", "BOLAO_TUTORIAL", "MAIS_MILIONARIA_TUTORIAL", "SUPER_SETE_TUTORIAL", "RAPIDAO_KEY"),
            store.values.keys,
        )
        repository.setTutorialDismissed(BettingTutorial.POOL, false)
        assertFalse(repository.isTutorialDismissed(BettingTutorial.POOL))
        store.values["BOLAO_TUTORIAL"] = true
        assertTrue(SharedPreferencesOnboardingRepository(store).isTutorialDismissed(BettingTutorial.POOL))
    }

    private class InMemoryStore : BooleanPreferenceStore {
        val values = mutableMapOf<String, Boolean>()

        override fun getBoolean(key: String, defaultValue: Boolean): Boolean = values[key] ?: defaultValue

        override fun putBoolean(key: String, value: Boolean) {
            values[key] = value
        }
    }
}
