package br.gov.caixa.loterias.feature.marketplacefilter.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.event.SingleConsumerEventQueue
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.domain.rule.MarketplaceFilterRules
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.marketplaceFilterState
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.marketplaceModalities
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.retailerDisplayLabel
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.toDomain
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterStateAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.reduceMarketplaceFilterState
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import kotlinx.coroutines.launch

class MarketplaceFilterViewModel(
    private val getFilterParameters: GetMarketplaceFilterParametersUseCase,
    private val getBettingParameters: GetBettingParametersUseCase,
    private val searchRetailers: SearchRetailersUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val initialFilter: MarketplaceFilter = MarketplaceFilter.empty(),
    private val clearFilter: MarketplaceFilter = MarketplaceFilter.forModality(
        initialFilter.modalityId ?: 0,
        initialFilter.contestType ?: 0
    ),
    lotteryEnabled: Boolean = true,
    initialRetailer: br.gov.caixa.loterias.feature.retail.domain.model.Retailer? = null,
    private val getSpecialContests: GetMarketplaceSpecialContestsUseCase? = null
) : ViewModel() {
    private val mutableState = reduxStateStore<MarketplaceFilterUiState, MarketplaceFilterStateAction>(
        marketplaceFilterState(initialFilter, lotteryEnabled).let { restored ->
            initialRetailer?.takeIf { it.id.toString() == initialFilter.lottery }?.let { retailer ->
                restored.copy(selectedRetailer = retailer, lottery = retailerDisplayLabel(retailer))
            } ?: restored
        },
        ::reduceMarketplaceFilterState
    ).also { addCloseable(it) }
    val state = mutableState.state
    private var lastIndividualModality = mutableState.value.selectedModality

    private val mutableAppliedFilters = SingleConsumerEventQueue<MarketplaceFilter>()
    val appliedFilters = mutableAppliedFilters.events

    init {
        loadParameters()
    }

    /** RESULT_OK da CX sem payload de filtro reconstrói apenas a última modalidade pesquisada. */
    fun filterAfterBack(): MarketplaceFilter = MarketplaceFilter.forModality(initialFilter.modalityId ?: 0, initialFilter.contestType ?: 0)

    fun onAction(action: MarketplaceFilterAction) {
        when (action) {
            MarketplaceFilterAction.Apply -> applyFilter()

            MarketplaceFilterAction.Back -> Unit

            MarketplaceFilterAction.Clear -> {
                lastIndividualModality = marketplaceFilterState(clearFilter, state.value.lotteryEnabled).selectedModality
                mutableState.dispatch(MarketplaceFilterStateAction.Cleared(marketplaceFilterState(clearFilter, state.value.lotteryEnabled)))
            }

            is MarketplaceFilterAction.ChangeLottery -> changeLottery(action.value)

            is MarketplaceFilterAction.SelectModality -> {
                if (!action.value.isAll) lastIndividualModality = action.value
                mutableState.dispatch(MarketplaceFilterStateAction.ModalitySelected(action.value))
            }

            is MarketplaceFilterAction.SelectRetailer -> mutableState.dispatch(MarketplaceFilterStateAction.RetailerSelected(action.value))

            is MarketplaceFilterAction.ChangeMinimumValue -> mutableState.dispatch(
                MarketplaceFilterStateAction.MinimumValueChanged(action.value)
            )

            is MarketplaceFilterAction.ChangeMaximumValue -> mutableState.dispatch(
                MarketplaceFilterStateAction.MaximumValueChanged(action.value)
            )

            is MarketplaceFilterAction.ChangeMinimumShares -> mutableState.dispatch(
                MarketplaceFilterStateAction.MinimumSharesChanged(action.value)
            )

            is MarketplaceFilterAction.ChangeMaximumShares -> mutableState.dispatch(
                MarketplaceFilterStateAction.MaximumSharesChanged(action.value)
            )

            is MarketplaceFilterAction.SelectBetQuantity -> mutableState.dispatch(
                MarketplaceFilterStateAction.BetQuantitySelected(action.value)
            )

            is MarketplaceFilterAction.SelectNumberQuantity -> mutableState.dispatch(
                MarketplaceFilterStateAction.NumberQuantitySelected(action.value)
            )

            is MarketplaceFilterAction.ChangeWantedNumbers -> mutableState.dispatch(
                MarketplaceFilterStateAction.WantedNumbersChanged(action.value)
            )

            is MarketplaceFilterAction.ChangeUnwantedNumbers -> mutableState.dispatch(
                MarketplaceFilterStateAction.UnwantedNumbersChanged(action.value)
            )
        }
    }

    fun loadParameters() {
        viewModelScope.launch(dispatchers.io) {
            val filterOutcome = getFilterParameters()
            val bettingOutcome = getBettingParameters()
            val specialContests = (getSpecialContests?.invoke() as? AppResult.Success)?.value
                ?: MarketplaceSpecialContests()
            val parameters = (filterOutcome as? AppResult.Success)?.value
            val modalities = (bettingOutcome as? AppResult.Success)?.value?.let { marketplaceModalities(it, specialContests) }.orEmpty()
            val valid = parameters?.isValid == true && bettingOutcome is AppResult.Success
            mutableState.dispatch(
                MarketplaceFilterStateAction.ParametersReceived(
                    parameters?.betOptions.orEmpty(),
                    modalities,
                    if (valid) null else messageText("configuracao_do_filtro_indisponi_ecb11f")
                )
            )
        }
    }

    private fun changeLottery(value: String) {
        mutableState.dispatch(MarketplaceFilterStateAction.LotteryChanged(value, lastIndividualModality))
        if (value.length == RETAILER_QUERY_LENGTH) searchRetailerSuggestions(value)
    }

    private fun searchRetailerSuggestions(query: String) {
        viewModelScope.launch(dispatchers.io) {
            when (val outcome = searchRetailers(query)) {
                is AppResult.Success -> mutableState.dispatch(MarketplaceFilterStateAction.RetailersReceived(query, outcome.value))
                is AppResult.Failure -> mutableState.dispatch(MarketplaceFilterStateAction.RetailersFailed(query))
            }
        }
    }

    private fun applyFilter() {
        val current = state.value
        val overflowMessage = when {
            current.minimumShares.isNotEmpty() && current.minimumShares.toIntOrNull() == null ->
                messageText("quantidade_minima_nao_pode_ser_m_d73775")

            current.maximumShares.isNotEmpty() && current.maximumShares.toIntOrNull() == null ->
                messageText("quantidade_maxima_nao_pode_ser_m_1d14f1")

            else -> null
        }
        if (overflowMessage != null) {
            mutableState.dispatch(MarketplaceFilterStateAction.SharesOverflowed(overflowMessage))
            return
        }
        val filter = current.toDomain()
        val validation = MarketplaceFilterRules.validate(filter)
        if (validation.isValid) {
            viewModelScope.launch { mutableAppliedFilters.emit(filter) }
        } else {
            mutableState.dispatch(MarketplaceFilterStateAction.ValidationFailed(validation.message))
        }
    }

    private companion object {
        const val RETAILER_QUERY_LENGTH = 6
    }
}
