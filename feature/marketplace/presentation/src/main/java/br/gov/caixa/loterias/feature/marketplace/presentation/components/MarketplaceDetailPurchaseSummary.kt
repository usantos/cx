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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplaceDetailMoney
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceDetailUiState
import java.math.BigDecimal

@Composable
internal fun DetailPurchaseSummary(state: MarketplaceDetailUiState, cartTotal: BigDecimal, cartBetCount: Int, cart: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val ink = LoteriasCaixaDSColorTokens.MarketplaceFooterText
    Row(
        Modifier.padding(horizontal = 24.dp).padding(bottom = 8.dp).fillMaxWidth().height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DetailFooterValue(
            uiStrings.getString(LocalizedR.string.ui_valor_dessa_aposta_4e1785),
            marketplaceDetailMoney(state.purchaseAmount.toPlainString()),
            (Modifier.weight(1f)).uiId("pool.0065")
        )
        Box(Modifier.width(1.dp).height(44.dp).background(LoteriasCaixaDSColorTokens.MarketplaceFooterDivider))
        DetailFooterValue(
            uiStrings.getString(LocalizedR.string.ui_total_do_carrinho_b01305),
            marketplaceDetailMoney(cartTotal.toPlainString()),
            (Modifier.weight(1f)).uiId("pool.0066")
        )
        Box(Modifier.width(1.dp).height(44.dp).background(LoteriasCaixaDSColorTokens.MarketplaceFooterDivider))
        Box(
            Modifier.width(60.dp).fillMaxHeight()
                .uiId("pool.0067").clickable(enabled = cart != null, role = Role.Button) { cart?.invoke() }
                .semantics { contentDescription = uiStrings.getString(LocalizedR.string.ui_carrinho_de_apostas_23d953) },
            Alignment.Center
        ) {
            Icon(LoteriasCaixaDSIcons.Cart, null, (Modifier.size(34.dp)).uiId("pool.0068"), tint = ink)
            Box(
                Modifier.align(
                    Alignment.TopEnd
                ).padding(top = 11.dp, end = 10.dp).size(18.dp).background(LoteriasCaixaDS.colors.accent, CircleShape),
                Alignment.Center
            ) {
                Text(
                    if (cartBetCount >
                        CART_BADGE_MAX_COUNT
                    ) {
                        uiStrings.getString(LocalizedR.string.ui_999_d78178)
                    } else {
                        cartBetCount.toString()
                    },
                    style = detailText(CART_BADGE_TEXT_SIZE, FontWeight.Bold),
                    color = ink,
                    modifier = Modifier.uiId("pool.0069")
                )
            }
        }
    }
}

@Composable
internal fun DetailFooterValue(label: String, value: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            label,
            style = detailText(PURCHASE_SUMMARY_LABEL_SIZE),
            color = LoteriasCaixaDSColorTokens.MarketplaceFooterText,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("pool.0072")
        )
        Text(
            value,
            style = detailText(PURCHASE_SUMMARY_VALUE_SIZE, FontWeight.Bold),
            color = LoteriasCaixaDSColorTokens.MarketplaceFooterText,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("pool.0073")
        )
    }
}

private const val CART_BADGE_MAX_COUNT = 999
private const val CART_BADGE_TEXT_SIZE = 9
private const val PURCHASE_SUMMARY_LABEL_SIZE = 12
private const val PURCHASE_SUMMARY_VALUE_SIZE = 13
