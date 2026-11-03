package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.account.presentation.delivery.AccountUiEntry
import br.gov.caixa.loterias.feature.account.presentation.state.RegistrationUiState

@Composable
fun RegistrationScreen(
    state: RegistrationUiState,
    canSubmit: Boolean,
    actions: RegistrationActions,
    onBack: () -> Unit,
    onTerms: () -> Unit,
    onZipCodeRedirect: () -> Unit,
    access: FeatureEntryAccess = FeatureEntryAccess.OPTIONAL,
) {
    FeatureModuleContent(
        module = AppModule.ACCOUNT,
        entryType = AccountUiEntry::class.java,
        onBack = onBack,
        access = access,
        preserveActive = true
    ) { entry ->
        entry.RegistrationScreen(state, canSubmit, actions, onBack, onTerms, onZipCodeRedirect)
    }
}
