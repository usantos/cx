package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.CartItemBadge
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import br.gov.caixa.loterias.core.domain.cart.CartPoolShare
import br.gov.caixa.loterias.core.domain.cart.CartRepository
import br.gov.caixa.loterias.core.domain.cart.CartValidationResult
import br.gov.caixa.loterias.core.domain.service.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import br.gov.caixa.loterias.core.network.CartBetDto
import br.gov.caixa.loterias.core.network.CartDto
import com.google.gson.JsonElement
import br.gov.caixa.loterias.core.network.BackendHttpFailure
import java.io.IOException
import java.math.BigDecimal

class RemoteCartRepository(private val remote: CartRemoteDataSource) : CartRepository {
    override suspend fun current(): ShoppingCart = remote.currentCart().toDomainCart()

    override suspend fun remove(item: CartItem): ShoppingCart {
        val dto = when (item.kind) {
            CartItemKind.COMBO -> remote.removeCartCombo(item.id)
            CartItemKind.INDIVIDUAL_BET, CartItemKind.POOL_SHARE -> remote.removeCartBet(item.id)
        }
        return dto.toDomainCart()
    }

    override suspend fun clear(): ShoppingCart = remote.clearCart().toDomainCart()

    override suspend fun validate(): CartValidationResult = try {
        try {
            remote.validateCart()
            CartValidationResult.Accepted
        } catch (failure: BackendHttpFailure) {
            CartValidationResult.Rejected(failure.toServiceFailure(allowRegistration = false))
        }
    } catch (_: com.google.gson.stream.MalformedJsonException) {
        CartValidationResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE))
    } catch (_: com.google.gson.JsonParseException) {
        CartValidationResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE))
    } catch (failure: IOException) {
        CartValidationResult.Rejected(ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()))
    }

    override suspend fun validateFavoriteName(name: String) = remote.validateFavoriteCartName(name)

    override suspend fun saveFavorite(name: String) = remote.saveCurrentCartFavorite(name)
}

internal fun CartDto?.toDomainCart(): ShoppingCart {
    val cart = requireNotNull(this) { "Carrinho sem payload" }
    val mappedItems = buildList {
        cart.poolShares.orEmpty().forEach { add(it.toCartItem(CartItemKind.POOL_SHARE)) }
        cart.individualBets.orEmpty().forEach { add(it.toCartItem(CartItemKind.INDIVIDUAL_BET)) }
        cart.combos.orEmpty().forEach { combo ->
            val bets = combo.bets.orEmpty()
            val comboBadges = buildSet {
                add(CartItemBadge.COMBO)
                if (bets.any(CartBetDto::isMirror)) add(CartItemBadge.MIRROR)
                if (bets.any(CartBetDto::isSurprise)) add(CartItemBadge.SURPRISE)
                if (bets.any { it.recurringDraws.orZero() > 0 }) add(CartItemBadge.RECURRING)
            }
            add(
                CartItem(
                    id = combo.id ?: 0,
                    kind = CartItemKind.COMBO,
                    title = combo.type?.name.orEmpty().ifBlank { "Combo" },
                    description = bets.size.betCountLabel(),
                    amount = bets.fold(BigDecimal.ZERO) { total, bet -> total + bet.amount.orZero() },
                    modalityCode = "COMBO",
                    badges = comboBadges,
                    comboBetCount = bets.size,
                ),
            )
        }
    }
    return ShoppingCart(
        id = cart.id ?: 0,
        total = cart.summaryTotal(mappedItems),
        items = mappedItems,
        expiredPoolShareRemoved = cart.expiredPoolShareRemoved == true,
        totalBetCount = cart.bets?.size,
    )
}

private fun CartBetDto.toCartItem(kind: CartItemKind): CartItem {
    val reservation = poolReservation
    val numberRows = selectedNumbers.numberRows()
    val modalityCode = modality.modalityCode()
    val modalityName = modality.modalityName(contestType)
    val surprise = isSurprise()
    val recurringDrawCount = recurringDraws.orZero().takeIf { it > 0 } ?: 1
    val clovers = selectedClovers.flattenNumbers()
    val pool = reservation?.let {
        CartPoolShare(
            code = it.poolCode.orEmpty(),
            reservedShare = it.shareNumber.orZero(),
            totalShares = it.totalShares.orZero(),
            expiresAt = it.expiresAt.orEmpty(),
        )
    }
    val badges = buildSet {
        if (exchange == true) add(CartItemBadge.EXCHANGE)
        if (kind == CartItemKind.POOL_SHARE) add(CartItemBadge.POOL)
        if (combo == true) add(CartItemBadge.COMBO)
        if (isMirror()) add(CartItemBadge.MIRROR)
        if (surprise) add(CartItemBadge.SURPRISE)
        if (recurringDraws.orZero() > 0) add(CartItemBadge.RECURRING)
    }
    return CartItem(
        id = id ?: 0,
        kind = kind,
        title = when (kind) {
            CartItemKind.POOL_SHARE -> "$modalityName • Bolão ${reservation?.poolCode.orEmpty()}"
            else -> modalityName
        },
        description = when (kind) {
            CartItemKind.POOL_SHARE -> reservation?.shareNumber?.let { "Cota $it" } ?: "Cota de bolão"
            else -> if (surprise && numberRows.flatten().isEmpty()) {
                List(numberQuantity.orZero().coerceAtLeast(0)) { "XX" }.joinToString(" - ")
            } else numberRows.flatten().joinToString("  ") { it.toString().padStart(2, '0') }
                .ifBlank { "${betQuantity ?: 1} aposta(s)" }
        },
        amount = if (kind == CartItemKind.POOL_SHARE) reservation?.total.orZero() else amount.orZero(),
        modalityCode = modalityCode,
        contestNumber = targetContest ?: initialContest,
        numberQuantity = numberQuantity ?: numberRows.flatten().size,
        recurringDraws = recurringDrawCount,
        selectedNumbers = numberRows,
        selectedClovers = clovers,
        badges = badges,
        complementaryLabel = complementaryLabel(modalityCode, surprise, clovers),
        poolShare = pool,
        gameRows = lotecaMatches.matchRows() + lotogolMatches.matchRows(),
    )
}

private fun String.displayName(): String = trim().lowercase()
    .split('_', '-', ' ')
    .filter(String::isNotBlank)
    .joinToString(" ") { word -> word.replaceFirstChar(Char::uppercase) }

private fun JsonElement?.numberRows(): List<List<Int>> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray && asJsonArray.any { it.isJsonArray } -> asJsonArray.map { it.flattenNumbers() }
    else -> listOf(flattenNumbers()).filter(List<Int>::isNotEmpty)
}

private fun JsonElement?.flattenNumbers(): List<Int> = when {
    this == null || isJsonNull -> emptyList()
    isJsonArray -> asJsonArray.flatMap { it.flattenNumbers() }
    isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(asInt)
    else -> emptyList()
}

private fun JsonElement?.modalityCode(): String {
    val raw = when {
        this == null || isJsonNull -> ""
        isJsonPrimitive -> asString
        isJsonObject -> asJsonObject.firstText("descricaoGestor", "nome", "descricao", "valor")
        else -> ""
    }
    return raw.trim().uppercase().replace('-', '_').replace(' ', '_')
}

private fun JsonElement?.modalityName(contestType: JsonElement?): String {
    if (this == null || isJsonNull) return ""
    if (isJsonObject) {
        val value = asJsonObject
        val special = contestType.textValue().contains("ESPECIAL", ignoreCase = true)
        return value.firstText(*(if (special) arrayOf("descricaoEspecial", "descricao", "nome", "descricaoGestor") else arrayOf("descricao", "nome", "descricaoGestor")))
            .ifBlank { modalityCode().displayName() }
    }
    return modalityCode().displayName()
}

private fun JsonElement?.textValue(): String = when {
    this == null || isJsonNull -> ""
    isJsonPrimitive -> asString
    isJsonObject -> asJsonObject.firstText("descricao", "nome", "valor", "abreviacao")
    else -> ""
}

private fun com.google.gson.JsonObject.firstText(vararg names: String): String = names.firstNotNullOfOrNull { name ->
    get(name)?.takeUnless { it.isJsonNull }?.let { element ->
        when {
            element.isJsonPrimitive -> element.asString
            element.isJsonObject -> element.asJsonObject.firstText("nome", "descricao", "valor", "uf")
            else -> null
        }
    }?.takeIf(String::isNotBlank)
}.orEmpty()

private fun CartBetDto.isSurprise(): Boolean {
    if (surprise == true) return true
    val value = surpriseIndicator.textValue()
    return when {
        value.isBlank() -> false
        value.equals("0") -> false
        value.contains("NAO", ignoreCase = true) || value.contains("NÃO", ignoreCase = true) -> false
        else -> true
    }
}

private fun CartBetDto.isMirror(): Boolean = mirror == true || generateMirror == true

private fun CartBetDto.complementaryLabel(
    modalityCode: String,
    surprise: Boolean,
    clovers: List<Int>,
): String? = when {
    modalityCode.contains("DIA_DE_SORTE") -> "Mês da sorte: ${if (surprise) "???" else luckyMonth.textValue().ifBlank { "???" }}"
    modalityCode.contains("TIMEMANIA") -> "Time do coração: ${if (surprise) "???" else favoriteTeam.teamName().ifBlank { "-" }}"
    modalityCode.contains("MILIONARIA") -> "Trevos: ${if (surprise && clovers.isEmpty()) List(cloverQuantity.orZero()) { "XX" }.joinToString(" - ") else clovers.joinToString(" - ")}"
    else -> null
}

private fun JsonElement?.teamName(): String {
    if (this == null || !isJsonObject) return textValue()
    val name = asJsonObject.firstText("nome", "descricaoCurta", "descricaoLonga")
    val state = asJsonObject.firstText("uf")
    return listOf(name, state).filter(String::isNotBlank).joinToString("/")
}

private fun JsonElement?.matchRows(): List<String> {
    if (this == null || !isJsonArray) return emptyList()
    return asJsonArray.mapIndexedNotNull { index, element ->
        if (!element.isJsonObject) return@mapIndexedNotNull null
        val match = element.asJsonObject
        val home = match.get("equipe1").teamName()
        val away = match.get("equipe2").teamName()
        val result = listOf(
            match.firstText("indexPlacarEquipe1"),
            match.firstText("indexPlacarEquipe2"),
        ).filter(String::isNotBlank).joinToString(" x ")
        val selection = match.firstText("letraLegenda", "descricaoLegenda")
        "${match.firstText("numero").ifBlank { (index + 1).toString() }}. $home x $away${listOf(result, selection).filter(String::isNotBlank).joinToString(" · ", prefix = if (result.isNotBlank() || selection.isNotBlank()) " · " else "")}".trim()
    }
}

private fun Int.betCountLabel(): String = if (this == 1) "1 aposta" else "$this apostas"

private fun Int?.orZero() = this ?: 0

private fun BigDecimal?.orZero() = this ?: BigDecimal.ZERO
