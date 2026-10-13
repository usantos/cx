package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal

data class LotteryModality(
    val id: Int,
    val name: String,
    val code: String,
    val specialName: String = "",
    val managerName: String = "",
)

data class LotteryGameParameters(
    val modality: LotteryModality,
    val contestNumber: Int,
    val drawDate: String,
    val maximumNumber: Int,
    val minimumSelection: Int,
    val maximumSelection: Int,
    val maximumSurprises: Int,
    val recurringOptions: List<Int>,
    val prices: List<BetPrice>,
    val minimumClovers: Int = 0,
    val maximumClovers: Int = 0,
    val maximumCloverNumber: Int = 0,
    val open: Boolean = true,
    val contestType: String = "NORMAL",
    val estimatedPrize: BigDecimal = BigDecimal.ZERO,
    val closingDateTime: String = "",
    val teams: List<SportsTeam> = emptyList(),
    val matches: List<SportsMatch> = emptyList(),
    val luckyMonths: List<LuckyMonth> = emptyList(),
    val acceptsMirror: Boolean = false,
    val hasNextContest: Boolean = false,
)

data class LuckyMonth(
    val number: Int,
    val name: String,
    val abbreviation: String = "",
)

data class SportsTeam(
    val number: Int,
    val name: String,
    val state: String = "",
    val shortDescription: String = "",
    val longDescription: String = "",
    val countryNumber: Int = 0,
    val country: String = "",
    val countryCode: String = "",
    val className: String = "",
    val selectionIndicator: Boolean = false,
    val shieldBase64: String? = null,
)

data class SportsMatch(
    val number: Int,
    val home: SportsTeam,
    val away: SportsTeam,
    val legendDescription: String = "",
    val legendLetter: String = "",
)

data class LotecaMatchSelection(
    val match: SportsMatch,
    val homeWin: Boolean,
    val draw: Boolean,
    val awayWin: Boolean,
)

data class LotogolMatchSelection(
    val match: SportsMatch,
    val homeScore: Int,
    val awayScore: Int,
)

data class BetPrice(
    val numberQuantity: Int,
    val cloverQuantity: Int,
    val amount: BigDecimal,
    val doubleQuantity: Int = 0,
    val tripleQuantity: Int = 0,
)

data class BettingParameters(
    val dailyLimit: BigDecimal,
    val minimumCartAmount: BigDecimal,
    val maximumCartBets: Int,
    val games: List<LotteryGameParameters>,
    val availablePoolModalities: List<PoolModalityAvailability> = emptyList(),
)

data class PoolModalityAvailability(
    val modalityId: Int,
    val modalityCode: String,
    val contestType: String,
    val contestNumber: Int? = null,
    val resaleClosingDate: String = "",
    val resaleClosingTime: String = "",
)

data class BetComboEntry(
    val modality: LotteryModality,
    val contestType: String,
    val betQuantity: Int,
    val recurringDraws: Int,
    val numberQuantity: Int,
    val cloverQuantity: Int,
    val contestNumber: Int,
)

data class BetCombo(
    val id: Int,
    val name: String,
    val description: String,
    val new: Boolean,
    val amount: BigDecimal,
    val entries: List<BetComboEntry>,
)

data class BetDraft(
    val modalityId: Int,
    val contestType: String = "NORMAL",
    val numbers: List<Int>,
    val clovers: List<Int> = emptyList(),
    val recurringDraws: Int = 0,
    val surprise: Boolean = false,
    val quantity: Int = 1,
    val numberQuantity: Int = numbers.size,
    val favoriteTeam: SportsTeam? = null,
    val lotecaSelections: List<LotecaMatchSelection> = emptyList(),
    val lotogolSelections: List<LotogolMatchSelection> = emptyList(),
    val superSevenColumns: List<List<Int>> = emptyList(),
    val luckyMonth: LuckyMonth? = null,
    val generateMirror: Boolean = false,
    val cloverQuantity: Int = clovers.size,
    val contestNumber: Int? = null,
    val allowLocalDuplicate: Boolean = false,
)

interface BettingRepository {
    suspend fun modalities(): List<LotteryModality>
    suspend fun parameters(): BettingParameters
    suspend fun combos(): List<BetCombo>
    suspend fun addBet(draft: BetDraft): ShoppingCart
    suspend fun addCombo(comboId: Int, quantity: Int, surprise: Boolean): ShoppingCart
}

fun interface GetLotteryModalitiesUseCase {
    suspend operator fun invoke(): AppResult<List<LotteryModality>>
}

class GetLotteryModalities(private val repository: BettingRepository) : GetLotteryModalitiesUseCase {
    override suspend fun invoke() = appResultOf { repository.modalities() }
}

fun interface GetBettingParametersUseCase {
    suspend operator fun invoke(): AppResult<BettingParameters>
}

class GetBettingParameters(private val repository: BettingRepository) : GetBettingParametersUseCase {
    override suspend fun invoke() = appResultOf { repository.parameters() }
}

fun interface GetBetCombosUseCase {
    suspend operator fun invoke(): AppResult<List<BetCombo>>
}

class GetBetCombos(private val repository: BettingRepository) : GetBetCombosUseCase {
    override suspend fun invoke() = appResultOf { repository.combos() }
}

fun interface AddBetToCartUseCase {
    suspend operator fun invoke(draft: BetDraft): AppResult<ShoppingCart>
}

class AddBetToCart(private val repository: BettingRepository) : AddBetToCartUseCase {
    override suspend fun invoke(draft: BetDraft) = appResultOf {
        require(draft.modalityId > 0) { "Modalidade inválida" }
        require(
            draft.numbers.isNotEmpty() || draft.surprise ||
                draft.lotecaSelections.isNotEmpty() || draft.lotogolSelections.isNotEmpty() ||
                draft.superSevenColumns.isNotEmpty(),
        ) { "Selecione os números ou palpites da aposta" }
        require(draft.quantity > 0) { "Quantidade de apostas inválida" }
        require(
            draft.numberQuantity > 0 || draft.lotecaSelections.isNotEmpty() || draft.lotogolSelections.isNotEmpty(),
        ) { "Quantidade de números ou palpites inválida" }
        repository.addBet(draft)
    }
}

fun interface AddComboToCartUseCase {
    suspend operator fun invoke(comboId: Int, quantity: Int, surprise: Boolean): AppResult<ShoppingCart>
}

class AddComboToCart(private val repository: BettingRepository) : AddComboToCartUseCase {
    override suspend fun invoke(comboId: Int, quantity: Int, surprise: Boolean) = appResultOf {
        require(comboId > 0) { "Combo inválido" }
        require(quantity > 0) { "Quantidade inválida" }
        repository.addCombo(comboId, quantity, surprise)
    }
}
