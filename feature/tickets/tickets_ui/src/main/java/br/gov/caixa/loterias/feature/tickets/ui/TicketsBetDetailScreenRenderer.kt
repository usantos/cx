package br.gov.caixa.loterias.feature.tickets.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsBetDetailScreenEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import java.math.BigDecimal

internal class TicketsBetDetailScreenRenderer : TicketsBetDetailScreenEntry {
    @Composable
    override fun BetDetailRoute(
        bet: ConfirmedBet,
        onBack: () -> Unit,
        onViewResult: () -> Unit,
        onRedeemPrize: (() -> Unit)?,
        onHelp: () -> Unit,
        onEdit: (() -> Unit)?,
        onCart: () -> Unit,
        onAddToCart: (() -> Unit)?,
        onDownloadReceipt: (() -> Unit)?,
        favoriteExpanded: Boolean,
        favoriteName: String,
        favoriteSaved: Boolean,
        onToggleFavorite: () -> Unit,
        onFavoriteNameChange: (String) -> Unit,
        onSaveFavorite: () -> Unit,
        drawResults: List<br.gov.caixa.loterias.feature.results.domain.model.DrawResult>,
        cartTotal: java.math.BigDecimal,
        cartBetCount: Int,
    ) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.BetDetailRouteDelivered(
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

    @Composable
    override fun BetDetailUnavailableScreen(state: BetDetailUiState, onRetry: () -> Unit, onBack: () -> Unit) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.BetDetailUnavailableScreenDelivered(state, onRetry, onBack)
    }
}
