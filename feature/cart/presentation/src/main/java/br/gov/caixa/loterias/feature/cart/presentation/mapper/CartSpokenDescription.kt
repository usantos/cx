package br.gov.caixa.loterias.feature.cart.presentation.mapper

import br.gov.caixa.loterias.core.presentation.accessibility.PortugueseSpeech
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartLotecaSelection
import java.util.Locale

/** Fala apenas de apresentação; nunca altera números, preços, seleções ou estado de reserva. */
internal fun cartSpokenDescription(item: CartItem, expiry: PoolExpiry? = null): String {
    val words = PortugueseSpeech
    return buildList {
        add(words.expand(currentCartModalityName(item).replace("+", "mais ")))
        item.contestNumber?.let { add("Concurso ${words.number(it)}") }
        addAll(spokenSelections(item, words))
        item.complementaryLabel?.takeIf(String::isNotBlank)?.takeUnless {
            it.startsWith("Trevos:", ignoreCase = true) &&
                (item.selectedClovers.isNotEmpty() || CartItemBadge.SURPRISE in item.badges)
        }?.let { add(words.expand(it)) }
        item.badges.forEach { add(words.expand(it.label.lowercase(Locale.ROOT))) }
        if (item.recurringDraws > 1) add("Repetição por ${words.number(item.recurringDraws)} concursos")
        if (item.comboBetCount > 0) {
            val quantity = words.number(item.comboBetCount, feminine = true).replaceFirstChar(Char::titlecase)
            add("$quantity ${if (item.comboBetCount == 1) "aposta incluída" else "apostas incluídas"}")
        }
        item.poolShare?.let { share ->
            add("Código do bolão: ${words.identifier(share.code)}")
            add(
                "Cota reservada: ${words.number(
                    share.reservedShare,
                    feminine = true
                )} de ${words.number(share.totalShares, feminine = true)}"
            )
            add(spokenExpiry(expiry, words))
        }
        add("Valor: ${words.currency(item.amount)}")
    }.filter(String::isNotBlank).joinToString(". ", postfix = ".") { it.trim().trimEnd('.') }
}

private fun spokenSelections(item: CartItem, words: PortugueseSpeech): List<String> = buildList {
    val surprise = CartItemBadge.SURPRISE in item.badges
    when {
        item.lotecaSelections.isNotEmpty() -> item.lotecaSelections.forEach { add(spokenMatch(it, words)) }

        surprise -> add("Números da Surpresinha não exibidos")

        item.modalityCode.uppercase(Locale.ROOT) in SUPER_SEVEN_CODES -> item.selectedNumbers.forEachIndexed { index, numbers ->
            add("Coluna ${words.number(index + 1)}: ${numbers.joinToString(", ", transform = words::number)}")
        }

        item.selectedNumbers.any(List<Int>::isNotEmpty) -> {
            add("Números: ${item.selectedNumbers.flatten().joinToString(", ", transform = words::number)}")
        }

        item.description.isNotBlank() -> add(words.expand(item.description))
    }
    if (!surprise && item.selectedClovers.isNotEmpty()) {
        add("Trevos: ${item.selectedClovers.joinToString(", ", transform = words::number)}")
    }
    if (surprise && item.selectedClovers.isNotEmpty()) add("Trevos da Surpresinha não exibidos")
    if (!surprise && item.lotecaSelections.isEmpty()) item.gameRows.forEach { add(words.expand(it)) }
}

private fun spokenMatch(match: CartLotecaSelection, words: PortugueseSpeech): String = buildString {
    append("Jogo ${words.number(match.number)}")
    if (match.description.isNotBlank()) append(", ${words.expand(match.description)}")
    append(": ${words.expand(match.homeLabel)} ${selectionWord(match.homeSelected)}")
    append(", empate ${selectionWord(match.drawSelected)}")
    append(", ${words.expand(match.awayLabel)} ${selectionWord(match.awaySelected)}")
}

private fun selectionWord(selected: Boolean) = if (selected) "selecionado" else "não selecionado"

private fun spokenExpiry(expiry: PoolExpiry?, words: PortugueseSpeech): String = when (expiry?.state) {
    null, PoolExpiryState.UNKNOWN -> "Prazo de expiração indisponível"

    PoolExpiryState.EXPIRED -> "Cota expirada"

    else -> {
        val parts = expiry.remainingLabel.split(':').mapNotNull(String::toIntOrNull)
        if (parts.size == 2) {
            val minutes = parts[0]
            val seconds = parts[1]
            "Expira em ${words.number(minutes)} ${if (minutes == 1) "minuto" else "minutos"} e " +
                "${words.number(seconds)} ${if (seconds == 1) "segundo" else "segundos"}"
        } else {
            "Prazo de expiração indisponível"
        }
    }
}

private val SUPER_SEVEN_CODES = setOf("SUPER_7", "SUPER_SETE", "SUPERSETE", "SUPER-SETE", "SUPER7")
