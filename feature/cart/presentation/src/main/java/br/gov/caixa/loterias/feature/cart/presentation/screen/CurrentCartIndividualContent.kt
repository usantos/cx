package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSBetTypeIcons
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSCurrentCartColors
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartNativeText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.cartText

@Composable
internal fun CurrentCartNumbers(item: CartItem, loteca: Boolean, superSeven: Boolean) {
    val superSevenColumnWidth = with(LocalDensity.current) { 22.dp.toPx().toInt().toDp() }
    val superSevenColumnGap = with(LocalDensity.current) { 6.dp.toPx().toInt().toDp() }
    Column {
        if (loteca) {
            // As seleções de partidas ocupam toda a largura do cartão abaixo do concurso e do valor.
        } else if (superSeven) {
            Row(Modifier.padding(start = superSevenColumnGap), horizontalArrangement = Arrangement.spacedBy(superSevenColumnGap)) {
                repeat(SUPER_SEVEN_COLUMN_COUNT) { column ->
                    Column(Modifier.width(superSevenColumnWidth), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.size(superSevenColumnWidth).background(LoteriasCaixaDSColorTokens.TicketChoiceMuted)
                                .legacyAutomationId("cart-super-seven-header-$column"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                (column + 1).toString(),
                                color = LoteriasCaixaDSColorTokens.White,
                                style = cartNativeText(
                                    NUMBER_AND_BADGE_LABEL_TEXT_SIZE,
                                    FontWeight.Bold
                                ).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily),
                                modifier = Modifier.uiId("cart.0113")
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        val numbers = if (CartItemBadge.SURPRISE in item.badges) {
                            listOf("XX")
                        } else {
                            item.selectedNumbers.getOrNull(column).orEmpty().map(Int::toString)
                        }
                        numbers.forEach {
                            Text(
                                it,
                                style = cartText(SUPER_SEVEN_DIGIT_TEXT_SIZE).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily),
                                color = LoteriasCaixaDSCurrentCartColors.PoolText,
                                modifier = Modifier.uiId("cart.0114")
                            )
                        }
                    }
                }
            }
        } else {
            val lines = if (CartItemBadge.SURPRISE in item.badges) {
                listOf(List(NUMBERS_PER_LINE) { "XX" }.joinToString(" - "))
            } else {
                item.selectedNumbers.flatten().chunked(NUMBERS_PER_LINE).map { numbers ->
                    numbers.joinToString(" - ") { it.toString().padStart(2, '0') }
                }
            }
            lines.forEach {
                Text(
                    it,
                    style = cartText().copy(letterSpacing = 1.4.sp),
                    color = CurrentCartText,
                    modifier = Modifier
                        .uiId("cart.0115")
                )
            }
            if (lines.isEmpty() &&
                item.description.isNotBlank()
            ) {
                Text(item.description, style = cartNativeText(), color = CurrentCartText, modifier = Modifier.uiId("cart.0116"))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CurrentCartBadges(indicators: List<CartItemBadge>) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        indicators.forEach { badge ->
            val icon = when (badge) {
                CartItemBadge.MIRROR -> LoteriasCaixaDSBetTypeIcons.Mirror
                CartItemBadge.SURPRISE -> LoteriasCaixaDSBetTypeIcons.Surprise
                else -> LoteriasCaixaDSBetTypeIcons.Recurring
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, (Modifier.size(icon.defaultWidth, icon.defaultHeight)).uiId("cart.0120"), tint = CurrentCartText)
                Spacer(Modifier.width(6.dp))
                Text(
                    when (badge) {
                        CartItemBadge.MIRROR -> uiStrings.getString(LocalizedR.string.ui_espelho_0c9ce2)
                        CartItemBadge.SURPRISE -> uiStrings.getString(LocalizedR.string.ui_surpresinha_34302e)
                        else -> uiStrings.getString(LocalizedR.string.ui_teimosinha_dba980)
                    },
                    style = cartNativeText(
                        NUMBER_AND_BADGE_LABEL_TEXT_SIZE,
                        FontWeight.Bold
                    ).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily),
                    color = CurrentCartText,
                    modifier = Modifier.uiId("cart.0121")
                )
            }
        }
    }
}

private const val SUPER_SEVEN_COLUMN_COUNT = 7
private const val NUMBER_AND_BADGE_LABEL_TEXT_SIZE = 14
private const val SUPER_SEVEN_DIGIT_TEXT_SIZE = 12
private const val NUMBERS_PER_LINE = 6
