package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationAction
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceRapidConfigurationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class RapidConfigurationFavorites(
    private val mutableState: ReduxStateStore<RapidConfigurationUiState, RapidConfigurationAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getFavorites: GetFavoriteBetsUseCase?,
    private val isSessionCurrent: () -> Boolean = { true },
) {

    fun loadFavorites() = scope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        val useCase = getFavorites ?: return@launch
        if (mutableState.value.favoritesLoading) return@launch
        mutableState.dispatch(RapidConfigurationAction.FavoritesLoading)
        val result = useCase(null)
        if (!isSessionCurrent()) return@launch
        when (result) {
            is AppResult.Success -> mutableState.dispatch(RapidConfigurationAction.FavoritesLoaded(result.value))
            is AppResult.Failure -> mutableState.dispatch(RapidConfigurationAction.FavoritesFailed(result.error))
        }
    }

    fun toggleFavorite(id: Long) = mutableState.dispatch(RapidConfigurationAction.FavoriteToggled(id))
}
