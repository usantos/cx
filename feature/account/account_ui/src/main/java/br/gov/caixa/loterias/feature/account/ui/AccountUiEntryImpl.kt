package br.gov.caixa.loterias.feature.account.ui

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.feature.account.presentation.delivery.AccountUiEntry
import br.gov.caixa.loterias.feature.account.presentation.screen.PersonalDataActions
import br.gov.caixa.loterias.feature.account.presentation.screen.RegistrationActions
import br.gov.caixa.loterias.feature.account.presentation.screen.RenderAccountTokenScreen
import br.gov.caixa.loterias.feature.account.presentation.screen.RenderLoginScreen
import br.gov.caixa.loterias.feature.account.presentation.screen.RenderPersonalDataScreen
import br.gov.caixa.loterias.feature.account.presentation.screen.RenderRegistrationScreen
import br.gov.caixa.loterias.feature.account.presentation.screen.RenderSuccessScreen
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationUiState

@Keep
class AccountUiEntryImpl : AccountUiEntry {
    @Composable
    override fun LoginScreen(
        state: LoginUiState,
        versionName: String,
        onTryNow: () -> Unit,
        onLogin: () -> Unit,
        onBiometricChanged: (Boolean) -> Unit,
        modifier: Modifier,
        environmentSelectorVisible: Boolean,
        environmentSelector: @Composable (Boolean) -> Unit,
        onSwitchUser: () -> Unit,
    ) {
        RenderLoginScreen(
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

    @Composable
    override fun PersonalDataScreen(state: PersonalDataUiState, imeVisible: Boolean, onBack: () -> Unit, actions: PersonalDataActions) {
        RenderPersonalDataScreen(state, imeVisible, onBack, actions)
    }

    @Composable
    override fun RegistrationScreen(
        state: RegistrationUiState,
        canSubmit: Boolean,
        actions: RegistrationActions,
        onBack: () -> Unit,
        onTerms: () -> Unit,
        onZipCodeRedirect: () -> Unit,
    ) {
        RenderRegistrationScreen(state, canSubmit, actions, onBack, onTerms, onZipCodeRedirect)
    }

    @Composable
    override fun AccountTokenScreen(modifier: Modifier) {
        RenderAccountTokenScreen(modifier)
    }

    @Composable
    override fun SuccessScreen(registration: Boolean) {
        RenderSuccessScreen(registration)
    }
}
