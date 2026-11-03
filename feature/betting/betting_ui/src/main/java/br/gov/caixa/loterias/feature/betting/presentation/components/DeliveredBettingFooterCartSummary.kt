package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
internal fun BettingFooterCartSummary(state: BettingGameUiState, cartTotal: java.math.BigDecimal, cartCount: Int, onCart: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().height(SummaryHeight), verticalAlignment = Alignment.CenterVertically) {
        BettingDSFooterAmount(
            uiStrings.getString(DeliveryR.string.ui_valor_dessa_aposta_4e1785),
            formatBetAmount(state.totalPrice),
            Modifier.weight(1f)
        )
        Box(Modifier.width(1.dp).height(SummaryDividerHeight).background(LoteriasCaixaDSColorTokens.SimulationMuted))
        BettingDSFooterAmount(
            uiStrings.getString(DeliveryR.string.ui_total_do_carrinho_b01305),
            formatBetAmount(cartTotal),
            Modifier.weight(1f)
        )
        Box(Modifier.width(1.dp).height(SummaryDividerHeight).background(LoteriasCaixaDSColorTokens.SimulationMuted))
        BettingDSCartFooter(cartCount, onCart)
    }
}

/** Carrinho e badge desenhados em Compose com dimensões e cores do LoteriasCaixaDS/rodapé legado. */
@Composable
internal fun BettingDSCartFooter(cartCount: Int, onCart: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.width(CartWidth).fillMaxHeight().legacyAutomationId("betting-cart-footer")
            .uiId("bet.0032").clickable(enabled = onCart != null, role = Role.Button) { onCart?.invoke() }
            .semantics {
                contentDescription = uiStrings.getString(DeliveryR.string.ui_carrinho_de_apostas_1_s_itens_e4b649, cartCount.toString())
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(DeliveryR.drawable.ic_cart),
            contentDescription = null,
            modifier = Modifier.size(CartIconWidth, SummaryDividerHeight)
        )
        Box(
            Modifier.align(Alignment.TopEnd).padding(top = LabelTopGap, end = ContentGap)
                .size(CounterText).background(LoteriasCaixaDSColorTokens.SimulationBadge, CircleShape)
        )
        Box(
            Modifier.align(Alignment.TopEnd).padding(top = LabelTopGap, end = ContentGap).size(ContentInset),
            contentAlignment = Alignment.Center
        ) {
            Text(
                cartCount.toString(),
                color = LoteriasCaixaDSColorTokens.SocialGraphite,
                fontFamily = LoteriasCaixaDSFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = ControlCornerFontSize,
                textAlign = TextAlign.Center,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
                modifier = Modifier.uiId("bet.0033")
            )
        }
    }
}

/** Rótulos do rodapé em tipografia e métrica do LoteriasCaixaDS, com o pixel observado no XCover. */
@Composable
internal fun BettingDSFooterAmount(label: String, amount: String, modifier: Modifier) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    fun roundedSp(size: Float) = with(density) { (size.sp.roundToPx().toFloat() / this.density / this.fontScale).sp }
    Column(
        modifier.wrapContentHeight().legacyAutomationId("betting-footer-amount-$label")
            .semantics { contentDescription = "$label $amount" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            label,
            modifier = (Modifier.offset { IntOffset(-1, 0) }).uiId("bet.0034"),
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = roundedSp(SUMMARY_LABEL_TEXT_SIZE),
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true))
        )
        Text(
            amount,
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            fontFamily = LoteriasCaixaDSFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = roundedSp(SUMMARY_AMOUNT_TEXT_SIZE),
            textAlign = TextAlign.Center,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
            modifier = Modifier.uiId("bet.0035")
        )
    }
}

private val LabelTopGap = 10.dp
private val ControlCornerFontSize = 12.sp
private val CounterText = 15.dp
private val ContentInset = 16.dp
private val SummaryDividerHeight = 32.dp
private val CartIconWidth = 36.dp
private val CartWidth = 56.dp
private val SummaryHeight = 64.dp
private val ContentGap = 8.dp

private const val SUMMARY_LABEL_TEXT_SIZE = 11f

private const val SUMMARY_AMOUNT_TEXT_SIZE = 14f
