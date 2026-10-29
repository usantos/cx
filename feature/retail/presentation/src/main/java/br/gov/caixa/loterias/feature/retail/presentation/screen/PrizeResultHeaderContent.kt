package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.caixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.layout.CaixaDSPrizeMessage
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.mapper.prizeAwardedRows
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun PrizeResultHeader(state: PrizeRedemptionUiState, style: br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val details = state.details
    Row(
        Modifier.fillMaxWidth().height(56.dp).background(style.light),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(90.dp)
                .fillMaxHeight()
                .background(
                    style.dark,
                    GenericShape { size, _ ->
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width * .68f, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            caixaDSCampaignDrawable(
                details?.modalityId ?: 2,
                details?.modalityName.orEmpty(),
                CaixaDSLotteryArtwork.TRAPEZE
            )?.let { resource ->
                androidx.compose.foundation.Image(caixaDSDrawablePainter(resource), null, Modifier.fillMaxSize())
            }
            Icon(
                caixaDSLotteryClover(details?.modalityId ?: 2),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = (Modifier.padding(start = 16.dp).size(with(LocalDensity.current) { 22.sp.toDp() }, 22.dp)).uiId("retail.0108")
            )
        }
        Text(
            details?.modalityName.orEmpty().lowercase(Locale.forLanguageTag("pt-BR")),
            (Modifier.weight(1f)).uiId("retail.0109"),
            color = style.content,
            fontSize = 16.sp,
            fontFamily = CaixaDSFuturaFontFamily,
            fontWeight = FontWeight.Bold
        )
        PrizeResultContests(state, style)
    }
    Column(
        Modifier.fillMaxWidth().background(style.light),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val amount = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
            .format(details?.netValue ?: java.math.BigDecimal.ZERO)
        CaixaDSPrizeMessage(
            message = if (details?.netValue?.signum() == 0 || details == null) {
                uiStrings.getString(LocalizedR.string.ui_voce_foi_premiado_9441db)
            } else {
                uiStrings.getString(LocalizedR.string.ui_voce_foi_premiado_com_total_de_1_s_f8d6ec, amount)
            },
            contentColor = style.content,
            modifier = (Modifier.fillMaxWidth()).uiId("retail.0112")
        )
        if (details != null) PrizeAwardTable(details, style.content)
    }
}

@Composable
private fun PrizeResultContests(state: PrizeRedemptionUiState, style: br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val details = state.details
    val awarded = details?.let(::prizeAwardedRows).orEmpty()
    Column(Modifier.padding(end = 24.dp), horizontalAlignment = Alignment.End) {
        val range = details != null && (details.recurringDraws > 0 || details.initialContest != details.targetContest)
        Text(
            if (range) {
                uiStrings.getString(
                    LocalizedR.string.ui_concursos_80935e
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_concurso_1997f5)
            },
            color = style.content,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.uiId("retail.0110")
        )
        val contestText = when {
            details == null -> ""

            details.targetContest > 0 && details.initialContest != details.targetContest ->
                "${details.initialContest} - ${details.targetContest}"

            details.recurringDraws > 0 && awarded.isNotEmpty() -> "${awarded.first().contest} - ${awarded.last().contest}"

            else -> details.targetContest.takeIf { it > 0 }?.toString().orEmpty()
        }
        Text(
            contestText,
            color = style.content,
            fontSize = 16.sp,
            fontFamily = CaixaDSFuturaFontFamily,
            modifier = Modifier.uiId("retail.0111")
        )
    }
}
