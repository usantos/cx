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
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

@Composable
internal fun LoadingData(modifier: Modifier = Modifier) =
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

@Composable
internal fun ErrorData(message: String, retry: () -> Unit, modifier: Modifier = Modifier): Unit = CaixaDSEmptyState(
    title = androidx.compose.ui.res.stringResource(LocalizedR.string.final_load_failure),
    message = message,
    actionLabel = androidx.compose.ui.res.stringResource(LocalizedR.string.final_retry),
    onAction = retry,
    modifier = (modifier).uiId("tickets.0127")
)

@Composable
internal fun BetDataCard(bet: ConfirmedBet, onOpen: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSCard(onClick = onOpen, modifier = Modifier.uiId("tickets.0128")) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(
                uiStrings.getString(
                    LocalizedR.string.ui_1_s_concurso_2_s_9f3926,
                    bet.modality.ifBlank { uiStrings.getString(LocalizedR.string.final_bet) },
                    bet.contest.toString()
                ),
                color = CaixaDS.colors.brand,
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
                Text(bet.date, color = CaixaDS.colors.mutedText, modifier = Modifier.uiId("tickets.0131"))
                Text(
                    bet.status.ifBlank { uiStrings.getString(LocalizedR.string.ui_em_acompanhamento_9afa47) },
                    color = if (bet.prize.signum() > 0) CaixaDS.colors.success else CaixaDS.colors.text,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("tickets.0132")
                )
            }
        }
    }
}
