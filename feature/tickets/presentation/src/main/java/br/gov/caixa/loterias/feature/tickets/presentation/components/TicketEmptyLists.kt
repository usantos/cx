package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR

@Composable
internal fun LegacyEmptyBets(onBetNow: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_fez_apostas_no_period_002a42),
            (Modifier.padding(horizontal = 16.dp)).uiId("tickets.0078"),
            color = LoteriasCaixaDSColorTokens.TicketIntro,
            style = ticketListText(14.sp, FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        if (onBetNow != null) {
            Box(
                Modifier.padding(top = 26.dp).width(200.dp).height(54.dp)
                    .background(
                        LoteriasCaixaDSColorTokens.TealAction,
                        RoundedCornerShape(4.dp)
                    ).uiId("tickets.0079").clickable(onClick = onBetNow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_aposte_aqui_a5080d),
                    color = LoteriasCaixaDSColorTokens.White,
                    style = ticketListText(14.sp, FontWeight.Bold),
                    modifier = Modifier.uiId("tickets.0080")
                )
            }
        }
    }
}

@Composable
internal fun LegacyEmptyPurchases() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_voce_ainda_nao_possui_compras_neste_0585aa),
        (Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 30.dp)).uiId("tickets.0081"),
        color = LoteriasCaixaDSColorTokens.TicketIntro,
        style = ticketListText(14.sp, FontWeight.Bold),
        textAlign = TextAlign.Center
    )
}
