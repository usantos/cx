package br.gov.caixa.loterias.feature.tickets.presentation.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoading
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDetailFavoriteOverwriteDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDetailReceiptEffect
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetDetailServiceFailureDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetRepeatActionsOverlay
import br.gov.caixa.loterias.feature.tickets.presentation.screen.BetDetailRoute
import br.gov.caixa.loterias.feature.tickets.presentation.screen.BetDetailUnavailableScreen
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel

@Composable
fun BetDetailDataRoute(
    viewModel: BetDetailViewModel,
    onBack: () -> Unit,
    onViewResult: (ConfirmedBet) -> Unit,
    onRedeemPrize: ((ConfirmedBet) -> Unit)? = null,
    onHelp: () -> Unit = {},
    onEdit: ((ConfirmedBet) -> Unit)? = null,
    onCart: () -> Unit = {},
    onServiceRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit = {},
    cartTotal: java.math.BigDecimal = java.math.BigDecimal.ZERO,
    cartBetCount: Int = 0,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val failure = state.serviceFailure
    LaunchedEffect(failure) {
        if (failure is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RedirectRequired) {
            viewModel.dismissServiceFailure()
            onServiceRedirect(failure.destination)
        }
    }
    BetDetailServiceFailureDialog(failure, viewModel, onBack)
    if (!state.detailsReady && !state.loading) {
        BetDetailUnavailableScreen(state, viewModel, onBack)
        return
    }
    BetDetailReceiptEffect(viewModel)
    Box(Modifier.fillMaxSize()) {
        BetDetailRoute(
            bet = state.bet,
            onBack = onBack,
            onViewResult = { onViewResult(state.bet) },
            onRedeemPrize = onRedeemPrize?.let { callback -> { callback(state.bet) } },
            onHelp = onHelp,
            onEdit = onEdit?.let { callback -> { callback(state.bet) } },
            onCart = onCart,
            onAddToCart = viewModel::addCurrentBetToCart,
            onDownloadReceipt = viewModel::downloadReceipt,
            favoriteExpanded = state.favoriteExpanded,
            favoriteName = state.favoriteName,
            favoriteSaved = state.favoriteSaved,
            drawResults = state.drawResults,
            cartTotal = cartTotal,
            cartBetCount = cartBetCount,
            onToggleFavorite = viewModel::toggleFavoriteEditor,
            onFavoriteNameChange = viewModel::changeFavoriteName,
            onSaveFavorite = viewModel::submitFavorite
        )
        if (state.loading) {
            Box(
                Modifier.fillMaxSize().background(CaixaDS.colors.surface.copy(alpha = 0.88f)),
                contentAlignment = Alignment.Center
            ) {
                CaixaDSLoading("Carregando…", modifier = Modifier.uiId("tickets.0140"))
            }
        }
    }
    BetRepeatActionsOverlay(viewModel, showLoading = false)
    BetDetailFavoriteOverwriteDialog(state, viewModel)
}
