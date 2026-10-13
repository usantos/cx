package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** Text and callbacks supplied by the owning flow; no payment/domain dependency. */
data class CaixaDSLegacyPixPanelText(
    val intro: String = "Copie o código abaixo e utilize o Pix Copia e Cola no aplicativo que você fará o pagamento.",
    val title: String = "Pix Copia e Cola",
    val singlePayment: String = "*O código poderá ser pago somente 1 vez.",
    val timeIntro: String = "Você tem",
    val timeOutro: String = "Para efetuar o pagamento",
    val expired: String = "Código Expirado",
    val checkPayment: String = "Verificar Pagamento",
    val purchases: String = "Ir para as\nMinhas Compras",
    val attention: String = "Atenção:",
    val warning: String = "",
)

private val pixPanelStyle = TextStyle(fontFamily = CaixaDSFontFamily,
    platformStyle = PlatformTextStyle(includeFontPadding = true))

/** Literal packed fragment_card_pix/fragment_copia_cola_pix metrics, shared by both callers. */
@Composable
fun CaixaDSLegacyPixPanel(
    purchaseId: Long,
    copyPaste: String,
    remainingSeconds: Long,
    checking: Boolean,
    onCheckPayment: () -> Unit,
    onCopied: () -> Unit,
    onPurchases: () -> Unit,
    modifier: Modifier = Modifier,
    formattedAmount: String = "",
    showIntro: Boolean = true,
    showHeader: Boolean = true,
    showRecoveryGuidance: Boolean = true,
    texts: CaixaDSLegacyPixPanelText = CaixaDSLegacyPixPanelText(),
) {
    val clipboard = LocalClipboardManager.current
    var copiedRecently by remember(purchaseId) { mutableStateOf(false) }
    val expired = remainingSeconds <= 0
    LaunchedEffect(purchaseId, expired) {
        if (expired && clipboard.getText()?.text == copyPaste.trim()) {
            // Invalidate this expired code without replacing text copied from another source.
            clipboard.setText(AnnotatedString(texts.expired))
        }
    }
    LaunchedEffect(copiedRecently) {
        if (copiedRecently) { delay(2_000); copiedRecently = false }
    }
    Column(modifier.fillMaxWidth().background(CaixaDSColorTokens.White)) {
        if (showIntro) Text(texts.intro, Modifier.fillMaxWidth().padding(start = 17.dp, end = 17.dp, top = 25.dp),
            color = CaixaDS.colors.legacyCartDark, fontSize = 14.sp, style = pixPanelStyle)
        if (showHeader) {
            Text("Pix", Modifier.fillMaxWidth().padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                color = CaixaDS.colors.legacyCartDark, fontWeight = FontWeight.Bold, fontSize = 14.sp, style = pixPanelStyle)
            Row(Modifier.fillMaxWidth().height(68.dp)) {
                Box(Modifier.width(5.dp).fillMaxHeight().background(CaixaDS.colors.legacyPix))
                Row(Modifier.weight(1f).fillMaxHeight().padding(start = 42.dp, end = 28.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    CaixaDSPixBrand(Modifier.size(100.dp, 36.dp))
                    Text(formattedAmount, color = CaixaDSColorTokens.LegacyNotificationText,
                        fontWeight = FontWeight.Bold, fontSize = 15.sp, style = pixPanelStyle)
                }
            }
            Box(Modifier.fillMaxWidth().height(.2.dp).background(CaixaDS.colors.legacyGraphite))
        }
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            if (showHeader) Box(Modifier.width(5.dp).fillMaxHeight().background(CaixaDS.colors.legacyPix))
            Column(Modifier.weight(1f).padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(texts.title, Modifier.fillMaxWidth(), color = CaixaDS.colors.legacyCartDark,
                    fontWeight = FontWeight.Bold, fontSize = 14.sp, style = pixPanelStyle)
                Row(Modifier.fillMaxWidth().padding(top = 8.dp).height(40.dp)
                    .background(if (expired) CaixaDS.colors.legacyDisabledControl else CaixaDSColorTokens.White, RoundedCornerShape(4.dp))
                    .border(1.2.dp, if (expired) CaixaDS.colors.legacyDisabledControl else CaixaDSColorTokens.LegacyPixOutline, RoundedCornerShape(4.dp)),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(if (expired) texts.expired else copyPaste.take(19) + if (copyPaste.length > 19) "..." else "",
                        Modifier.weight(1f).padding(start = 16.dp), maxLines = 1, overflow = TextOverflow.Ellipsis,
                        color = if (expired) CaixaDSColorTokens.White else CaixaDS.colors.legacyGraphite,
                        style = pixPanelStyle)
                    if (!expired) Box(Modifier.width(60.dp).fillMaxSize()
                        .background(if (copiedRecently) CaixaDS.colors.legacyPix else CaixaDS.colors.legacyPixCopyButton)
                        .clickable(enabled = !copiedRecently) {
                            clipboard.setText(AnnotatedString(copyPaste.trim())); copiedRecently = true; onCopied()
                        }, contentAlignment = Alignment.Center) {
                        CaixaDSPixCopyIcon(copiedRecently, Modifier.size(if (copiedRecently) 24.dp else 15.dp))
                    }
                }
                Text(texts.singlePayment, Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp),
                    color = CaixaDS.colors.legacyCartDark, fontSize = 14.sp, style = pixPanelStyle)
                Text(texts.timeIntro, Modifier.fillMaxWidth().padding(top = 32.dp),
                    color = CaixaDS.colors.legacyCartDark, fontSize = 14.sp, textAlign = TextAlign.Center, style = pixPanelStyle)
                val timerColor = when {
                    remainingSeconds < 60 -> CaixaDSColorTokens.LegacyPixUrgent
                    remainingSeconds < 120 -> CaixaDSColorTokens.LegacyPixWarning
                    else -> CaixaDS.colors.legacyCartDark
                }
                Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    CaixaDSPixTimerIcon(Modifier.size(25.dp, 22.dp), timerColor)
                    Text("%02d:%02d".format(remainingSeconds.coerceAtLeast(0) / 60, remainingSeconds.coerceAtLeast(0) % 60),
                        Modifier.padding(start = 8.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold,
                        color = timerColor, style = pixPanelStyle)
                }
                Text(texts.timeOutro, Modifier.padding(top = 8.dp),
                    color = CaixaDS.colors.legacyCartDark, fontSize = 14.sp, style = pixPanelStyle)
                // APK constrains both actions to timeOutro: Verify at 32dp and history at
                // 16dp. Expiration keeps Verify INVISIBLE; history overlays that measured slot.
                Box(Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.TopCenter) {
                    if (expired || checking) {
                        PixPanelStatusButton(texts.checkPayment, {}, Modifier.alpha(0f), exposeText = false)
                        if (expired && showRecoveryGuidance) {
                            PixPanelStatusButton(texts.purchases, onPurchases, Modifier.offset(y = (-16).dp))
                        }
                        if (checking) PixPanelStatusProgress()
                    } else PixPanelStatusButton(texts.checkPayment, onCheckPayment)
                }
                if (showRecoveryGuidance) {
                    Text(texts.attention, Modifier.fillMaxWidth().padding(top = 16.dp),
                        color = CaixaDS.colors.legacyCartDark, fontWeight = FontWeight.Bold, style = pixPanelStyle)
                    Text(texts.warning, Modifier.fillMaxWidth().padding(top = 16.dp),
                        color = CaixaDS.colors.legacyCartDark, fontSize = 14.sp, style = pixPanelStyle)
                }
            }
        }
    }
}

@Composable
private fun PixPanelStatusButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    exposeText: Boolean = true,
) {
    val boldStart = label.indexOf('\n').let { if (it < 0) 0 else it + 1 }
    val labelText = buildAnnotatedString {
        append(label)
        if (boldStart < label.length) {
            addStyle(
                SpanStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold),
                boldStart,
                label.length,
            )
        }
    }
    val interaction = if (exposeText) {
        Modifier.clickable(onClick = onClick, role = Role.Button)
    } else {
        Modifier.alpha(0f).clearAndSetSemantics { }
    }
    Box(
        modifier = modifier
            .then(interaction)
            .defaultMinSize(minWidth = 88.dp, minHeight = 48.dp)
            .border(1.dp, CaixaDS.colors.legacyPaymentStatus, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = labelText,
            color = CaixaDSColorTokens.White,
            fontFamily = CaixaDSLegacyRegularFontFamily,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            style = pixPanelStyle.copy(fontSize = 16.sp, lineHeight = 20.sp, color = Color.White),
        )
    }
}

/** Compose/DS equivalent of the legacy match-parent inline payment loading indicator. */
@Composable
private fun PixPanelStatusProgress() {
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth().height(4.dp),
        color = CaixaDSColorTokens.Blue700,
        trackColor = Color.Transparent,
    )
}
