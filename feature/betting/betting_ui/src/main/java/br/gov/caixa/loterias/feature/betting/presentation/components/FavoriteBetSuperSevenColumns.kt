package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun FavoriteSuperSevenColumns(
    columns: List<List<Int>>,
    style: br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row {
        columns.forEachIndexed { index, numbers ->
            Column(Modifier.width(ColumnWidth), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.padding(top = HeaderInset).size(ColumnBadgeSize).background(style.light, RoundedCornerShape(CornerRadius)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiStrings.getString(DeliveryR.string.ui_1_s_d020a2, (index + 1).toString()),
                        color = style.content,
                        fontSize = NumberFontSize,
                        fontWeight = FontWeight.Bold,
                        fontFamily = LoteriasCaixaDSFuturaFontFamily,
                        style = favoriteLegacyTextStyle,
                        modifier = Modifier.uiId("bet.0121")
                    )
                }
                Column(Modifier.padding(top = NumberColumnTopGap)) {
                    numbers.sorted().forEach { number ->
                        Box(
                            Modifier.size(
                                NumberSize
                            ).background(
                                LoteriasCaixaDSColorTokens.White,
                                RoundedCornerShape(NumberCorner)
                            ).border(1.dp, style.dark, RoundedCornerShape(NumberCorner)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                number.toString(),
                                color = style.content,
                                fontSize = NumberFontSize,
                                style = favoriteLegacyTextStyle,
                                modifier = Modifier.uiId("bet.0122")
                            )
                        }
                    }
                }
            }
        }
    }
}

private val NumberFontSize = 14.sp
private val NumberCorner = 15.dp
private val ColumnBadgeSize = 25.dp
private val NumberSize = 30.dp
private val CornerRadius = 4.dp
private val ColumnWidth = 41.dp
private val NumberColumnTopGap = 7.dp
private val HeaderInset = 8.dp
