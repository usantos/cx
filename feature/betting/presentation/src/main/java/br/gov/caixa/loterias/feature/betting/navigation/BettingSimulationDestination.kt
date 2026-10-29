package br.gov.caixa.loterias.feature.betting.navigation

import br.gov.caixa.loterias.core.navigation.state.activeResumingCartSummary
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

/** Consumidor compartilhado pelo destino de navegação e pelo hospedeiro de regressão da edição retida. */
@Composable
fun BettingSimulationDestination(
    destinationIdentity: Any,
    modalityId: Int,
    viewModelKey: String,
    pendingEdit: ConfirmedBet?,
    createViewModel: (ConfirmedBet?) -> BettingGameViewModel,
    getCurrentCart: GetCurrentCartUseCase,
    clearPendingEdit: () -> Unit,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onCart: () -> Unit,
    shakeEnabled: Boolean = false,
    onFavoriteLoginRequired: (() -> Unit)? = null,
    isAuthenticated: Boolean = false,
) {
    // Limpar a intenção externa de edição após o sucesso não deve substituir o modelo desta entrada.
    val editSeed = remember(destinationIdentity) { pendingEdit?.takeIf { it.modalityId == modalityId } }
    // O ViewModelStore da entrada de navegação delimita esta chave, inclusive na reentrada da composição.
    val model = viewModel(key = viewModelKey) { createViewModel(editSeed) }
    var cartRevision by remember { mutableIntStateOf(0) }
    val cartSummary = activeResumingCartSummary(getCurrentCart, cartRevision)
    BettingGameRoute(
        viewModel = model,
        isAuthenticated = isAuthenticated,
        fallbackModalityId = modalityId,
        cartTotal = cartSummary?.total ?: java.math.BigDecimal.ZERO,
        cartBetCount = cartSummary?.betCount ?: 0,
        shakeEnabled = shakeEnabled,
        onFavoriteLoginRequired = onFavoriteLoginRequired,
        onHelp = onHelp,
        onCart = onCart,
        onBack = { clearPendingEdit(); onBack() },
        onAdded = { clearPendingEdit(); cartRevision++ },
    )
}
