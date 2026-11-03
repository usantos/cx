package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.account.presentation.delivery.AccountUiEntry
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
    access: FeatureEntryAccess = FeatureEntryAccess.OPTIONAL,
) {
    FeatureModuleContent(
        module = AppModule.ACCOUNT,
        entryType = AccountUiEntry::class.java,
        onBack = onTryNow,
        access = access,
        preserveActive = true
    ) { entry ->
        entry.LoginScreen(
            state,
            versionName,
            onTryNow,
            onLogin,
            onBiometricChanged,
            modifier,
            environmentSelectorVisible,
            environmentSelector,
            onSwitchUser
        )
    }
}
