package br.gov.caixa.loterias.feature.onboarding

import br.gov.caixa.loterias.feature.onboarding.presentation.state.HostStartupAction
import br.gov.caixa.loterias.feature.onboarding.presentation.state.HostStartupState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingAction
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingUiState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueGateAction
import br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueGateState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.QueueStatus
import br.gov.caixa.loterias.feature.onboarding.presentation.state.reduceHostStartupState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.reduceOnboardingState
import br.gov.caixa.loterias.feature.onboarding.presentation.state.reduceQueueGateState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingReducersTest {
    private val initialAttempt = 4
    private val overflowPage = 99

    @Test
    fun `security retry preserves elapsed splash and increments attempt`() {
        val failed = reduceHostStartupState(HostStartupState(minimumSplashElapsed = true), HostStartupAction.SecurityPrepared(false))
        val retry = reduceHostStartupState(failed, HostStartupAction.Retried)
        assertTrue(retry.minimumSplashElapsed)
        assertFalse(retry.securityFailed)
        assertEquals(failed.attempt + 1, retry.attempt)
    }

    @Test
    fun `queue retry resets admission and preserves attempt history`() {
        val passed = reduceQueueGateState(QueueGateState(attempt = initialAttempt), QueueGateAction.Passed)
        val retry = reduceQueueGateState(passed, QueueGateAction.Retried)
        assertEquals(QueueStatus.WAITING, retry.status)
        assertEquals(initialAttempt + 1, retry.attempt)
    }

    @Test
    fun `introduction page is bounded and tutorial toggle preserves route`() {
        val initial = OnboardingUiState(route = "onboarding/introduction")
        val last = reduceOnboardingState(initial, OnboardingAction.PageChanged(overflowPage))
        assertEquals(2, last.introductionPage)
        assertEquals(0, reduceOnboardingState(last, OnboardingAction.PageChanged(-1)).introductionPage)
        assertEquals(initial.route, reduceOnboardingState(last, OnboardingAction.TutorialPreferenceToggled).route)
    }
}
