package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState

interface TicketsBetDetailScreenEntry {
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
    )

    @Composable
    fun BetDetailUnavailableScreen(state: BetDetailUiState, onRetry: () -> Unit, onBack: () -> Unit)
}
