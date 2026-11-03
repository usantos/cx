package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import br.gov.caixa.loterias.feature.marketplace.ui.R as DeliveryR
import java.math.BigDecimal

@Composable
fun DetailPurchasePanel(
    state: MarketplaceDetailUiState,
    decrease: () -> Unit,
    increase: () -> Unit,
    add: () -> Unit,
    cartTotal: BigDecimal,
    cartBetCount: Int,
    cart: (() -> Unit)?,
) {
    if (LocalDensity.current.fontScale > 1f) {
        MarketplaceLargeFontPurchasePanel(state, decrease, increase, add, cartTotal, cartBetCount, cart)
        return
    }
    LegacyDetailPurchasePanel(state, decrease, increase, add, cartTotal, cartBetCount, cart)
}

@Composable
private fun LegacyDetailPurchasePanel(
    state: MarketplaceDetailUiState,
    decrease: () -> Unit,
    increase: () -> Unit,
    add: () -> Unit,
    cartTotal: BigDecimal,
    cartBetCount: Int,
    cart: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val ink = LoteriasCaixaDSColorTokens.MarketplaceFooterText
    // A ação usa a mesma paleta da modalidade apresentada no detalhe.
    val colors = marketplaceColors(state.detail?.modality.orEmpty())
    Column(Modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.White).legacyAutomationId("pool-detail-purchase-panel")) {
        DetailPurchaseQuantity(state, decrease, increase)
        state.addToCartError?.let {
            Text(
                it,
                (Modifier.padding(horizontal = 24.dp)).uiId("pool.0062"),
                style = detailText(14),
                color = LoteriasCaixaDS.colors.error
            )
        }
        Box(
            Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 8.dp).fillMaxWidth().height(48.dp)
                .background(colors.light, RoundedCornerShape(4.dp)).legacyAutomationId("pool-detail-add-to-cart")
                .uiId(
                    "pool.0063"
                ).clickable(enabled = state.selectedShares > 0 && !state.isAddingToCart, role = Role.Button, onClick = add),
            Alignment.Center
        ) {
            Text(
                if (state.isAddingToCart) {
                    uiStrings.getString(DeliveryR.string.ui_adicionando_6ae81e)
                } else {
                    uiStrings.getString(DeliveryR.string.ui_adicionar_ao_carrinho_bf86b8)
                },
                style = detailText(16, FontWeight.Bold),
                color = colors.content,
                modifier = Modifier.uiId("pool.0064")
            )
        }
        DetailPurchaseSummary(state, cartTotal, cartBetCount, cart)
    }
}
