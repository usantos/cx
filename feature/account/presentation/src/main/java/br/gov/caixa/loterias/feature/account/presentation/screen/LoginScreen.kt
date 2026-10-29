package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.account.presentation.route.findActivity
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState

@Composable
fun LoginScreen(
    state: LoginUiState,
    versionName: String,
    onTryNow: () -> Unit,
    onLogin: () -> Unit,
    onBiometricChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    environmentSelectorVisible: Boolean = false,
    environmentSelector: @Composable (Boolean) -> Unit = {},
    onSwitchUser: () -> Unit = {},
) {
    br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSStatusBarColor(
        br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens.Blue700,
        darkIcons = false
    )
    val view = LocalView.current
    DisposableEffect(view) {
        val activity = view.context.findActivity()
        activity?.let {
            WindowCompat.getInsetsController(it.window, view).apply {
                isAppearanceLightNavigationBars = true
            }
        }
        onDispose {
            activity?.let {
                WindowCompat.getInsetsController(it.window, view).apply {
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }
    if (state.isLoading || state.blockedAccountCheckFailed || state.consultationOnlyNoticePending) {
        AccountTokenScreen(modifier)
        return
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CaixaDS.colors.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(CaixaDS.colors.authenticationBackground)
    ) {
        val largeFont = androidx.compose.ui.platform.LocalDensity.current.fontScale > 1f
        if (largeFont) {
            Column(Modifier.fillMaxSize().uiId("account.0049").verticalScroll(rememberScrollState())) {
                LoginPrimaryContent(state, onTryNow, onLogin, largeFont = true, onSwitchUser = onSwitchUser)
                Spacer(Modifier.height(CaixaDS.spacing.xl))
                LoginFooterContent(
                    state,
                    versionName,
                    onBiometricChanged,
                    environmentSelectorVisible,
                    environmentSelector,
                    largeFont = true
                )
            }
        } else {
            LoginPrimaryContent(
                state,
                onTryNow,
                onLogin,
                largeFont = false,
                modifier = Modifier.align(Alignment.TopCenter),
                onSwitchUser = onSwitchUser
            )
            LoginFooterContent(
                state,
                versionName,
                onBiometricChanged,
                environmentSelectorVisible,
                environmentSelector,
                largeFont = false,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
