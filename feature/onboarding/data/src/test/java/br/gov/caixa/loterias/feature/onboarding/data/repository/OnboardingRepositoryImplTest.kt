package br.gov.caixa.loterias.feature.onboarding.data.repository

import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.onboarding.data.datasource.local.OnboardingLocalDataSource
import br.gov.caixa.loterias.feature.onboarding.data.repository.OnboardingRepositoryImpl

class OnboardingRepositoryImplTest {
    @Test
    fun `reads legacy key and writes completion`() = kotlinx.coroutines.test.runTest {
        val store = InMemoryStore()
        val repository = OnboardingRepositoryImpl(store)

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
        assertTrue(OnboardingRepositoryImpl(store).isTutorialDismissed(BettingTutorial.POOL))
    }

    private class InMemoryStore : OnboardingLocalDataSource {
        val values = mutableMapOf<String, Boolean>()

        override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean = values[key] ?: defaultValue

        override suspend fun putBoolean(key: String, value: Boolean) {
            values[key] = value
        }
    }
}
