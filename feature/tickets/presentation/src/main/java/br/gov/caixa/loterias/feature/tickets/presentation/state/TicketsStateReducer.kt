package br.gov.caixa.loterias.feature.tickets.presentation.state

import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseBetFilters

internal sealed interface TicketsHistoryAction : TicketsStateAction

internal sealed interface TicketsPurchaseFilterAction : TicketsStateAction

internal sealed interface TicketsQueryAction : TicketsStateAction

internal sealed interface TicketsPixAction : TicketsStateAction

internal sealed interface TicketsPurchaseAction : TicketsStateAction

internal sealed interface TicketsFavoriteAction : TicketsStateAction

internal fun reduceTicketsState(state: TicketsUiState, action: TicketsStateAction): TicketsUiState = when (action) {
    is TicketsBetFilterAction -> reduceTicketsBetFilterState(state, action)
    is TicketsHistoryAction -> reduceTicketsHistoryState(state, action)
    is TicketsPurchaseFilterAction -> reduceTicketsPurchaseFilterState(state, action)
    is TicketsQueryAction -> reduceTicketsQueryState(state, action)
    is TicketsPixAction -> reduceTicketsPixState(state, action)
    is TicketsPurchaseAction -> reduceTicketsPurchaseState(state, action)
    is TicketsFavoriteAction -> reduceTicketsFavoriteState(state, action)
}

private fun reduceTicketsHistoryState(state: TicketsUiState, action: TicketsHistoryAction): TicketsUiState = when (action) {
    is TicketsStateAction.OperationFailed -> state.copy(
        isLoading = false,
        isLoadingMore = false,
        loaded = true,
        errorMessage = action.message
    )

    TicketsStateAction.BetsLoading -> state.copy(isLoading = true, errorMessage = null)

    is TicketsStateAction.BetsConfigurationReceived -> state.copy(
        betConfiguration = action.configuration,
        modalities = action.modalities,
        betFilters = initialBetFilters(state.betFilters, action.configuration)
    )

    TicketsStateAction.BetFiltersApplied -> state.copy(showBetFilters = false, isLoading = true, errorMessage = null)

    is TicketsStateAction.BetsReceived -> state.copy(
        bets = action.page.bets,
        historyBackend = action.page.backend,
        isLoading = false,
        loaded = true
    )

    TicketsStateAction.PurchasesLoading -> state.copy(isLoading = true, errorMessage = null)

    is TicketsStateAction.PurchaseMonthsReceived -> state.copy(
        purchaseMonths = action.months,
        purchaseFilters = state.purchaseFilters.copy(
            month = state.purchaseFilters.month?.takeIf { it in action.months } ?: action.months.firstOrNull()
        )
    )

    TicketsStateAction.PurchasesReloading -> state.copy(isLoading = true, errorMessage = null)

    TicketsStateAction.PurchasePageLoading -> state.copy(isLoadingMore = true)

    is TicketsStateAction.PurchasePageReceived -> state.copy(
        purchases = if (action.append) {
            (state.purchases + action.purchases).distinctBy(Purchase::id)
        } else {
            action.purchases
        },
        isLoading = false,
        isLoadingMore = false,
        canLoadMore = action.purchases.size == PURCHASE_PAGE_SIZE,
        loaded = true
    )
}

private fun reduceTicketsPurchaseFilterState(state: TicketsUiState, action: TicketsPurchaseFilterAction): TicketsUiState = when (action) {
    is TicketsStateAction.PurchaseMonthSelected -> state.copy(
        purchaseFilters = state.purchaseFilters.copy(month = action.month)
    )

    TicketsStateAction.PurchaseFiltersLoading -> state.copy(isLoading = true, errorMessage = null)

    is TicketsStateAction.PurchaseFiltersReceived -> state.copy(
        paymentMethods = action.methods,
        purchaseStatuses = action.statuses,
        isLoading = false,
        showPurchaseFilters = true
    )

    TicketsStateAction.PurchaseFiltersClosed -> state.copy(showPurchaseFilters = false)

    is TicketsStateAction.PaymentMethodSelected -> state.copy(
        purchaseFilters = state.purchaseFilters.copy(paymentMethodId = action.id, statusId = null),
        isLoading = true
    )

    is TicketsStateAction.PurchaseStatusesReceived -> state.copy(
        purchaseStatuses = action.statuses,
        isLoading = false,
        showPurchaseFilters = true
    )

    is TicketsStateAction.PurchaseStatusSelected -> state.copy(
        purchaseFilters = state.purchaseFilters.copy(statusId = action.id)
    )

    TicketsStateAction.PurchaseFiltersCleared -> state.copy(
        purchaseFilters = state.purchaseFilters.copy(paymentMethodId = null, statusId = null),
        showPurchaseFilters = false
    )

    TicketsStateAction.PurchaseFiltersApplied -> state.copy(showPurchaseFilters = false)

    else -> reduceTicketsPurchaseBetFilterState(state, action)
}

private fun reduceTicketsPurchaseBetFilterState(state: TicketsUiState, action: TicketsPurchaseFilterAction): TicketsUiState =
    when (action) {
        is TicketsStateAction.PurchaseBetContestSelected -> state.copy(
            purchaseBetFilters = state.purchaseBetFilters.copy(contest = action.value)
        )

        is TicketsStateAction.PurchaseBetStatusSelected -> state.copy(
            purchaseBetFilters = state.purchaseBetFilters.copy(statusId = action.value)
        )

        is TicketsStateAction.PurchaseBetModalitySelected -> state.copy(
            purchaseBetFilters = state.purchaseBetFilters.copy(modalityId = action.value)
        )

        TicketsStateAction.PurchaseBetFiltersOpened -> state.copy(showPurchaseBetFilters = true)

        TicketsStateAction.PurchaseBetFiltersClosed -> state.copy(showPurchaseBetFilters = false)

        TicketsStateAction.PurchaseBetFiltersCleared -> state.copy(
            purchaseBetFilters = PurchaseBetFilters(),
            showPurchaseBetFilters = false
        )

        TicketsStateAction.PurchaseBetFiltersApplied -> state.copy(showPurchaseBetFilters = false)

        else -> state
    }

private fun reduceTicketsQueryState(state: TicketsUiState, action: TicketsQueryAction): TicketsUiState = when (action) {
    TicketsStateAction.TicketNavigationConsumed -> consumeTicketNavigation(state)

    is TicketsStateAction.TicketCodeChanged -> state.copy(
        ticketCode = action.value.filter(Char::isLetterOrDigit).uppercase().take(TICKET_CODE_LENGTH),
        errorMessage = null
    )

    is TicketsStateAction.TicketRetailerCodeChanged -> state.copy(
        ticketRetailerCode = action.value.filter(Char::isLetterOrDigit).uppercase().take(TICKET_RETAILER_CODE_LENGTH),
        errorMessage = null
    )

    is TicketsStateAction.ScannedTicketAccepted -> state.copy(ticketCode = action.value, ticketRetailerCode = "", errorMessage = null)

    is TicketsStateAction.TicketCodeRejected -> state.copy(errorMessage = action.message)

    TicketsStateAction.TicketQueryStarted -> state.copy(isLoading = true, loaded = false, ticketResult = null, errorMessage = null)

    is TicketsStateAction.TicketResultReceived -> state.copy(
        bets = action.result.bets,
        ticketResult = action.result,
        isLoading = false,
        loaded = true
    )

    TicketsStateAction.TicketErrorDismissed -> state.copy(errorMessage = null)

    TicketsStateAction.TicketModalitiesLoading -> state.copy(isLoading = true, errorMessage = null)

    is TicketsStateAction.TicketModalitiesReceived -> state.copy(
        ticketModalities = action.modalities.filter { it.code.normalizedTicketCode() in legacyTicketModalityCodes },
        isLoading = false,
        loaded = true
    )

    is TicketsStateAction.TicketModalityToggled -> state.copy(
        selectedTicketModalityIds = if (action.id in
            state.selectedTicketModalityIds
        ) {
            state.selectedTicketModalityIds - action.id
        } else {
            state.selectedTicketModalityIds +
                action.id
        }
    )
}

private fun reduceTicketsPixState(state: TicketsUiState, action: TicketsPixAction): TicketsUiState = when (action) {
    TicketsStateAction.PixStatusLoading -> state.copy(checkingPixPurchase = true, errorMessage = null)

    is TicketsStateAction.PixStatusFailed -> reduceTicketsPixStatusFailure(state, action)

    is TicketsStateAction.PixStatusReceived -> reduceTicketsPixProgress(state, action)

    is TicketsStateAction.PixDetailsRefreshFailed -> reduceTicketsPixDetailsFailure(state, action)

    is TicketsStateAction.PixRedirectConsumed -> run reduce@{
        if (state.pixPurchaseRedirect !=
            action.expected
        ) {
            return@reduce state
        }
        state.copy(pixPurchaseRedirect = null, pixPurchasePaused = true)
    }

    is TicketsStateAction.PixNoticeConsumed -> run reduce@{
        if (state.pixPurchaseNotice !==
            action.expected
        ) {
            return@reduce state
        }
        state.copy(pixPurchaseNotice = null, pixPurchasePaused = action.pause)
    }

    TicketsStateAction.PixRedirectResumed -> state.copy(pixPurchasePaused = false)

    TicketsStateAction.PixPurchaseReset -> state.copy(
        checkingPixPurchase = false,
        pixPurchaseCompleted = false,
        pixPurchaseRedirect = null,
        pixPurchasePaused = false,
        pixPurchaseNotice = null
    )
}

private fun reduceTicketsPurchaseState(state: TicketsUiState, action: TicketsPurchaseAction): TicketsUiState = when (action) {
    is TicketsStateAction.PurchaseLoading -> state.copy(
        selectedPurchase = action.purchase,
        purchaseBetFilters = PurchaseBetFilters(),
        purchaseStatusDialog = null,
        favoriteSaved = false,
        isLoading = true,
        errorMessage = null
    )

    TicketsStateAction.PurchaseStatusDialogDismissed -> state.copy(purchaseStatusDialog = null)

    TicketsStateAction.DamMessageDismissed -> state.copy(damMessage = null)

    is TicketsStateAction.PurchaseDetailsReceived -> state.copy(
        selectedPurchase = action.purchase,
        bets = action.bets,
        isLoading = false,
        loaded = true,
        purchaseStatusDialog = action.dialog
    )

    is TicketsStateAction.DamBlocked -> state.copy(damMessage = action.message)
}

private fun reduceTicketsFavoriteState(state: TicketsUiState, action: TicketsFavoriteAction): TicketsUiState = when (action) {
    TicketsStateAction.PurchaseFavoriteOpened -> state.copy(
        purchaseStatusDialog = null,
        showFavoriteDialog = true,
        favoriteName = "",
        keepSurprises = true
    )

    TicketsStateAction.PurchaseFavoriteClosed -> state.copy(showFavoriteDialog = false, favoriteName = "")

    is TicketsStateAction.PurchaseFavoriteNameChanged -> state.copy(
        favoriteName = action.value.take(PURCHASE_FAVORITE_NAME_LENGTH),
        errorMessage = null
    )

    TicketsStateAction.PurchaseKeepSurprisesToggled -> state.copy(keepSurprises = !state.keepSurprises)

    is TicketsStateAction.PurchaseFavoriteNameRejected -> state.copy(errorMessage = action.message)

    TicketsStateAction.PurchaseFavoriteValidationStarted -> state.copy(isLoading = true, errorMessage = null)

    TicketsStateAction.PurchaseFavoriteOverwriteRequired -> state.copy(
        isLoading = false,
        favoriteOverwriteConfirmation = true
    )

    TicketsStateAction.PurchaseFavoriteOverwriteConfirmed -> state.copy(favoriteOverwriteConfirmation = false)

    TicketsStateAction.PurchaseFavoriteOverwriteCancelled -> state.copy(favoriteOverwriteConfirmation = false)

    TicketsStateAction.PurchaseFavoriteSavedDismissed -> state.copy(favoriteSaved = false)

    TicketsStateAction.LocalPurchaseFavoriteSaved -> state.copy(showFavoriteDialog = false, favoriteSaved = true)

    TicketsStateAction.PurchaseFavoriteSaveStarted -> state.copy(isLoading = true, errorMessage = null)

    TicketsStateAction.PurchaseFavoriteSaved -> state.copy(
        isLoading = false,
        showFavoriteDialog = false,
        favoriteSaved = true
    )
}

private const val PURCHASE_PAGE_SIZE = 10
private val legacyTicketModalityCodes = setOf(
    "QUINA",
    "LOTECA",
    "LOTOGOL",
    "LOTOFACIL",
    "LOTOMANIA",
    "MEGASENA",
    "TIMEMANIA",
    "DUPLASENA"
)
private fun String.normalizedTicketCode(): String = uppercase().filter(Char::isLetterOrDigit)

private fun initialBetFilters(
    current: BetFilters,
    configuration: br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration,
): BetFilters {
    val period = configuration.defaultPeriod?.takeIf { it in configuration.periods }
        ?: configuration.periods.firstOrNull()
    return current.copy(
        period = current.period ?: period,
        status = configuration.defaultStatus ?: current.status,
        ordering = configuration.defaultOrdering ?: current.ordering,
        betType = configuration.betTypes.firstOrNull()?.value ?: current.betType,
        contestType = configuration.contestTypes.firstOrNull()?.value ?: current.contestType
    )
}

private fun consumeTicketNavigation(state: TicketsUiState): TicketsUiState = if (!state.loaded || state.errorMessage != null ||
    (state.ticketResult == null && state.bets.isEmpty())
) {
    state
} else {
    state.copy(loaded = false)
}

private fun reduceTicketsPixStatusFailure(state: TicketsUiState, action: TicketsStateAction.PixStatusFailed): TicketsUiState = run reduce@{
    if (state.selectedPurchase?.id !=
        action.purchaseId
    ) {
        return@reduce state
    }
    state.copy(
        checkingPixPurchase = false,
        errorMessage = action.message
    )
}

private fun reduceTicketsPixProgress(state: TicketsUiState, action: TicketsStateAction.PixStatusReceived): TicketsUiState = run reduce@{
    if (state.selectedPurchase?.id !=
        action.purchaseId
    ) {
        return@reduce state
    }
    state.copy(
        checkingPixPurchase = false,
        pixPurchaseCompleted = action.progress.completed,
        pixPurchaseRedirect = action.progress.redirect,
        pixPurchaseNotice = action.notice
    )
}

private fun reduceTicketsPixDetailsFailure(state: TicketsUiState, action: TicketsStateAction.PixDetailsRefreshFailed): TicketsUiState =
    run reduce@{
        if (state.selectedPurchase?.id !=
            action.purchaseId
        ) {
            return@reduce state
        }
        state.copy(errorMessage = action.message)
    }

private const val TICKET_CODE_LENGTH = 23
private const val TICKET_RETAILER_CODE_LENGTH = 9
private const val PURCHASE_FAVORITE_NAME_LENGTH = 60
