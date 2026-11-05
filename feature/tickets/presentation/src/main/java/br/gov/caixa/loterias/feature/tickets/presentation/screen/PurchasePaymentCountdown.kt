package br.gov.caixa.loterias.feature.tickets.presentation.screen

import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

fun Purchase.paymentSecondsRemaining(now: LocalDateTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))): Long {
    val serverZone = ZoneId.of("America/Sao_Paulo")
    val expiration = runCatching { OffsetDateTime.parse(paymentExpiresAt).toInstant() }
        .recoverCatching { LocalDateTime.parse(paymentExpiresAt).atZone(serverZone).toInstant() }
        .getOrNull() ?: return 0
    return Duration.between(now.atZone(serverZone).toInstant(), expiration).seconds.coerceAtLeast(0)
}
