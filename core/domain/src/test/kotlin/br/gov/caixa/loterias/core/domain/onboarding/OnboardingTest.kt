package br.gov.caixa.loterias.core.domain.onboarding

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroduction
import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissed
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ShouldShowIntroduction

class OnboardingTest {
    @Test
    fun `introduction is shown until completion is stored`() = kotlinx.coroutines.test.runTest {
        val repository = InMemoryOnboardingRepository()
        val shouldShow = ShouldShowIntroduction(repository)
        val complete = CompleteIntroduction(repository)

        assertTrue(shouldShow())
        complete()
        assertFalse(shouldShow())

        SetBettingTutorialDismissed(repository)(BettingTutorial.SUPER_SETE, true)
        assertTrue(repository.dismissed)
    }

    private class InMemoryOnboardingRepository : OnboardingRepository {
        private var completed = false
        var dismissed = false

        override suspend fun isIntroductionCompleted() = completed
        override suspend fun isTutorialDismissed(tutorial: BettingTutorial) = dismissed && tutorial == BettingTutorial.SUPER_SETE

        override suspend fun completeIntroduction() {
            completed = true
        }

        override suspend fun setTutorialDismissed(tutorial: BettingTutorial, dismissed: Boolean) {
            this.dismissed = dismissed && tutorial == BettingTutorial.SUPER_SETE
        }
    }
}
