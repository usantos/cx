package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.loteriasCaixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import java.math.BigDecimal
import java.util.Locale

@Composable
internal fun AwardedTicketResult(
    modalityId: Int,
    modalityName: String,
    style: br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle,
    total: BigDecimal,
    isAwarded: Boolean,
    visual: TicketResultVisualData?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth().height(56.dp).background(style.light), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(90.dp).height(56.dp)) {
            Image(rememberResultDrawablePainter(resultTrapezoid(modalityId)), null, Modifier.fillMaxSize())
            Image(loteriasCaixaDSLotteryClover(modalityId), null, Modifier.align(Alignment.CenterStart).padding(start = 16.dp).size(22.dp))
        }
        Text(
            modalityName.lowercase(Locale.forLanguageTag("pt-BR")),
            color = style.content,
            style = resultText(16.sp, FontWeight.Bold).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily),
            modifier = Modifier.uiId("tickets.0105")
        )
    }
    Column(Modifier.fillMaxWidth().background(style.dark), horizontalAlignment = Alignment.CenterHorizontally) {
        if (isAwarded) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_pode_comemorar_f533cc),
                (Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 10.dp)).uiId("tickets.0106"),
                color = style.darkContent,
                style = resultText(14.sp),
                textAlign = TextAlign.Center
            )
            Text(
                if (total.signum() > 0) {
                    uiStrings.getString(
                        LocalizedR.string.ui_voce_foi_premiado_com_total_de_1_s_2bb4e9,
                        formatBrazilianCurrency(total)
                    )
                } else {
                    uiStrings.getString(LocalizedR.string.ui_voce_foi_premiado_9441db)
                },
                (Modifier.fillMaxWidth()).uiId("tickets.0107"),
                color = style.darkContent,
                style = resultText(20.1.sp, FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
        if (visual?.awardRows?.isNotEmpty() == true) {
            TicketAwardTable(visual.awardRows, style.darkContent)
        }
    }
    CurrentTicketContest(visual)
}
