package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import br.gov.caixa.loterias.feature.marketplace.R
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplaceDetailMoney
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import java.math.BigDecimal

@Composable
internal fun MarketplaceLargeFontPurchasePanel(
    state: MarketplaceDetailUiState,
    decrease: () -> Unit,
    increase: () -> Unit,
    add: () -> Unit,
    cartTotal: BigDecimal,
    cartBetCount: Int,
    cart: (() -> Unit)?,
) {
    val colors = marketplaceColors(state.detail?.modality.orEmpty())
    Column(Modifier.fillMaxWidth().background(CaixaDSColorTokens.White).legacyAutomationId("pool-detail-purchase-panel")) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.ui_quantidade_de_cotas_ddb3fd),
                (Modifier.weight(1f)).uiId("pool.0096"),
                style = detailText(PURCHASE_LABEL_SIZE),
                color = CaixaDSColorTokens.MarketplaceFooterText
            )
            DetailQuantityButton("-", stringResource(R.string.ui_diminuir_cotas_74fb74), state.canDecrease, decrease)
            Box(Modifier.widthIn(min = 36.dp).heightIn(min = 36.dp), Alignment.Center) {
                Text(
                    state.selectedShares.toString(),
                    (Modifier.width(IntrinsicSize.Min)).uiId("pool.0097"),
                    softWrap = false,
                    style = detailText(PURCHASE_QUANTITY_SIZE),
                    color = CaixaDSColorTokens.MarketplaceFooterText
                )
            }
            DetailQuantityButton("+", stringResource(R.string.ui_aumentar_cotas_203e21), state.canIncrease, increase)
        }
        state.addToCartError?.let {
            Text(
                it,
                (Modifier.padding(horizontal = 24.dp)).uiId("pool.0098"),
                style = detailText(PURCHASE_LABEL_SIZE),
                color = CaixaDS.colors.error
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
                stringResource(if (state.isAddingToCart) R.string.ui_adicionando_6ae81e else R.string.ui_adicionar_ao_carrinho_bf86b8),
                (Modifier.fillMaxWidth()).uiId("pool.0100"),
                style = detailText(PURCHASE_ACTION_SIZE, FontWeight.Bold),
                color = colors.content,
                textAlign = TextAlign.Center
            )
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            LargeFontDetailValue(
                stringResource(R.string.ui_valor_dessa_aposta_4e1785),
                marketplaceDetailMoney(state.purchaseAmount.toPlainString()),
                (Modifier.weight(1f)).uiId("pool.0101")
            )
            Box(Modifier.width(1.dp).height(44.dp).background(CaixaDSColorTokens.MarketplaceFooterDivider))
            LargeFontDetailValue(
                stringResource(R.string.ui_total_do_carrinho_b01305),
                marketplaceDetailMoney(cartTotal.toPlainString()),
                (Modifier.weight(1f)).uiId("pool.0102")
            )
            LargeFontDetailCart(cartBetCount, cart)
        }
    }
}

@Composable
private fun LargeFontDetailValue(label: String, value: String, modifier: Modifier) {
    Column(
        modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            label,
            (Modifier.fillMaxWidth()).uiId("pool.0103"),
            style = detailText(PURCHASE_CAPTION_SIZE),
            color = CaixaDSColorTokens.MarketplaceFooterText,
            textAlign = TextAlign.Center
        )
        Text(
            value,
            (Modifier.fillMaxWidth()).uiId("pool.0104"),
            style = detailText(PURCHASE_AMOUNT_SIZE, FontWeight.Bold),
            color = CaixaDSColorTokens.MarketplaceFooterText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LargeFontDetailCart(count: Int, onCart: (() -> Unit)?) {
    val description = stringResource(R.string.ui_carrinho_de_apostas_23d953)
    Box(
        Modifier.width(60.dp).heightIn(min = 56.dp)
            .uiId("pool.0105").clickable(enabled = onCart != null, role = Role.Button) { onCart?.invoke() }
            .semantics { contentDescription = description },
        Alignment.Center
    ) {
        Icon(CaixaDSIcons.Cart, null, (Modifier.size(34.dp)).uiId("pool.0106"), tint = CaixaDSColorTokens.MarketplaceFooterText)
        Box(
            Modifier.align(Alignment.TopEnd).background(CaixaDS.colors.accent, CircleShape).padding(horizontal = 4.dp, vertical = 2.dp),
            Alignment.Center
        ) {
            Text(
                if (count > MAX_BADGE_COUNT) stringResource(R.string.ui_999_d78178) else count.toString(),
                (Modifier.width(IntrinsicSize.Min)).uiId("pool.0107"),
                softWrap = false,
                style = detailText(PURCHASE_BADGE_SIZE, FontWeight.Bold),
                color = CaixaDSColorTokens.MarketplaceFooterText
            )
        }
    }
}

private const val PURCHASE_LABEL_SIZE = 14
private const val PURCHASE_QUANTITY_SIZE = 18
private const val PURCHASE_ACTION_SIZE = 16
private const val PURCHASE_CAPTION_SIZE = 12
private const val PURCHASE_AMOUNT_SIZE = 13
private const val PURCHASE_BADGE_SIZE = 9
private const val MAX_BADGE_COUNT = 999
