package br.gov.caixa.loterias.feature.retail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.retail.Retailer
import br.gov.caixa.loterias.core.domain.retail.SearchRetailersUseCase
import br.gov.caixa.loterias.core.domain.retail.SetRetailerFavoriteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RetailSearchUiState(
    val query: String = "",
    val retailers: List<Retailer> = emptyList(),
    val loading: Boolean = false,
    val searched: Boolean = false,
    val favoriteCandidate: Retailer? = null,
    val errorMessage: String? = null,
)

class RetailSearchViewModel(
    private val searchRetailers: SearchRetailersUseCase,
    private val setFavorite: SetRetailerFavoriteUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(RetailSearchUiState())
    val state = mutableState.asStateFlow()
    private var searchJob: Job? = null

    fun changeQuery(value: String) {
        val query = value.take(80)
        searchJob?.cancel()
        if (query.trim().length < 3) {
            mutableState.value = RetailSearchUiState(query = query)
            return
        }
        mutableState.value = mutableState.value.copy(query = query, errorMessage = null)
        searchJob = viewModelScope.launch(dispatchers.io) {
            delay(400)
            search(query)
        }
    }

    fun searchNow() {
        searchJob?.cancel()
        val query = mutableState.value.query
        if (query.trim().length < 3) {
            mutableState.value = mutableState.value.copy(errorMessage = "Informe pelo menos 3 caracteres.")
            return
        }
        searchJob = viewModelScope.launch(dispatchers.io) { search(query) }
    }

    fun requestToggleFavorite(retailer: Retailer) {
        mutableState.value = mutableState.value.copy(favoriteCandidate = retailer)
    }

    fun cancelToggleFavorite() {
        mutableState.value = mutableState.value.copy(favoriteCandidate = null)
    }

    fun confirmToggleFavorite() {
        val retailer = mutableState.value.favoriteCandidate ?: return
        mutableState.value = mutableState.value.copy(loading = true, favoriteCandidate = null, errorMessage = null)
        viewModelScope.launch(dispatchers.io) {
            when (setFavorite(retailer.id, !retailer.favorite)) {
                is AppResult.Success -> mutableState.value = mutableState.value.copy(
                    retailers = mutableState.value.retailers.map {
                        if (it.id == retailer.id) it.copy(favorite = !retailer.favorite) else it
                    },
                    loading = false,
                )
                is AppResult.Failure -> mutableState.value = mutableState.value.copy(
                    loading = false,
                    errorMessage = if (retailer.favorite) "Não foi possível remover de favoritas." else "Não foi possível favoritar.",
                )
            }
        }
    }

    fun dismissError() { mutableState.value = mutableState.value.copy(errorMessage = null) }

    private suspend fun search(query: String) {
        mutableState.value = mutableState.value.copy(loading = true, errorMessage = null)
        when (val result = searchRetailers(query)) {
            is AppResult.Success -> if (mutableState.value.query == query) mutableState.value = mutableState.value.copy(
                retailers = result.value,
                loading = false,
                searched = true,
            )
            is AppResult.Failure -> if (mutableState.value.query == query) mutableState.value = mutableState.value.copy(
                retailers = emptyList(),
                loading = false,
                searched = true,
                errorMessage = "Não foi possível buscar as lotéricas.",
            )
        }
    }
}
