package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.availableNumberQuantities
import br.gov.caixa.loterias.feature.betting.presentation.mapper.calculateBetTotal
import br.gov.caixa.loterias.feature.betting.presentation.mapper.resolveBetPrice
import java.math.BigDecimal

enum class BetEntryMode { MANUAL, SURPRISE }

enum class BetSelectionStep { NUMBERS, COMPLEMENT }

enum class LotecaOutcome { HOME, DRAW, AWAY }

data class BettingGameUiState(
    val loading: Boolean = true,
    val recoveryReview: br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint? = null,
    val specialContests: HomeSpecialContests = HomeSpecialContests(),
    val game: LotteryGameParameters? = null,
    val mode: BetEntryMode = BetEntryMode.MANUAL,
    val selectionStep: BetSelectionStep = BetSelectionStep.NUMBERS,
    val selectedNumbers: Set<Int> = emptySet(),
    val selectedClovers: Set<Int> = emptySet(),
    val selectedTeam: SportsTeam? = null,
    val teamQuery: String = "",
    val lotecaOutcomes: Map<Int, Set<LotecaOutcome>> = emptyMap(),
    val lotogolScores: Map<Int, Pair<Int?, Int?>> = emptyMap(),
    val superSevenColumns: List<Set<Int>> = List(SUPER_SEVEN_COLUMNS) { emptySet() },
    val selectedLuckyMonth: LuckyMonth? = null,
    val generateMirror: Boolean = false,
    val numberQuantity: Int = 0,
    val recurringDraws: Int = 0,
    val surpriseQuantity: Int = 1,
    val surpriseCloverQuantity: Int = 0,
    val manualCloverQuantity: Int = 0,
    val warningMessage: String? = null,
    val errorMessage: String? = null,
    val addedToCart: Boolean = false,
    val confirmedCart: br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart? = null,
    val dailyLimitConfirmation: Boolean = false,
    val dailyLimit: BigDecimal? = null,
    val duplicateConfirmation: Boolean = false,
    val minimumCartAmount: BigDecimal = BigDecimal.ZERO,
    val favoriteExpanded: Boolean = false,
    val favoriteName: String = "",
    val favoriteSaved: Boolean = false,
    val favoriteOverwriteConfirmation: Boolean = false,
    val favoriteOverwriteMessage: String? = null,
) {
    val hasPendingEdits: Boolean get() = !addedToCart && when (mode) {
        BetEntryMode.MANUAL -> selectedNumbers.isNotEmpty() || superSevenColumns.any { it.isNotEmpty() } ||
            lotecaOutcomes.values.any { it.isNotEmpty() } || lotogolScores.values.any { it.first != null || it.second != null }

        BetEntryMode.SURPRISE -> surpriseQuantity > 1 || numberQuantity > (game?.minimumSelection ?: numberQuantity) ||
            surpriseCloverQuantity > (game?.minimumClovers ?: surpriseCloverQuantity) || generateMirror
    }
    val canFavorite: Boolean get() = mode == BetEntryMode.MANUAL && canAddToCart &&
        game?.modality?.id !in listOf(LOTECA_ID, LOTOGOL_ID) &&
        (!requiresManualComplement || selectionStep == BetSelectionStep.COMPLEMENT)
    val numberQuantityOptions: List<Int> get() = game?.let(::availableNumberQuantities).orEmpty()
    val cloverQuantityOptions: List<Int> get() = game?.prices.orEmpty()
        .filter { it.numberQuantity == numberQuantity && it.cloverQuantity > 0 }
        .map { it.cloverQuantity }.distinct().sorted()
    val recurringOptions: List<Int> get() = listOf(0) + game?.recurringOptions.orEmpty().filter { it > 0 }
    val unitPrice: BigDecimal get() = game?.let { current ->
        if (current.modality.id == LOTECA_ID) {
            val doubles = lotecaOutcomes.values.count { it.size == 2 }
            val triples = lotecaOutcomes.values.count { it.size == 3 }
            br.gov.caixa.loterias.feature.betting.domain.rule.BetPricing.lotecaPrice(current, doubles, triples)?.amount
                ?: current.prices.firstOrNull()?.amount
                ?: BigDecimal.ZERO
        } else {
            resolveBetPrice(
                current,
                numberQuantity,
                if (mode ==
                    BetEntryMode.SURPRISE
                ) {
                    surpriseCloverQuantity
                } else if (current.modality.id ==
                    MAIS_MILIONARIA_ID
                ) {
                    manualCloverQuantity
                } else {
                    selectedClovers.size
                }
            )
        }
    } ?: BigDecimal.ZERO
    val totalPrice: BigDecimal get() = calculateBetTotal(
        unitPrice = unitPrice,
        recurringDraws = recurringDraws,
        quantity = if (mode == BetEntryMode.SURPRISE) surpriseQuantity else 1,
        mirror = generateMirror
    )
    val requiresManualComplement: Boolean get() = mode == BetEntryMode.MANUAL && game?.let {
        it.modality.id == TIMEMANIA_ID || it.modality.id == DIA_DE_SORTE_ID ||
            it.modality.id == MAIS_MILIONARIA_ID
    } == true
    val numbersAreComplete: Boolean get() = game?.let { current ->
        when (current.modality.id) {
            SUPER_SEVEN_ID -> superSevenColumns.all(Set<Int>::isNotEmpty) &&
                superSevenColumns.sumOf(Set<Int>::size) == numberQuantity

            else -> numberQuantity > 0 && selectedNumbers.size == numberQuantity
        }
    } == true
    val primaryActionLabel: String get() = if (selectionStep == BetSelectionStep.NUMBERS && requiresManualComplement) {
        when (game?.modality?.id) {
            TIMEMANIA_ID -> messageText("escolher_time_do_coracao_c6bef9")
            DIA_DE_SORTE_ID -> messageText("escolher_mes_de_sorte_38ec85")
            MAIS_MILIONARIA_ID -> "Escolher Trevos"
            else -> messageText("continuar_63e2bb")
        }
    } else {
        messageText("adicionar_ao_carrinho_bf86b8")
    }
    val canPerformPrimaryAction: Boolean get() = !loading && !addedToCart &&
        (if (selectionStep == BetSelectionStep.NUMBERS && requiresManualComplement) numbersAreComplete else canAddToCart)
    val canAddToCart: Boolean get() = !loading && !addedToCart && game?.let { current ->
        br.gov.caixa.loterias.feature.betting.domain.rule.BetSelectionEligibility.canRegister(
            current,
            br.gov.caixa.loterias.feature.betting.domain.rule.BetSelectionStatus(
                surprise = mode == BetEntryMode.SURPRISE,
                numberQuantity = numberQuantity, numberCount = selectedNumbers.size,
                cloverCount = selectedClovers.size, manualCloverQuantity = manualCloverQuantity,
                surpriseCloverQuantity = surpriseCloverQuantity, surpriseQuantity = surpriseQuantity,
                teamSelected = selectedTeam != null, monthSelected = selectedLuckyMonth != null,
                superSevenColumns = superSevenColumns, lotecaOutcomeCounts = lotecaOutcomes.mapValues { it.value.size },
                lotogolScores = lotogolScores
            )
        )
    } == true
}

internal const val LOTOGOL_ID = 14
internal const val LOTECA_ID = 19
internal const val TIMEMANIA_ID = 20
internal const val SUPER_SEVEN_ID = 7
internal const val DIA_DE_SORTE_ID = 11
internal const val MAIS_MILIONARIA_ID = 9
private const val SUPER_SEVEN_COLUMNS = 7
