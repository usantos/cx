package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplaceDetailMoney
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import java.math.BigDecimal

@Composable
internal fun DetailPurchasePanel(
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
    val ink = CaixaDSColorTokens.MarketplaceFooterText
    // A ação usa a mesma paleta da modalidade apresentada no detalhe.
    val colors = marketplaceColors(state.detail?.modality.orEmpty())
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).legacyAutomationId("pool-detail-purchase-panel")) {
        DetailPurchaseQuantity(state, decrease, increase)
        state.addToCartError?.let {
            Text(
                it,
                (Modifier.padding(horizontal = 24.dp)).uiId("pool.0062"),
                style = detailText(14),
                color = CaixaDS.colors.error
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
                    uiStrings.getString(LocalizedR.string.ui_adicionando_6ae81e)
                } else {
                    uiStrings.getString(LocalizedR.string.ui_adicionar_ao_carrinho_bf86b8)
                },
                style = detailText(16, FontWeight.Bold),
                color = colors.content,
                modifier = Modifier.uiId("pool.0064")
            )
        }
        Row(
            Modifier.padding(horizontal = 24.dp).padding(bottom = 8.dp).fillMaxWidth().height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailFooterValue(
                uiStrings.getString(LocalizedR.string.ui_valor_dessa_aposta_4e1785),
                marketplaceDetailMoney(state.purchaseAmount.toPlainString()),
                (Modifier.weight(1f)).uiId("pool.0065")
            )
            Box(Modifier.width(1.dp).height(44.dp).background(CaixaDSColorTokens.MarketplaceFooterDivider))
            DetailFooterValue(
                uiStrings.getString(LocalizedR.string.ui_total_do_carrinho_b01305),
                marketplaceDetailMoney(cartTotal.toPlainString()),
                (Modifier.weight(1f)).uiId("pool.0066")
            )
            Box(Modifier.width(1.dp).height(44.dp).background(CaixaDSColorTokens.MarketplaceFooterDivider))
            Box(
                Modifier.width(60.dp).fillMaxHeight()
                    .uiId("pool.0067").clickable(enabled = cart != null, role = Role.Button) { cart?.invoke() }
                    .semantics { contentDescription = uiStrings.getString(LocalizedR.string.ui_carrinho_de_apostas_23d953) },
                Alignment.Center
            ) {
                Icon(CaixaDSIcons.Cart, null, (Modifier.size(34.dp)).uiId("pool.0068"), tint = ink)
                Box(
                    Modifier.align(
                        Alignment.TopEnd
                    ).padding(top = 11.dp, end = 10.dp).size(18.dp).background(CaixaDS.colors.accent, CircleShape),
                    Alignment.Center
                ) {
                    Text(
                        if (cartBetCount > 999) uiStrings.getString(LocalizedR.string.ui_999_d78178) else cartBetCount.toString(),
                        style = detailText(9, FontWeight.Bold),
                        color = ink,
                        modifier = Modifier.uiId("pool.0069")
                    )
                }
            }
        }
    }
}

@Composable
internal fun DetailQuantityButton(label: String, description: String, enabled: Boolean, action: () -> Unit) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Box(
        Modifier.then(if (largeFont) Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp) else Modifier.size(48.dp))
            .uiId("pool.0070").clickable(enabled = enabled, role = Role.Button, onClick = action)
            .semantics { contentDescription = description }.padding(8.dp)
            .background(
                if (enabled) CaixaDSColorTokens.MarketplaceFooterText else CaixaDSColorTokens.MarketplaceFooterDisabled,
                RoundedCornerShape(3.dp)
            ),
        Alignment.Center
    ) {
        Text(label, style = detailText(16), color = CaixaDSColorTokens.White, modifier = Modifier.uiId("pool.0071"))
    }
}

@Composable
private fun DetailFooterValue(label: String, value: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            label,
            style = detailText(12),
            color = CaixaDSColorTokens.MarketplaceFooterText,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("pool.0072")
        )
        Text(
            value,
            style = detailText(13, FontWeight.Bold),
            color = CaixaDSColorTokens.MarketplaceFooterText,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("pool.0073")
        )
    }
}

@Composable
private fun DetailPurchaseQuantity(state: MarketplaceDetailUiState, decrease: () -> Unit, increase: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val ink = CaixaDSColorTokens.MarketplaceFooterText
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp).padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_quantidade_de_cotas_ddb3fd),
            (Modifier.weight(1f)).uiId("pool.0060"),
            style = detailText(14),
            color = ink
        )
        DetailQuantityButton("-", uiStrings.getString(LocalizedR.string.ui_diminuir_cotas_74fb74), state.canDecrease, decrease)
        Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
            Text(state.selectedShares.toString(), style = detailText(18), color = ink, modifier = Modifier.uiId("pool.0061"))
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(1.dp).background(ink))
        }
        DetailQuantityButton("+", uiStrings.getString(LocalizedR.string.ui_aumentar_cotas_203e21), state.canIncrease, increase)
    }
}
