package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR
import java.math.BigDecimal

fun resultText(size: androidx.compose.ui.unit.TextUnit, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = LoteriasCaixaDSFontFamily,
    fontSize = size,
    fontWeight = weight,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

@Composable
fun LegacyTicketResultContent(bets: List<ConfirmedBet>, visual: TicketResultVisualData?, onBetNow: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val bet = bets.firstOrNull()
    val modalityId = visual?.modalityId ?: bet?.modalityId ?: 2
    val modalityName = visual?.modalityName?.takeIf(String::isNotBlank) ?: bet?.modality.orEmpty()
    val style = loteriasCaixaDSLotteryStyle(modalityId, modalityName)
    val total = visual?.netPrize ?: bets.fold(BigDecimal.ZERO) { amount, entry -> amount + entry.prize }
    val isAwarded = visual?.ticketStatusId?.let { it == 100 || it == 103 } ?: (total.signum() > 0)
    Column(Modifier.fillMaxSize().uiId("tickets.0100").verticalScroll(rememberScrollState())) {
        if (isAwarded || visual?.awardRows?.isNotEmpty() == true) {
            AwardedTicketResult(modalityId, modalityName, style, total, isAwarded, visual)
        } else {
            val label = ticketResultLabel(visual, bet)
            Box(Modifier.fillMaxWidth().height(111.dp).background(style.dark), contentAlignment = Alignment.Center) {
                Text(
                    label,
                    (Modifier.fillMaxWidth()).uiId("tickets.0101"),
                    color = LoteriasCaixaDSColorTokens.White,
                    style = resultText(20.sp, FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            }
        }
        if (onBetNow != null) {
            Box(
                Modifier.padding(top = 17.dp).width(160.dp).height(61.dp)
                    .align(Alignment.CenterHorizontally).background(LoteriasCaixaDSColorTokens.PrizeBetNow)
                    .uiId("tickets.0102").clickable(onClick = onBetNow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_aposte_agora_8c4ff2),
                    color = LoteriasCaixaDSColorTokens.White,
                    style = resultText(14.2.sp, FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.uiId("tickets.0103")
                )
            }
        }
    }
}

@Composable
private fun ticketResultLabel(visual: TicketResultVisualData?, bet: ConfirmedBet?): String {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    return when (visual?.ticketStatusId) {
        10 -> uiStrings.getString(DeliveryR.string.ui_concurso_prescrito_8021f2)
        11 -> uiStrings.getString(DeliveryR.string.ui_o_premio_deste_bilhete_ja_foi_pago_p_ce473d)
        9, 101 -> uiStrings.getString(DeliveryR.string.ui_nao_premiado_ou_ainda_nao_apurado_d69d0e)
        else -> bet?.status.orEmpty()
    }
}
