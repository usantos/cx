package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR

@Composable
internal fun NativeBetDetailHeader(bet: ConfirmedBet, style: LoteriasCaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val color = style.darkContent
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Text(
            if (bet.recurringDraws > 0) {
                uiStrings.getString(
                    LocalizedR.string.ui_concursos_80935e
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_concurso_1997f5)
            },
            (Modifier.padding(top = 8.dp)).uiId("tickets.0183"),
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            if (bet.recurringDraws > 0) {
                uiStrings.getString(
                    LocalizedR.string.ui_1_s_2_s_3_s_apostas_be142d,
                    bet.contest.toString(),
                    (bet.contest + bet.recurringDraws - 1).toString(),
                    bet.recurringDraws.toString()
                )
            } else {
                bet.contest.toString()
            },
            color = color,
            fontSize = 18.sp,
            modifier = Modifier.uiId("tickets.0184")
        )
        Row(Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.padding(start = 16.dp)) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_data_aposta_59182a),
                    color = color,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("tickets.0185")
                )
                Text(bet.date, color = color, fontSize = 18.sp, modifier = Modifier.uiId("tickets.0186"))
                if (bet.time.isNotBlank()) Text(bet.time, color = color, fontSize = 18.sp, modifier = Modifier.uiId("tickets.0187"))
            }
            Box(Modifier.padding(horizontal = 20.dp).width(1.dp).height(if (bet.time.isBlank()) 47.dp else 72.dp).background(color))
            Column(Modifier.weight(1f)) {
                val status = when {
                    bet.statusId in setOf(
                        4L,
                        101L
                    ) || bet.status.contains(
                        "não apurado",
                        true
                    ) -> uiStrings.getString(LocalizedR.string.ui_concurso_nao_apurado_ff0687)

                    bet.status.contains("não premiad", true) -> uiStrings.getString(LocalizedR.string.ui_aposta_nao_premiada_b3d311)

                    else -> bet.status
                }
                Text(status, color = color, fontSize = 18.sp, modifier = Modifier.uiId("tickets.0188"))
                val prize = bet.receipt?.netPrize ?: bet.prize
                if (prize.signum() > 0) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_valor_do_premio_940005),
                        color = color,
                        fontSize = 18.sp,
                        modifier = Modifier.uiId("tickets.0189")
                    )
                    Text(
                        formatBrazilianCurrency(prize),
                        color = color,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .uiId("tickets.0190")
                    )
                }
            }
        }
    }
}
