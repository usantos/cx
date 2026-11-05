package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatBetAmount
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun RapidBetDetails(item: CartItem, style: LoteriasCaixaDSLotteryStyle, offset: Float, panelWidth: Float) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.width(229.dp).offset { IntOffset((offset - panelWidth).toInt(), 0) }
            .background(style.dark).padding(top = 14.dp, bottom = 14.dp)
    ) {
        Row {
            Column(Modifier.padding(start = 16.dp)) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_voce_apostou_773116),
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontSize = 14.sp,
                    color = LoteriasCaixaDSColorTokens.White,
                    modifier = Modifier.uiId("bet.0198")
                )
                Text(
                    if (item.numberQuantity == 0) {
                        uiStrings.getString(DeliveryR.string.format_rapid_no_numbers)
                    } else {
                        uiStrings.getQuantityString(
                            DeliveryR.plurals.format_rapid_number_quantity,
                            item.numberQuantity,
                            item.numberQuantity
                        )
                    },
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = LoteriasCaixaDSColorTokens.White,
                    modifier = Modifier.uiId("bet.0199")
                )
            }
            Column(Modifier.padding(start = 30.dp)) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_para_237b14),
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontSize = 14.sp,
                    color = LoteriasCaixaDSColorTokens.White,
                    modifier = Modifier.uiId("bet.0200")
                )
                Text(
                    uiStrings.getQuantityString(
                        DeliveryR.plurals.format_rapid_draw_quantity,
                        item.recurringDraws.coerceAtLeast(1),
                        item.recurringDraws.coerceAtLeast(1)
                    ),
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = LoteriasCaixaDSColorTokens.White,
                    modifier = Modifier.uiId("bet.0201")
                )
            }
        }
        item.complementaryLabel?.let {
            Text(
                it,
                (Modifier.padding(start = 16.dp, top = 4.dp)).uiId("bet.0202"),
                fontFamily = LoteriasCaixaDSFontFamily,
                fontSize = 14.sp,
                color = LoteriasCaixaDSColorTokens.White
            )
        }
    }
}

@Composable
fun RapidBetSummary(item: CartItem, style: LoteriasCaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().padding(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(Modifier.width(100.dp)) {
            Text(
                item.title.lowercase(),
                fontFamily = LoteriasCaixaDSFuturaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.2.sp,
                color = style.listText,
                maxLines = 1,
                modifier = Modifier.uiId("bet.0204")
            )
            Text(
                uiStrings.getString(DeliveryR.string.ui_conc_1_s_f4692e, item.contestNumber ?: ""),
                fontFamily = LoteriasCaixaDSFontFamily,
                fontSize = 14.2.sp,
                color = LoteriasCaixaDS.colors.text,
                maxLines = 1,
                modifier = Modifier.uiId("bet.0205")
            )
        }
        Column(Modifier.weight(1f).padding(start = 16.dp)) {
            Text(
                if (item.selectedNumbers.isNotEmpty()) {
                    item.selectedNumbers.flatten().joinToString(" - ") {
                        if (it ==
                            LOTOMANIA_ZERO_NUMBER
                        ) {
                            "00"
                        } else {
                            it.toString().padStart(2, '0')
                        }
                    }
                } else {
                    item.description
                },
                fontFamily = LoteriasCaixaDSFontFamily,
                fontSize = 14.2.sp,
                color = LoteriasCaixaDS.colors.graphite,
                modifier = Modifier.uiId("bet.0206")
            )
            item.complementaryLabel?.let {
                Text(
                    it,
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontSize = 14.2.sp,
                    color = LoteriasCaixaDS.colors.graphite,
                    modifier = Modifier.uiId("bet.0207")
                )
            }
        }
        Text(
            formatBetAmount(item.amount),
            (Modifier.width(70.dp)).uiId("bet.0208"),
            fontFamily = LoteriasCaixaDSFontFamily,
            fontSize = 14.2.sp,
            color = LoteriasCaixaDS.colors.text,
            textAlign = TextAlign.End
        )
    }
}

private const val LOTOMANIA_ZERO_NUMBER = 100
