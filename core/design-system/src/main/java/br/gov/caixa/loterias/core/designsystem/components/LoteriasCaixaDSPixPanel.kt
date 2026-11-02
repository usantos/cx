package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import kotlinx.coroutines.delay

/** Métricas literais dos recursos empacotados fragment_card_pix/fragment_copia_cola_pix, compartilhadas pelos dois chamadores. */
@Composable
fun LoteriasCaixaDSPixPanel(
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
    texts: LoteriasCaixaDSPixPanelText = loteriasCaixaDSPixPanelText(),
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
            delay(PixPanelMetrics.COPY_FEEDBACK_MILLIS)
            copiedRecently = false
        }
    }
    Column(modifier.fillMaxWidth().background(LoteriasCaixaDSColorTokens.White)) {
        if (showIntro) {
            Text(
                texts.intro,
                Modifier.fillMaxWidth().padding(start = 17.dp, end = 17.dp, top = 25.dp),
                color = LoteriasCaixaDS.colors.cartDark,
                fontSize = 14.sp,
                style = pixPanelStyle
            )
        }
        if (showHeader) PixPanelHeader(formattedAmount)
        PixPanelContent(
            copyPaste = copyPaste,
            remainingSeconds = remainingSeconds,
            expired = expired,
            checking = checking,
            copiedRecently = copiedRecently,
            showHeader = showHeader,
            showRecoveryGuidance = showRecoveryGuidance,
            texts = texts,
            onCopy = {
                clipboard.setText(AnnotatedString(copyPaste.trim()))
                copiedRecently = true
                onCopied()
            },
            onPurchases = onPurchases,
            onCheckPayment = onCheckPayment
        )
    }
}

private object PixPanelMetrics {
    const val COPY_FEEDBACK_MILLIS: Long = 2_000
}
