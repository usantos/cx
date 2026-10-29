package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixBrand
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixCopyIcon
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSPixTimerIcon
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import kotlinx.coroutines.delay

/** Texto e funções de retorno fornecidos pelo fluxo responsável; sem dependência de pagamento ou domínio. */
data class CaixaDSPixPanelText(
    val intro: String,
    val title: String,
    val singlePayment: String,
    val timeIntro: String,
    val timeOutro: String,
    val expired: String,
    val checkPayment: String,
    val purchases: String,
    val attention: String,
    val warning: String = "",
)

/** Resolve os textos padrão do painel com os recursos do contexto Compose atual. */
@Composable
fun caixaDSPixPanelText(warning: String = ""): CaixaDSPixPanelText = CaixaDSPixPanelText(
    intro = stringResource(LocalizedR.string.format_pix_intro),
    title = stringResource(LocalizedR.string.format_pix_title),
    singlePayment = stringResource(LocalizedR.string.format_pix_single_payment),
    timeIntro = stringResource(LocalizedR.string.format_pix_time_intro),
    timeOutro = stringResource(LocalizedR.string.format_pix_time_outro),
    expired = stringResource(LocalizedR.string.format_pix_expired),
    checkPayment = stringResource(LocalizedR.string.format_pix_check_payment),
    purchases = stringResource(LocalizedR.string.format_pix_purchases),
    attention = stringResource(LocalizedR.string.format_pix_attention),
    warning = warning
)

private val pixPanelStyle = TextStyle(
    fontFamily = CaixaDSFontFamily,
    platformStyle = PlatformTextStyle(includeFontPadding = true)
)

/** Métricas literais dos recursos empacotados fragment_card_pix/fragment_copia_cola_pix, compartilhadas pelos dois chamadores. */
@Composable
fun CaixaDSPixPanel(
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
    texts: CaixaDSPixPanelText = caixaDSPixPanelText(),
) {
    val clipboard = LocalClipboardManager.current
    var copiedRecently by remember(purchaseId) { mutableStateOf(false) }
    val expired = remainingSeconds <= 0
    LaunchedEffect(purchaseId, expired) {
        if (expired && clipboard.getText()?.text == copyPaste.trim()) {
            // Invalida este código expirado sem substituir texto copiado de outra fonte.
            clipboard.setText(AnnotatedString(texts.expired))
        }
    }
    LaunchedEffect(copiedRecently) {
        if (copiedRecently) {
            delay(2_000)
            copiedRecently = false
        }
    }
    Column(modifier.fillMaxWidth().background(CaixaDSColorTokens.White)) {
        if (showIntro) {
            Text(
                texts.intro,
                Modifier.fillMaxWidth().padding(start = 17.dp, end = 17.dp, top = 25.dp),
                color = CaixaDS.colors.cartDark,
                fontSize = 14.sp,
                style = pixPanelStyle
            )
        }
        if (showHeader) PixPanelHeader(formattedAmount)
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            if (showHeader) Box(Modifier.width(5.dp).fillMaxHeight().background(CaixaDS.colors.pix))
            Column(
                Modifier.weight(1f).padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    texts.title,
                    Modifier.fillMaxWidth(),
                    color = CaixaDS.colors.cartDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    style = pixPanelStyle
                )
                PixPanelCopyField(copyPaste, expired, copiedRecently, texts.expired) {
                    clipboard.setText(AnnotatedString(copyPaste.trim()))
                    copiedRecently = true
                    onCopied()
                }
                Text(
                    texts.singlePayment,
                    Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp),
                    color = CaixaDS.colors.cartDark,
                    fontSize = 14.sp,
                    style = pixPanelStyle
                )
                PixPanelTimer(remainingSeconds, texts)
                PixPanelPaymentActions(expired, checking, showRecoveryGuidance, texts, onPurchases, onCheckPayment)
                if (showRecoveryGuidance) {
                    Text(
                        texts.attention,
                        Modifier.fillMaxWidth().padding(top = 16.dp),
                        color = CaixaDS.colors.cartDark,
                        fontWeight = FontWeight.Bold,
                        style = pixPanelStyle
                    )
                    Text(
                        texts.warning,
                        Modifier.fillMaxWidth().padding(top = 16.dp),
                        color = CaixaDS.colors.cartDark,
                        fontSize = 14.sp,
                        style = pixPanelStyle
                    )
                }
            }
        }
    }
}

@Composable
private fun PixPanelHeader(formattedAmount: String) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        uiStrings.getString(LocalizedR.string.ui_pix_fa7d3d),
        Modifier.fillMaxWidth().padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
        color = CaixaDS.colors.cartDark,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        style = pixPanelStyle
    )
    Row(Modifier.fillMaxWidth().height(68.dp)) {
        Box(Modifier.width(5.dp).fillMaxHeight().background(CaixaDS.colors.pix))
        Row(
            Modifier.weight(1f).fillMaxHeight().padding(start = 42.dp, end = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CaixaDSPixBrand((Modifier.size(100.dp, 36.dp)).uiId("ds.0044"))
            Text(
                formattedAmount,
                color = CaixaDSColorTokens.NotificationText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                style = pixPanelStyle
            )
        }
    }
    Box(Modifier.fillMaxWidth().height(.2.dp).background(CaixaDS.colors.graphite))
}

@Composable
private fun PixPanelCopyField(copyPaste: String, expired: Boolean, copiedRecently: Boolean, expiredLabel: String, onCopy: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxWidth().padding(top = 8.dp).height(40.dp)
            .background(if (expired) CaixaDS.colors.disabledControl else CaixaDSColorTokens.White, RoundedCornerShape(4.dp))
            .border(1.2.dp, if (expired) CaixaDS.colors.disabledControl else CaixaDSColorTokens.PixOutline, RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (expired) {
                expiredLabel
            } else {
                copyPaste.take(19) +
                    if (copyPaste.length > 19) uiStrings.getString(LocalizedR.string.ui_texto_ab5df6) else ""
            },
            Modifier.weight(1f).padding(start = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (expired) CaixaDSColorTokens.White else CaixaDS.colors.graphite,
            style = pixPanelStyle
        )
        if (!expired) {
            Box(
                Modifier.width(60.dp).fillMaxSize()
                    .background(if (copiedRecently) CaixaDS.colors.pix else CaixaDS.colors.pixCopyButton)
                    .uiId("ds.0045").clickable(enabled = !copiedRecently) {
                        onCopy()
                    },
                contentAlignment = Alignment.Center
            ) {
                CaixaDSPixCopyIcon(copiedRecently, (Modifier.size(if (copiedRecently) 24.dp else 15.dp)).uiId("ds.0046"))
            }
        }
    }
}

@Composable
private fun PixPanelTimer(remainingSeconds: Long, texts: CaixaDSPixPanelText) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Text(
        texts.timeIntro,
        Modifier.fillMaxWidth().padding(top = 32.dp),
        color = CaixaDS.colors.cartDark,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        style = pixPanelStyle
    )
    val timerColor = when {
        remainingSeconds < 60 -> CaixaDSColorTokens.PixUrgent
        remainingSeconds < 120 -> CaixaDSColorTokens.PixWarning
        else -> CaixaDS.colors.cartDark
    }
    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        CaixaDSPixTimerIcon((Modifier.size(25.dp, 22.dp)).uiId("ds.0047"), timerColor)
        Text(
            uiStrings.getString(
                LocalizedR.string.ui_02d_02d_0b2e34
            ).format(remainingSeconds.coerceAtLeast(0) / 60, remainingSeconds.coerceAtLeast(0) % 60),
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
        color = CaixaDS.colors.cartDark,
        fontSize = 14.sp,
        style = pixPanelStyle
    )
}

@Composable
private fun PixPanelPaymentActions(
    expired: Boolean,
    checking: Boolean,
    showRecoveryGuidance: Boolean,
    texts: CaixaDSPixPanelText,
    onPurchases: () -> Unit,
    onCheckPayment: () -> Unit,
) {
    // O APK restringe as duas ações a timeOutro: verificação em 32dp e histórico em
    // 16dp. A expiração mantém a verificação INVISIBLE; o histórico sobrepõe essa área medida.
    Box(Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.TopCenter) {
        if (expired || checking) {
            PixPanelStatusButton(texts.checkPayment, {}, (Modifier.alpha(0f)).uiId("ds.0048"), exposeText = false)
            if (expired && showRecoveryGuidance) {
                PixPanelStatusButton(texts.purchases, onPurchases, (Modifier.offset(y = (-16).dp)).uiId("ds.0049"))
            }
            if (checking) PixPanelStatusProgress()
        } else {
            PixPanelStatusButton(texts.checkPayment, onCheckPayment, modifier = Modifier.uiId("ds.0050"))
        }
    }
}

@Composable
private fun PixPanelStatusButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, exposeText: Boolean = true) {
    val boldStart = label.indexOf('\n').let { if (it < 0) 0 else it + 1 }
    val labelText = buildAnnotatedString {
        append(label)
        if (boldStart < label.length) {
            addStyle(
                SpanStyle(fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.Bold),
                boldStart,
                label.length
            )
        }
    }
    val interaction = if (exposeText) {
        Modifier.uiId("ds.0051").clickable(onClick = onClick, role = Role.Button)
    } else {
        Modifier.alpha(0f).clearAndSetSemantics { }
    }
    Box(
        modifier = modifier
            .then(interaction)
            .defaultMinSize(minWidth = 88.dp, minHeight = 48.dp)
            .background(CaixaDS.colors.paymentStatus, RoundedCornerShape(4.dp))
            .border(1.dp, CaixaDS.colors.paymentStatus, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = labelText,
            color = CaixaDSColorTokens.White,
            fontFamily = CaixaDSRegularFontFamily,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            style = pixPanelStyle.copy(fontSize = 16.sp, lineHeight = 20.sp, color = Color.White)
        )
    }
}

/** Equivalente Compose/DS do indicador de pagamento em linha legado que ocupa toda a largura disponível. */
@Composable
private fun PixPanelStatusProgress() {
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth().height(4.dp),
        color = CaixaDSColorTokens.Blue700,
        trackColor = Color.Transparent
    )
}
