package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.confirmedBetContestStatus
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun NativeBetDetailSelectionsDelivered(bet: ConfirmedBet, results: List<DrawResult>, style: LoteriasCaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (bet.recurringDraws > 0 && results.isNotEmpty()) {
        results.forEach { result ->
            var expanded by remember(bet.id, result.contest) { mutableStateOf(false) }
            Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Row(
                    Modifier.fillMaxWidth()
                        .uiId("tickets.0208").clickable { expanded = !expanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            uiStrings.getString(DeliveryR.string.ui_resultado_do_concurso_1_s_98d81e, result.contest.toString()),
                            color = style.darkContent,
                            fontSize = 18.sp,
                            modifier = Modifier.uiId("tickets.0209")
                        )
                        Text(
                            uiStrings.getString(DeliveryR.string.ui_situacao_1_s_796e19, confirmedBetContestStatus(bet, result.contest)),
                            color = style.darkContent,
                            fontSize = 16.sp,
                            modifier = Modifier.uiId("tickets.0210")
                        )
                    }
                    Text(
                        if (expanded) {
                            uiStrings.getString(
                                DeliveryR.string.ui_texto_07d452
                            )
                        } else {
                            uiStrings.getString(DeliveryR.string.ui_texto_641b9b)
                        },
                        color = style.darkContent,
                        fontSize = 24.sp,
                        modifier = Modifier.uiId("tickets.0211")
                    )
                }
                if (expanded) ConfirmedBetSelectionDelivered(bet, style.darkContent, result, style.dark)
                Box(Modifier.fillMaxWidth().padding(top = 18.dp).height(1.dp).background(style.darkContent))
            }
        }
    } else {
        ConfirmedBetSelectionDelivered(bet, style.darkContent, results.firstOrNull(), style.dark)
    }
}
