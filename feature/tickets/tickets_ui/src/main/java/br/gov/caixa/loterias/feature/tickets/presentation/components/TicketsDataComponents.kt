package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun LoadingData(modifier: Modifier = Modifier) =
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

@Composable
fun ErrorData(message: String, retry: () -> Unit, modifier: Modifier = Modifier): Unit = LoteriasCaixaDSEmptyState(
    title = androidx.compose.ui.res.stringResource(DeliveryR.string.final_load_failure),
    message = message,
    actionLabel = androidx.compose.ui.res.stringResource(DeliveryR.string.final_retry),
    onAction = retry,
    modifier = (modifier).uiId("tickets.0127")
)

@Composable
fun BetDataCard(bet: ConfirmedBet, onOpen: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSCard(onClick = onOpen, modifier = Modifier.uiId("tickets.0128")) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(
                uiStrings.getString(
                    DeliveryR.string.ui_1_s_concurso_2_s_9f3926,
                    bet.modality.ifBlank { uiStrings.getString(DeliveryR.string.final_bet) },
                    bet.contest.toString()
                ),
                color = LoteriasCaixaDS.colors.brand,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("tickets.0129")
            )
            if (bet.numbers.isNotEmpty()) {
                Text(
                    bet.numbers.joinToString("  ") { it.toString().padStart(2, '0') },
                    modifier = Modifier
                        .uiId("tickets.0130")
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(bet.date, color = LoteriasCaixaDS.colors.mutedText, modifier = Modifier.uiId("tickets.0131"))
                Text(
                    bet.status.ifBlank { uiStrings.getString(DeliveryR.string.ui_em_acompanhamento_9afa47) },
                    color = if (bet.prize.signum() > 0) LoteriasCaixaDS.colors.success else LoteriasCaixaDS.colors.text,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("tickets.0132")
                )
            }
        }
    }
}
