package br.gov.caixa.loterias.core.navigation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.viewmodel.DestinationSnapshotViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase

/** Retrato restrito ao destino; nunca retém o carrinho de outra sessão. */
@Composable
fun activeCartSummary(getCurrentCart: GetCurrentCartUseCase, refreshKey: Any? = null): ShoppingCart? {
    val owner = remember(getCurrentCart) {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
    DisposableEffect(owner) { onDispose { owner.viewModelStore.clear() } }
    val model = viewModel<DestinationSnapshotViewModel<ShoppingCart>>(viewModelStoreOwner = owner) {
        DestinationSnapshotViewModel({
            when (val result = getCurrentCart()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> null
            }
        })
    }
    val snapshot by model.state.collectAsStateWithLifecycle()
    LaunchedEffect(model, refreshKey) { model.refresh() }
    return snapshot
}

/** Relê o carrinho do servidor sempre que este destino fica ativo após uma reserva ou edição. */
@Composable
fun activeResumingCartSummary(getCurrentCart: GetCurrentCartUseCase, refreshKey: Any? = null): ShoppingCart? {
    var resumeRevision by remember(getCurrentCart) { androidx.compose.runtime.mutableIntStateOf(0) }
    androidx.lifecycle.compose.LifecycleResumeEffect(getCurrentCart) {
        resumeRevision++
        onPauseOrDispose { }
    }
    return if (resumeRevision == 0) null else activeCartSummary(getCurrentCart, resumeRevision to refreshKey)
}
