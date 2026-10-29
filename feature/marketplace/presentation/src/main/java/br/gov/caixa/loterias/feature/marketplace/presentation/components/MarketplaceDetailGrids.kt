package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSDetailClover
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSMarketplaceNumberRing
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet
import kotlin.math.roundToInt

@Composable
internal fun DetailBetGrid(position: Int, bet: MarketplaceBet, color: Color, modality: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (modality.contains("SUPER", true) && bet.numberColumns.isNotEmpty()) {
        DetailSuperSevenGrid(position, bet, color, marketplaceColors(modality).dark)
        return
    }
    Column(Modifier.fillMaxWidth()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_jogo_1_s_aa1e61, (position + 1).toString()),
            (Modifier.padding(16.dp)).uiId("pool.0037"),
            style = detailText(14, FontWeight.Bold),
            color = color
        )
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            bet.numbers.chunked(6).forEachIndexed { rowIndex, numbers ->
                Row(Modifier.fillMaxWidth()) {
                    repeat(6) { column ->
                        Box(Modifier.weight(1f).padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            numbers.getOrNull(column)?.let { number ->
                                Box(
                                    Modifier.size(35.dp).legacyAutomationId("pool-detail-ball-$position-${rowIndex * 6 + column}"),
                                    Alignment.Center
                                ) {
                                    Icon(
                                        CaixaDSMarketplaceNumberRing,
                                        null,
                                        (Modifier.fillMaxSize()).uiId("pool.0038"),
                                        tint = CaixaDSColorTokens.MarketplaceNumber
                                    )
                                    Text(
                                        if (number == 0 || number == 100) {
                                            uiStrings.getString(
                                                LocalizedR.string.ui_00_f15343
                                            )
                                        } else {
                                            number.toString()
                                        },
                                        style = detailText(14, FontWeight.Bold),
                                        color = CaixaDSColorTokens.MarketplaceNumber,
                                        modifier = Modifier.uiId("pool.0039")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (modality.contains("TIMEMANIA", true)) {
            Text(
                uiStrings.getString(
                    LocalizedR.string.ui_time_do_coracao_1_s_7e407c,
                    bet.favoriteTeam?.let(::detailFavoriteTeamName) ?: "-"
                ),
                (Modifier.padding(16.dp)).uiId("pool.0040"),
                style = detailText(14),
                color = color
            )
        }
        if (modality.contains("DIA", true)) {
            Text(
                uiStrings.getString(
                    LocalizedR.string.ui_mes_de_sorte_1_s_42ca39,
                    bet.luckyMonth?.name ?: "-"
                ),
                (Modifier.padding(16.dp)).uiId("pool.0041"),
                style = detailText(14),
                color = color
            )
        }
        DetailClovers(position, bet, color)
    }
}

@Composable
private fun DetailSuperSevenGrid(position: Int, bet: MarketplaceBet, textColor: Color, columnColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val measurer = rememberTextMeasurer()
    val headerWidth = with(LocalDensity.current) { measurer.measure("1", detailText(14)).size.width.toDp() } + 16.dp
    val columnStride = headerWidth + 24.dp
    val firstColumnStart = 24.dp + headerWidth / 2 - 25.5.dp
    Column(Modifier.fillMaxWidth()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_jogo_1_s_aa1e61, (position + 1).toString()),
            (Modifier.padding(start = 16.dp, top = 16.dp)).uiId("pool.0045"),
            style = detailText(14, FontWeight.Bold),
            color = textColor
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_colunas_dbcb93),
            (Modifier.padding(start = 16.dp, top = 8.dp)).uiId("pool.0046"),
            style = detailText(14),
            color = textColor
        )
        Row(Modifier.padding(start = 24.dp, top = 8.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            repeat(7) { index ->
                Text(
                    (index + 1).toString(),
                    (Modifier.background(columnColor).padding(8.dp))
                        .uiId("pool.0047"),
                    style = detailText(14),
                    color = CaixaDSColorTokens.White
                )
            }
        }
        Text(
            uiStrings.getString(LocalizedR.string.ui_numeros_186859),
            (Modifier.padding(start = 16.dp, top = 16.dp)).uiId("pool.0048"),
            style = detailText(14),
            color = textColor
        )
        DetailSuperSevenNumbers(position, bet, firstColumnStart, columnStride)
    }
}

private fun detailFavoriteTeamName(team: SportsTeam): String =
    if (team.name.isBlank()) "-" else team.name + team.state.takeIf { it.isNotBlank() }?.let { "-$it" }.orEmpty()

private fun detailTeamName(team: SportsTeam): String {
    if (team.selectionIndicator) return team.name
    val suffix = if (team.countryCode.isNotBlank() &&
        (!team.countryCode.equals("BRA", true) || team.name.equals("BRASIL", true))
    ) {
        team.countryCode
    } else {
        team.state
    }
    return team.name + suffix.takeIf { it.isNotBlank() }?.let { "/$it" }.orEmpty()
}

@Composable
internal fun DetailLotecaMatch(index: Int, selection: LotecaMatchSelection) {
    val legend = selection.match.legendDescription.takeIf { it.length > 4 }?.drop(4)?.let { "  ($it)" }.orEmpty()
    Column(Modifier.fillMaxWidth().padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailLotecaChoice("${detailTeamName(selection.match.home)}$legend", selection.homeWin, "pool-detail-loteca-$index-home")
        DetailLotecaChoice("X", selection.draw, "pool-detail-loteca-$index-draw")
        DetailLotecaChoice(
            "${detailTeamName(selection.match.away)}$legend",
            selection.awayWin,
            "pool-detail-loteca-$index-away",
            endAtLabelBottom = true
        )
        DetailDivider(Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun DetailLotecaChoice(label: String, checked: Boolean, tag: String, endAtLabelBottom: Boolean = false) {
    Layout(modifier = Modifier.fillMaxWidth().padding(start = 16.dp), content = {
        Box(
            Modifier.size(28.dp, if (checked) 28.dp else 27.dp).legacyAutomationId(tag)
                .semantics {
                    selected = checked
                    role = Role.RadioButton
                    contentDescription = label
                }
        ) {
            // ImageView da CX desenha este vetor translúcido como fundo e como imagem tonalizada.
            repeat(2) {
                Icon(
                    painterResource(
                        if (checked) R.drawable.legacy_marketplace_ic_radio_checked else R.drawable.legacy_marketplace_ic_radio_unchecked
                    ),
                    null,
                    (Modifier.fillMaxSize()).uiId("pool.0051"),
                    tint = CaixaDSColorTokens.Black
                )
            }
        }
        Text(label, (Modifier.fillMaxWidth()).uiId("pool.0052"), style = detailText(14, FontWeight.Bold), color = CaixaDSColorTokens.Black)
    }) { measurables, constraints ->
        val indicator = measurables[0].measure(constraints.copy(minWidth = 0, minHeight = 0))
        val gap = 8.dp.roundToPx()
        val textWidth = (constraints.maxWidth - indicator.width - gap).coerceAtLeast(0)
        val text = measurables[1].measure(Constraints(maxWidth = textWidth))
        val textTop = ((indicator.height - text.height) / 2f).roundToInt()
        // O divisor da CX é restrito abaixo do TextView do visitante, e não do botão de opção mais alto.
        val height = if (endAtLabelBottom) textTop + text.height else indicator.height
        layout(constraints.maxWidth, height) {
            indicator.placeRelative(0, 0)
            text.placeRelative(indicator.width + gap, textTop)
        }
    }
}

@Composable
private fun DetailClovers(position: Int, bet: MarketplaceBet, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (bet.clovers.isNotEmpty()) {
        Box(Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_trevos_0f4867),
                (Modifier.padding(start = 16.dp)).uiId("pool.0042"),
                style = detailText(14),
                color = color
            )
            Column(Modifier.padding(top = 16.dp)) {
                bet.clovers.chunked(6).forEachIndexed { rowIndex, clovers ->
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        repeat(6) { column ->
                            Box(Modifier.weight(1f).padding(vertical = 8.dp), Alignment.Center) {
                                clovers.getOrNull(column)?.let { number ->
                                    Box(
                                        Modifier.size(
                                            35.dp
                                        ).legacyAutomationId("pool-detail-clover-$position-${rowIndex * 6 + column}"),
                                        Alignment.Center
                                    ) {
                                        Icon(
                                            CaixaDSDetailClover,
                                            null,
                                            (Modifier.fillMaxSize()).uiId("pool.0043"),
                                            tint = CaixaDSColorTokens.MarketplaceNumber
                                        )
                                        Text(
                                            number.toString(),
                                            style = detailText(14, FontWeight.Bold),
                                            color = CaixaDSColorTokens.MarketplaceNumber,
                                            modifier = Modifier.uiId("pool.0044")
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
        repeat(7) { column ->
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
                            CaixaDSMarketplaceNumberRing,
                            null,
                            (Modifier.fillMaxSize()).uiId("pool.0049"),
                            tint = CaixaDSColorTokens.MarketplaceNumber
                        )
                        Text(
                            number.toString(),
                            style = detailText(14, FontWeight.Bold),
                            color = CaixaDSColorTokens.MarketplaceNumber,
                            modifier = Modifier.uiId("pool.0050")
                        )
                    }
                }
            }
        }
    }
}
