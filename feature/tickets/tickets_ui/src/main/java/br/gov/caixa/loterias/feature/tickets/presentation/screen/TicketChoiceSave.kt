package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButton as Button
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSButtonDefaults as ButtonDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun TicketChoiceSaveDelivered(selectedTicketModalityIds: Set<Int>, onSave: (Set<Int>) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.fillMaxWidth().height(135.dp).background(LoteriasCaixaDS.colors.surface),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            Modifier.fillMaxWidth().height(95.dp).align(Alignment.BottomCenter)
                .background(LoteriasCaixaDS.colors.line)
        )
        Button(
            onClick = { onSave(selectedTicketModalityIds) },
            enabled = selectedTicketModalityIds.isNotEmpty(),
            modifier = (Modifier.width(184.dp).height(84.dp)).uiId("tickets.0143"),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LoteriasCaixaDS.colors.teal,
                contentColor = LoteriasCaixaDSColorTokens.White,
                disabledContainerColor = LoteriasCaixaDS.colors.disabledButton,
                disabledContentColor = LoteriasCaixaDSColorTokens.White
            )
        ) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_salvar_aef7cd),
                fontSize = 17.7.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("tickets.0144")
            )
        }
    }
}
