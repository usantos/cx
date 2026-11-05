package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun BetDetailResultActionsDelivered(
    bet: ConfirmedBet,
    drawResults: List<br.gov.caixa.loterias.feature.results.domain.model.DrawResult>,
    style: br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle,
    onViewResult: () -> Unit,
    onRedeemPrize: (() -> Unit)?,
    onDownloadReceipt: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    NativeBetDetailSelectionsDelivered(bet, drawResults, style)
    if (drawResults.isEmpty() && bet.statusId !in setOf(RESULT_PENDING_STATUS_ID, RESULT_PROCESSING_STATUS_ID)) {
        NativeDetailButtonDelivered(
            uiStrings.getString(DeliveryR.string.ui_ver_resultado_2011cb),
            style,
            onViewResult,
            Modifier.padding(top = 12.dp)
        )
    }
    if (bet.prize.signum() > 0 && !bet.redeemedPrize) {
        Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(LoteriasCaixaDSIcons.Time, null, (Modifier.size(25.dp)).uiId("tickets.0157"), tint = style.darkContent)
            Text(
                uiStrings.getString(DeliveryR.string.ui_prazo_para_recebimento_de_premio_90_14a8d5),
                (Modifier.padding(start = 8.dp)).uiId("tickets.0158"),
                color = style.darkContent,
                fontSize = 18.sp
            )
        }
        NativeDetailButtonDelivered(
            uiStrings.getString(DeliveryR.string.ui_resgatar_premio_e68bcd),
            style,
            onRedeemPrize,
            Modifier.padding(top = 12.dp)
        )
    }
    NativeDetailButtonDelivered(
        uiStrings.getString(DeliveryR.string.ui_gerar_comprovante_a92bea),
        style,
        onDownloadReceipt,
        Modifier.padding(top = 12.dp),
        download = true
    )
    Spacer(Modifier.height(100.dp))
}

private const val RESULT_PENDING_STATUS_ID = 4L

private const val RESULT_PROCESSING_STATUS_ID = 101L
