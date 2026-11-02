package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSCurrentCartIcon
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSCurrentCartColors
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartNativeText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.formatCartAmount
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartText

@Composable
internal fun CurrentCartAmountAndCounter(cart: ShoppingCart?, hasItems: Boolean) {
    if (LocalDensity.current.fontScale > 1f) {
        CurrentCartLargeFontSummary(cart, hasItems)
        return
    }
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().height(43.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(if (hasItems) SUMMARY_AMOUNT_WEIGHT else 1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_total_do_carrinho_b01305),
                style = cartNativeText(),
                color = CurrentCartText,
                modifier = Modifier.uiId("cart.0052")
            )
            Text(
                formatCartAmount(cart?.total),
                style = cartNativeText(SUMMARY_AMOUNT_TEXT_SIZE, FontWeight.SemiBold),
                color = CurrentCartText,
                modifier = Modifier.uiId("cart.0053")
            )
        }
        if (hasItems) {
            Box(
                Modifier.legacyAutomationId(
                    "cart-summary-divider"
                ).width(1.dp).fillMaxHeight().background(LoteriasCaixaDSCurrentCartColors.SummaryDivider)
            )
            BoxWithConstraints(Modifier.weight(SUMMARY_COUNT_WEIGHT).fillMaxHeight()) {
                Box(
                    Modifier.padding(
                        start = (maxWidth - 41.dp) * SUMMARY_ICON_OFFSET_FRACTION
                    ).align(Alignment.CenterStart).size(41.dp, 32.dp)
                ) {
                    Icon(
                        LoteriasCaixaDSCurrentCartIcon,
                        uiStrings.getString(LocalizedR.string.ui_carrinho_61cc7d),
                        (Modifier.fillMaxSize()).uiId("cart.0054"),
                        tint = Color.Unspecified
                    )
                    Box(
                        Modifier.legacyAutomationId("cart-summary-badge").align(Alignment.TopEnd).widthIn(min = 15.dp).height(15.dp)
                            .background(LoteriasCaixaDSCurrentCartColors.Badge, CircleShape).padding(horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val count = cart?.betCount ?: 0
                        Text(
                            if (count > MAXIMUM_DISPLAYED_BETS) "..." else count.toString(),
                            color = CurrentCartText,
                            maxLines = 1,
                            softWrap = false,
                            style = cartNativeText(
                                SUMMARY_COUNT_TEXT_SIZE,
                                FontWeight.SemiBold
                            ).copy(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            modifier = Modifier.uiId("cart.0055")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentCartLargeFontSummary(cart: ShoppingCart?, hasItems: Boolean) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_total_do_carrinho_b01305),
                modifier = (Modifier.fillMaxWidth()).uiId("cart.0056"),
                style = cartNativeText(),
                color = CurrentCartText,
                textAlign = TextAlign.Center
            )
            Text(
                formatCartAmount(cart?.total),
                modifier = (Modifier.fillMaxWidth()).uiId("cart.0057"),
                textAlign = TextAlign.Center,
                style = cartNativeText(SUMMARY_AMOUNT_TEXT_SIZE, FontWeight.SemiBold),
                color = CurrentCartText
            )
        }
        if (hasItems) {
            Box(
                Modifier.legacyAutomationId("cart-summary-divider").width(1.dp).height(48.dp)
                    .background(LoteriasCaixaDSCurrentCartColors.SummaryDivider)
            )
            Box(Modifier.padding(start = 12.dp).width(64.dp).heightIn(min = 48.dp)) {
                Icon(
                    LoteriasCaixaDSCurrentCartIcon,
                    uiStrings.getString(LocalizedR.string.ui_carrinho_61cc7d),
                    (Modifier.align(Alignment.CenterStart).size(41.dp, 32.dp)).uiId("cart.0058"),
                    tint = Color.Unspecified
                )
                Box(
                    Modifier.legacyAutomationId("cart-summary-badge").align(Alignment.TopEnd)
                        .background(LoteriasCaixaDSCurrentCartColors.Badge, CircleShape).padding(horizontal = 4.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val count = cart?.betCount ?: 0
                    Text(
                        if (count > MAXIMUM_DISPLAYED_BETS) "..." else count.toString(),
                        modifier = (Modifier.width(IntrinsicSize.Min)).uiId("cart.0059"),
                        color = CurrentCartText,
                        softWrap = false,
                        maxLines = 1,
                        style = cartNativeText(SUMMARY_COUNT_TEXT_SIZE, FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

private const val MAXIMUM_DISPLAYED_BETS = 999

private const val SUMMARY_AMOUNT_TEXT_SIZE = 14

private const val SUMMARY_COUNT_TEXT_SIZE = 12

private const val SUMMARY_AMOUNT_WEIGHT = 0.6f

private const val SUMMARY_COUNT_WEIGHT = 0.4f

private const val SUMMARY_ICON_OFFSET_FRACTION = 0.35f
