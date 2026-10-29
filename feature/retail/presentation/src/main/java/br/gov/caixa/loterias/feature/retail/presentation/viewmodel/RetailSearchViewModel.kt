package br.gov.caixa.loterias.feature.retail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import br.gov.caixa.loterias.feature.retail.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.presentation.state.RetailSearchStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.RetailSearchUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.reduceRetailSearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RetailSearchViewModel(
    private val searchRetailers: SearchRetailersUseCase,
    private val setFavorite: SetRetailerFavoriteUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = reduxStateStore<RetailSearchUiState, RetailSearchStateAction>(
        RetailSearchUiState(),
        ::reduceRetailSearchState
    ).also { addCloseable(it) }
    val state = mutableState.state
    private var searchJob: Job? = null

    fun changeQuery(value: String) {
        val query = value.take(80)
        searchJob?.cancel()
        if (query.trim().length < 3) {
            mutableState.dispatch(RetailSearchStateAction.QueryChanged(query))
            return
        }
        mutableState.dispatch(RetailSearchStateAction.QueryChanged(query))
        searchJob = viewModelScope.launch(dispatchers.io) {
            delay(400)
            search(query)
        }
    }

    fun searchNow() {
        searchJob?.cancel()
        val query = mutableState.value.query
        if (query.trim().length < 3) {
            mutableState.dispatch(RetailSearchStateAction.SearchRejected("Informe pelo menos 3 caracteres."))
            return
        }
        searchJob = viewModelScope.launch(dispatchers.io) { search(query) }
    }

    fun requestToggleFavorite(retailer: Retailer) {
        mutableState.dispatch(RetailSearchStateAction.FavoriteRequested(retailer))
    }

    fun cancelToggleFavorite() {
        mutableState.dispatch(RetailSearchStateAction.FavoriteCancelled)
    }

    fun confirmToggleFavorite() {
        val retailer = mutableState.value.favoriteCandidate ?: return
        mutableState.dispatch(RetailSearchStateAction.FavoriteStarted)
        viewModelScope.launch(dispatchers.io) {
            when (setFavorite(retailer.id, !retailer.favorite)) {
                is AppResult.Success -> mutableState.dispatch(RetailSearchStateAction.FavoriteSaved(retailer))

                is AppResult.Failure -> mutableState.dispatch(
                    RetailSearchStateAction.FavoriteFailed(
                        if (retailer.favorite) {
                            messageText(
                                "nao_foi_possivel_remover_de_favo_41e8c6"
                            )
                        } else {
                            messageText("nao_foi_possivel_favoritar_65b94b")
                        }
                    )
                )
            }
        }
    }

    fun dismissError() {
        mutableState.dispatch(RetailSearchStateAction.ErrorDismissed)
    }

    private suspend fun search(query: String) {
        mutableState.dispatch(RetailSearchStateAction.SearchStarted)
        when (val result = searchRetailers(query)) {
            is AppResult.Success -> mutableState.dispatch(RetailSearchStateAction.SearchReceived(query, result.value))

            is AppResult.Failure -> mutableState.dispatch(
                RetailSearchStateAction.SearchFailed(query, messageText("nao_foi_possivel_buscar_as_loter_06f491"))
            )
        }
    }
}
