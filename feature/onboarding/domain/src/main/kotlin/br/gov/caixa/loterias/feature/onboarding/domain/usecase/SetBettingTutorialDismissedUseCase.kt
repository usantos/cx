package br.gov.caixa.loterias.feature.onboarding.domain.usecase

import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial

fun interface SetBettingTutorialDismissedUseCase {
    suspend operator fun invoke(tutorial: BettingTutorial, dismissed: Boolean)
}
