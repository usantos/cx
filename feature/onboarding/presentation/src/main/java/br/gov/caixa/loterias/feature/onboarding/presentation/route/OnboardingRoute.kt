package br.gov.caixa.loterias.feature.onboarding.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSAlertDialog as AlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.feature.onboarding.R
import br.gov.caixa.loterias.feature.onboarding.R as LocalizedR
import br.gov.caixa.loterias.feature.onboarding.presentation.components.AgeScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.components.IntroductionScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.components.TutorialScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingEffect
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.OnboardingViewModel

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onUnderage: () -> Unit,
    onResponsibleGaming: () -> Unit,
    onStartBettingTutorial: () -> Unit,
    onSkipBettingTutorial: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                OnboardingEffect.Completed -> onComplete()
                OnboardingEffect.Underage -> onUnderage()
                OnboardingEffect.StartBettingTutorial -> onStartBettingTutorial()
                OnboardingEffect.SkipBettingTutorial -> onSkipBettingTutorial()
            }
        }
    }
    if (state.storageFailed) {
        AlertDialog(
            onDismissRequest = viewModel::dismissStorageError,
            text = { Text(stringResource(R.string.onboarding_storage_failure), modifier = Modifier.uiId("start.0029")) },
            confirmButton = {
                TextButton(
                    onClick = viewModel::dismissStorageError,
                    modifier = Modifier
                        .uiId("start.0030")
                ) { Text(stringResource(R.string.startup_understood), modifier = Modifier.uiId("start.0031")) }
            },
            modifier = Modifier.uiId("start.0032")
        )
    }
    when (state.route) {
        "onboarding/introduction" -> IntroductionScreen(state, viewModel)

        "onboarding/tutorial" -> TutorialScreen(
            checked = state.tutorialDoNotShowAgain,
            onToggle = viewModel::toggleTutorialDoNotShowAgain,
            onStart = viewModel::startBettingTutorial,
            onSkip = viewModel::skipBettingTutorial
        )

        "onboarding/age" -> AgeScreen(onBack, onResponsibleGaming)

        "onboarding/splash" -> SplashScreen(modifier = Modifier.uiId("start.0033"))

        else -> error(uiStrings.getString(LocalizedR.string.ui_rota_inicial_sem_layout_compose_1_s_6572cd, state.route))
    }
}
