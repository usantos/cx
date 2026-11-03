package br.gov.caixa.loterias.feature.cart.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.presentation.delivery.CartCurrentCartBodyEntry
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState

internal class CartCurrentCartBodyRenderer : CartCurrentCartBodyEntry {
    @Composable
    override fun CurrentCartContent(
        state: CartUiState,
        padding: PaddingValues,
        items: List<CartItem>,
        nowMillis: Long,
        onRemove: (CartItem) -> Unit,
        onPoolDetail: (String) -> Unit,
        onComboDetail: (Long) -> Unit,
    ) {
        br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartContentDelivered(
            state,
            padding,
            items,
            nowMillis,
            onRemove,
            onPoolDetail,
            onComboDetail
        )
    }
}
