package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeHelpAction
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeQrCodeImage
import br.gov.caixa.loterias.feature.retail.presentation.mapper.LotteryQrUrgency
import br.gov.caixa.loterias.feature.retail.presentation.mapper.lotteryQrUrgency
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.ui.R as DeliveryR

@Composable
internal fun PrizeLotteryDataScreen(state: PrizeRedemptionUiState, onBack: () -> Unit, onHelp: () -> Unit, onRefresh: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val haptic = LocalHapticFeedback.current
    val urgency = lotteryQrUrgency(state.remainingSeconds)
    val pulseTransition = rememberInfiniteTransition(label = uiStrings.getString(DeliveryR.string.ui_qr_expiration_pulse_f3d2b8))
    val pulse by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (urgency == LotteryQrUrgency.PULSE || urgency == LotteryQrUrgency.VIBRATE) 1.14f else 1f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = uiStrings.getString(DeliveryR.string.ui_qr_expiration_scale_c3fb77)
    )
    LaunchedEffect(state.remainingSeconds) {
        if (urgency == LotteryQrUrgency.VIBRATE) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_loterica_6552e8),
        onBack = onBack,
        topBarAction = { PrizeHelpAction(onHelp) },

        containerColor = LoteriasCaixaDSColorTokens.White,
        modifier = Modifier.uiId("retail.0070")
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(LoteriasCaixaDSColorTokens.White)
                .uiId("retail.0071").verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.fillMaxWidth().height(300.dp).background(LoteriasCaixaDSColorTokens.FavoriteBlue),
                contentAlignment = Alignment.Center
            ) {
                state.lotteryCode?.qrCode?.takeIf(String::isNotBlank)?.let { PrizeQrCodeImage(it) }
            }
            PrizeLotteryValidity(state, urgency, pulse, onRefresh)
            Box(
                Modifier.fillMaxWidth().padding(top = 15.dp).height(1.dp)
                    .background(LoteriasCaixaDSColorTokens.PrizeDivider)
            )
            PrizeLotteryInstruction(
                number = 1,
                text = uiStrings.getString(DeliveryR.string.ui_escolha_uma_loterica_mais_proxima_503a55),
                modifier = (Modifier.padding(top = 39.dp)).uiId("retail.0076")
            )
            PrizeLotteryInstruction(
                number = 2,
                text = uiStrings.getString(DeliveryR.string.ui_mostre_o_qr_code_ao_atendente_a23f79),
                modifier = (Modifier.padding(top = 35.dp)).uiId("retail.0077")
            )
            PrizeLotteryInstruction(
                number = 3,
                text = uiStrings.getString(DeliveryR.string.ui_aproveite_seu_premio_8adb07),
                highlightedText = uiStrings.getString(DeliveryR.string.ui_premio_449164),
                modifier = (Modifier.padding(top = 39.dp, bottom = 30.dp)).uiId("retail.0078")
            )
        }
    }
}
