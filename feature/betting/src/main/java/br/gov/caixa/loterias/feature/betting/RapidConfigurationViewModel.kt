package br.gov.caixa.loterias.feature.betting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.FavoriteBet
import br.gov.caixa.loterias.core.domain.betting.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.core.domain.betting.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.core.domain.betting.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.betting.RapidBetConfiguration
import br.gov.caixa.loterias.core.domain.betting.SaveRapidBetConfigurationUseCase
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RapidConfigurationUiState(
    val loading: Boolean = true,
    val page: Int = 0,
    val minimumAmount: BigDecimal = BigDecimal.ZERO,
    val maximumAmount: BigDecimal = BigDecimal.ZERO,
    val minimumPrize: BigDecimal = BigDecimal.ZERO,
    val requiredNumbers: Set<Int> = emptySet(),
    val excludedNumbers: Set<Int> = emptySet(),
    val availableModalities: List<LotteryModality> = emptyList(),
    val selectedModalityIds: Set<Int> = emptySet(),
    val favoriteBetIds: Set<Long> = emptySet(),
    val availableFavorites: List<FavoriteBet> = emptyList(),
    val favoritesLoading: Boolean = false,
    val favoritesError: String? = null,
    val minimumAllowedAmount: BigDecimal? = null,
    val maximumAllowedAmount: BigDecimal? = null,
    val amountLimitsLoading: Boolean = false,
    val amountLimitsError: String? = null,
    val saved: Boolean = false,
    val errorMessage: String? = null,
    val warningMessage: String? = null,
) {
    val canSave: Boolean get() = minimumAmount >= BigDecimal.ZERO &&
        maximumAmount >= minimumAmount && selectedModalityIds.isNotEmpty() &&
        requiredNumbers.intersect(excludedNumbers).isEmpty()
}

class RapidConfigurationViewModel(
    private val getConfiguration: GetRapidBetConfigurationUseCase,
    private val getModalities: GetLotteryModalitiesUseCase,
    private val saveConfiguration: SaveRapidBetConfigurationUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val getFavorites: GetFavoriteBetsUseCase? = null,
    private val getParameters: GetBettingParametersUseCase? = null,
    private val verifyAccess: VerifyAccountAccessUseCase? = null,
) : ViewModel() {
    private val mutableState = MutableStateFlow(RapidConfigurationUiState())
    val state = mutableState.asStateFlow()

    init { load(); loadFavorites(); loadAmountLimits() }

    fun load() = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null, saved = false) }
        val configurationResult = getConfiguration()
        val modalitiesResult = getModalities()
        if (configurationResult is AppResult.Failure) {
            applyFailure(configurationResult.error)
            return@launch
        }
        if (modalitiesResult is AppResult.Failure) {
            applyFailure(modalitiesResult.error)
            return@launch
        }
        val configuration = (configurationResult as AppResult.Success).value
        val available = (modalitiesResult as AppResult.Success).value.filter { it.id > 0 }
        val selected = configuration.modalities.map(LotteryModality::id).toSet()
        mutableState.update {
            it.copy(
                loading = false,
                minimumAmount = configuration.minimumAmount,
                maximumAmount = configuration.maximumAmount,
                minimumPrize = configuration.minimumPrize,
                requiredNumbers = configuration.requiredNumbers,
                excludedNumbers = configuration.excludedNumbers,
                availableModalities = available,
                selectedModalityIds = selected.ifEmpty { available.map(LotteryModality::id).toSet() },
                favoriteBetIds = configuration.favoriteBetIds,
                errorMessage = null,
            )
        }
    }

    fun loadAmountLimits() = viewModelScope.launch(dispatchers.io) {
        val parametersUseCase = getParameters ?: return@launch
        val accessUseCase = verifyAccess ?: return@launch
        if (state.value.amountLimitsLoading) return@launch
        mutableState.update { it.copy(amountLimitsLoading = true, amountLimitsError = null) }
        val parameters = parametersUseCase()
        val access = accessUseCase()
        if (parameters is AppResult.Success && access is AppResult.Success &&
            parameters.value.minimumCartAmount >= BigDecimal.ZERO && access.value.dailyLimit >= parameters.value.minimumCartAmount
        ) {
            mutableState.update { it.copy(amountLimitsLoading = false, minimumAllowedAmount = parameters.value.minimumCartAmount, maximumAllowedAmount = access.value.dailyLimit) }
        } else {
            mutableState.update { it.copy(amountLimitsLoading = false, amountLimitsError = "Não foi possível carregar os limites da aposta. Tente novamente.") }
        }
    }

    fun loadFavorites() = viewModelScope.launch(dispatchers.io) {
        val useCase = getFavorites ?: return@launch
        if (state.value.favoritesLoading) return@launch
        mutableState.update { it.copy(favoritesLoading = true, favoritesError = null) }
        when (val result = useCase(null)) {
            is AppResult.Success -> mutableState.update {
                it.copy(favoritesLoading = false, availableFavorites = result.value.filter { bet -> bet.id > 0 }.distinctBy(FavoriteBet::id))
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(favoritesLoading = false, favoritesError = when (val error = result.error) {
                    AppError.Network -> "Não foi possível carregar as apostas favoritas. Verifique sua internet."
                    is AppError.Validation -> error.reason.ifBlank { "Não foi possível carregar as apostas favoritas." }
                    AppError.Unexpected -> "Não foi possível carregar as apostas favoritas."
                })
            }
        }
    }

    fun toggleFavorite(id: Long) = mutableState.update { current ->
        if (current.availableFavorites.none { it.id == id }) return@update current
        current.copy(favoriteBetIds = if (id in current.favoriteBetIds) current.favoriteBetIds - id else current.favoriteBetIds + id)
    }

    fun setPage(page: Int) = mutableState.update { it.copy(page = page.coerceIn(0, LAST_PAGE)) }

    fun setMinimumPrize(value: BigDecimal) = mutableState.update {
        it.copy(minimumPrize = value.coerceAtLeast(BigDecimal.ZERO))
    }

    fun setMinimumAmount(value: BigDecimal) = mutableState.update {
        val floor = it.minimumAllowedAmount
        val ceiling = it.maximumAllowedAmount
        if (floor != null && ceiling != null) {
            val minimum = value.coerceIn(floor, ceiling)
            val maximum = if (minimum >= it.maximumAmount) minimum.add(BigDecimal("5")).coerceAtMost(ceiling) else it.maximumAmount.coerceAtMost(ceiling)
            it.copy(minimumAmount = minimum, maximumAmount = maximum.coerceAtLeast(minimum))
        } else {
            it.copy(minimumAmount = value.coerceAtLeast(BigDecimal.ZERO).coerceAtMost(it.maximumAmount))
        }
    }

    fun setMaximumAmount(value: BigDecimal) = mutableState.update {
        val floor = it.minimumAllowedAmount
        val ceiling = it.maximumAllowedAmount
        if (floor != null && ceiling != null) {
            val maximum = value.coerceIn(floor, ceiling)
            val minimum = if (maximum <= it.minimumAmount) maximum.subtract(BigDecimal("5")).coerceAtLeast(floor) else it.minimumAmount.coerceAtLeast(floor)
            it.copy(maximumAmount = maximum, minimumAmount = minimum.coerceAtMost(maximum))
        } else {
            it.copy(maximumAmount = value.coerceAtLeast(it.minimumAmount))
        }
    }

    fun setCustomAmount(value: BigDecimal?, minimum: Boolean): Boolean {
        val current = state.value
        val floor = current.minimumAllowedAmount ?: return false
        val ceiling = current.maximumAllowedAmount ?: return false
        if (value == null || value < floor || value > ceiling) return false
        if (minimum) setMinimumAmount(value) else if (value <= floor.add(BigDecimal("5"))) {
            val maximum = floor.add(BigDecimal("5")).coerceAtMost(ceiling)
            mutableState.update { it.copy(minimumAmount = floor, maximumAmount = maximum, warningMessage = "O valor escolhido tem que ser um pouco maior que o valor mínimo do carrinho, para isso já ajustamos o máximo para ${formatRapidAmount(maximum)} e o mínimo para ${formatRapidAmount(floor)} assim tudo vai funcionar perfeitamente.") }
        } else setMaximumAmount(value)
        return true
    }

    fun toggleModality(id: Int) = mutableState.update { current ->
        if (current.availableModalities.none { it.id == id }) return@update current
        current.copy(
            selectedModalityIds = if (id in current.selectedModalityIds) {
                current.selectedModalityIds - id
            } else current.selectedModalityIds + id,
        )
    }

    fun toggleRequiredNumber(number: Int) = toggleNumber(number, required = true)

    fun toggleExcludedNumber(number: Int) = toggleNumber(number, required = false)

    fun clearRequiredNumbers() = mutableState.update { it.copy(requiredNumbers = emptySet()) }

    fun clearExcludedNumbers() = mutableState.update { it.copy(excludedNumbers = emptySet()) }

    fun dismissMessage() = mutableState.update { it.copy(errorMessage = null, warningMessage = null) }

    fun save() {
        val current = state.value
        if (!current.canSave) {
            mutableState.update { it.copy(errorMessage = "Revise a configuração do Rapidão antes de salvar.") }
            return
        }
        val selectedModalities = current.availableModalities.filter { it.id in current.selectedModalityIds }
        val configuration = RapidBetConfiguration(
            minimumAmount = current.minimumAmount,
            maximumAmount = current.maximumAmount,
            minimumPrize = current.minimumPrize,
            requiredNumbers = current.requiredNumbers,
            excludedNumbers = current.excludedNumbers,
            modalities = selectedModalities,
            favoriteBetIds = current.favoriteBetIds,
        )
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loading = true, errorMessage = null) }
            when (val result = saveConfiguration(configuration)) {
                is AppResult.Success -> mutableState.update { it.copy(loading = false, saved = true) }
                is AppResult.Failure -> applyFailure(result.error)
            }
        }
    }

    private fun toggleNumber(number: Int, required: Boolean) = mutableState.update { current ->
        if (number !in 1..100) return@update current
        val selected = if (required) current.requiredNumbers else current.excludedNumbers
        val opposite = if (required) current.excludedNumbers else current.requiredNumbers
        if (number in opposite) {
            current.copy(warningMessage = "Este número já foi selecionado no outro filtro.")
        } else if (required) {
            current.copy(requiredNumbers = if (number in selected) selected - number else selected + number)
        } else {
            current.copy(excludedNumbers = if (number in selected) selected - number else selected + number)
        }
    }

    private fun applyFailure(error: AppError) = mutableState.update {
        it.copy(loading = false, errorMessage = when (error) {
            AppError.Network -> "Não foi possível carregar o Rapidão. Verifique sua internet."
            is AppError.Validation -> error.reason.ifBlank { "Não foi possível salvar o Rapidão." }
            AppError.Unexpected -> "Ocorreu um erro inesperado no Rapidão."
        })
    }

    private fun formatRapidAmount(value: BigDecimal) = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR")).format(value).replace('\u00a0', ' ')

    private companion object { const val LAST_PAGE = 6 }
}
