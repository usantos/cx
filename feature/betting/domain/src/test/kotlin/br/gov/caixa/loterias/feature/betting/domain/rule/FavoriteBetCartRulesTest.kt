package br.gov.caixa.loterias.feature.betting.domain.rule

import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteBetCartRulesTest {
    @Test fun `Loteca favorite never treats empty numeric selections as a duplicate`() {
        assertFalse(duplicate(LotteryModality(LOTECA_MODALITY_ID, "Loteca", "LOTECA")))
    }

    @Test fun `Lotogol favorite never treats empty numeric selections as a duplicate`() {
        assertFalse(duplicate(LotteryModality(LOTOGOL_MODALITY_ID, "Lotogol", "LOTOGOL")))
    }

    @Test fun `numeric modalities still compare their recorded selections`() {
        assertTrue(
            duplicate(
                LotteryModality(MEGA_SENA_MODALITY_ID, "Mega-Sena", "MEGA_SENA"),
                listOf(1, 2, THIRD_NUMBER, FOURTH_NUMBER, FIFTH_NUMBER, SIX_NUMBERS)
            )
        )
    }

    @Test fun `sports favorites use authoritative minimum regardless of numeric price rows`() {
        listOf(
            LotteryModality(LOTECA_MODALITY_ID, "Loteca", "LOTECA"),
            LotteryModality(LOTOGOL_MODALITY_ID, "Lotogol", "LOTOGOL")
        ).forEach { modality ->
            val favorite = FavoriteBet(FAVORITE_ID, modality.id, modality.name, "Minha aposta", emptyList())
            val game = LotteryGameParameters(
                modality, CONTEST_NUMBER, "", 0, 0, 0, 0, listOf(0),
                listOf(BetPrice(SPORTS_PRICE_PROGNOSES, 0, BigDecimal("24.00"))), minimumBetAmount = BigDecimal("3.00")
            )
            assertEquals(BigDecimal("3.00"), favoriteBetPrice(favorite, listOf(game)))
        }
    }

    @Test fun `numeric price prefers exact clovers and preserves numbers-only fallback`() {
        val modality = LotteryModality(MILIONARIA_MODALITY_ID, "+Milionária", "MAIS_MILIONARIA")
        val favorite = FavoriteBet(FAVORITE_ID, modality.id, modality.name, "Jogo", listOf(listOf(1, 2)), clovers = listOf(1, 2))
        val game = LotteryGameParameters(
            modality, CONTEST_NUMBER, "", MILIONARIA_MAXIMUM_NUMBER, 2, MILIONARIA_MAXIMUM_SELECTION, MAXIMUM_SURPRISES, listOf(0),
            listOf(
                BetPrice(2, 0, BigDecimal("6.00")),
                BetPrice(2, 2, BigDecimal("9.00"))
            )
        )
        assertEquals(BigDecimal("9.00"), favoriteBetPrice(favorite, listOf(game)))
        assertEquals(BigDecimal("6.00"), favoriteBetPrice(favorite.copy(clovers = listOf(1)), listOf(game)))
        assertEquals(BigDecimal.ZERO, favoriteBetPrice(favorite.copy(numberColumns = listOf(listOf(1, 2, THIRD_NUMBER))), listOf(game)))
        assertEquals(BigDecimal.ZERO, favoriteBetPrice(favorite, emptyList()))
    }

    @Test fun `super seven duplicates compare each column without merging selections`() {
        val modality = LotteryModality(SUPER_SEVEN_MODALITY_ID, "Super Sete", "SUPER_SETE")
        val favorite = FavoriteBet(FAVORITE_ID, modality.id, modality.name, "Colunas", listOf(listOf(1, 2), listOf(THIRD_NUMBER)))
        val game =
            LotteryGameParameters(
                modality,
                CONTEST_NUMBER,
                "",
                SUPER_SEVEN_MAXIMUM_DIGIT,
                SUPER_SEVEN_MINIMUM_SELECTION,
                SUPER_SEVEN_MAXIMUM_SELECTION,
                MAXIMUM_SURPRISES,
                listOf(
                    0
                ),
                emptyList()
            )
        val item = CartItem(
            CART_ITEM_ID,
            CartItemKind.INDIVIDUAL_BET,
            "",
            "",
            BigDecimal.ONE,
            modalityCode = "SUPER_SETE",
            selectedNumbers = listOf(listOf(2, 1), listOf(THIRD_NUMBER))
        )
        val cart = ShoppingCart(1, BigDecimal.ONE, listOf(item))
        assertTrue(favoriteBetAlreadyInCart(favorite, cart, listOf(game)))
        assertFalse(
            favoriteBetAlreadyInCart(
                favorite,
                cart.copy(
                    items = listOf(
                        item.copy(selectedNumbers = listOf(listOf(1), listOf(2, THIRD_NUMBER)))
                    )
                ),
                listOf(game)
            )
        )
    }

    @Test fun `duplicates normalize modality and preserve clover distinctions and item kind`() {
        val modality = LotteryModality(MILIONARIA_MODALITY_ID, "+Milionária", "MAIS_MILIONARIA")
        val favorite = FavoriteBet(FAVORITE_ID, MILIONARIA_MODALITY_ID, modality.name, "Jogo", listOf(listOf(1, 2)), clovers = listOf(1, 2))
        val game =
            LotteryGameParameters(
                modality, CONTEST_NUMBER, "", MILIONARIA_MAXIMUM_NUMBER, 2, MILIONARIA_MAXIMUM_SELECTION, MAXIMUM_SURPRISES,
                listOf(
                    0
                ),
                emptyList()
            )
        val item = CartItem(
            CART_ITEM_ID,
            CartItemKind.INDIVIDUAL_BET,
            "",
            "",
            BigDecimal.ONE,
            modalityCode = "mais-milionária",
            selectedNumbers = listOf(listOf(2, 1)),
            selectedClovers = listOf(2, 1)
        )
        val cart = ShoppingCart(1, BigDecimal.ONE, listOf(item))
        assertTrue(favoriteBetAlreadyInCart(favorite, cart, listOf(game)))
        assertFalse(
            favoriteBetAlreadyInCart(
                favorite,
                cart.copy(items = listOf(item.copy(selectedClovers = listOf(1, THIRD_NUMBER)))),
                listOf(game)
            )
        )
        assertFalse(favoriteBetAlreadyInCart(favorite, cart.copy(items = listOf(item.copy(modalityCode = "QUINA"))), listOf(game)))
        assertFalse(favoriteBetAlreadyInCart(favorite, cart.copy(items = listOf(item.copy(kind = CartItemKind.COMBO))), listOf(game)))
        assertFalse(favoriteBetAlreadyInCart(favorite, null, listOf(game)))
        assertFalse(favoriteBetAlreadyInCart(favorite, cart, emptyList()))
    }

    private fun duplicate(modality: LotteryModality, numbers: List<Int> = emptyList()): Boolean {
        val favorite = FavoriteBet(FAVORITE_ID, modality.id, modality.name, "Minha aposta", listOf(numbers))
        val game = LotteryGameParameters(
            modality, CONTEST_NUMBER, "", MEGA_SENA_MAXIMUM_NUMBER, SIX_NUMBERS, MEGA_SENA_MAXIMUM_SELECTION, MAXIMUM_SURPRISES, listOf(0),
            listOf(BetPrice(SIX_NUMBERS, 0, BigDecimal("6.00")))
        )
        val cart = ShoppingCart(
            1,
            BigDecimal.TEN,
            listOf(
                CartItem(
                    CART_ITEM_ID,
                    CartItemKind.INDIVIDUAL_BET,
                    modality.name,
                    "",
                    BigDecimal.ONE,
                    modalityCode = modality.code,
                    selectedNumbers = listOf(numbers.reversed())
                )
            )
        )
        return favoriteBetAlreadyInCart(favorite, cart, listOf(game))
    }
}

private const val LOTECA_MODALITY_ID = 19
private const val LOTOGOL_MODALITY_ID = 14
private const val MEGA_SENA_MODALITY_ID = 2
private const val MILIONARIA_MODALITY_ID = 9
private const val SUPER_SEVEN_MODALITY_ID = 12
private const val FAVORITE_ID = 9L
private const val CART_ITEM_ID = 4L
private const val CONTEST_NUMBER = 100
private const val SPORTS_PRICE_PROGNOSES = 14
private const val MAXIMUM_SURPRISES = 10
private const val MILIONARIA_MAXIMUM_NUMBER = 50
private const val MILIONARIA_MAXIMUM_SELECTION = 12
private const val SUPER_SEVEN_MAXIMUM_DIGIT = 9
private const val SUPER_SEVEN_MINIMUM_SELECTION = 7
private const val SUPER_SEVEN_MAXIMUM_SELECTION = 21
private const val MEGA_SENA_MAXIMUM_NUMBER = 60
private const val MEGA_SENA_MAXIMUM_SELECTION = 20
private const val THIRD_NUMBER = 3
private const val FOURTH_NUMBER = 4
private const val FIFTH_NUMBER = 5
private const val SIX_NUMBERS = 6
