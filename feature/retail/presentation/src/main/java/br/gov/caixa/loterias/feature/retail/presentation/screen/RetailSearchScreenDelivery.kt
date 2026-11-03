package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.retail.presentation.delivery.RetailUiEntry
import br.gov.caixa.loterias.feature.retail.presentation.state.RetailSearchUiState

@Composable
internal fun RetailSearchScreen(state: RetailSearchUiState, actions: RetailSearchActions, onBack: () -> Unit, onHelp: () -> Unit) {
    FeatureModuleContent(
        module = AppModule.RETAIL,
        entryType = RetailUiEntry::class.java,
        onBack = onBack
    ) { entry ->
        entry.RetailSearchScreen(state, actions, onBack, onHelp)
    }
}
