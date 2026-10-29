package br.gov.caixa.loterias.feature.onboarding.domain.repository

import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial

/** Contrato de preferências mantido no domínio para que a política de inicialização não dependa das APIs Android. */
interface OnboardingRepository {
    suspend fun isIntroductionCompleted(): Boolean
    suspend fun completeIntroduction()
    suspend fun isTutorialDismissed(tutorial: BettingTutorial): Boolean
    suspend fun setTutorialDismissed(tutorial: BettingTutorial, dismissed: Boolean)
}
