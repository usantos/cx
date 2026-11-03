package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun MarketplaceLargeFontPurchasePanel(
    state: MarketplaceDetailUiState,
    decrease: () -> Unit,
    increase: () -> Unit,
    add: () -> Unit,
    cartTotal: BigDecimal,
    cartBetCount: Int,
    cart: (() -> Unit)?,
) {
    val colors = marketplaceColors(state.detail?.modality.orEmpty())
    Column(Modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.White).legacyAutomationId("pool-detail-purchase-panel")) {
        LargeFontPurchaseQuantity(state, decrease, increase)
        state.addToCartError?.let {
            Text(
                it,
                (Modifier.padding(horizontal = 24.dp)).uiId("pool.0098"),
                style = detailText(PURCHASE_LABEL_SIZE),
                color = LoteriasCaixaDS.colors.error
            )
        }
        Box(
            Modifier.padding(horizontal = 24.dp, vertical = 8.dp).fillMaxWidth().heightIn(min = 48.dp)
                .background(colors.light, RoundedCornerShape(4.dp)).legacyAutomationId("pool-detail-add-to-cart")
                .uiId("pool.0099").clickable(
                    enabled = state.selectedShares > 0 && !state.isAddingToCart,
                    role = Role.Button,
                    onClick = add
                ).padding(8.dp),
            Alignment.Center
        ) {
            Text(
                stringResource(
                    if (state.isAddingToCart) DeliveryR.string.ui_adicionando_6ae81e else DeliveryR.string.ui_adicionar_ao_carrinho_bf86b8
                ),
                (Modifier.fillMaxWidth()).uiId("pool.0100"),
                style = detailText(PURCHASE_ACTION_SIZE, FontWeight.Bold),
                color = colors.content,
                textAlign = TextAlign.Center
            )
        }
        LargeFontPurchaseSummary(state, cartTotal, cartBetCount, cart)
    }
}

const val PURCHASE_LABEL_SIZE = 14
const val PURCHASE_QUANTITY_SIZE = 18
const val PURCHASE_ACTION_SIZE = 16
const val PURCHASE_CAPTION_SIZE = 12
const val PURCHASE_AMOUNT_SIZE = 13
const val PURCHASE_BADGE_SIZE = 9
const val MAX_BADGE_COUNT = 999
