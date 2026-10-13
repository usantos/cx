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
internal fun activeCartSummary(getCurrentCart: GetCurrentCartUseCase): ShoppingCart? {
    var snapshot by remember(getCurrentCart) { mutableStateOf<ShoppingCart?>(null) }
    LaunchedEffect(getCurrentCart) {
        when (val result = withContext(Dispatchers.IO) { getCurrentCart() }) {
            is AppResult.Success -> snapshot = result.value
            is AppResult.Failure -> snapshot = null
        }
    }
    return snapshot
}
