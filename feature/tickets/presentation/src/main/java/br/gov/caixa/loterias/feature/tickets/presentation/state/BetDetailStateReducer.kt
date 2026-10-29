package br.gov.caixa.loterias.feature.tickets.presentation.state

internal sealed interface BetDetailDetailsAction : BetDetailStateAction

internal sealed interface BetDetailFavoriteAction : BetDetailStateAction

internal sealed interface BetDetailReceiptAction : BetDetailStateAction

internal sealed interface BetDetailRepeatAction : BetDetailStateAction

internal fun reduceBetDetailState(state: BetDetailUiState, action: BetDetailStateAction): BetDetailUiState = when (action) {
    is BetDetailDetailsAction -> reduceBetDetailDetailsState(state, action)
    is BetDetailFavoriteAction -> reduceBetDetailFavoriteState(state, action)
    is BetDetailReceiptAction -> reduceBetDetailReceiptState(state, action)
    is BetDetailRepeatAction -> reduceBetDetailRepeatState(state, action)
}

private fun reduceBetDetailDetailsState(state: BetDetailUiState, action: BetDetailDetailsAction): BetDetailUiState = when (action) {
    is BetDetailStateAction.DetailsLoaded -> BetDetailUiState(action.bet)
    is BetDetailStateAction.RecoveryFailed -> BetDetailUiState(action.bet, detailsReady = false, errorMessage = action.message)
    BetDetailStateAction.DetailsLoading -> state.copy(loading = true, detailsReady = false, errorMessage = null, serviceFailure = null)
    is BetDetailStateAction.DetailsFailed -> state.copy(loading = false, errorMessage = action.message)
    is BetDetailStateAction.ServiceRejected -> state.copy(loading = false, serviceFailure = action.failure)
    is BetDetailStateAction.DrawResultsReceived -> state.copy(drawResults = action.results)
    BetDetailStateAction.ServiceFailureDismissed -> state.copy(serviceFailure = null)
    BetDetailStateAction.InitialDetailsLoading -> state.copy(loading = true, detailsReady = false)
    BetDetailStateAction.ErrorDismissed -> state.copy(errorMessage = null)
}

private fun reduceBetDetailFavoriteState(state: BetDetailUiState, action: BetDetailFavoriteAction): BetDetailUiState = when (action) {
    BetDetailStateAction.FavoriteEditorToggled -> state.copy(
        favoriteExpanded = !state.favoriteExpanded,
        favoriteSaved = false,
        errorMessage = null
    )

    is BetDetailStateAction.FavoriteNameChanged -> state.copy(
        favoriteName = action.value.take(FAVORITE_BET_NAME_LENGTH),
        errorMessage = null
    )

    BetDetailStateAction.FavoriteValidationStarted -> state.copy(favoriteOverwriteConfirmation = false, favoriteOverwriteMessage = null)

    is BetDetailStateAction.FavoriteNameRejected -> state.copy(errorMessage = action.message)

    BetDetailStateAction.FavoriteSavingStarted -> state.copy(loading = true, errorMessage = null)

    is BetDetailStateAction.FavoriteOverwriteRequired -> state.copy(
        loading = false,
        favoriteOverwriteConfirmation = true,
        favoriteOverwriteMessage = action.message
    )

    is BetDetailStateAction.FavoriteValidationFailed -> state.copy(loading = false, errorMessage = action.message)

    BetDetailStateAction.FavoriteOverwriteConfirmed -> state.copy(
        favoriteOverwriteConfirmation = false,
        favoriteOverwriteMessage = null,
        loading = true
    )

    BetDetailStateAction.FavoriteOverwriteDismissed -> state.copy(favoriteOverwriteConfirmation = false, favoriteOverwriteMessage = null)

    BetDetailStateAction.FavoriteSaved -> state.copy(loading = false, favoriteExpanded = false, favoriteSaved = true)

    is BetDetailStateAction.FavoriteSaveFailed -> state.copy(loading = false, errorMessage = action.message)
}

private fun reduceBetDetailReceiptState(state: BetDetailUiState, action: BetDetailReceiptAction): BetDetailUiState = when (action) {
    is BetDetailStateAction.ReceiptUnavailable -> state.copy(errorMessage = action.message)

    BetDetailStateAction.ReceiptLoading -> state.copy(loading = true, errorMessage = null)

    BetDetailStateAction.ReceiptDelivered -> state.copy(loading = false)

    is BetDetailStateAction.ReceiptFailed -> state.copy(loading = false, errorMessage = action.message)

    is BetDetailStateAction.ReceiptOpenFailed -> state.copy(
        loading = false,
        errorMessage = action.message
    )
}

private fun reduceBetDetailRepeatState(state: BetDetailUiState, action: BetDetailRepeatAction): BetDetailUiState = when (action) {
    BetDetailStateAction.CartAdditionDismissed -> state.copy(cartAdditionConfirmed = false)

    is BetDetailStateAction.RepeatSelectionMissing -> state.copy(errorMessage = action.message)

    BetDetailStateAction.RepeatLoading -> state.copy(loading = true, errorMessage = null)

    is BetDetailStateAction.RepeatParametersFailed -> state.copy(loading = false, errorMessage = action.message)

    is BetDetailStateAction.RepeatContestSelected -> selectRepeatContest(state, action.index)

    BetDetailStateAction.RepeatContestConfirmed -> state.copy(repeatContestOptions = emptyList(), loading = true)

    BetDetailStateAction.RepeatContestDismissed -> state.copy(repeatContestOptions = emptyList(), repeatConversion = null, loading = false)

    BetDetailStateAction.RepeatConversionConfirmed -> state.copy(repeatConversion = null, loading = true)

    is BetDetailStateAction.RepeatUnavailable -> state.copy(
        loading = false,
        errorMessage = action.message
    )

    is BetDetailStateAction.RepeatConversionRequired -> state.copy(loading = false, repeatConversion = action.option)

    is BetDetailStateAction.RepeatContestsReceived -> state.copy(
        loading = false,
        repeatContestOptions = action.available,
        selectedRepeatContestIndex = action.selected
    )

    BetDetailStateAction.RepeatAddedToCart -> state.copy(loading = false, cartAdditionConfirmed = true)

    is BetDetailStateAction.RepeatAdditionFailed -> state.copy(loading = false, errorMessage = action.message)
}

private fun selectRepeatContest(state: BetDetailUiState, index: Int): BetDetailUiState =
    if (index !in state.repeatContestOptions.indices) state else state.copy(selectedRepeatContestIndex = index)

private const val FAVORITE_BET_NAME_LENGTH = 25
