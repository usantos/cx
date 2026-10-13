package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilter
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceFilterRules
import br.gov.caixa.loterias.core.domain.retail.SearchRetailersUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarketplaceFilterViewModel(
    private val getFilterParameters: GetMarketplaceFilterParametersUseCase,
    private val getBettingParameters: GetBettingParametersUseCase,
    private val searchRetailers: SearchRetailersUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        MarketplaceFilterUiState(
            numberOptions = allNumberQuantityOptions,
        ),
    )
    val state = mutableState.asStateFlow()

    private val mutableAppliedFilters = MutableSharedFlow<MarketplaceFilter>()
    val appliedFilters = mutableAppliedFilters.asSharedFlow()

    init {
        loadParameters()
    }

    fun onAction(action: MarketplaceFilterAction) {
        when (action) {
            MarketplaceFilterAction.Apply -> applyFilter()
            MarketplaceFilterAction.Back -> Unit
            is MarketplaceFilterAction.ChangeLottery -> changeLottery(action.value)
            is MarketplaceFilterAction.ChangeMinimumShares,
            is MarketplaceFilterAction.ChangeMaximumShares -> mutableState.update {
                it.reduce(action).withShareValidation().copy(validationMessage = null)
            }
            else -> mutableState.update { it.reduce(action).copy(validationMessage = null) }
        }
    }

    fun loadParameters() {
        viewModelScope.launch(dispatchers.io) {
            val filterOutcome = getFilterParameters()
            val bettingOutcome = getBettingParameters()
            mutableState.update { current ->
                val betOptions = (filterOutcome as? AppResult.Success)?.value?.betOptions.orEmpty()
                val modalities = (bettingOutcome as? AppResult.Success)?.value
                    ?.let(::marketplaceModalities).orEmpty()
                val selected = current.selectedModality?.let { previous ->
                    modalities.firstOrNull { it.id == previous.id && it.contestType == previous.contestType }
                }
                val valid = (filterOutcome as? AppResult.Success)?.value?.isValid == true &&
                    bettingOutcome is AppResult.Success
                current.copy(
                    modalities = modalities,
                    selectedModality = selected,
                    betOptions = betOptions,
                    numberOptions = selected?.numberOptions ?: allNumberQuantityOptions,
                    configurationError = if (valid) null else "Configuração do filtro indisponível",
                )
            }
        }
    }

    private fun changeLottery(value: String) {
        mutableState.update { current ->
            val changed = current.reduce(MarketplaceFilterAction.ChangeLottery(value))
            when {
                value.length < RETAILER_QUERY_LENGTH -> changed.copy(
                    retailerSuggestions = emptyList(),
                    retailerLoading = false,
                )
                value.length > RETAILER_QUERY_LENGTH -> changed.copy(
                    retailerSuggestions = filterRetailers(value, changed.cachedRetailers),
                    retailerLoading = false,
                )
                else -> changed.copy(
                    retailerSuggestions = emptyList(),
                    retailerLoading = true,
                )
            }
        }
        if (value.length == RETAILER_QUERY_LENGTH) searchRetailerSuggestions(value)
    }

    private fun searchRetailerSuggestions(query: String) {
        viewModelScope.launch(dispatchers.io) {
            when (val outcome = searchRetailers(query)) {
                is AppResult.Success -> mutableState.update { current ->
                    if (!current.lottery.startsWith(query, ignoreCase = true) || current.selectedRetailer != null) {
                        current
                    } else {
                        val suggestions = if (current.lottery.length == RETAILER_QUERY_LENGTH) {
                            outcome.value
                        } else {
                            filterRetailers(current.lottery, outcome.value)
                        }
                        current.copy(
                            cachedRetailers = outcome.value,
                            retailerSuggestions = suggestions,
                            retailerLoading = false,
                            retailerMessage = "Sua busca não retornou resultados."
                                .takeIf { current.lottery.length == RETAILER_QUERY_LENGTH && suggestions.isEmpty() },
                        )
                    }
                }
                is AppResult.Failure -> mutableState.update { current ->
                    if (current.lottery.startsWith(query, ignoreCase = true)) {
                        current.copy(retailerLoading = false, retailerSuggestions = emptyList())
                    } else {
                        current
                    }
                }
            }
        }
    }

    private fun applyFilter() {
        val filter = state.value.toDomain()
        val validation = MarketplaceFilterRules.validate(filter)
        if (validation.isValid) {
            viewModelScope.launch { mutableAppliedFilters.emit(filter) }
        } else {
            mutableState.update { it.copy(validationMessage = validation.message) }
        }
    }

    private companion object {
        const val RETAILER_QUERY_LENGTH = 6
    }
}
