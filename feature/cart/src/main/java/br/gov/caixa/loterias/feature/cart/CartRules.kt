package br.gov.caixa.loterias.feature.cart

import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Locale

internal const val CART_LOCATION_CONSENT = "As apostas só podem ocorrer em território nacional. Portanto, você somente conseguirá pagar uma compra se estiver no Brasil, não havendo impedimento para utilizar os demais serviços.\nPodemos consultar a sua localização?"
internal const val CART_LOCATION_BLOCKED = "As apostas só podem ocorrer em território nacional. Suas apostas serão mantidas no carrinho para a conclusão da compra quando você estiver no país."

internal fun drawDateLabel(value: String): String {
    val trimmed = value.trim()
    val slashDate = Regex("(\\d{2})/(\\d{2})").find(trimmed)
    if (slashDate != null) return slashDate.value
    val isoDate = Regex("\\d{4}-(\\d{2})-(\\d{2})").find(trimmed)
    return isoDate?.let { "${it.groupValues[2]}/${it.groupValues[1]}" }.orEmpty()
}

internal data class CartLine(val priceCents: Int, val quantity: Int = 1)

internal fun totalCartCents(lines: List<CartLine>): Int =
    lines.sumOf { line -> line.priceCents.coerceAtLeast(0) * line.quantity.coerceAtLeast(0) }

internal fun formatCurrency(cents: Int): String =
    "R$ %,.2f".format(Locale.forLanguageTag("pt-BR"), cents.coerceAtLeast(0) / 100.0)

internal data class CartSectionItems(
    val kind: CartItemKind,
    val items: List<CartItem>,
)

internal fun visibleCartSections(items: List<CartItem>): List<CartSectionItems> = listOf(
    CartItemKind.POOL_SHARE,
    CartItemKind.INDIVIDUAL_BET,
    CartItemKind.COMBO,
).mapNotNull { kind ->
    items.filter { it.kind == kind }.takeIf(List<CartItem>::isNotEmpty)?.let { CartSectionItems(kind, it) }
}

internal fun canClearCart(cart: ShoppingCart?): Boolean = cart != null &&
    (cart.betCount > 0 || cart.items.any { it.kind == CartItemKind.POOL_SHARE || it.kind == CartItemKind.COMBO })

internal fun canFavoriteCart(cart: ShoppingCart?, authenticated: Boolean): Boolean {
    if (!authenticated || cart == null || cart.betCount <= 0) return false
    return cart.items.all { it.kind == CartItemKind.INDIVIDUAL_BET }
}

internal fun quantityLabel(quantity: Int, singular: String, plural: String): String =
    "${quantity.coerceAtLeast(0)} ${if (quantity == 1) singular else plural}"

internal enum class PoolExpiryState { UNKNOWN, ACTIVE, WARNING, EXPIRED }

internal data class PoolExpiry(
    val state: PoolExpiryState,
    val remainingLabel: String,
)

internal fun hasExpiredPoolShares(cart: ShoppingCart?, now: Instant): Boolean = cart?.items.orEmpty().any {
    it.kind == CartItemKind.POOL_SHARE && it.poolShare?.let { share ->
        poolExpiry(share.expiresAt, now).state == PoolExpiryState.EXPIRED
    } == true
}

internal fun poolExpiry(expiresAt: String, now: Instant): PoolExpiry {
    val expiration = reservationExpirationInstant(expiresAt)
        ?: return PoolExpiry(PoolExpiryState.UNKNOWN, "--:--")
    val remaining = Duration.between(now, expiration)
    val totalSeconds = remaining.seconds
    if (totalSeconds <= 0) return PoolExpiry(PoolExpiryState.EXPIRED, "00:00")
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return PoolExpiry(
        state = if (totalSeconds < 60) PoolExpiryState.WARNING else PoolExpiryState.ACTIVE,
        remainingLabel = "%02d:%02d".format(Locale.ROOT, minutes, seconds),
    )
}

internal fun reservationExpirationInstant(value: String): Instant? {
    val trimmed = value.trim()
    if (trimmed.isEmpty()) return null
    return runCatching { OffsetDateTime.parse(trimmed).toInstant() }
        .recoverCatching { Instant.parse(trimmed) }
        .recoverCatching {
            LocalDateTime.parse(trimmed.take(19))
                .atZone(ZoneId.of("America/Sao_Paulo"))
                .toInstant()
        }
        .getOrNull()
}
