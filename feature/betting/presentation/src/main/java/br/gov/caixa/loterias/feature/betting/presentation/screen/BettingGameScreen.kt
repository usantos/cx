package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState

@Composable
fun BettingGameScreen(
    state: BettingGameUiState,
    actions: BettingGameActions,
    cartTotal: java.math.BigDecimal,
    cartBetCount: Int,
    onCart: (() -> Unit)?,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    fallbackModalityId: Int?,
    onRetry: () -> Unit,
    content: @Composable () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.BettingGameScreen(
            state,
            actions,
            cartTotal,
            cartBetCount,
            onCart,
            onClear,
            onBack,
            onHelp,
            fallbackModalityId,
            onRetry,
            content
        )
    }
}
