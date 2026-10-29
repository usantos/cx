package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSOutlinedButton as OutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSSurface as Surface
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSCurrentCartIcon
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSCurrentCartColors
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartNativeText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.formatCartAmount
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartBlue
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartText

private const val MAXIMUM_DISPLAYED_BETS = 999

/** Rodapé branco elevado de activity_carrinho; seu resumo zerado também permanece presente. */
@Composable
internal fun CurrentCartFooter(cart: ShoppingCart?, paymentEnabled: Boolean, onAdd: () -> Unit, onPay: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val hasItems = cart?.items?.isNotEmpty() == true
    val largeFont = LocalDensity.current.fontScale > 1f
    Surface(color = CaixaDSColorTokens.White, shadowElevation = 12.dp) {
        Column(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 24.dp)) {
            OutlinedButton(
                onClick = onAdd,
                modifier = (Modifier.fillMaxWidth().then(if (largeFont) Modifier.heightIn(min = 44.dp) else Modifier.height(44.dp)))
                    .uiId("cart.0048"),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CaixaDSCurrentCartColors.ActionBorder),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = CaixaDSColorTokens.White, contentColor = CurrentCartBlue)
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_adicionar_nova_aposta_d81512),
                    modifier = (if (largeFont) Modifier.fillMaxWidth() else Modifier).uiId("cart.0049"),
                    textAlign = TextAlign.Center,
                    style = cartNativeText(16, FontWeight.Bold)
                )
            }
            if (hasItems) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onPay,
                    enabled = paymentEnabled,
                    modifier = (Modifier.fillMaxWidth().then(if (largeFont) Modifier.heightIn(min = 44.dp) else Modifier.height(44.dp)))
                        .uiId("cart.0050"),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CurrentCartBlue,
                        contentColor = CaixaDSColorTokens.White,
                        disabledContainerColor = CaixaDSCurrentCartColors.DisabledAction,
                        disabledContentColor = CaixaDSColorTokens.White
                    )
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_ir_para_pagamento_5e155c),
                        modifier = (if (largeFont) Modifier.fillMaxWidth() else Modifier).uiId("cart.0051"),
                        textAlign = TextAlign.Center,
                        style = cartNativeText(16, FontWeight.Bold)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            CurrentCartAmountAndCounter(cart, hasItems)
        }
    }
}

@Composable
private fun CurrentCartAmountAndCounter(cart: ShoppingCart?, hasItems: Boolean) {
    if (LocalDensity.current.fontScale > 1f) {
        CurrentCartLargeFontSummary(cart, hasItems)
        return
    }
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().height(43.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(if (hasItems) 0.6f else 1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_total_do_carrinho_b01305),
                style = cartNativeText(),
                color = CurrentCartText,
                modifier = Modifier.uiId("cart.0052")
            )
            Text(
                formatCartAmount(cart?.total),
                style = cartNativeText(14, FontWeight.SemiBold),
                color = CurrentCartText,
                modifier = Modifier.uiId("cart.0053")
            )
        }
        if (hasItems) {
            Box(
                Modifier.legacyAutomationId(
                    "cart-summary-divider"
                ).width(1.dp).fillMaxHeight().background(CaixaDSCurrentCartColors.SummaryDivider)
            )
            BoxWithConstraints(Modifier.weight(0.4f).fillMaxHeight()) {
                Box(Modifier.padding(start = (maxWidth - 41.dp) * 0.35f).align(Alignment.CenterStart).size(41.dp, 32.dp)) {
                    Icon(
                        CaixaDSCurrentCartIcon,
                        uiStrings.getString(LocalizedR.string.ui_carrinho_61cc7d),
                        (Modifier.fillMaxSize()).uiId("cart.0054"),
                        tint = Color.Unspecified
                    )
                    Box(
                        Modifier.legacyAutomationId("cart-summary-badge").align(Alignment.TopEnd).widthIn(min = 15.dp).height(15.dp)
                            .background(CaixaDSCurrentCartColors.Badge, CircleShape).padding(horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val count = cart?.betCount ?: 0
                        Text(
                            if (count > MAXIMUM_DISPLAYED_BETS) "..." else count.toString(),
                            color = CurrentCartText,
                            maxLines = 1,
                            softWrap = false,
                            style = cartNativeText(
                                12,
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
                style = cartNativeText(14, FontWeight.SemiBold),
                color = CurrentCartText
            )
        }
        if (hasItems) {
            Box(
                Modifier.legacyAutomationId("cart-summary-divider").width(1.dp).height(48.dp)
                    .background(CaixaDSCurrentCartColors.SummaryDivider)
            )
            Box(Modifier.padding(start = 12.dp).width(64.dp).heightIn(min = 48.dp)) {
                Icon(
                    CaixaDSCurrentCartIcon,
                    uiStrings.getString(LocalizedR.string.ui_carrinho_61cc7d),
                    (Modifier.align(Alignment.CenterStart).size(41.dp, 32.dp)).uiId("cart.0058"),
                    tint = Color.Unspecified
                )
                Box(
                    Modifier.legacyAutomationId("cart-summary-badge").align(Alignment.TopEnd)
                        .background(CaixaDSCurrentCartColors.Badge, CircleShape).padding(horizontal = 4.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val count = cart?.betCount ?: 0
                    Text(
                        if (count > MAXIMUM_DISPLAYED_BETS) "..." else count.toString(),
                        modifier = (Modifier.width(IntrinsicSize.Min)).uiId("cart.0059"),
                        color = CurrentCartText,
                        softWrap = false,
                        maxLines = 1,
                        style = cartNativeText(12, FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
