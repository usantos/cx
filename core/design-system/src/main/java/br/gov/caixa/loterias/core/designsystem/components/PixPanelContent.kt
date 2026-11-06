package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS

@Composable
internal fun PixPanelContent(
    copyPaste: String,
    remainingSeconds: Long,
    expired: Boolean,
    checking: Boolean,
    copiedRecently: Boolean,
    showHeader: Boolean,
    showRecoveryGuidance: Boolean,
    texts: LoteriasCaixaDSPixPanelText,
    onCopy: () -> Unit,
    onPurchases: () -> Unit,
    onCheckPayment: () -> Unit,
) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        if (showHeader) Box(Modifier.width(5.dp).fillMaxHeight().background(LoteriasCaixaDS.colors.pix))
        Column(
            Modifier.weight(1f).padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!expired) PixPanelQrCode(copyPaste)
            Text(
                texts.title,
                Modifier.fillMaxWidth(),
                color = LoteriasCaixaDS.colors.cartDark,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                style = pixPanelStyle
            )
            PixPanelCopyField(copyPaste, expired, copiedRecently, texts.expired, onCopy)
            Text(
                texts.singlePayment,
                Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp),
                color = LoteriasCaixaDS.colors.cartDark,
                fontSize = 14.sp,
                style = pixPanelStyle
            )
            PixPanelTimer(remainingSeconds, texts)
            PixPanelPaymentActions(expired, checking, showRecoveryGuidance, texts, onPurchases, onCheckPayment)
            if (showRecoveryGuidance) {
                Text(
                    texts.attention,
                    Modifier.fillMaxWidth().padding(top = 16.dp),
                    color = LoteriasCaixaDS.colors.cartDark,
                    fontWeight = FontWeight.Bold,
                    style = pixPanelStyle
                )
                Text(
                    texts.warning,
                    Modifier.fillMaxWidth().padding(top = 16.dp),
                    color = LoteriasCaixaDS.colors.cartDark,
                    fontSize = 14.sp,
                    style = pixPanelStyle
                )
            }
        }
    }
}
