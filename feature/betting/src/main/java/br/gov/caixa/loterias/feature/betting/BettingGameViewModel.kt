package br.gov.caixa.loterias.feature.betting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.FavoriteBetDraft
import br.gov.caixa.loterias.core.domain.betting.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.betting.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.core.domain.betting.AddBetToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.BetDraft
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters
import br.gov.caixa.loterias.core.domain.betting.LuckyMonth
import br.gov.caixa.loterias.core.domain.betting.LotecaMatchSelection
import br.gov.caixa.loterias.core.domain.betting.LotogolMatchSelection
import br.gov.caixa.loterias.core.domain.betting.SportsTeam
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BetEntryMode { MANUAL, SURPRISE }

enum class BetSelectionStep { NUMBERS, COMPLEMENT }

enum class LotecaOutcome { HOME, DRAW, AWAY }

data class BettingGameUiState(
    val loading: Boolean = true,
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
    val confirmedCart: br.gov.caixa.loterias.core.domain.cart.ShoppingCart? = null,
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
            current.prices.firstOrNull {
                it.doubleQuantity == doubles && it.tripleQuantity == triples
            }?.amount ?: current.prices.firstOrNull()?.amount ?: BigDecimal.ZERO
        } else {
            resolveBetPrice(
                current,
                numberQuantity,
                if (mode == BetEntryMode.SURPRISE) surpriseCloverQuantity else if (current.modality.id == MAIS_MILIONARIA_ID) manualCloverQuantity else selectedClovers.size,
            )
        }
    } ?: BigDecimal.ZERO
    val totalPrice: BigDecimal get() = calculateBetTotal(
        unitPrice = unitPrice,
        recurringDraws = recurringDraws,
        quantity = if (mode == BetEntryMode.SURPRISE) surpriseQuantity else 1,
    ).let { if (generateMirror) it.multiply(BigDecimal.valueOf(2)) else it }
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
            TIMEMANIA_ID -> "Escolher Time do Coração"
            DIA_DE_SORTE_ID -> "Escolher Mês de Sorte"
            MAIS_MILIONARIA_ID -> "Escolher Trevos"
            else -> "Continuar"
        }
    } else "Adicionar ao carrinho"
    val canPerformPrimaryAction: Boolean get() = !loading && !addedToCart &&
        (if (selectionStep == BetSelectionStep.NUMBERS && requiresManualComplement) numbersAreComplete else canAddToCart)
    val canAddToCart: Boolean get() = !loading && !addedToCart && game?.let { current ->
        current.open && when (mode) {
            BetEntryMode.MANUAL -> when (current.modality.id) {
                LOTECA_ID -> {
                    val prognoses = lotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size)
                    val doubles = lotecaOutcomes.values.count { it.size == 2 }
                    val triples = lotecaOutcomes.values.count { it.size == 3 }
                    current.matches.isNotEmpty() && current.matches.all {
                        lotecaOutcomes[it.number].orEmpty().isNotEmpty()
                    } && prognoses == numberQuantity && (doubles > 0 || triples > 0) && current.prices.any {
                        it.doubleQuantity == doubles && it.tripleQuantity == triples
                    }
                }
                LOTOGOL_ID -> current.matches.isNotEmpty() && current.matches.all {
                    lotogolScores[it.number]?.let { scores -> scores.first != null && scores.second != null } == true
                }
                SUPER_SEVEN_ID -> superSevenColumns.size == SUPER_SEVEN_COLUMNS &&
                    superSevenColumns.all(Set<Int>::isNotEmpty) &&
                    superSevenColumns.sumOf(Set<Int>::size) == numberQuantity
                DIA_DE_SORTE_ID -> numberQuantity > 0 && selectedNumbers.size == numberQuantity &&
                    selectedLuckyMonth != null
                else -> numberQuantity > 0 && selectedNumbers.size == numberQuantity &&
                    (if (current.modality.id == MAIS_MILIONARIA_ID) selectedClovers.size == manualCloverQuantity && manualCloverQuantity in current.minimumClovers..current.maximumClovers else selectedClovers.size in current.minimumClovers..current.maximumClovers.coerceAtLeast(current.minimumClovers)) &&
                    (current.modality.id != TIMEMANIA_ID || selectedTeam != null)
            }
            BetEntryMode.SURPRISE -> surpriseQuantity in 1..current.maximumSurprises.coerceAtLeast(1) &&
                (current.modality.id != DIA_DE_SORTE_ID || selectedLuckyMonth != null) &&
                (current.modality.id != MAIS_MILIONARIA_ID ||
                    surpriseCloverQuantity in current.minimumClovers..current.maximumClovers)
        }
    } == true
}

class BettingGameViewModel(
    private val modalityId: Int?,
    private val getParameters: GetBettingParametersUseCase,
    private val addBet: AddBetToCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val randomTeam: (List<SportsTeam>) -> SportsTeam? = { it.randomOrNull() },
    private val initialNumbers: List<Int> = emptyList(),
    private val initialContestTypeId: Long = 0,
    private val initialClovers: List<Int> = emptyList(),
    private val initialTeam: SportsTeam? = null,
    private val initialLuckyMonth: LuckyMonth? = null,
    private val initialLotecaSelections: List<LotecaMatchSelection> = emptyList(),
    private val initialLotogolSelections: List<LotogolMatchSelection> = emptyList(),
    private val initialSuperSevenColumns: List<List<Int>> = emptyList(),
    private val initialRecurringDraws: Int = 0,
    private val initialMirror: Boolean = false,
    private val expectedContestNumber: Int? = null,
    private val validateFavoriteBet: ValidateFavoriteBetUseCase? = null,
    private val saveFavoriteBet: SaveFavoriteBetUseCase? = null,
    private val initialParameters: BettingParameters? = null,
) : ViewModel() {
    private val mutableState = MutableStateFlow(BettingGameUiState())
    val state = mutableState.asStateFlow()
    private var warnedAboutAdditionalNumbers = false
    private var warnedAboutAdditionalClovers = false
    private var favoriteRevision = 0L
    private var pendingFavoriteDraft: FavoriteBetDraft? = null

    init {
        val prepared = initialParameters?.takeIf { selectGame(it) != null }
        if (prepared != null) applyParameters(prepared) else load()
    }

    fun load() = viewModelScope.launch(dispatchers.io) {
        updateState { it.copy(loading = true, errorMessage = null, addedToCart = false) }
        when (val result = getParameters()) {
            is AppResult.Failure -> {
                updateState { it.copy(game = null) }
                applyFailure(result.error)
            }
            is AppResult.Success -> applyParameters(result.value)
        }
    }

    private fun applyParameters(parameters: BettingParameters) {
        val game = selectGame(parameters)
        if (game == null) {
            updateState { it.copy(game = null) }
            applyFailure(AppError.Validation("Nenhuma modalidade disponível para aposta."))
        } else {
            val quantities = availableNumberQuantities(game)
            val restoredNumbers = initialNumbers.distinct().filter { it in 1..game.maximumNumber }
                .take(game.maximumSelection).toSet()
            val restoredColumns = List(SUPER_SEVEN_COLUMNS) { column ->
                initialSuperSevenColumns.getOrNull(column).orEmpty().distinct()
                    .filter { it in 0..9 }.toSet()
            }
            val restoredLotecaOutcomes = initialLotecaSelections
                .filter { selection -> game.matches.any { it.number == selection.match.number } }
                .associate { selection ->
                    selection.match.number to buildSet {
                        if (selection.homeWin) add(LotecaOutcome.HOME)
                        if (selection.draw) add(LotecaOutcome.DRAW)
                        if (selection.awayWin) add(LotecaOutcome.AWAY)
                    }
                }
            val restoredSelectionSize = if (game.modality.id == SUPER_SEVEN_ID) {
                restoredColumns.sumOf(Set<Int>::size)
            } else if (game.modality.id == LOTECA_ID) {
                restoredLotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size)
            } else {
                restoredNumbers.size
            }
            val restoredQuantity = restoredSelectionSize.takeIf { it in quantities }
            val restoredTeam = initialTeam?.let { seed ->
                game.teams.firstOrNull { it.number == seed.number } ?: seed
            }
            val restoredMonth = initialLuckyMonth?.let { seed ->
                game.luckyMonths.firstOrNull { it.number == seed.number } ?: seed
            }
            updateState {
                it.copy(
                    loading = false,
                    game = game,
                    minimumCartAmount = parameters.minimumCartAmount,
                    numberQuantity = restoredQuantity ?: quantities.firstOrNull() ?: game.minimumSelection,
                    selectedNumbers = restoredNumbers,
                    recurringDraws = initialRecurringDraws.takeIf { it in game.recurringOptions } ?: 0,
                    selectedClovers = initialClovers.distinct()
                        .filter { it in 1..game.maximumCloverNumber }.toSet(),
                    selectedTeam = restoredTeam,
                    teamQuery = restoredTeam?.let { team ->
                        listOf(team.name, team.state).filter(String::isNotBlank).joinToString("-")
                    }.orEmpty(),
                    lotecaOutcomes = restoredLotecaOutcomes,
                    lotogolScores = initialLotogolSelections.associate { selection ->
                        selection.match.number to (selection.homeScore to selection.awayScore)
                    },
                    superSevenColumns = restoredColumns,
                    selectedLuckyMonth = restoredMonth,
                    generateMirror = initialMirror && game.acceptsMirror,
                    surpriseCloverQuantity = game.minimumClovers,
                    manualCloverQuantity = initialClovers.distinct().size
                        .coerceIn(game.minimumClovers, game.maximumClovers.coerceAtLeast(game.minimumClovers)),
                    errorMessage = null,
                )
            }
        }

    }

    private fun selectGame(parameters: BettingParameters): LotteryGameParameters? {
        val requestedContestType = when (initialContestTypeId) {
            1L -> "NORMAL"
            2L -> "ESPECIAL"
            else -> null
        }
        val candidates = parameters.games.filter {
            (modalityId == null || it.modality.id == modalityId) &&
                (requestedContestType == null || it.contestType.equals(requestedContestType, true)) &&
                (expectedContestNumber == null || it.contestNumber == expectedContestNumber)
        }
        return if (modalityId == null) candidates.firstOrNull { it.open } ?: candidates.firstOrNull()
        else candidates.firstOrNull()
    }

    fun requestFavorite() {
        if (!state.value.canFavorite) return
        updateState { it.copy(favoriteExpanded = true, favoriteName = "", errorMessage = null) }
    }

    fun changeFavoriteName(value: String) = updateState {
        it.copy(favoriteName = value, errorMessage = null)
    }

    fun submitFavorite() {
        val current = state.value
        if (!current.canFavorite || current.favoriteOverwriteConfirmation) return
        val draft = current.toFavoriteDraft(current.favoriteName.trim().take(25)) ?: return
        if (draft.name.isBlank()) {
            updateState { it.copy(favoriteExpanded = false, errorMessage = "Favor inserir um nome para sua aposta favorita") }
            return
        }
        val validate = validateFavoriteBet
        if (validate == null || saveFavoriteBet == null) {
            updateState { it.copy(errorMessage = "Não foi possível salvar a aposta favorita.") }
            return
        }
        val revision = favoriteRevision
        updateState { it.copy(loading = true, favoriteExpanded = false, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            val result = validate(draft)
            if (revision != favoriteRevision) {
                updateState { it.copy(loading = false) }
                return@launch
            }
            when (result) {
                is AppResult.Success -> saveFavorite(draft, revision)
                is AppResult.Failure -> {
                    val error = result.error
                    if (error is AppError.Validation) {
                        pendingFavoriteDraft = draft
                        updateState { it.copy(loading = false, favoriteExpanded = false,
                            favoriteOverwriteConfirmation = true, favoriteOverwriteMessage = error.reason) }
                    } else updateState {
                        it.copy(loading = false, errorMessage = error.favoriteMessage())
                    }
                }
            }
        }
    }

    fun cancelFavorite() {
        favoriteRevision++
        pendingFavoriteDraft = null
        updateState { it.copy(favoriteExpanded = false, favoriteOverwriteConfirmation = false,
            favoriteOverwriteMessage = null) }
    }

    fun confirmFavoriteOverwrite() {
        if (state.value.loading || !state.value.favoriteOverwriteConfirmation) return
        val draft = pendingFavoriteDraft ?: return
        pendingFavoriteDraft = null
        val revision = favoriteRevision
        updateState { it.copy(loading = true, favoriteOverwriteConfirmation = false,
            favoriteOverwriteMessage = null, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) { saveFavorite(draft, revision) }
    }

    fun dismissFavoriteOverwrite() {
        pendingFavoriteDraft = null
        updateState { it.copy(favoriteOverwriteConfirmation = false, favoriteOverwriteMessage = null) }
    }

    private suspend fun saveFavorite(draft: FavoriteBetDraft, revision: Long) {
        val save = saveFavoriteBet ?: return
        when (val result = save(draft)) {
            is AppResult.Success -> updateState {
                it.copy(loading = false, favoriteExpanded = false,
                    favoriteSaved = revision == favoriteRevision, favoriteOverwriteConfirmation = false,
                    favoriteOverwriteMessage = null)
            }
            is AppResult.Failure -> updateState {
                it.copy(loading = false, errorMessage = result.error.favoriteMessage())
            }
        }
    }

    private fun updateState(transform: (BettingGameUiState) -> BettingGameUiState) {
        mutableState.update { current ->
            val next = transform(current)
            if (current.toFavoriteDraft("") != next.toFavoriteDraft("") ||
                current.mode != next.mode || current.numberQuantity != next.numberQuantity) {
                favoriteRevision++
                pendingFavoriteDraft = null
                next.copy(favoriteSaved = false, favoriteExpanded = false,
                    favoriteOverwriteConfirmation = false, favoriteOverwriteMessage = null)
            } else next
        }
    }

    private fun AppError.favoriteMessage(): String = when (this) {
        AppError.Network -> "Não foi possível acessar o serviço. Verifique sua conexão."
        is AppError.Validation -> reason.ifBlank { "Não foi possível salvar a aposta favorita." }
        AppError.Unexpected -> "Não foi possível salvar a aposta favorita. Tente novamente."
    }

    fun fillShake(renew: Boolean) = updateState { it.withShake(renew) }

    fun selectMode(mode: BetEntryMode) = updateState {
        it.copy(
            mode = mode,
            selectionStep = BetSelectionStep.NUMBERS,
            generateMirror = false,
            errorMessage = null,
        )
    }

    fun toggleNumber(number: Int) = updateState { current ->
        val game = current.game ?: return@updateState current
        if (number !in 1..game.maximumNumber) return@updateState current
        if (number in current.selectedNumbers) {
            val updated = current.selectedNumbers - number
            val quantity = maxOf(updated.size, game.minimumSelection)
            return@updateState current.copy(
                selectedNumbers = updated,
                numberQuantity = quantity,
                selectedClovers = if (quantity != current.numberQuantity) emptySet() else current.selectedClovers,
                manualCloverQuantity = if (quantity != current.numberQuantity) game.minimumClovers else current.manualCloverQuantity,
                generateMirror = current.generateMirror && updated.size == quantity,
                selectionStep = if (quantity != current.numberQuantity) BetSelectionStep.NUMBERS else current.selectionStep,
                errorMessage = null,
            )
        }
        if (current.selectedNumbers.size >= current.numberQuantity) {
            if (current.numberQuantity >= game.maximumSelection) {
                return@updateState current.copy(
                    warningMessage = "Não é possível selecionar quantidade de números superior ao limite para modalidade Lotérica.",
                )
            }
            val quantity = (current.numberQuantity + 1).coerceAtMost(game.maximumSelection)
            val shouldWarn = !warnedAboutAdditionalNumbers
            if (shouldWarn) warnedAboutAdditionalNumbers = true
            return@updateState current.copy(
                selectedNumbers = current.selectedNumbers + number,
                numberQuantity = quantity,
                selectedClovers = emptySet(),
                manualCloverQuantity = game.minimumClovers,
                generateMirror = false,
                selectionStep = BetSelectionStep.NUMBERS,
                warningMessage = if (shouldWarn) {
                    "Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta."
                } else current.warningMessage,
                errorMessage = null,
            )
        }
        current.copy(
            selectedNumbers = current.selectedNumbers + number,
            generateMirror = current.generateMirror && current.selectedNumbers.size + 1 == current.numberQuantity,
            errorMessage = null,
        )
    }

    fun toggleSuperSevenDigit(column: Int, digit: Int) = updateState { current ->
        val game = current.game ?: return@updateState current
        if (game.modality.id != SUPER_SEVEN_ID || column !in 0 until SUPER_SEVEN_COLUMNS || digit !in 0..9) {
            return@updateState current
        }
        val columns = current.superSevenColumns.toMutableList()
        val selected = columns[column]
        val total = columns.sumOf(Set<Int>::size)
        if (digit in selected) {
            val maximumPerColumn = superSevenMaximumPerColumn(current.numberQuantity)
            if (selected.size != maximumPerColumn) {
                return@updateState current.copy(
                    warningMessage = "Você não pode fazer isso, pois nessa coluna é necessário ter selecionado ${selected.size} número(s)",
                )
            }
            columns[column] = selected - digit
            return@updateState current.copy(
                superSevenColumns = columns,
                errorMessage = null,
            )
        }
        if (total >= game.maximumSelection) {
            return@updateState current.copy(warningMessage = "Não é possível selecionar uma quantidade de números superior ao limite da modalidade.")
        }
        val prospectiveQuantity = if (total >= current.numberQuantity) {
            current.numberQuantityOptions.firstOrNull { it > current.numberQuantity } ?: current.numberQuantity
        } else current.numberQuantity
        val maximumPerColumn = superSevenMaximumPerColumn(prospectiveQuantity)
        if (selected.size >= maximumPerColumn) {
            return@updateState current.copy(
                warningMessage = "Para esta aposta, selecione no máximo $maximumPerColumn número(s) por coluna.",
            )
        }
        columns[column] = selected + digit
        val newTotal = total + 1
        current.copy(
            superSevenColumns = columns,
            numberQuantity = if (newTotal > current.numberQuantity) prospectiveQuantity else current.numberQuantity,
            errorMessage = null,
        )
    }

    fun selectLuckyMonth(month: LuckyMonth) = updateState { current ->
        if (month !in current.game?.luckyMonths.orEmpty()) current else current.copy(
            selectedLuckyMonth = month,
            errorMessage = null,
        )
    }

    fun toggleMirror() = updateState { current ->
        val game = current.game ?: return@updateState current
        val available = game.acceptsMirror && when (current.mode) {
            BetEntryMode.MANUAL -> current.selectedNumbers.size == current.numberQuantity
            BetEntryMode.SURPRISE -> true
        }
        if (!available) current else current.copy(generateMirror = !current.generateMirror, errorMessage = null)
    }

    fun toggleClover(clover: Int) = updateState { current ->
        val game = current.game ?: return@updateState current
        current.copy(
            selectedClovers = toggleSelectedNumber(
                current.selectedClovers,
                clover,
                1..game.maximumCloverNumber,
                if (game.modality.id == MAIS_MILIONARIA_ID) current.manualCloverQuantity else game.maximumClovers,
            ),
            errorMessage = null,
        )
    }

    fun chooseCloverQuantity(quantity: Int) = updateState { current ->
        if (quantity !in current.cloverQuantityOptions || quantity == current.manualCloverQuantity) return@updateState current
        val shouldWarn = quantity > (current.game?.minimumClovers ?: quantity) && !warnedAboutAdditionalClovers
        if (shouldWarn) warnedAboutAdditionalClovers = true
        current.copy(
            manualCloverQuantity = quantity,
            selectedClovers = emptySet(),
            favoriteSaved = false,
            warningMessage = if (shouldWarn) "Você aumentou a quantidade de trevos, o valor será alterado. Fique atento ao valor final da sua aposta." else current.warningMessage,
            errorMessage = null,
        )
    }

    fun updateTeamQuery(value: String) = updateState {
        it.copy(teamQuery = value.take(60), errorMessage = null)
    }

    fun selectTeam(team: SportsTeam) = updateState { current ->
        if (team !in current.game?.teams.orEmpty()) current else current.copy(
            selectedTeam = team,
            teamQuery = listOf(team.name, team.state).filter(String::isNotBlank).joinToString("-"),
            errorMessage = null,
        )
    }

    fun toggleLotecaOutcome(matchNumber: Int, outcome: LotecaOutcome) = updateState { current ->
        val game = current.game ?: return@updateState current
        if (game.modality.id != LOTECA_ID || game.matches.none { it.number == matchNumber }) return@updateState current
        val selected = current.lotecaOutcomes[matchNumber].orEmpty()
        if (outcome in selected) {
            val updated = selected - outcome
            val outcomes = if (updated.isEmpty()) current.lotecaOutcomes - matchNumber
                else current.lotecaOutcomes + (matchNumber to updated)
            val doubles = outcomes.values.count { it.size == 2 }
            val triples = outcomes.values.count { it.size == 3 }
            if ((doubles > 0 || triples > 0) && game.prices.none {
                    it.doubleQuantity == doubles && it.tripleQuantity == triples
                }) {
                return@updateState current.copy(
                    warningMessage = "Essa combinação de simples, duplos e triplos não é permitida para a Loteca. Tente outra combinação.",
                )
            }
            return@updateState current.copy(lotecaOutcomes = outcomes, errorMessage = null)
        }

        val currentPrognoses = current.lotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size)
        if (currentPrognoses >= MAX_LOTECA_PROGNOSES) {
            return@updateState current.copy(
                warningMessage = "Não é possível selecionar quantidade de números superior ao limite para modalidade Lotérica.",
            )
        }

        val updated = selected + outcome
        val outcomes = current.lotecaOutcomes + (matchNumber to updated)
        val doubles = outcomes.values.count { it.size == 2 }
        val triples = outcomes.values.count { it.size == 3 }
        if ((doubles > 0 || triples > 0) && game.prices.none {
                it.doubleQuantity == doubles && it.tripleQuantity == triples
            }) {
            return@updateState current.copy(
                warningMessage = "Essa combinação de simples, duplos e triplos não é permitida para a Loteca. Tente outra combinação.",
            )
        }

        val prognosisCount = currentPrognoses + 1
        current.copy(
            lotecaOutcomes = outcomes,
            numberQuantity = maxOf(current.numberQuantity, prognosisCount),
            errorMessage = null,
        )
    }

    fun selectLotogolScore(matchNumber: Int, home: Boolean, score: Int) = updateState { current ->
        if (score !in 0..5 || current.game?.matches?.none { it.number == matchNumber } != false) return@updateState current
        val previous = current.lotogolScores[matchNumber] ?: (null to null)
        val updated = if (home) score to previous.second else previous.first to score
        current.copy(
            lotogolScores = current.lotogolScores + (matchNumber to updated),
            errorMessage = null,
        )
    }

    fun chooseNumberQuantity(quantity: Int) = updateState { current ->
        if (quantity !in current.numberQuantityOptions) return@updateState current
        if (current.game?.modality?.id == LOTECA_ID) {
            val increased = quantity > current.numberQuantity && !warnedAboutAdditionalNumbers
            if (increased) warnedAboutAdditionalNumbers = true
            return@updateState current.copy(
                numberQuantity = quantity,
                lotecaOutcomes = if (quantity < current.numberQuantity) emptyMap() else current.lotecaOutcomes,
                warningMessage = if (increased) {
                    "Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta."
                } else current.warningMessage,
                errorMessage = null,
            )
        }
        val shouldWarn = quantity > current.numberQuantityOptions.firstOrNull().orZero() && !warnedAboutAdditionalNumbers
        if (shouldWarn) warnedAboutAdditionalNumbers = true
        val selectedQuantity = if (current.game?.modality?.id == SUPER_SEVEN_ID) {
            current.superSevenColumns.sumOf(Set<Int>::size)
        } else {
            current.selectedNumbers.size
        }
        val mustClearSelection = selectedQuantity > quantity
        current.copy(
            numberQuantity = quantity,
            selectedNumbers = if (mustClearSelection) emptySet() else current.selectedNumbers,
            superSevenColumns = if (current.game?.modality?.id == SUPER_SEVEN_ID && mustClearSelection) {
                List(SUPER_SEVEN_COLUMNS) { emptySet() }
            } else current.superSevenColumns,
            generateMirror = false,
            selectionStep = BetSelectionStep.NUMBERS,
            manualCloverQuantity = current.game?.minimumClovers ?: 0,
            selectedClovers = emptySet(),
            warningMessage = if (shouldWarn) {
                "Você aumentou a quantidade de números, o valor será alterado. Fique atento ao valor final da sua aposta."
            } else current.warningMessage,
        )
    }

    fun chooseRecurringDraws(draws: Int) = updateState { current ->
        if (draws !in current.recurringOptions) current else current.copy(recurringDraws = draws)
    }

    fun warnSpecialRecurringUnavailable() = updateState {
        it.copy(warningMessage = "Por ser uma modalidade especial, esse concurso não permite teimosinhas.")
    }

    fun increaseSurpriseQuantity() = updateState { current ->
        val maximum = current.game?.maximumSurprises?.coerceAtLeast(1) ?: 1
        current.copy(surpriseQuantity = (current.surpriseQuantity + 1).coerceAtMost(maximum))
    }

    fun decreaseSurpriseQuantity() = updateState { current ->
        current.copy(surpriseQuantity = (current.surpriseQuantity - 1).coerceAtLeast(1))
    }

    fun increaseSurpriseCloverQuantity() = updateState { current ->
        val game = current.game ?: return@updateState current
        current.copy(surpriseCloverQuantity = (current.surpriseCloverQuantity + 1).coerceAtMost(game.maximumClovers))
    }

    fun decreaseSurpriseCloverQuantity() = updateState { current ->
        val game = current.game ?: return@updateState current
        current.copy(surpriseCloverQuantity = (current.surpriseCloverQuantity - 1).coerceAtLeast(game.minimumClovers))
    }

    fun clearSelection() = updateState {
        it.copy(
            selectedNumbers = emptySet(),
            selectedClovers = emptySet(),
            selectedTeam = null,
            teamQuery = "",
            lotecaOutcomes = emptyMap(),
            lotogolScores = emptyMap(),
            superSevenColumns = List(SUPER_SEVEN_COLUMNS) { emptySet() },
            selectedLuckyMonth = null,
            generateMirror = false,
            selectionStep = BetSelectionStep.NUMBERS,
            errorMessage = null,
        )
    }

    fun clearCurrentStep() = updateState { current ->
        if (current.selectionStep != BetSelectionStep.COMPLEMENT) {
            return@updateState current.copy(
                selectedNumbers = emptySet(),
                superSevenColumns = List(SUPER_SEVEN_COLUMNS) { emptySet() },
                lotecaOutcomes = emptyMap(),
                lotogolScores = emptyMap(),
                generateMirror = false,
                errorMessage = null,
            )
        }
        when (current.game?.modality?.id) {
            TIMEMANIA_ID -> current.copy(selectedTeam = null, teamQuery = "", errorMessage = null)
            DIA_DE_SORTE_ID -> current.copy(selectedLuckyMonth = null, errorMessage = null)
            MAIS_MILIONARIA_ID -> current.copy(selectedClovers = emptySet(), errorMessage = null)
            else -> current
        }
    }

    fun primaryAction() {
        val current = state.value
        if (current.loading || current.addedToCart) return
        if (current.selectionStep == BetSelectionStep.NUMBERS && current.requiresManualComplement) {
            if (current.numbersAreComplete) {
                updateState { it.copy(selectionStep = BetSelectionStep.COMPLEMENT, errorMessage = null) }
            } else {
                updateState { it.copy(errorMessage = incompleteBetMessage(it)) }
            }
            return
        }
        addToCart()
    }

    /** Returns true when the screen consumed back to return from a complementary selection. */
    fun returnToNumbers(): Boolean {
        if (state.value.selectionStep != BetSelectionStep.COMPLEMENT) return false
        updateState { it.copy(selectionStep = BetSelectionStep.NUMBERS, errorMessage = null) }
        return true
    }

    fun acknowledgeCartAddition() = updateState { it.copy(addedToCart = false) }

    fun dismissMessage() = updateState { it.copy(warningMessage = null, errorMessage = null) }

    fun confirmLocalDuplicate() { updateState { it.copy(duplicateConfirmation = false) }; addToCart(allowLocalDuplicate = true) }
    fun cancelLocalDuplicate() = updateState { it.copy(duplicateConfirmation = false) }
    fun addToCart(allowLocalDuplicate: Boolean = false) {
        val current = state.value
        if (current.loading || current.addedToCart) return
        val game = current.game ?: return
        if (!game.open) {
            updateState { it.copy(errorMessage = "O registro de apostas individuais para o concurso ${game.contestNumber} foi encerrado.") }
            return
        }
        if (!current.canAddToCart) {
            updateState { it.copy(errorMessage = incompleteBetMessage(it)) }
            return
        }
        updateState { it.copy(loading = true, errorMessage = null) }
        viewModelScope.launch(dispatchers.io) {
            val surprise = current.mode == BetEntryMode.SURPRISE
            val draft = BetDraft(
                modalityId = game.modality.id,
                contestNumber = game.contestNumber,
                allowLocalDuplicate = allowLocalDuplicate,
                contestType = game.contestType,
                numbers = if (surprise) emptyList() else current.selectedNumbers.sorted(),
                clovers = if (surprise) emptyList() else current.selectedClovers.sorted(),
                recurringDraws = current.recurringDraws,
                surprise = surprise,
                quantity = if (surprise) current.surpriseQuantity else 1,
                numberQuantity = current.numberQuantity,
                favoriteTeam = current.selectedTeam ?: if (surprise && game.modality.id == TIMEMANIA_ID) {
                    randomTeam(game.teams)
                } else null,
                lotecaSelections = game.matches.mapNotNull { match ->
                    val outcomes = current.lotecaOutcomes[match.number].orEmpty()
                    if (outcomes.isEmpty()) null else LotecaMatchSelection(
                        match = match,
                        homeWin = LotecaOutcome.HOME in outcomes,
                        draw = LotecaOutcome.DRAW in outcomes,
                        awayWin = LotecaOutcome.AWAY in outcomes,
                    )
                },
                lotogolSelections = game.matches.mapNotNull { match ->
                    val scores = current.lotogolScores[match.number] ?: return@mapNotNull null
                    LotogolMatchSelection(
                        match = match,
                        homeScore = scores.first ?: return@mapNotNull null,
                        awayScore = scores.second ?: return@mapNotNull null,
                    )
                },
                superSevenColumns = if (surprise) emptyList() else current.superSevenColumns.map { it.sorted() },
                luckyMonth = current.selectedLuckyMonth,
                generateMirror = current.generateMirror,
                cloverQuantity = if (surprise) current.surpriseCloverQuantity else current.selectedClovers.size,
            )
            when (val result = addBet(draft)) {
                is AppResult.Success -> updateState {
                    BettingGameUiState(
                        loading = false,
                        game = game,
                        minimumCartAmount = it.minimumCartAmount,
                        numberQuantity = availableNumberQuantities(game).firstOrNull() ?: game.minimumSelection,
                        manualCloverQuantity = game.minimumClovers,
                        surpriseCloverQuantity = game.minimumClovers,
                        addedToCart = true,
                        confirmedCart = result.value,
                    )
                }
                is AppResult.Failure -> applyFailure(result.error)
            }
        }
    }

    private fun incompleteBetMessage(state: BettingGameUiState): String = when (state.mode) {
        BetEntryMode.SURPRISE -> "Informe uma quantidade válida de Surpresinhas."
        BetEntryMode.MANUAL -> {
            when (state.game?.modality?.id) {
                TIMEMANIA_ID -> if (state.selectedNumbers.size == state.numberQuantity) {
                    return "Selecione o time do coração."
                }
                LOTECA_ID -> return "Selecione todos os jogos e ao menos um duplo ou triplo válido."
                LOTOGOL_ID -> return "Informe o placar de todos os jogos."
                SUPER_SEVEN_ID -> return "Selecione a quantidade escolhida, com pelo menos um número em cada coluna."
                DIA_DE_SORTE_ID -> if (state.selectedNumbers.size == state.numberQuantity) {
                    return "Selecione o mês da sorte."
                }
            }
            val missing = (state.numberQuantity - state.selectedNumbers.size).coerceAtLeast(0)
            if (missing == 1) "Selecione mais 1 número." else "Selecione mais $missing números."
        }
    }

    private fun applyFailure(error: AppError) = updateState {
        if (error is AppError.Validation && error.reason == br.gov.caixa.loterias.core.domain.cart.VISITOR_DUPLICATE_CONFIRMATION)
            it.copy(loading = false, duplicateConfirmation = true, errorMessage = null)
        else it.copy(loading = false, errorMessage = error.userMessage())
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> "Não foi possível carregar a aposta. Verifique sua internet."
        is AppError.Validation -> reason.ifBlank { "Não foi possível atualizar a aposta." }
        AppError.Unexpected -> "Ocorreu um erro inesperado ao carregar a aposta."
    }
}

private fun Int?.orZero() = this ?: 0

internal fun superSevenMaximumPerColumn(numberQuantity: Int) = when (numberQuantity) {
    in 0..7 -> 1
    in 8..14 -> 2
    else -> 3
}

internal const val LOTOGOL_ID = 14
internal const val LOTECA_ID = 19
internal const val TIMEMANIA_ID = 20
internal const val SUPER_SEVEN_ID = 7
internal const val DIA_DE_SORTE_ID = 11
internal const val MAIS_MILIONARIA_ID = 9
private const val SUPER_SEVEN_COLUMNS = 7
private const val MAX_LOTECA_PROGNOSES = 26

private fun BettingGameUiState.toFavoriteDraft(name: String): FavoriteBetDraft? = game?.let { current ->
    FavoriteBetDraft(
        modalityId = current.modality.id,
        modalityName = current.modality.name,
        name = name,
        numberColumns = if (current.modality.id == SUPER_SEVEN_ID) superSevenColumns.map { it.sorted() }
            else listOf(selectedNumbers.sorted()),
        clovers = selectedClovers.sorted(),
        favoriteTeam = selectedTeam,
        luckyMonth = selectedLuckyMonth,
    )
}
