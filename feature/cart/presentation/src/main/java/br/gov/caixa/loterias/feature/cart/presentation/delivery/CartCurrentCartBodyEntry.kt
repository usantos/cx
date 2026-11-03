package br.gov.caixa.loterias.feature.cart.presentation.delivery

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState

interface CartCurrentCartBodyEntry {
    @Composable
    fun CurrentCartContent(
        state: CartUiState,
        padding: PaddingValues,
        items: List<CartItem>,
        nowMillis: Long,
        onRemove: (CartItem) -> Unit,
        onPoolDetail: (String) -> Unit,
        onComboDetail: (Long) -> Unit,
    )
}
