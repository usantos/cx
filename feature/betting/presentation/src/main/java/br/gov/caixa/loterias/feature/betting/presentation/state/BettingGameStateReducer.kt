package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetDraft

internal fun reduceBettingGameState(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState = when (action) {
    is BettingGameAction.DraftCommitted -> action.actions.fold(state, ::reduceBettingGameState)

    is BettingGameAction.RestoreConfirmedDraft -> action.checkpoint

    is BettingGameAction.CheckpointRestored -> action.checkpoint

    is BettingGameAction.RecoveryRejected -> BettingGameUiState(
        loading = false,
        errorMessage = action.message,
        recoveryReview = action.review
    )

    is BettingGameAction.DraftWriteFailed -> state.copy(loading = false, errorMessage = action.message)

    else -> reduceBettingGameMutation(state, action)
}

private fun reduceBettingGameMutation(state: BettingGameUiState, action: BettingGameAction): BettingGameUiState {
    val cart = reduceBettingGameCart(state, action)
    val favorites = reduceBettingGameFavorites(cart, action)
    val parameters = reduceBettingGameParameters(favorites, action)
    val steps = reduceBettingGameSteps(parameters, action)
    val numbers = reduceBettingNumberSelection(steps, action)
    val quantities = reduceBettingSelectionQuantities(numbers, action)
    val reset = reduceBettingSelectionReset(quantities, action)
    val sports = reduceBettingSportsSelection(reset, action)
    val superSeven = reduceBettingSuperSevenSelection(sports, action)
    val notice = reduceBettingGameNotice(superSeven, action)
    val reduced = reduceBettingGame(notice, action)
    return if (bettingFavoriteSelectionChanged(state, reduced)) {
        reduced.copy(
            favoriteSaved = false,
            favoriteExpanded = false,
            favoriteOverwriteConfirmation = false,
            favoriteOverwriteMessage = null
        )
    } else {
        reduced
    }
}

internal fun bettingFavoriteSelectionChanged(before: BettingGameUiState, after: BettingGameUiState): Boolean =
    before.toFavoriteDraft("") != after.toFavoriteDraft("") || before.mode != after.mode || before.numberQuantity != after.numberQuantity

internal fun BettingGameUiState.toFavoriteDraft(name: String): FavoriteBetDraft? = game?.let { current ->
    FavoriteBetDraft(
        modalityId = current.modality.id,
        modalityName = current.modality.name,
        name = name,
        numberColumns = if (current.modality.id == SUPER_SEVEN_ID) {
            superSevenColumns.map { it.sorted() }
        } else {
            listOf(selectedNumbers.sorted())
        },
        clovers = selectedClovers.sorted(),
        favoriteTeam = selectedTeam,
        luckyMonth = selectedLuckyMonth
    )
}
