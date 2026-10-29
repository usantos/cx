package br.gov.caixa.loterias.feature.tickets.presentation.state

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

/** Capturado ao receber os detalhes e então compartilhado pela renderização e pelas consultas de pagamento. */
internal data class PurchasePixDeadline(val purchaseId: Long, val expiresAt: String, val elapsedDeadlineMillis: Long) {
    fun remainingSeconds(elapsedMillis: Long): Long =
        ((elapsedDeadlineMillis - elapsedMillis).coerceAtLeast(0) / 1_000)

    companion object {
        fun capture(purchaseId: Long, expiresAt: String, serverDateTime: String, elapsedMillis: Long,
            deviceInstant: Instant,
        ): PurchasePixDeadline {
            val expiration = parse(expiresAt)
            val now = parse(serverDateTime) ?: deviceInstant
            val durationMillis = expiration?.let { Duration.between(now, it).toMillis().coerceAtLeast(0) } ?: 0
            return PurchasePixDeadline(purchaseId, expiresAt, elapsedMillis + durationMillis)
        }
        private fun parse(value: String): Instant? = runCatching { OffsetDateTime.parse(value).toInstant() }
            .recoverCatching { LocalDateTime.parse(value).atZone(ZoneId.of("America/Sao_Paulo")).toInstant() }
            .getOrNull()
    }
}
