package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.cart.R as LocalizedR

@Composable
fun CurrentCartContent(
    state: CartUiState,
    padding: PaddingValues,
    items: List<CartItem>,
    nowMillis: Long,
    onRemove: (CartItem) -> Unit,
    onPoolDetail: (String) -> Unit,
    onComboDetail: (Long) -> Unit,
) {
    br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent(
        module = br.gov.caixa.loterias.core.domain.configuration.model.AppModule.CART,
        entryType = br.gov.caixa.loterias.feature.cart.presentation.delivery.CartUiEntry::class.java,
        embedded = true,
        modifier = Modifier.padding(padding),
        preserveActive = true
    ) { entry -> entry.CurrentCartContent(state, padding, items, nowMillis, onRemove, onPoolDetail, onComboDetail) }
}

@Composable
internal fun CartError(message: String, padding: PaddingValues, onRetry: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxSize().padding(padding).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = LoteriasCaixaDS.colors.error, modifier = Modifier.uiId("cart.0109"))
        Text(
            uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            (Modifier.uiId("cart.0110").clickable(onClick = onRetry).padding(20.dp)).uiId("cart.0111"),
            color = LoteriasCaixaDS.colors.brand
        )
    }
}
