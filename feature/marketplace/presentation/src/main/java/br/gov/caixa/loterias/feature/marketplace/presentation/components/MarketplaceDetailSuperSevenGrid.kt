package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSMarketplaceNumberRing
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet

private const val SUPER_SEVEN_TEXT_SIZE = 14
private const val SUPER_SEVEN_COLUMN_COUNT = 7

@Composable
internal fun DetailSuperSevenGrid(position: Int, bet: MarketplaceBet, textColor: Color, columnColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val measurer = rememberTextMeasurer()
    val headerWidth = with(LocalDensity.current) { measurer.measure("1", detailText(SUPER_SEVEN_TEXT_SIZE)).size.width.toDp() } + 16.dp
    val columnStride = headerWidth + 24.dp
    val firstColumnStart = 24.dp + headerWidth / 2 - 25.5.dp
    Column(Modifier.fillMaxWidth()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_jogo_1_s_aa1e61, (position + 1).toString()),
            (Modifier.padding(start = 16.dp, top = 16.dp)).uiId("pool.0045"),
            style = detailText(SUPER_SEVEN_TEXT_SIZE, FontWeight.Bold),
            color = textColor
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_colunas_dbcb93),
            (Modifier.padding(start = 16.dp, top = 8.dp)).uiId("pool.0046"),
            style = detailText(SUPER_SEVEN_TEXT_SIZE),
            color = textColor
        )
        Row(Modifier.padding(start = 24.dp, top = 8.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            repeat(SUPER_SEVEN_COLUMN_COUNT) { index ->
                Text(
                    (index + 1).toString(),
                    (Modifier.background(columnColor).padding(8.dp))
                        .uiId("pool.0047"),
                    style = detailText(SUPER_SEVEN_TEXT_SIZE),
                    color = LoteriasCaixaDSColorTokens.White
                )
            }
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_numeros_186859),
            (Modifier.padding(start = 16.dp, top = 16.dp)).uiId("pool.0048"),
            style = detailText(SUPER_SEVEN_TEXT_SIZE),
            color = textColor
        )
        DetailSuperSevenNumbers(position, bet, firstColumnStart, columnStride)
    }
}

@Composable
private fun DetailSuperSevenNumbers(
    position: Int,
    bet: MarketplaceBet,
    firstColumnStart: androidx.compose.ui.unit.Dp,
    columnStride: androidx.compose.ui.unit.Dp,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.fillMaxWidth().padding(top = 8.dp).height(51.dp * (bet.numberColumns.maxOfOrNull { it.size } ?: 0))) {
        repeat(SUPER_SEVEN_COLUMN_COUNT) { column ->
            Column(
                Modifier.offset(
                    x = firstColumnStart + columnStride * column + if (column ==
                        0
                    ) {
                        4.dp
                    } else {
                        0.dp
                    }
                ).width(51.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                bet.numberColumns.getOrNull(column).orEmpty().forEachIndexed { row, number ->
                    Box(
                        Modifier.padding(vertical = 8.dp).size(35.dp)
                            .legacyAutomationId("pool-detail-super-$position-$column-$row")
                            .semantics {
                                contentDescription = uiStrings.getString(
                                    LocalizedR.string.ui_coluna_1_s_numero_2_s_4e3a5f,
                                    (column + 1).toString(),
                                    number.toString()
                                )
                            },
                        Alignment.Center
                    ) {
                        Icon(
                            LoteriasCaixaDSMarketplaceNumberRing,
                            null,
                            (Modifier.fillMaxSize()).uiId("pool.0049"),
                            tint = LoteriasCaixaDSColorTokens.MarketplaceNumber
                        )
                        Text(
                            number.toString(),
                            style = detailText(SUPER_SEVEN_TEXT_SIZE, FontWeight.Bold),
                            color = LoteriasCaixaDSColorTokens.MarketplaceNumber,
                            modifier = Modifier.uiId("pool.0050")
                        )
                    }
                }
            }
        }
    }
}
