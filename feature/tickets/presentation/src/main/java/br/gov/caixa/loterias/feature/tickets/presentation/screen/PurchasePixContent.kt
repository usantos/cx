package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSPixPanel
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlinx.coroutines.delay

@Composable
internal fun PendingPurchasePix(purchase: Purchase, checking: Boolean, remainingSeconds: () -> Long, onCheckPayment: () -> Unit) {
    var remaining by remember(purchase.id, purchase.paymentExpiresAt) { mutableLongStateOf(remainingSeconds()) }
    var copied by remember(purchase.id) { androidx.compose.runtime.mutableStateOf(false) }
    LaunchedEffect(purchase.id, purchase.paymentExpiresAt) {
        while (remaining > 0) {
            delay(1_000)
            remaining = remainingSeconds()
        }
    }
    LaunchedEffect(purchase.id, copied) {
        if (copied) {
            while (remaining > 0) {
                delay(20_000)
                if (remaining > 0) onCheckPayment()
            }
        }
    }
    CaixaDSPixPanel(
        purchaseId = purchase.id, copyPaste = purchase.pixCopyPaste, remainingSeconds = remaining,
        checking = checking, onCheckPayment = onCheckPayment, onCopied = { copied = true }, onPurchases = {},
        showIntro = false, showHeader = false, showRecoveryGuidance = false, modifier = Modifier.uiId("tickets.0238")
    )
}

internal fun Purchase.paymentSecondsRemaining(now: LocalDateTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))): Long {
    val serverZone = ZoneId.of("America/Sao_Paulo")
    val expiration = runCatching { OffsetDateTime.parse(paymentExpiresAt).toInstant() }
        .recoverCatching { LocalDateTime.parse(paymentExpiresAt).atZone(serverZone).toInstant() }
        .getOrNull() ?: return 0
    return Duration.between(now.atZone(serverZone).toInstant(), expiration).seconds.coerceAtLeast(0)
}
