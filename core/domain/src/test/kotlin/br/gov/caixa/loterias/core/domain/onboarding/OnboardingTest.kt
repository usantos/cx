package br.gov.caixa.loterias.core.domain.onboarding

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingTest {
    @Test
    fun `introduction is shown until completion is stored`() {
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

        override fun isIntroductionCompleted() = completed
        override fun isTutorialDismissed(tutorial: BettingTutorial) = dismissed && tutorial == BettingTutorial.SUPER_SETE

        override fun completeIntroduction() {
            completed = true
        }

        override fun setTutorialDismissed(tutorial: BettingTutorial, dismissed: Boolean) {
            this.dismissed = dismissed && tutorial == BettingTutorial.SUPER_SETE
        }
    }
}
