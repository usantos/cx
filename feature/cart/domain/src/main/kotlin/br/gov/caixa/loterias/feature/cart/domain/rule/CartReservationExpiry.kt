package br.gov.caixa.loterias.feature.cart.domain.rule

import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

object CartReservationExpiry {
    private const val LOCAL_TIMESTAMP_LENGTH = 19
    fun expirationInstant(value: String): Instant? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return null
        return runCatching { OffsetDateTime.parse(trimmed).toInstant() }
            .recoverCatching { Instant.parse(trimmed) }
            .recoverCatching {
                LocalDateTime.parse(trimmed.take(LOCAL_TIMESTAMP_LENGTH)).atZone(ZoneId.of("America/Sao_Paulo")).toInstant()
            }
            .getOrNull()
    }

    fun removalDue(expiresAt: String, now: Instant): Boolean = expirationInstant(expiresAt)?.let { !it.isAfter(now) } == true

    /** A finalização e a exibição existentes consideram expirada uma fração restante de um segundo. */
    fun hasExpiredPoolShares(cart: ShoppingCart?, now: Instant): Boolean = cart?.items.orEmpty().any { item ->
        item.kind == CartItemKind.POOL_SHARE && item.poolShare?.let { share ->
            expirationInstant(share.expiresAt)?.let { Duration.between(now, it).seconds <= 0 } == true
        } == true
    }
}
