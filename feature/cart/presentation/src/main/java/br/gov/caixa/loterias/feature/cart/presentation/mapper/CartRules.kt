package br.gov.caixa.loterias.feature.cart.presentation.mapper

import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.formatting.localizedMessage as messageText
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Locale

internal val CART_LOCATION_CONSENT get() = messageText("as_apostas_so_podem_ocorrer_em_t_ee6a34")
internal val CART_LOCATION_BLOCKED get() = messageText("as_apostas_so_podem_ocorrer_em_t_efd4af")

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

internal fun formatCurrency(cents: Int): String = "R$ %,.2f".format(Locale.forLanguageTag("pt-BR"), cents.coerceAtLeast(0) / 100.0)

internal data class CartSectionItems(val kind: CartItemKind, val items: List<CartItem>)

internal fun visibleCartSections(items: List<CartItem>): List<CartSectionItems> = listOf(
    CartItemKind.POOL_SHARE,
    CartItemKind.INDIVIDUAL_BET,
    CartItemKind.COMBO
).mapNotNull { kind ->
    items.filter { it.kind == kind }.takeIf(List<CartItem>::isNotEmpty)?.let { CartSectionItems(kind, it) }
}

/** CarrinhoActivity da CX atual conta os cartões individuais, combos e bolões exibidos. */
internal fun canClearCart(cart: ShoppingCart?): Boolean = cart?.items?.isNotEmpty() == true

internal fun canFavoriteCart(cart: ShoppingCart?, authenticated: Boolean): Boolean =
    authenticated && cart?.items?.isNotEmpty() == true && cart.items.none { it.kind == CartItemKind.POOL_SHARE }

internal fun quantityLabel(quantity: Int, singular: String, plural: String): String =
    "${quantity.coerceAtLeast(0)} ${if (quantity == 1) singular else plural}"

internal enum class PoolExpiryState { UNKNOWN, ACTIVE, WARNING, EXPIRED }

internal data class PoolExpiry(val state: PoolExpiryState, val remainingLabel: String)

internal fun hasExpiredPoolShares(cart: ShoppingCart?, now: Instant): Boolean =
    br.gov.caixa.loterias.feature.cart.domain.rule.CartReservationExpiry.hasExpiredPoolShares(cart, now)

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
        remainingLabel = "%02d:%02d".format(Locale.ROOT, minutes, seconds)
    )
}

internal fun reservationExpirationInstant(value: String): Instant? =
    br.gov.caixa.loterias.feature.cart.domain.rule.CartReservationExpiry.expirationInstant(value)
