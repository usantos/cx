package br.gov.caixa.loterias.feature.account.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.account.presentation.delivery.AccountUiEntry
import br.gov.caixa.loterias.feature.account.presentation.state.PersonalDataUiState

@Composable
fun PersonalDataScreen(state: PersonalDataUiState, imeVisible: Boolean, onBack: () -> Unit, actions: PersonalDataActions) {
    FeatureModuleContent(
        module = AppModule.ACCOUNT,
        entryType = AccountUiEntry::class.java,
        onBack = onBack,
        access = FeatureEntryAccess.OPTIONAL,
        preserveActive = true
    ) { entry ->
        entry.PersonalDataScreen(state, imeVisible, onBack, actions)
    }
}
