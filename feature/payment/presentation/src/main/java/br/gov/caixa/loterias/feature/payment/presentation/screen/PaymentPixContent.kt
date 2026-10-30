package br.gov.caixa.loterias.feature.payment.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSPixPanel
import br.gov.caixa.loterias.core.designsystem.components.caixaDSPixPanelText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.payment.domain.model.PixCharge
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PIX_EXPIRED_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PIX_RECOVERY_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.secondsUntil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay

@Composable
internal fun PixContent(pix: PixCharge?, checking: Boolean, modifier: Modifier, onCheckPayment: () -> Unit, onPurchases: () -> Unit) {
    if (pix == null) return
    val lifecycleOwner = LocalLifecycleOwner.current
    var remaining by remember(pix.expiresAt) { mutableLongStateOf(secondsUntil(pix.expiresAt)) }
    var copied by remember(pix.purchaseId) { mutableStateOf(false) }
    LaunchedEffect(pix.expiresAt) {
        while (remaining > 0) {
            delay(1_000)
            remaining = secondsUntil(pix.expiresAt)
        }
    }
    LaunchedEffect(pix.purchaseId, pix.expiresAt, copied, lifecycleOwner) {
        if (copied) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (remaining > 0) {
                    delay(20_000)
                    if (remaining > 0) onCheckPayment()
                }
            }
        }
    }
    Column(modifier.fillMaxSize().uiId("pay.0100").verticalScroll(rememberScrollState()).background(CaixaDS.colors.canvas)) {
        CaixaDSPixPanel(
            purchaseId = pix.purchaseId, copyPaste = pix.copyPaste, remainingSeconds = remaining,
            formattedAmount = formatBrazilianCurrency(pix.amount), checking = checking,
            onCheckPayment = onCheckPayment, onCopied = { copied = true }, onPurchases = onPurchases,
            texts = caixaDSPixPanelText(warning = if (remaining <= 0) PIX_EXPIRED_GUIDANCE else PIX_RECOVERY_GUIDANCE),
            modifier = Modifier
                .uiId("pay.0101")
        )
    }
}
