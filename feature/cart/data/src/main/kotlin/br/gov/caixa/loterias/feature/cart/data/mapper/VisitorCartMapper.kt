package br.gov.caixa.loterias.feature.cart.data.mapper

import br.gov.caixa.loterias.feature.cart.data.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemBadge
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartLotecaSelection
import br.gov.caixa.loterias.feature.cart.domain.model.CartModality
import br.gov.caixa.loterias.feature.cart.domain.model.CartNumbers
import br.gov.caixa.loterias.feature.cart.domain.model.CartTeam
import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartBet
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.model.VisitorCartSelection
import java.math.BigDecimal

/** Projeta o armazenamento decodificado apenas na identidade de domínio usada pela política de duplicação. */
internal fun LocalCartBet.visitorCartSelection() = VisitorCartSelection(
    modality, targetContest, contestType.value, numbers, clovers.orEmpty(), repeatCount,
    favoriteTeam?.number, luckyMonth?.number, loteca, lotogol
)

/** Usa a mesma projeção das apostas locais ao conferir o lote retornado após o login. */
fun List<LocalCartBet>.toVisitorCartSnapshot(): ShoppingCart = legacyVisitorCart(this)

internal fun legacyVisitorCart(bets: List<LocalCartBet>): ShoppingCart {
    val items = bets.map { bet ->
        val rows = when (val numbers = bet.numbers) {
            is CartNumbers.Flat -> listOf(numbers.values)
            is CartNumbers.Columns -> numbers.values
            null -> emptyList()
        }
        CartItem(
            bet.localId, CartItemKind.INDIVIDUAL_BET,
            when (bet.modality) {
                CartModality.MEGA_SENA -> messageText("mega_sena_1af8ff")
                CartModality.MAIS_MILIONARIA -> messageText("milionaria_11be79")
                CartModality.LOTOFACIL -> messageText("lotofacil_d3ed7c")
                CartModality.SUPER_7 -> messageText("super_sete_8d4823")
                CartModality.DIA_DE_SORTE -> messageText("dia_de_sorte_b5c7a5")
                CartModality.DUPLA_SENA -> messageText("dupla_sena_a2c9e5")
                else -> bet.modality.name.lowercase().replaceFirstChar { it.titlecase() }
            },
            if (bet.quickPick && rows.flatten().isEmpty()) {
                List(bet.numberCount) { "XX" }.joinToString(" - ")
            } else {
                rows.flatten().joinToString("  ") { it.toString().padStart(2, '0') }
            },
            bet.amount,
            modalityCode = bet.modality.name, contestNumber = bet.targetContest, numberQuantity = bet.numberCount,
            recurringDraws = bet.repeatCount.coerceAtLeast(1), selectedNumbers = rows, selectedClovers = bet.clovers.orEmpty(),
            badges = buildSet {
                if (bet.quickPick) add(CartItemBadge.SURPRISE)
                if (bet.repeatCount >
                    0
                ) {
                    add(CartItemBadge.RECURRING)
                }
                if (bet.mirrored) add(CartItemBadge.MIRROR)
            },
            complementaryLabel = bet.favoriteTeam?.name ?: bet.luckyMonth?.name,
            lotecaSelections = bet.loteca.mapIndexed { index, match ->
                fun label(team: CartTeam): String = listOfNotNull(
                    team.name?.takeIf(String::isNotBlank) ?: team.shortDescription ?: team.longDescription,
                    team.state?.takeIf { it.isNotBlank() && !team.isNationalTeam }
                ).joinToString("/")
                CartLotecaSelection(
                    index + 1,
                    label(match.first.team),
                    label(match.second.team),
                    match.first.winner,
                    match.draw,
                    match.second.winner
                )
            },
            gameRows =
                bet.loteca.map {
                    "${it.first.team.name} ${if (it.first.winner) "1" else ""} " +
                        "${if (it.draw) "X" else ""} ${if (it.second.winner) "2" else ""} ${it.second.team.name}"
                } +
                    bet.lotogol.map { "${it.first.team.name} ${it.first.score} × ${it.second.score} ${it.second.team.name}" }
        )
    }
    return ShoppingCart(0, items.fold(BigDecimal.ZERO) { total, item -> total + item.amount }, items, totalBetCount = items.size)
}
