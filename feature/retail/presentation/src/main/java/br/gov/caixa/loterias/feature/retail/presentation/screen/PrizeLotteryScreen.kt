package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeHelpAction
import br.gov.caixa.loterias.feature.retail.presentation.components.PrizeQrCodeImage
import br.gov.caixa.loterias.feature.retail.presentation.mapper.LotteryQrUrgency
import br.gov.caixa.loterias.feature.retail.presentation.mapper.formatRemainingTime
import br.gov.caixa.loterias.feature.retail.presentation.mapper.lotteryQrUrgency
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState

@Composable
internal fun PrizeLotteryDataScreen(state: PrizeRedemptionUiState, onBack: () -> Unit, onHelp: () -> Unit, onRefresh: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val haptic = LocalHapticFeedback.current
    val urgency = lotteryQrUrgency(state.remainingSeconds)
    val pulseTransition = rememberInfiniteTransition(label = uiStrings.getString(LocalizedR.string.ui_qr_expiration_pulse_f3d2b8))
    val pulse by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (urgency == LotteryQrUrgency.PULSE || urgency == LotteryQrUrgency.VIBRATE) 1.14f else 1f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = uiStrings.getString(LocalizedR.string.ui_qr_expiration_scale_c3fb77)
    )
    LaunchedEffect(state.remainingSeconds) {
        if (urgency == LotteryQrUrgency.VIBRATE) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_loterica_6552e8),
        onBack = onBack,
        topBarAction = { PrizeHelpAction(onHelp) },

        containerColor = CaixaDSColorTokens.White,
        modifier = Modifier.uiId("retail.0070")
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).background(CaixaDSColorTokens.White)
                .uiId("retail.0071").verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.fillMaxWidth().height(300.dp).background(CaixaDSColorTokens.FavoriteBlue), contentAlignment = Alignment.Center) {
                state.lotteryCode?.qrCode?.takeIf(String::isNotBlank)?.let { PrizeQrCodeImage(it) }
            }
            PrizeLotteryValidity(state, urgency, pulse, onRefresh)
            Box(
                Modifier.fillMaxWidth().padding(top = 15.dp).height(1.dp)
                    .background(CaixaDSColorTokens.PrizeDivider)
            )
            PrizeLotteryInstruction(
                number = 1,
                text = uiStrings.getString(LocalizedR.string.ui_escolha_uma_loterica_mais_proxima_503a55),
                modifier = (Modifier.padding(top = 39.dp)).uiId("retail.0076")
            )
            PrizeLotteryInstruction(
                number = 2,
                text = uiStrings.getString(LocalizedR.string.ui_mostre_o_qr_code_ao_atendente_a23f79),
                modifier = (Modifier.padding(top = 35.dp)).uiId("retail.0077")
            )
            PrizeLotteryInstruction(
                number = 3,
                text = uiStrings.getString(LocalizedR.string.ui_aproveite_seu_premio_8adb07),
                highlightedText = uiStrings.getString(LocalizedR.string.ui_premio_449164),
                modifier = (Modifier.padding(top = 39.dp, bottom = 30.dp)).uiId("retail.0078")
            )
        }
    }
}

@Composable
private fun PrizeLotteryInstruction(number: Int, text: String, modifier: Modifier = Modifier, highlightedText: String? = null) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).background(CaixaDSColorTokens.White, CircleShape)
                .border(1.dp, CaixaDS.colors.line, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                number.toString(),
                color = CaixaDS.colors.brand,
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.uiId("retail.0079")
            )
        }
        val instruction = buildAnnotatedString {
            if (highlightedText == null || highlightedText !in text) {
                append(text)
            } else {
                val start = text.indexOf(highlightedText)
                append(text.substring(0, start))
                withStyle(SpanStyle(color = CaixaDSColorTokens.PrizeGreen, fontWeight = FontWeight.Bold)) {
                    append(highlightedText)
                }
                append(text.substring(start + highlightedText.length))
            }
        }
        Text(
            text = instruction,
            modifier = (Modifier.weight(1f).padding(start = 8.dp)).uiId("retail.0080"),
            color = CaixaDS.colors.graphite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PrizeLotteryValidity(state: PrizeRedemptionUiState, urgency: LotteryQrUrgency, pulse: Float, onRefresh: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val valid = urgency != LotteryQrUrgency.EXPIRED
    Text(
        if (valid) {
            uiStrings.getString(
                LocalizedR.string.ui_o_qr_code_e_valido_por_1_s_minutos_a_679f76,
                (state.lotteryCode?.validityMinutes ?: 15).toString()
            )
        } else {
            uiStrings.getString(LocalizedR.string.ui_o_qr_code_expirou_e58bcc)
        },
        (Modifier.fillMaxWidth().padding(top = 10.dp)).uiId("retail.0072"),
        textAlign = TextAlign.Center,
        color = if (valid) CaixaDS.colors.text else CaixaDSColorTokens.PrizeUrgent,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
    if (valid) {
        Text(
            formatRemainingTime(state.remainingSeconds),
            (
                Modifier.fillMaxWidth().padding(top = 15.dp).background(CaixaDSColorTokens.White)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                ).uiId("retail.0073"),
            color = if (urgency == LotteryQrUrgency.NORMAL) {
                CaixaDSColorTokens.PrizeGreen
            } else {
                CaixaDSColorTokens.PrizeUrgent
            },
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
    if (!valid) {
        Box(
            Modifier.padding(top = 15.dp).width(183.dp).height(56.dp)
                .background(CaixaDSColorTokens.White)
                .border(1.dp, CaixaDS.colors.teal, RectangleShape)
                .uiId("retail.0074").clickable(onClick = onRefresh),
            contentAlignment = Alignment.Center
        ) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_gerar_novamente_b608ab),
                color = CaixaDS.colors.teal,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.uiId("retail.0075")
            )
        }
    }
}
