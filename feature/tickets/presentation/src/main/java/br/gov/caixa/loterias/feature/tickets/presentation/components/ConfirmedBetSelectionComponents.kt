package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.caixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.detailSportsResultLabel
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.superSevenDrawColumns

@Composable
internal fun ConfirmedBetDrawnResults(bet: ConfirmedBet, contentColor: Color, background: Color, result: DrawResult?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    result?.takeIf { it.firstDrawNumbers.isNotEmpty() }?.let { draw ->
        Text(
            uiStrings.getString(LocalizedR.string.ui_numeros_sorteados_513d0e),
            (Modifier.padding(top = 40.dp)).uiId("tickets.0002"),
            color = contentColor,
            fontSize = 18.sp
        )
        if (draw.secondDrawNumbers.isNotEmpty()) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_1o_sorteio_a7dc6e),
                color = contentColor,
                fontSize = 18.sp,
                modifier = Modifier.uiId("tickets.0003")
            )
        }
        if (bet.modalityId == 7 || bet.modality.contains("super", true)) {
            SuperSevenColumns(superSevenDrawColumns(draw), contentColor, background)
        } else {
            NativeNumberGrid(draw.firstDrawNumbers, contentColor, background, plain = true)
        }
        if (draw.secondDrawNumbers.isNotEmpty()) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_2o_sorteio_679b24),
                (Modifier.padding(top = 8.dp)).uiId("tickets.0004"),
                color = contentColor,
                fontSize = 18.sp
            )
            NativeNumberGrid(draw.secondDrawNumbers, contentColor, background, plain = true)
        }
        ConfirmedBetExtraDrawResults(bet, draw, contentColor, background)
    }
    result?.let { draw ->
        val matches = draw.lotecaMatches.map { it to false } + draw.lotogolMatches.map { it to true }
        if (matches.isNotEmpty()) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_resultado_dos_jogos_b19143),
                (Modifier.padding(top = 24.dp)).uiId("tickets.0010"),
                color = contentColor,
                fontSize = 18.sp
            )
            matches.forEach { (match, lotogol) ->
                Text(
                    detailSportsResultLabel(match, lotogol),
                    color = contentColor,
                    fontSize = 14.sp,
                    modifier = Modifier.uiId("tickets.0011")
                )
            }
        }
    }
}

@Composable
internal fun ConfirmedBetNumbers(bet: ConfirmedBet, contentColor: Color, background: Color, result: DrawResult?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_meus_numeros_f099c2),
        (Modifier.padding(top = if (result != null) 10.dp else 40.dp)).uiId("tickets.0012"),
        color = contentColor,
        fontSize = 18.sp
    )
    NativeNumberGrid(bet.numbers, contentColor, background, result?.firstDrawNumbers.orEmpty().toSet())
    if (bet.clovers.isNotEmpty()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_trevos_0f4867),
            (Modifier.padding(top = 8.dp)).uiId("tickets.0013"),
            color = contentColor,
            fontSize = 18.sp
        )
        NativeNumberGrid(bet.clovers, contentColor, background, result?.clovers.orEmpty().toSet())
    }
    if (result?.secondDrawNumbers?.isNotEmpty() == true) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_2o_sorteio_679b24),
            (Modifier.padding(top = 8.dp)).uiId("tickets.0014"),
            color = contentColor,
            fontSize = 18.sp
        )
        NativeNumberGrid(bet.numbers, contentColor, background, result.secondDrawNumbers.toSet())
    }
}

@Composable
private fun NativeNumberGrid(numbers: List<Int>, color: Color, background: Color, matched: Set<Int> = emptySet(), plain: Boolean = false) {
    Column(Modifier.padding(top = 3.dp)) {
        numbers.chunked(6).forEach { row ->
            Row {
                row.forEach { number ->
                    val hit = !plain && number in matched
                    Box(
                        Modifier.sizeIn(minWidth = 43.dp, minHeight = 43.dp).padding(4.dp)
                            .then(
                                if (plain || matched.isEmpty()) {
                                    Modifier
                                } else {
                                    Modifier.semantics(mergeDescendants = true) {
                                        contentDescription =
                                            "Número ${number.toString().padStart(2, '0')}, ${if (hit) "acertou" else "não acertou"}"
                                    }
                                }
                            ).then(
                                if (plain) {
                                    Modifier
                                } else {
                                    Modifier.border(1.dp, color, CircleShape)
                                        .background(if (hit) color else background, CircleShape)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            number.toString().padStart(2, '0'),
                            color = if (hit) background else color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.uiId("tickets.0015")
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SuperSevenSelection(bet: ConfirmedBet, contentColor: Color, background: Color, result: DrawResult?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_suas_colunas_69913f),
        (Modifier.padding(top = 38.dp)).uiId("tickets.0016"),
        color = contentColor,
        fontSize = 17.sp
    )
    SuperSevenColumns(bet.numberColumns, contentColor, background, result?.let(::superSevenDrawColumns).orEmpty())
}

@Composable
private fun SuperSevenColumns(columns: List<List<Int>>, contentColor: Color, background: Color, drawn: List<List<Int>> = emptyList()) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().padding(top = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        columns.take(7).forEachIndexed { index, column ->
            Column(
                Modifier.semantics(mergeDescendants = true) {
                    contentDescription = "Coluna ${index + 1}: " + column.joinToString(", ") { digit ->
                        "$digit" +
                            if (drawn.isEmpty()) {
                                ""
                            } else if (drawn.getOrNull(index)?.contains(digit) == true) {
                                " acertou"
                            } else {
                                " não acertou"
                            }
                    }
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_1_s_d020a2, (index + 1).toString()),
                    color = contentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("tickets.0017")
                )
                column.forEach { digit ->
                    val hit = drawn.getOrNull(index)?.contains(digit) == true
                    Box(
                        Modifier.padding(top = 4.dp).sizeIn(minWidth = 31.dp, minHeight = 31.dp)
                            .border(1.dp, contentColor, RoundedCornerShape(3.dp))
                            .background(if (hit) contentColor else background, RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            digit.toString(),
                            color = if (hit) background else contentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.uiId("tickets.0018")
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun LotecaSelection(selections: List<LotecaMatchSelection>, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_sua_aposta_3bcfc2),
        (Modifier.padding(top = 28.dp, bottom = 8.dp)).uiId("tickets.0019"),
        color = contentColor,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Row(Modifier.fillMaxWidth().padding(bottom = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_coluna_862f07),
            (Modifier.width(43.dp)).uiId("tickets.0020"),
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_time_33b934),
            (Modifier.weight(1f)).uiId("tickets.0021"),
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_1_x_2_e8072e),
            (Modifier.width(82.dp)).uiId("tickets.0022"),
            color = contentColor,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
    selections.forEach { selection ->
        Row(
            Modifier.fillMaxWidth().padding(vertical = 4.dp).semantics {
                isTraversalGroup = true
                val outcomes = listOfNotNull(
                    selection.match.home.name.takeIf { selection.homeWin },
                    "Empate".takeIf { selection.draw },
                    selection.match.away.name.takeIf { selection.awayWin }
                )
                val match = selection.match
                contentDescription = "Jogo ${match.number}: ${match.home.name} x ${match.away.name}; " +
                    "selecionados: ${outcomes.joinToString(", ")}"
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selection.match.number.toString(), (Modifier.width(43.dp)).uiId("tickets.0023"), color = contentColor, fontSize = 12.sp)
            Column(Modifier.weight(1f)) {
                Text(
                    selection.match.home.name,
                    color = contentColor,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.uiId("tickets.0024")
                )
                Text(
                    selection.match.away.name,
                    color = contentColor,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.uiId("tickets.0025")
                )
            }
            OutcomeMark(selection.homeWin, contentColor)
            OutcomeMark(selection.draw, contentColor)
            OutcomeMark(selection.awayWin, contentColor)
        }
    }
}

@Composable
private fun OutcomeMark(selected: Boolean, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.padding(start = 5.dp).size(22.dp).border(1.dp, contentColor, CircleShape)
            .background(if (selected) contentColor else CaixaDSColorTokens.Transparent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_texto_1dabba),
                color = CaixaDSColorTokens.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("tickets.0026")
            )
        }
    }
}

@Composable
internal fun LotogolSelection(bet: ConfirmedBet, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_sua_aposta_3bcfc2),
        (Modifier.padding(top = 28.dp, bottom = 8.dp)).uiId("tickets.0027"),
        color = contentColor,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    bet.lotogolSelections.forEachIndexed { index, selection ->
        Row(
            Modifier.fillMaxWidth().padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_1_s_d020a2, (index + 1).toString()),
                (Modifier.width(22.dp)).uiId("tickets.0028"),
                color = contentColor,
                fontSize = 12.sp
            )
            Text(
                selection.match.home.name,
                (Modifier.weight(1f)).uiId("tickets.0029"),
                color = contentColor,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ScoreMark(selection.homeScore, contentColor)
            Text(
                uiStrings.getString(LocalizedR.string.ui_x_2d7116),
                (Modifier.padding(horizontal = 5.dp)).uiId("tickets.0030"),
                color = contentColor,
                fontSize = 12.sp
            )
            ScoreMark(selection.awayScore, contentColor)
            Text(
                selection.match.away.name,
                (Modifier.weight(1f).padding(start = 7.dp)).uiId("tickets.0031"),
                color = contentColor,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ScoreMark(score: Int, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.size(26.dp).border(1.dp, contentColor, RoundedCornerShape(3.dp)), contentAlignment = Alignment.Center) {
        Text(
            if (score >= 5) uiStrings.getString(LocalizedR.string.ui_5_fa676d) else score.toString(),
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("tickets.0032")
        )
    }
}

@Composable
internal fun ConfirmedBetComplements(bet: ConfirmedBet, contentColor: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    bet.favoriteTeam?.let { team ->
        Text(
            uiStrings.getString(
                LocalizedR.string.ui_time_do_coracao_1_s_2_s_3b5c68,
                team.name,
                team.state.takeIf(String::isNotBlank)?.let { "/$it" }.orEmpty()
            ),
            (Modifier.padding(top = 17.dp)).uiId("tickets.0033"),
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
    bet.luckyMonth?.let { month ->
        Text(
            uiStrings.getString(LocalizedR.string.ui_mes_da_sorte_1_s_35a139, month.name),
            (Modifier.padding(top = 17.dp)).uiId("tickets.0034"),
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ConfirmedBetExtraDrawResults(bet: ConfirmedBet, draw: DrawResult, contentColor: Color, background: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (draw.clovers.isNotEmpty()) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_trevos_0f4867),
            color = contentColor,
            fontSize = 18.sp,
            modifier = Modifier
                .uiId("tickets.0005")
        )
        NativeNumberGrid(draw.clovers, contentColor, background, plain = true)
    }
    draw.favoriteTeamReward?.let {
        Text(
            uiStrings.getString(LocalizedR.string.ui_time_sorteado_1_s_ca30eb, it.selection),
            color = contentColor,
            fontSize = 18.sp,
            modifier = Modifier.uiId("tickets.0006")
        )
    }
    draw.luckyMonthReward?.let {
        Text(
            uiStrings.getString(LocalizedR.string.ui_mes_sorteado_1_s_af82b8, it.selection),
            color = contentColor,
            fontSize = 18.sp,
            modifier = Modifier.uiId("tickets.0007")
        )
    }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(Modifier.width(212.dp).height(28.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(contentColor))
            Box(Modifier.size(20.dp).background(contentColor, CircleShape), contentAlignment = Alignment.Center) {
                val clover = caixaDSMarketplaceFilterClover(bet.modalityId, bet.modality, selected = false)
                if (clover != null) {
                    Icon(painterResource(clover), null, (Modifier.size(12.dp)).uiId("tickets.0008"), tint = Color.Unspecified)
                } else {
                    Icon(
                        caixaDSLotteryClover(bet.modalityId, onDark = false),
                        null,
                        (Modifier.size(12.dp)).uiId("tickets.0009"),
                        tint = background
                    )
                }
            }
        }
    }
}
