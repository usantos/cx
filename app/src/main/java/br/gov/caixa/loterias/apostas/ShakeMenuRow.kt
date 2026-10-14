package br.gov.caixa.loterias.apostas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import br.gov.caixa.loterias.core.designsystem.*
import br.gov.caixa.loterias.core.domain.betting.ShakeSettingsRepository

@Composable
internal fun ShakeMenuRow(
    settings: ShakeSettingsRepository,
    active: Boolean,
    accent: Color = CaixaDS.colors.brand,
) {
    var enabled by remember { mutableStateOf(settings.enabled()) }
    var confirm by rememberSaveable { mutableStateOf(false) }
    var hideWarning by rememberSaveable { mutableStateOf(false) }
    var tooltip by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(active) {
        if (active && settings.tooltipPending()) {
            tooltip = true
            settings.consumeTooltip()
            kotlinx.coroutines.delay(5000)
            tooltip = false
        } else if (!active) tooltip = false
    }
    fun toggle() {
        if (settings.warningHidden()) { settings.setEnabled(!enabled); enabled = settings.enabled() }
        else { hideWarning = false; confirm = true }
    }
    Box(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 55.dp).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painterResource(R.drawable.ic_celular), null, Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(accent))
            Text("Aposta Shake", Modifier.padding(start = 12.dp).clickable { toggle() },
                style = TextStyle(fontSize = 16.sp, fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold,
                    platformStyle = PlatformTextStyle(includeFontPadding = true)),
                color = accent, maxLines = 1, softWrap = false)
            Box(
                Modifier.size(width = 40.dp, height = 48.dp).clickable { tooltip = !tooltip }
                    .semantics { contentDescription = "Informações sobre Aposta Shake" },
                contentAlignment = Alignment.Center,
            ) {
                Image(painterResource(R.drawable.ic_info), null, Modifier.size(16.dp), colorFilter = ColorFilter.tint(accent))
            }
            Spacer(Modifier.weight(1f))
            ShakeToggle(enabled = enabled, accent = accent, onClick = ::toggle)
        }
        if (tooltip) {
            Popup(alignment = Alignment.BottomCenter, offset = IntOffset(0, 8)) {
                Surface(color = accent, shape = RoundedCornerShape(6.dp), modifier = Modifier.widthIn(max = 260.dp)) {
                    Text("Agite o celular para preencher sua aposta com números aleatórios",
                        Modifier.fillMaxWidth().padding(16.dp), color = CaixaDSColorTokens.White,
                        style = CaixaDS.typography.bodyMedium.copy(fontSize = 14.sp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
    if (confirm) CaixaDSLegacyDialog(
        title = if (enabled) "Desativar Aposta Shake" else "Ativar Aposta Shake",
        message = if (enabled) "Deseja desativar a Aposta Shake nas modalidades de loterias?" else "Deseja ativar a Aposta Shake nas modalidades de loterias?",
        positiveLabel = "Sim", negativeLabel = "Não",
        onPositive = { settings.setWarningHidden(hideWarning); settings.setEnabled(!enabled); enabled = settings.enabled(); confirm = false },
        onNegative = { confirm = false },
        extraContent = {
            Row(Modifier.fillMaxWidth().clickable { hideWarning = !hideWarning }, verticalAlignment = Alignment.CenterVertically) {
                Checkbox(hideWarning, onCheckedChange = { hideWarning = it })
                Text("Não exibir o aviso novamente", style = CaixaDS.typography.bodySmall)
            }
        },
    )
}

@Composable
private fun ShakeToggle(enabled: Boolean, accent: Color, onClick: () -> Unit) {
    Box(
        Modifier.size(48.dp).clickable(onClick = onClick)
            .semantics { contentDescription = "Aposta Shake" },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.size(width = 48.dp, height = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (enabled) accent else CaixaDSColorTokens.LegacySocialCanvas)
                .border(1.5.dp, accent, RoundedCornerShape(12.dp)),
        ) {
            Box(
                Modifier.align(if (enabled) Alignment.CenterEnd else Alignment.CenterStart)
                    .size(24.dp).clip(CircleShape).background(accent),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(Modifier.size(12.dp)) {
                    val path = Path()
                    if (enabled) {
                        path.moveTo(size.width * .16f, size.height * .53f)
                        path.lineTo(size.width * .42f, size.height * .78f)
                        path.lineTo(size.width * .86f, size.height * .22f)
                    } else {
                        path.moveTo(size.width * .24f, size.height * .24f)
                        path.lineTo(size.width * .76f, size.height * .76f)
                        path.moveTo(size.width * .76f, size.height * .24f)
                        path.lineTo(size.width * .24f, size.height * .76f)
                    }
                    drawPath(path, CaixaDSColorTokens.White, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round))
                }
            }
        }
    }
}
