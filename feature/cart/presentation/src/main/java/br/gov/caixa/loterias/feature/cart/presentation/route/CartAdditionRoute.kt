package br.gov.caixa.loterias.feature.cart.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import br.gov.caixa.loterias.feature.cart.presentation.screen.LegacyBetAddition
import br.gov.caixa.loterias.feature.cart.presentation.screen.LegacyPoolAddition
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAdditionKind
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartAdditionViewModel

@Composable
fun CartAdditionRoute(
    viewModel: CartAdditionViewModel,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onOpenCart: () -> Unit,
    onHelp: ((Int, String) -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refreshReservation() }
    if (state.kind == CartAdditionKind.POOL_SHARE) {
        BackHandler(onBack = onBack)
        LegacyPoolAddition(state, onBack, onHome, onOpenCart, onHelp)
    } else {
        BackHandler(onBack = onBack)
        LegacyBetAddition(state, onBack, onHome, onOpenCart, onHelp)
    }
}
