package br.gov.caixa.loterias.feature.tickets.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.toFavoriteDraft
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.toFavoriteMessage
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class BetDetailFavoriteCoordinator(
    private val mutableState: ReduxStateStore<BetDetailUiState, BetDetailStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val validateFavoriteBet: ValidateFavoriteBetUseCase,
    private val saveFavoriteBet: SaveFavoriteBetUseCase
) {
    private var pendingFavoriteDraft: FavoriteBetDraft? = null

    fun toggleFavoriteEditor() = mutableState.dispatch(BetDetailStateAction.FavoriteEditorToggled)

    fun changeFavoriteName(value: String) = mutableState.dispatch(BetDetailStateAction.FavoriteNameChanged(value))

    fun submitFavorite() = scope.launch(dispatchers.io) {
        if (mutableState.value.loading) return@launch
        pendingFavoriteDraft = null
        mutableState.dispatch(BetDetailStateAction.FavoriteValidationStarted)
        val draft = mutableState.value.toFavoriteDraft()
        if (draft.name.isBlank()) {
            mutableState.dispatch(BetDetailStateAction.FavoriteNameRejected())
            return@launch
        }
        mutableState.dispatch(BetDetailStateAction.FavoriteSavingStarted)
        when (val result = validateFavoriteBet(draft)) {
            is AppResult.Success -> saveFavorite(draft)

            is AppResult.Failure -> when (val error = result.error) {
                is AppError.Validation -> {
                    pendingFavoriteDraft = draft
                    mutableState.dispatch(BetDetailStateAction.FavoriteOverwriteRequired(error.reason))
                }

                else -> mutableState.dispatch(BetDetailStateAction.FavoriteValidationFailed(result.error.toFavoriteMessage()))
            }
        }
    }

    fun confirmFavoriteOverwrite() = scope.launch(dispatchers.io) {
        val draft = pendingFavoriteDraft ?: return@launch
        if (!mutableState.value.favoriteOverwriteConfirmation || mutableState.value.loading) return@launch
        pendingFavoriteDraft = null
        mutableState.dispatch(BetDetailStateAction.FavoriteOverwriteConfirmed)
        saveFavorite(draft)
    }

    fun dismissFavoriteOverwrite() {
        pendingFavoriteDraft = null
        mutableState.dispatch(BetDetailStateAction.FavoriteOverwriteDismissed)
    }

    private suspend fun saveFavorite(draft: FavoriteBetDraft) {
        when (val result = saveFavoriteBet(draft)) {
            is AppResult.Success -> mutableState.dispatch(BetDetailStateAction.FavoriteSaved)
            is AppResult.Failure -> mutableState.dispatch(BetDetailStateAction.FavoriteSaveFailed(result.error.toFavoriteMessage()))
        }
    }
}
