package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSPixTimerIcon
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun PixPanelTimer(remainingSeconds: Long, texts: LoteriasCaixaDSPixPanelText) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        texts.timeIntro,
        Modifier.fillMaxWidth().padding(top = 32.dp),
        color = LoteriasCaixaDS.colors.cartDark,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        style = pixPanelStyle
    )
    val timerColor = when {
        remainingSeconds < URGENT_REMAINING_SECONDS -> LoteriasCaixaDSColorTokens.PixUrgent
        remainingSeconds < WARNING_REMAINING_SECONDS -> LoteriasCaixaDSColorTokens.PixWarning
        else -> LoteriasCaixaDS.colors.cartDark
    }
    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        LoteriasCaixaDSPixTimerIcon((Modifier.size(25.dp, 22.dp)).uiId("ds.0047"), timerColor)
        Text(
            uiStrings.getString(
                LocalizedR.string.ui_02d_02d_0b2e34
            ).format(remainingSeconds.coerceAtLeast(0) / SECONDS_PER_MINUTE, remainingSeconds.coerceAtLeast(0) % SECONDS_PER_MINUTE),
            Modifier.padding(start = 8.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = timerColor,
            style = pixPanelStyle
        )
    }
    Text(
        texts.timeOutro,
        Modifier.padding(top = 8.dp),
        color = LoteriasCaixaDS.colors.cartDark,
        fontSize = 14.sp,
        style = pixPanelStyle
    )
}

private const val SECONDS_PER_MINUTE = 60
private const val URGENT_REMAINING_SECONDS = SECONDS_PER_MINUTE
private const val WARNING_REMAINING_SECONDS = 2 * SECONDS_PER_MINUTE
