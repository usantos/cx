package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSCurrentCartColors
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartNativeText
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartBlue
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSOutlinedButton as OutlinedButton
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSSurface as Surface
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.cart.R as LocalizedR

/** Rodapé branco elevado de activity_carrinho; seu resumo zerado também permanece presente. */
@Composable
internal fun CurrentCartFooter(cart: ShoppingCart?, paymentEnabled: Boolean, onAdd: () -> Unit, onPay: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val hasItems = cart?.items?.isNotEmpty() == true
    val largeFont = LocalDensity.current.fontScale > 1f
    Surface(color = LoteriasCaixaDSColorTokens.White, shadowElevation = 12.dp) {
        Column(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 24.dp)) {
            OutlinedButton(
                onClick = onAdd,
                modifier = (Modifier.fillMaxWidth().then(if (largeFont) Modifier.heightIn(min = 44.dp) else Modifier.height(44.dp)))
                    .uiId("cart.0048"),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LoteriasCaixaDSCurrentCartColors.ActionBorder),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = LoteriasCaixaDSColorTokens.White,
                    contentColor = CurrentCartBlue
                )
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
                        contentColor = LoteriasCaixaDSColorTokens.White,
                        disabledContainerColor = LoteriasCaixaDSCurrentCartColors.DisabledAction,
                        disabledContentColor = LoteriasCaixaDSColorTokens.White
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
