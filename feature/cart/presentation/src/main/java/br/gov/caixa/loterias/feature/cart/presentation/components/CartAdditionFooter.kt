package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import java.math.BigDecimal

@Composable
internal fun PoolCartFooter(amount: BigDecimal, onOpenCart: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val labelStyle = TextStyle(
        fontFamily = CaixaDSFontFamily,
        fontSize = 12.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = true),
        textAlign = TextAlign.Center
    )
    Row(Modifier.fillMaxWidth().height(94.dp).background(CaixaDSColorTokens.BetFooter)) {
        Column(
            Modifier.weight(1f).fillMaxSize().padding(bottom = 13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                formatBrazilianCurrency(amount),
                (Modifier.padding(bottom = 12.dp)).uiId("cart.0003"),
                color = CaixaDSColorTokens.SecondaryText,
                style = labelStyle.copy(fontSize = if (amount > BigDecimal("999.99")) 9.8.sp else 14.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_valor_dessa_aposta_127399),
                color = CaixaDSColorTokens.BetLink,
                style = labelStyle,
                modifier = Modifier.uiId("cart.0004")
            )
        }
        Spacer(Modifier.width(184.dp))
        Column(
            Modifier.weight(1f).fillMaxSize().uiId("cart.0005").clickable(onClick = onOpenCart).padding(top = 8.dp, bottom = 13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painterResource(br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_icon_carrinho),
                contentDescription = null,
                modifier = Modifier.width(34.dp).height(40.dp)
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_carrinho_de_apostas_9fc087),
                color = CaixaDSColorTokens.BetLink,
                style = labelStyle,
                modifier = Modifier.uiId("cart.0006")
            )
        }
    }
}
