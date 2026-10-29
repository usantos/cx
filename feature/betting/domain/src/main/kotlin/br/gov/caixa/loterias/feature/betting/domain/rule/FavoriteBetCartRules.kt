package br.gov.caixa.loterias.feature.betting.domain.rule

import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import java.text.Normalizer

/** As verificações de favoritos do legado usam os prognósticos de uma aposta antes da seleção de concurso e recorrência. */
fun favoriteBetPrice(favorite: FavoriteBet, games: List<LotteryGameParameters>): BigDecimal {
    val game = games.firstOrNull { it.modality.id == favorite.modalityId }
    if (favorite.modalityId == LOTECA_MODALITY_ID || favorite.modalityId == LOTOGOL_MODALITY_ID) {
        game?.minimumBetAmount?.let { return it }
    }
    return game?.let { BetPricing.numericPrice(it, favorite.numbers.size, favorite.clovers.size)?.amount } ?: BigDecimal.ZERO
}

fun favoriteBetAlreadyInCart(favorite: FavoriteBet, cart: ShoppingCart?, games: List<LotteryGameParameters>): Boolean {
    if (favorite.modalityId == LOTECA_MODALITY_ID || favorite.modalityId == LOTOGOL_MODALITY_ID) return false
    val modality = games.firstOrNull { it.modality.id == favorite.modalityId }?.modality ?: return false
    val modalityNames = listOf(modality.id.toString(), modality.name, modality.code, modality.managerName)
        .filter(String::isNotBlank).map(::favoriteModalityKey).toSet()
    return cart?.items.orEmpty().any { item ->
        item.kind == CartItemKind.INDIVIDUAL_BET && favoriteModalityKey(item.modalityCode) in modalityNames &&
            (
                if (favorite.isSuperSeven) {
                    favorite.numberColumns.map { it.sorted() } == item.selectedNumbers.map { it.sorted() }
                } else {
                    favorite.numbers.sorted() == item.selectedNumbers.flatten().sorted()
                }
                ) &&
            (favorite.clovers.isEmpty() || favorite.clovers.sorted() == item.selectedClovers.sorted())
    }
}

private fun favoriteModalityKey(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
    .filter(Char::isLetterOrDigit).lowercase()

private const val LOTECA_MODALITY_ID = 19
private const val LOTOGOL_MODALITY_ID = 14
