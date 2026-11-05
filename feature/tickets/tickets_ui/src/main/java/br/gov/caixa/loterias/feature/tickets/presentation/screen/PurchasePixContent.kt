package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSPixPanel
import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase
import kotlinx.coroutines.delay

@Composable
fun PendingPurchasePixDelivered(purchase: Purchase, checking: Boolean, remainingSeconds: () -> Long, onCheckPayment: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var remaining by remember(purchase.id, purchase.paymentExpiresAt) { mutableLongStateOf(remainingSeconds()) }
    var copied by remember(purchase.id) { androidx.compose.runtime.mutableStateOf(false) }
    LaunchedEffect(purchase.id, purchase.paymentExpiresAt) {
        while (remaining > 0) {
            delay(1_000)
            remaining = remainingSeconds()
        }
    }
    LaunchedEffect(purchase.id, purchase.paymentExpiresAt, copied, lifecycleOwner) {
        if (copied) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (remaining > 0) {
                    delay(20_000)
                    if (remaining > 0) onCheckPayment()
                }
            }
        }
    }
    LoteriasCaixaDSPixPanel(
        purchaseId = purchase.id, copyPaste = purchase.pixCopyPaste, remainingSeconds = remaining,
        checking = checking, onCheckPayment = onCheckPayment, onCopied = { copied = true }, onPurchases = {},
        showIntro = false, showHeader = false, showRecoveryGuidance = false, modifier = Modifier.uiId("tickets.0238")
    )
}
