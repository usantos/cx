package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.components.AnimatedLotteryTutorial
import br.gov.caixa.loterias.feature.betting.presentation.components.SuperSevenTutorial
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialDefinition
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState

@Composable
fun BettingScreenDelivered(
    state: BettingFlowUiState,
    onPageSelected: (Int) -> Unit,
    onBack: () -> Unit,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
) {
    val definition = tutorialDefinition(state.spec.route)
    if (definition.usesLegacyOverlayControls) {
        AnimatedLotteryTutorial(
            state = state,
            definition = definition,
            onPageSelected = onPageSelected,
            onClose = onBack,
            onPrimary = onPrimary,
            onSecondary = onSecondary,
            onToggleDoNotShowAgain = onToggleDoNotShowAgain
        )
    } else {
        SuperSevenTutorial(
            state = state,
            definition = definition,
            onPageSelected = onPageSelected,
            onBack = onBack,
            onPrimary = onPrimary,
            onSkip = onSecondary
        )
    }
}
