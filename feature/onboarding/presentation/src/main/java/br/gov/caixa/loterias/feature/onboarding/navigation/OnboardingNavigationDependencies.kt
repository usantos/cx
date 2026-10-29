package br.gov.caixa.loterias.feature.onboarding.navigation

import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ShouldShowIntroductionUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface OnboardingNavigationDependencies {
    val completeIntroduction: CompleteIntroductionUseCase
    val setBettingTutorialDismissed: SetBettingTutorialDismissedUseCase
    val shouldShowIntroduction: ShouldShowIntroductionUseCase

    @androidx.compose.runtime.Composable
    fun StartupStatusDestination(route: String, onRecovered: () -> Unit)
}
