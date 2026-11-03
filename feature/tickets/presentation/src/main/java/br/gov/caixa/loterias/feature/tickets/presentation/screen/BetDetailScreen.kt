package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState

@Composable
fun BetDetailRoute(
    bet: ConfirmedBet,
    onBack: () -> Unit,
    onViewResult: () -> Unit,
    onRedeemPrize: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
    onEdit: (() -> Unit)? = null,
    onCart: () -> Unit = {},
    onAddToCart: (() -> Unit)? = null,
    onDownloadReceipt: (() -> Unit)? = null,
    favoriteExpanded: Boolean = false,
    favoriteName: String = "",
    favoriteSaved: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onFavoriteNameChange: (String) -> Unit = {},
    onSaveFavorite: () -> Unit = {},
    drawResults: List<br.gov.caixa.loterias.feature.results.domain.model.DrawResult> = emptyList(),
    cartTotal: java.math.BigDecimal = java.math.BigDecimal.ZERO,
    cartBetCount: Int = 0,
) {
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.BetDetailRoute(
            bet,
            onBack,
            onViewResult,
            onRedeemPrize,
            onHelp,
            onEdit,
            onCart,
            onAddToCart,
            onDownloadReceipt,
            favoriteExpanded,
            favoriteName,
            favoriteSaved,
            onToggleFavorite,
            onFavoriteNameChange,
            onSaveFavorite,
            drawResults,
            cartTotal,
            cartBetCount
        )
    }
}

@Composable
fun BetDetailUnavailableScreen(state: BetDetailUiState, onRetry: () -> Unit, onBack: () -> Unit) {
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.BetDetailUnavailableScreen(state, onRetry, onBack)
    }
}
