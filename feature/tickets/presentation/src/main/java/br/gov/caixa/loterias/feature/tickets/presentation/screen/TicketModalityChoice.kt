package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveLoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.tickets.R

@Composable
internal fun TicketModalityChoice(modality: LotteryModality, selected: Boolean, onClick: () -> Unit) {
    val color = ticketModalityColor(modality.code)
    Row(
        Modifier.fillMaxWidth().padding(4.dp).height(56.dp)
            .background(if (selected) color else LoteriasCaixaDS.colors.surface)
            .then(if (selected) Modifier else Modifier.border(1.dp, LoteriasCaixaDSColorTokens.TicketChoiceBorder))
            .uiId("tickets.0148").clickable(onClick = onClick)
            .padding(horizontal = if (selected) 12.dp else 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            loteriasCaixaDSDrawablePainter(
                loteriasCaixaDSCampaignDrawable(
                    modality.id,
                    modality.name,
                    if (selected) LoteriasCaixaDSLotteryArtwork.CLOVER_ON_DARK else LoteriasCaixaDSLotteryArtwork.CLOVER_ON_LIGHT
                )
                    ?: R.drawable.legacy_ticket_trevo
            ),
            contentDescription = null,
            modifier = (Modifier.size(18.dp)).uiId("tickets.0149"),
            tint = Color.Unspecified
        )
        Text(
            modality.name,
            (Modifier.padding(start = 8.dp)).uiId("tickets.0150"),
            color = if (selected) ticketModalityContent(modality) else LoteriasCaixaDSColorTokens.TicketChoiceMuted,
            fontFamily = LoteriasCaixaDSFuturaFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun ticketModalityColor(code: String): Color {
    val style = loteriasCaixaDSLotteryStyle(0, code)
    if (style != resolveLoteriasCaixaDSLotteryStyle(0, code, LoteriasCaixaDSCampaign.DEFAULT)) return style.light
    return when (code.uppercase().filter(Char::isLetterOrDigit)) {
        "MEGASENA" -> LoteriasCaixaDSColorTokens.MegaSenaLight
        "LOTOFACIL" -> LoteriasCaixaDSColorTokens.LotofacilLight
        "QUINA" -> LoteriasCaixaDSColorTokens.QuinaLight
        "LOTOMANIA" -> LoteriasCaixaDSColorTokens.LotomaniaLight
        "TIMEMANIA" -> LoteriasCaixaDSColorTokens.TimemaniaLight
        "DUPLASENA" -> LoteriasCaixaDSColorTokens.DuplaSenaLight
        "LOTECA" -> LoteriasCaixaDSColorTokens.Loteca
        "LOTOGOL" -> LoteriasCaixaDSColorTokens.Lotogol
        else -> LoteriasCaixaDS.colors.brand
    }
}

@Composable
private fun ticketModalityContent(modality: LotteryModality): Color {
    val style = loteriasCaixaDSLotteryStyle(modality.id, modality.name)
    return if (style != resolveLoteriasCaixaDSLotteryStyle(modality.id, modality.name, LoteriasCaixaDSCampaign.DEFAULT)) {
        style.content
    } else {
        LoteriasCaixaDSColorTokens.White
    }
}
