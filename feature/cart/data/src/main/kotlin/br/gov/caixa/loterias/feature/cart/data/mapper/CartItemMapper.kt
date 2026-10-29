package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.cart.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.feature.cart.data.dto.CartBetDto

internal fun CartBetDto.cartToCartItem(kind: CartItemKind): CartItem {
    val reservation = poolReservation
    val numberRows = selectedNumbers.cartNumberRows()
    val modalityCode = modality.cartModalityCode()
    val modalityName = modality.cartModalityName(contestType)
    val surprise = cartIsSurprise()
    val recurringDrawCount = recurringDraws.cartOrZero().takeIf { it > 0 } ?: 1
    val clovers = selectedClovers.cartFlattenNumbers()
    val pool = reservation?.let {
        CartPoolShare(
            code = it.poolCode.orEmpty(),
            reservedShare = it.shareNumber.cartOrZero(),
            totalShares = it.totalShares.cartOrZero(),
            expiresAt = it.expiresAt.orEmpty()
        )
    }
    val badges = buildSet {
        if (exchange == true) add(CartItemBadge.EXCHANGE)
        if (kind == CartItemKind.POOL_SHARE) add(CartItemBadge.POOL)
        if (combo == true) add(CartItemBadge.COMBO)
        if (cartIsMirror()) add(CartItemBadge.MIRROR)
        if (surprise) add(CartItemBadge.SURPRISE)
        if (recurringDraws.cartOrZero() > 0) add(CartItemBadge.RECURRING)
    }
    return CartItem(
        id = id ?: 0,
        kind = kind,
        title = when (kind) {
            CartItemKind.POOL_SHARE -> messageText("1_s_bolao_2_s_05fab1", modalityName, reservation?.poolCode.orEmpty())
            else -> modalityName
        },
        description = when (kind) {
            CartItemKind.POOL_SHARE -> reservation?.shareNumber?.let { messageText("cota_1_s_740517", it) }
                ?: messageText("cota_de_bolao_5dbb22")

            else -> if (surprise && numberRows.flatten().isEmpty()) {
                List(numberQuantity.cartOrZero().coerceAtLeast(0)) { "XX" }.joinToString(" - ")
            } else {
                numberRows.flatten().joinToString("  ") { it.toString().padStart(2, '0') }
                    .ifBlank { (betQuantity ?: 1).cartBetCountLabel() }
            }
        },
        amount = if (kind == CartItemKind.POOL_SHARE) reservation?.total.cartOrZero() else amount.cartOrZero(),
        modalityCode = modalityCode,
        contestNumber = targetContest ?: initialContest,
        numberQuantity = numberQuantity ?: numberRows.flatten().size,
        recurringDraws = recurringDrawCount,
        selectedNumbers = numberRows,
        selectedClovers = clovers,
        badges = badges,
        complementaryLabel = cartComplementaryLabel(modalityCode, surprise && !cartHasNumericSurprise(), clovers),
        poolShare = pool,
        gameRows = lotecaMatches.cartMatchRows() + lotogolMatches.cartMatchRows(),
        lotecaSelections = lotecaMatches.cartLotecaSelections()
    )
}

internal fun CartBetDto.cartIsSurprise(): Boolean {
    surprise?.let { return it }
    val value = cartSurpriseValue()
    return when {
        value.isBlank() -> false
        value == "0" || value == "1" -> false
        value == "2" || value == "3" -> true
        value.contains("NAO", ignoreCase = true) || value.contains("NÃO", ignoreCase = true) -> false
        else -> true
    }
}

private fun CartBetDto.cartSurpriseValue(): String {
    val indicator = surpriseIndicator
    return if (indicator?.isJsonObject == true) {
        indicator.asJsonObject.cartFirstText("valor", "descricao")
    } else {
        indicator.cartTextValue()
    }
}

private fun CartBetDto.cartHasNumericSurprise(): Boolean = cartSurpriseValue().let {
    it == "3" || it.contains("NUMERICA", ignoreCase = true) || it.contains("NUMÉRICA", ignoreCase = true)
}

internal fun CartBetDto.cartIsMirror(): Boolean = mirror == true || generateMirror == true

internal fun CartBetDto.cartComplementaryLabel(modalityCode: String, surprise: Boolean, clovers: List<Int>): String? = when {
    modalityCode.contains("DIA_DE_SORTE") -> messageText(
        "mes_da_sorte_1_s_35a139",
        if (surprise) "???" else luckyMonth.cartTextValue().ifBlank { "???" }
    )

    modalityCode.contains("TIMEMANIA") -> messageText(
        "time_do_coracao_1_s_7e407c",
        if (surprise) "???" else favoriteTeam.cartTeamName().ifBlank { "-" }
    )

    modalityCode.contains("MILIONARIA") -> messageText(
        "trevos_1_s_946f5c",
        if (surprise && clovers.isEmpty()) {
            List(cloverQuantity.cartOrZero()) { "XX" }.joinToString(" - ")
        } else {
            clovers.joinToString(" - ")
        }
    )

    else -> null
}

internal fun Int.cartBetCountLabel(): String = if (this == 1) messageText("1_aposta_4391cf") else messageText("1_s_apostas_7b352b", this)
