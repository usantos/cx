package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSBetTypeIcons
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

@Composable
internal fun NativeBetTypeBanner(bet: ConfirmedBet, style: LoteriasCaixaDSLotteryStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val items = buildList {
        if (bet.exchange) add("Troca" to LoteriasCaixaDSBetTypeIcons.Exchange)
        if (bet.poolShare) add(uiStrings.getString(LocalizedR.string.ui_bolao_99927e) to LoteriasCaixaDSBetTypeIcons.Pool)
        if (bet.combo) add("Combo" to LoteriasCaixaDSBetTypeIcons.Combo)
        if (bet.mirror) add("Espelho" to LoteriasCaixaDSBetTypeIcons.Mirror)
        if (bet.surprise) add("Surpresinha" to LoteriasCaixaDSBetTypeIcons.Surprise)
        if (bet.recurringDraws > 0) add("Teimosinha" to LoteriasCaixaDSBetTypeIcons.Recurring)
    }
    if (items.isEmpty()) return
    Column(Modifier.padding(top = 0.dp)) {
        FlowRow(Modifier.fillMaxWidth().background(style.light).padding(horizontal = 8.dp, vertical = 6.dp)) {
            items.forEach { (label, icon) ->
                Row(Modifier.padding(end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, (Modifier.size(20.dp)).uiId("tickets.0191"), tint = style.content)
                    Text(
                        label,
                        (Modifier.padding(start = 8.dp)).uiId("tickets.0192"),
                        color = style.content,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Canvas(Modifier.align(Alignment.CenterHorizontally).size(24.dp, 12.dp)) {
            drawPath(
                Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
                    close()
                },
                style.light
            )
        }
    }
}
