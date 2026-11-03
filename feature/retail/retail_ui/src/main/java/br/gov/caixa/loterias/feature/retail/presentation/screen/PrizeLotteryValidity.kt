package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.mapper.LotteryQrUrgency
import br.gov.caixa.loterias.feature.retail.presentation.mapper.formatRemainingTime
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.ui.R as DeliveryR

@Composable
internal fun PrizeLotteryValidity(state: PrizeRedemptionUiState, urgency: LotteryQrUrgency, pulse: Float, onRefresh: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val valid = urgency != LotteryQrUrgency.EXPIRED
    Text(
        if (valid) {
            uiStrings.getString(
                DeliveryR.string.ui_o_qr_code_e_valido_por_1_s_minutos_a_679f76,
                (state.lotteryCode?.validityMinutes ?: DEFAULT_QR_VALIDITY_MINUTES).toString()
            )
        } else {
            uiStrings.getString(DeliveryR.string.ui_o_qr_code_expirou_e58bcc)
        },
        (Modifier.fillMaxWidth().padding(top = 10.dp)).uiId("retail.0072"),
        textAlign = TextAlign.Center,
        color = if (valid) LoteriasCaixaDS.colors.text else LoteriasCaixaDSColorTokens.PrizeUrgent,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
    if (valid) {
        Text(
            formatRemainingTime(state.remainingSeconds),
            (
                Modifier.fillMaxWidth().padding(top = 15.dp).background(LoteriasCaixaDSColorTokens.White)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                ).uiId("retail.0073"),
            color = if (urgency == LotteryQrUrgency.NORMAL) {
                LoteriasCaixaDSColorTokens.PrizeGreen
            } else {
                LoteriasCaixaDSColorTokens.PrizeUrgent
            },
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
    if (!valid) {
        Box(
            Modifier.padding(top = 15.dp).width(183.dp).height(56.dp)
                .background(LoteriasCaixaDSColorTokens.White)
                .border(1.dp, LoteriasCaixaDS.colors.teal, RectangleShape)
                .uiId("retail.0074").clickable(onClick = onRefresh),
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(DeliveryR.string.ui_gerar_novamente_b608ab),
                color = LoteriasCaixaDS.colors.teal,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.uiId("retail.0075")
            )
        }
    }
}

private const val DEFAULT_QR_VALIDITY_MINUTES = 15
