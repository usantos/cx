package br.gov.caixa.loterias.feature.onboarding.data

import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.feature.onboarding.data.repository.OnboardingRepositoryImpl
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class OnboardingPersistenceTest {
    @Test fun `legacy introduction and explicit false tutorial migrate once then newer values survive`() = runTest {
        val context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("INTRODUCAO_KEY", 0).edit().putBoolean("INTRODUCAO_KEY", true).commit()
        context.getSharedPreferences("BOLAO_TUTORIAL", 0).edit().putBoolean("BOLAO_TUTORIAL", false).commit()
        val memory = MemoryStore()
        val factory = SecureStoreFactory { memory }
        val initial = OnboardingRepositoryImpl.fromContext(context, factory)
        assertTrue(initial.isIntroductionCompleted())
        assertFalse(initial.isTutorialDismissed(BettingTutorial.POOL))
        assertFalse(context.getSharedPreferences("INTRODUCAO_KEY", 0).contains("INTRODUCAO_KEY"))
        initial.setTutorialDismissed(BettingTutorial.POOL, true)
        context.getSharedPreferences("BOLAO_TUTORIAL", 0).edit().putBoolean("BOLAO_TUTORIAL", false).commit()
        val restored = OnboardingRepositoryImpl.fromContext(context, factory)
        assertTrue(restored.isTutorialDismissed(BettingTutorial.POOL))
        for (tutorial in BettingTutorial.entries) {
            restored.setTutorialDismissed(tutorial, true)
            assertTrue(OnboardingRepositoryImpl.fromContext(context, factory).isTutorialDismissed(tutorial))
            restored.setTutorialDismissed(tutorial, false)
            assertFalse(OnboardingRepositoryImpl.fromContext(context, factory).isTutorialDismissed(tutorial))
        }
        assertTrue(restored.isIntroductionCompleted())
    }

    private class MemoryStore : SecureRecordStore {
        var snapshot = SecureSnapshot(0, emptyMap())
        override suspend fun read() = snapshot
        override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot {
            snapshot = SecureSnapshot(snapshot.revision + 1, transform(snapshot.values))
            return snapshot
        }
    }
}
