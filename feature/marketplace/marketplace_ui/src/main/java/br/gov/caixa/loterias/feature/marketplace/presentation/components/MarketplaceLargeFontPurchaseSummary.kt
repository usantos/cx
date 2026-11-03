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
import androidx.compose.foundation.shape.CircleShape
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
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplaceDetailMoney
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import br.gov.caixa.loterias.feature.marketplace.ui.R as DeliveryR
import java.math.BigDecimal

@Composable
fun LargeFontPurchaseSummary(state: MarketplaceDetailUiState, cartTotal: BigDecimal, cartBetCount: Int, cart: (() -> Unit)?) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        LargeFontDetailValue(
            stringResource(DeliveryR.string.ui_valor_dessa_aposta_4e1785),
            marketplaceDetailMoney(state.purchaseAmount.toPlainString()),
            (Modifier.weight(1f)).uiId("pool.0101")
        )
        Box(Modifier.width(1.dp).height(44.dp).background(LoteriasCaixaDSColorTokens.MarketplaceFooterDivider))
        LargeFontDetailValue(
            stringResource(DeliveryR.string.ui_total_do_carrinho_b01305),
            marketplaceDetailMoney(cartTotal.toPlainString()),
            (Modifier.weight(1f)).uiId("pool.0102")
        )
        LargeFontDetailCart(cartBetCount, cart)
    }
}

@Composable
fun LargeFontDetailValue(label: String, value: String, modifier: Modifier) {
    Column(
        modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            label,
            (Modifier.fillMaxWidth()).uiId("pool.0103"),
            style = detailText(PURCHASE_CAPTION_SIZE),
            color = LoteriasCaixaDSColorTokens.MarketplaceFooterText,
            textAlign = TextAlign.Center
        )
        Text(
            value,
            (Modifier.fillMaxWidth()).uiId("pool.0104"),
            style = detailText(PURCHASE_AMOUNT_SIZE, FontWeight.Bold),
            color = LoteriasCaixaDSColorTokens.MarketplaceFooterText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LargeFontDetailCart(count: Int, onCart: (() -> Unit)?) {
    val description = stringResource(DeliveryR.string.ui_carrinho_de_apostas_23d953)
    Box(
        Modifier.width(60.dp).heightIn(min = 56.dp)
            .uiId("pool.0105").clickable(enabled = onCart != null, role = Role.Button) { onCart?.invoke() }
            .semantics { contentDescription = description },
        Alignment.Center
    ) {
        Icon(
            LoteriasCaixaDSIcons.Cart,
            null,
            (Modifier.size(34.dp)).uiId("pool.0106"),
            tint = LoteriasCaixaDSColorTokens.MarketplaceFooterText
        )
        Box(
            Modifier.align(
                Alignment.TopEnd
            ).background(LoteriasCaixaDS.colors.accent, CircleShape).padding(horizontal = 4.dp, vertical = 2.dp),
            Alignment.Center
        ) {
            Text(
                if (count > MAX_BADGE_COUNT) stringResource(DeliveryR.string.ui_999_d78178) else count.toString(),
                (Modifier.width(IntrinsicSize.Min)).uiId("pool.0107"),
                softWrap = false,
                style = detailText(PURCHASE_BADGE_SIZE, FontWeight.Bold),
                color = LoteriasCaixaDSColorTokens.MarketplaceFooterText
            )
        }
    }
}
