package br.gov.caixa.loterias.feature.results.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.results.presentation.delivery.ResultsUiEntry
import br.gov.caixa.loterias.feature.results.presentation.state.ConfirmedBetResultsState

@Composable
internal fun ConfirmedBetResultScreen(
    bet: ConfirmedBet,
    state: ConfirmedBetResultsState,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onRetry: () -> Unit,
    onRedeemPrize: (() -> Unit)?,
) {
    FeatureModuleContent(
        module = AppModule.RESULTS,
        entryType = ResultsUiEntry::class.java,
        onBack = onBack
    ) { entry ->
        entry.ConfirmedBetResultScreen(bet, state, onBack, onComplete, onRetry, onRedeemPrize)
    }
}
