package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListUiState
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

@Composable
fun RapidListScreen(
    state: RapidListUiState,
    onBack: () -> Unit,
    onConfiguration: () -> Unit,
    onGenerate: () -> Unit,
    onAdvance: () -> Unit,
    onRemove: (CartItem) -> Unit,
    onDismissError: () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.RapidListScreen(state, onBack, onConfiguration, onGenerate, onAdvance, onRemove, onDismissError)
    }
}
