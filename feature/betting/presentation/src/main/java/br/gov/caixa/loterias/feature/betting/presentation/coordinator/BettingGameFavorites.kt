package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.ValidateFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.SUPER_SEVEN_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.bettingFavoriteSelectionChanged
import br.gov.caixa.loterias.feature.betting.presentation.state.toFavoriteDraft
import kotlinx.coroutines.launch

internal class BettingGameFavorites(
    private val mutableState: ReduxStateStore<BettingGameUiState, BettingGameAction>,
    private val scope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val validateFavoriteBet: ValidateFavoriteBetUseCase?,
    private val saveFavoriteBet: SaveFavoriteBetUseCase?,
    private val stateWriter: ((BettingGameAction) -> Unit)? = null,
    private val beforeRemote: suspend (BettingGameUiState) -> Boolean = { true },
    private val afterRemote: suspend () -> Boolean = { true },
    private val isCurrent: () -> Boolean = { true },
    private val enqueueCommand: ((() -> Unit) -> Unit)? = null,
) {
    private var favoriteRevision = 0L
    private var pendingFavoriteDraft: FavoriteBetDraft? = null

    fun requestFavorite() {
        if (!mutableState.value.canFavorite) return
        dispatch(BettingGameAction.FavoriteRequested)
    }

    fun changeFavoriteName(value: String) = dispatch(BettingGameAction.FavoriteNameChanged(value))

    fun submitFavorite() {
        val current = mutableState.value
        if (!current.canFavorite || current.favoriteOverwriteConfirmation) return
        val draft = current.toFavoriteDraft(current.favoriteName.trim().take(25)) ?: return
        if (draft.name.isBlank()) {
            dispatch(BettingGameAction.FavoriteNameMissing())
            return
        }
        val validate = validateFavoriteBet
        if (validate == null || saveFavoriteBet == null) {
            dispatch(BettingGameAction.FavoriteUnavailable())
            return
        }
        val revision = favoriteRevision
        dispatch(BettingGameAction.FavoriteSavingStarted)
        scope.launch(dispatchers.io) {
            val result = validate(draft)
            if (revision != favoriteRevision) {
                dispatch(BettingGameAction.FavoriteLoadingFinished)
                return@launch
            }
            when (result) {
                is AppResult.Success -> saveFavorite(draft, revision)

                is AppResult.Failure -> {
                    val error = result.error
                    if (error is AppError.Validation) {
                        pendingFavoriteDraft = draft
                        dispatch(BettingGameAction.FavoriteOverwriteRequired(error.reason))
                    } else {
                        dispatch(BettingGameAction.FavoriteFailed(error.favoriteMessage()))
                    }
                }
            }
        }
    }

    fun cancelFavorite() {
        favoriteRevision++
        pendingFavoriteDraft = null
        dispatch(BettingGameAction.FavoriteCancelled)
    }

    fun confirmFavoriteOverwrite() {
        if (mutableState.value.loading || !mutableState.value.favoriteOverwriteConfirmation) return
        val draft = pendingFavoriteDraft ?: return
        pendingFavoriteDraft = null
        val revision = favoriteRevision
        dispatch(BettingGameAction.FavoriteOverwriteStarted)
        scope.launch(dispatchers.io) { saveFavorite(draft, revision) }
    }

    fun dismissFavoriteOverwrite() {
        pendingFavoriteDraft = null
        dispatch(BettingGameAction.FavoriteOverwriteDismissed)
    }

    private suspend fun saveFavorite(draft: FavoriteBetDraft, revision: Long) {
        val save = saveFavoriteBet ?: return
        if (!isCurrent() || !beforeRemote(mutableState.value)) return
        when (val result = save(draft)) {
            is AppResult.Success -> {
                if (!isCurrent() || !afterRemote()) return
                completeFavoriteSave(revision)
            }

            is AppResult.Failure -> dispatch(BettingGameAction.FavoriteFailed(result.error.favoriteMessage()))
        }
    }

    private fun completeFavoriteSave(revision: Long) {
        // Resolve a revisão somente quando os comandos persistentes anteriores tiverem sido executados.
        val complete: () -> Unit = {
            synchronized(mutableState) {
                dispatch(BettingGameAction.FavoriteSaved(revision == favoriteRevision))
            }
        }
        if (enqueueCommand != null) enqueueCommand.invoke(complete) else complete()
    }

    fun dispatch(action: BettingGameAction) {
        if (stateWriter != null) {
            stateWriter.invoke(action)
        } else {
            synchronized(mutableState) {
                val before = mutableState.value
                mutableState.dispatch(action)
                recordSelectionChange(before, mutableState.value)
            }
        }
    }

    fun recordSelectionChange(before: BettingGameUiState, after: BettingGameUiState) {
        if (bettingFavoriteSelectionChanged(before, after)) {
            favoriteRevision++
            pendingFavoriteDraft = null
        }
    }

    private fun AppError.favoriteMessage(): String = when (this) {
        AppError.Network -> messageText("nao_foi_possivel_acessar_o_servi_856170")
        is AppError.Validation -> reason.ifBlank { messageText("nao_foi_possivel_salvar_a_aposta_eab8cf") }
        AppError.Unexpected -> messageText("nao_foi_possivel_salvar_a_aposta_79ba1d")
    }
}
