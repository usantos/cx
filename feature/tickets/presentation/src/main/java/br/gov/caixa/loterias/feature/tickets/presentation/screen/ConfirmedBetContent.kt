package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.ConfirmedBetComplements
import br.gov.caixa.loterias.feature.tickets.presentation.components.ConfirmedBetDrawnResults
import br.gov.caixa.loterias.feature.tickets.presentation.components.ConfirmedBetNumbers
import br.gov.caixa.loterias.feature.tickets.presentation.components.LotecaSelection
import br.gov.caixa.loterias.feature.tickets.presentation.components.LotogolSelection
import br.gov.caixa.loterias.feature.tickets.presentation.components.SuperSevenSelection
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.confirmedBetDetailHitCount

@Composable
internal fun ConfirmedBetSelection(
    bet: ConfirmedBet,
    contentColor: Color,
    result: DrawResult? = null,
    background: Color = CaixaDSColorTokens.Transparent,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    ConfirmedBetDrawnResults(bet, contentColor, background, result)
    when {
        bet.lotecaSelections.isNotEmpty() -> LotecaSelection(bet.lotecaSelections, contentColor)

        bet.lotogolSelections.isNotEmpty() -> LotogolSelection(bet, contentColor)

        bet.numberColumns.size > 1 || bet.modalityId == 7 -> SuperSevenSelection(bet, contentColor, background, result)

        else -> {
            ConfirmedBetNumbers(bet, contentColor, background, result)
        }
    }
    ConfirmedBetComplements(bet, contentColor)
    result?.takeIf { it.firstDrawNumbers.isNotEmpty() }?.let {
        val hits = confirmedBetDetailHitCount(bet, it)
        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_acertos_bab9c0),
                color = contentColor,
                fontSize = 18.sp,
                modifier = Modifier
                    .uiId("tickets.0161")
            )
            Text(
                uiStrings.getString(LocalizedR.string.ui_1_s_numeros_f145a3, hits.toString()),
                (Modifier.padding(start = 4.dp)).uiId("tickets.0162"),
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Box(Modifier.fillMaxWidth().padding(top = 15.dp).height(1.dp).background(contentColor))
}
