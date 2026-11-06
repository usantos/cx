package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
internal fun PixPanelPaymentActions(
    expired: Boolean,
    checking: Boolean,
    showRecoveryGuidance: Boolean,
    texts: LoteriasCaixaDSPixPanelText,
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
                SpanStyle(fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.Bold),
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
            .background(LoteriasCaixaDS.colors.paymentStatus, RoundedCornerShape(4.dp))
            .border(1.dp, LoteriasCaixaDS.colors.paymentStatus, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = labelText,
            color = LoteriasCaixaDSColorTokens.White,
            fontFamily = LoteriasCaixaDSRegularFontFamily,
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
        color = LoteriasCaixaDSColorTokens.Blue700,
        trackColor = Color.Transparent
    )
}
