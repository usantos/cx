package br.gov.caixa.loterias.feature.account.presentation.delivery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.feature.account.presentation.screen.PersonalDataActions
import br.gov.caixa.loterias.feature.account.presentation.screen.RegistrationActions
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationUiState

interface AccountUiEntry : FeatureUiEntry {
    @Composable
    fun LoginScreen(
        state: LoginUiState,
        versionName: String,
        onTryNow: () -> Unit,
        onLogin: () -> Unit,
        onBiometricChanged: (Boolean) -> Unit,
        modifier: Modifier,
        environmentSelectorVisible: Boolean,
        environmentSelector: @Composable (Boolean) -> Unit,
        onSwitchUser: () -> Unit,
    )

    @Composable
    fun PersonalDataScreen(state: PersonalDataUiState, imeVisible: Boolean, onBack: () -> Unit, actions: PersonalDataActions)

    @Composable
    fun RegistrationScreen(
        state: RegistrationUiState,
        canSubmit: Boolean,
        actions: RegistrationActions,
        onBack: () -> Unit,
        onTerms: () -> Unit,
        onZipCodeRedirect: () -> Unit,
    )

    @Composable
    fun AccountTokenScreen(modifier: Modifier)

    @Composable
    fun SuccessScreen(registration: Boolean)
}
