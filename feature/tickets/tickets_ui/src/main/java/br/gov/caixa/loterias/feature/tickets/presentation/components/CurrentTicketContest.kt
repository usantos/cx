package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun CurrentTicketContest(visual: TicketResultVisualData?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (visual?.currentEstimate != null || visual?.currentClosing != null) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_informacoes_do_concurso_atual_ce38ae),
                color = LoteriasCaixaDSColorTokens.BetAction,
                style = resultText(14.2.sp, FontWeight.Bold),
                modifier = Modifier.uiId("tickets.0108")
            )
            visual.currentEstimate?.let {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_estimativa_de_premio_04eb68),
                    (Modifier.padding(top = 10.dp)).uiId("tickets.0109"),
                    color = LoteriasCaixaDSColorTokens.BetAction,
                    style = resultText(14.sp, FontWeight.Bold)
                )
                Text(
                    it,
                    (Modifier.padding(top = 5.dp)).uiId("tickets.0110"),
                    color = LoteriasCaixaDSColorTokens.BetAction,
                    style = resultText(14.sp)
                )
            }
            visual.currentClosing?.let {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_encerramento_das_apostas_6f9fb5),
                    (Modifier.padding(top = 10.dp)).uiId("tickets.0111"),
                    color = LoteriasCaixaDSColorTokens.BetAction,
                    style = resultText(14.2.sp, FontWeight.Bold)
                )
                Text(
                    it,
                    (Modifier.padding(top = 5.dp)).uiId("tickets.0112"),
                    color = LoteriasCaixaDSColorTokens.BetAction,
                    style = resultText(14.2.sp)
                )
            }
        }
    }
}
