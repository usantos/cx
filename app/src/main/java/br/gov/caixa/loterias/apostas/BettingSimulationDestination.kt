package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.domain.cart.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet
import br.gov.caixa.loterias.feature.betting.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.BettingGameViewModel

/** Consumer shared by the navigation destination and its retained-edit regression host. */
@Composable
internal fun BettingSimulationDestination(
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
) {
    // Clearing the external edit intent after success must not replace this entry's model.
    val editSeed = remember(destinationIdentity) { pendingEdit?.takeIf { it.modalityId == modalityId } }
    // The navigation entry's ViewModelStore scopes this key, including composition re-entry.
    val model = viewModel(key = viewModelKey) { createViewModel(editSeed) }
    var cartRevision by remember { mutableIntStateOf(0) }
    val cartSummary = activeResumingCartSummary(getCurrentCart, cartRevision)
    BettingGameRoute(
        viewModel = model,
        fallbackModalityId = modalityId,
        cartTotal = cartSummary?.total ?: java.math.BigDecimal.ZERO,
        cartBetCount = cartSummary?.betCount ?: 0,
        shakeEnabled = shakeEnabled,
        onHelp = onHelp,
        onCart = onCart,
        onBack = { clearPendingEdit(); onBack() },
        onAdded = { clearPendingEdit(); cartRevision++ },
    )
}
