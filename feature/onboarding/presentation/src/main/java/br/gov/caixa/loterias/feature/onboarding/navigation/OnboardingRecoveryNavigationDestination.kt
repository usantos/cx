package br.gov.caixa.loterias.feature.onboarding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSAlertDialog as AlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSTextButton as TextButton
import br.gov.caixa.loterias.feature.onboarding.R
import br.gov.caixa.loterias.feature.onboarding.presentation.screen.SplashScreen
import br.gov.caixa.loterias.feature.onboarding.presentation.state.StartupRecoveryTarget
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.StartupRecoveryNavigationViewModel

@Composable
internal fun OnboardingRecoveryNavigationDestination(
    route: String,
    dependencies: OnboardingNavigationDependencies,
    navController: NavHostController,
) {
    val recovery = viewModel(key = "startup-recovery-navigation-$route") {
        StartupRecoveryNavigationViewModel(dependencies.shouldShowIntroduction)
    }
    DisposableEffect(recovery) {
        recovery.activate()
        onDispose { recovery.deactivate() }
    }
    val recoveryState by recovery.state.collectAsStateWithLifecycle()
    LaunchedEffect(recoveryState.target) {
        val target = recoveryState.target ?: return@LaunchedEffect
        recovery.acknowledgeTarget()
        val targetRoute = when (target) {
            StartupRecoveryTarget.INTRODUCTION -> "onboarding/introduction"
            StartupRecoveryTarget.LOGIN -> "account/login"
        }
        navController.navigate(targetRoute) { popUpTo(route) { inclusive = true } }
    }
    dependencies.StartupStatusDestination(route, recovery::recover)
    if (recoveryState.loading) SplashScreen(modifier = Modifier.uiId("start.0001"))
    if (recoveryState.storageFailed) {
        AlertDialog(
            onDismissRequest = {},
            text = { Text(stringResource(R.string.onboarding_storage_failure), modifier = Modifier.uiId("start.0002")) },
            confirmButton = {
                TextButton(
                    onClick = recovery::retry,
                    modifier = Modifier
                        .uiId("start.0003")
                ) { Text(stringResource(R.string.startup_retry), modifier = Modifier.uiId("start.0004")) }
            },
            modifier = Modifier.uiId("start.0005")
        )
    }
}
