package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCheckbox

/** Cópia Compose dos estados de aposta-espelho usados na simulação legacy. */
@Composable
internal fun BettingLegacyMirror(selected: Boolean, onToggle: () -> Unit, manual: Boolean) {
    val foreground = if (manual) CaixaDSColorTokens.LegacyLotomaniaMirrorText
    else CaixaDSColorTokens.LegacySocialGraphite
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (manual) Modifier.background(CaixaDSColorTokens.LegacyLotomaniaMirrorBackground) else Modifier)
            .clickable(onClick = onToggle)
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = if (manual) 12.dp else 20.dp,
                bottom = if (manual) 16.dp else 0.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (manual) Arrangement.Start else Arrangement.Center,
    ) {
        CaixaDSLegacyCheckbox(
            checked = selected,
            onClick = onToggle,
            modifier = Modifier.size(20.dp),
            color = if (manual) CaixaDSColorTokens.LegacyLotomaniaDark else foreground,
        )
        Text(
            text = if (manual) mirrorDescription() else buildAnnotatedString { append("Aposta espelho") },
            modifier = Modifier.padding(start = if (manual) 12.dp else 16.dp),
            fontFamily = CaixaDSFontFamily,
            fontSize = if (manual) 12.sp else 14.2.sp,
            lineHeight = if (manual) 15.sp else 17.sp,
            color = foreground,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = true)),
        )
    }
}

private fun mirrorDescription() = buildAnnotatedString {
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("Aumente suas chances de ganhar com a aposta-espelho.\n")
    }
    append("Ao escolher esta opção, o sistema fará uma aposta automaticamente com os 50 números não escolhidos na aposta original. A mesma regra se aplica às apostas com Surpresinha e/ou Teimosinha.")
}
