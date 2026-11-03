package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSDetailClover
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSMarketplaceNumberRing
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet
import br.gov.caixa.loterias.feature.marketplace.ui.R as DeliveryR

@Composable
fun DetailBetGrid(position: Int, bet: MarketplaceBet, color: Color, modality: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (modality.contains("SUPER", true) && bet.numberColumns.isNotEmpty()) {
        DetailSuperSevenGrid(position, bet, color, marketplaceColors(modality).dark)
        return
    }
    Column(Modifier.fillMaxWidth()) {
        Text(
            uiStrings.getString(DeliveryR.string.ui_jogo_1_s_aa1e61, (position + 1).toString()),
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
                                        LoteriasCaixaDSMarketplaceNumberRing,
                                        null,
                                        (Modifier.fillMaxSize()).uiId("pool.0038"),
                                        tint = LoteriasCaixaDSColorTokens.MarketplaceNumber
                                    )
                                    Text(
                                        if (number == 0 || number == 100) {
                                            uiStrings.getString(
                                                DeliveryR.string.ui_00_f15343
                                            )
                                        } else {
                                            number.toString()
                                        },
                                        style = detailText(14, FontWeight.Bold),
                                        color = LoteriasCaixaDSColorTokens.MarketplaceNumber,
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
                    DeliveryR.string.ui_time_do_coracao_1_s_7e407c,
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
                    DeliveryR.string.ui_mes_de_sorte_1_s_42ca39,
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

private fun detailFavoriteTeamName(team: SportsTeam): String =
    if (team.name.isBlank()) "-" else team.name + team.state.takeIf { it.isNotBlank() }?.let { "-$it" }.orEmpty()

@Composable
private fun DetailClovers(position: Int, bet: MarketplaceBet, color: Color) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (bet.clovers.isNotEmpty()) {
        Box(Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_trevos_0f4867),
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
                                            LoteriasCaixaDSDetailClover,
                                            null,
                                            (Modifier.fillMaxSize()).uiId("pool.0043"),
                                            tint = LoteriasCaixaDSColorTokens.MarketplaceNumber
                                        )
                                        Text(
                                            number.toString(),
                                            style = detailText(14, FontWeight.Bold),
                                            color = LoteriasCaixaDSColorTokens.MarketplaceNumber,
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
