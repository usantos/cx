package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.cart.GetCurrentCartUseCase
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Snapshot scoped to the destination; never retains another session's cart. */
@Composable
internal fun activeCartSummary(getCurrentCart: GetCurrentCartUseCase, refreshKey: Any? = null): ShoppingCart? {
    var snapshot by remember(getCurrentCart) { mutableStateOf<ShoppingCart?>(null) }
    LaunchedEffect(getCurrentCart, refreshKey) {
        when (val result = withContext(Dispatchers.IO) { getCurrentCart() }) {
            is AppResult.Success -> snapshot = result.value
            is AppResult.Failure -> snapshot = null
        }
    }
    return snapshot
}

/** Re-read the backend cart whenever this destination becomes active after a reservation or edit. */
@Composable
internal fun activeResumingCartSummary(getCurrentCart: GetCurrentCartUseCase, refreshKey: Any? = null): ShoppingCart? {
    var resumeRevision by remember(getCurrentCart) { androidx.compose.runtime.mutableIntStateOf(0) }
    androidx.lifecycle.compose.LifecycleResumeEffect(getCurrentCart) {
        resumeRevision++
        onPauseOrDispose { }
    }
    return if (resumeRevision == 0) null else activeCartSummary(getCurrentCart, resumeRevision to refreshKey)
}
