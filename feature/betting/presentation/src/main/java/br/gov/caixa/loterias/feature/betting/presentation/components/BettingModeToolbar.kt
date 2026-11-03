package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

@Composable
internal fun BettingModeToolbar(
    state: BettingGameUiState,
    onManual: () -> Unit,
    onSurprise: () -> Unit,
    onNumberQuantity: () -> Unit,
    onRecurring: () -> Unit,
    onTutorial: (() -> Unit)?,
) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        embedded = true
    ) { entry ->
        entry.BettingModeToolbar(state, onManual, onSurprise, onNumberQuantity, onRecurring, onTutorial)
    }
}
