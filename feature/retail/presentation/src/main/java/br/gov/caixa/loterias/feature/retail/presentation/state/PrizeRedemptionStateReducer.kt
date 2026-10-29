package br.gov.caixa.loterias.feature.retail.presentation.state

import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixKeySource
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRegistrationNotice

internal sealed interface PrizeRedemptionLoadingAction : PrizeRedemptionStateAction

internal sealed interface PrizeRedemptionReceiptAction : PrizeRedemptionStateAction

internal sealed interface PrizeRedemptionNoticeAction : PrizeRedemptionStateAction

internal sealed interface PrizeRedemptionInputAction : PrizeRedemptionChannelAction

internal sealed interface PrizeRedemptionChannelAction : PrizeRedemptionStateAction

internal sealed interface PrizeRedemptionSubmissionAction : PrizeRedemptionStateAction

internal sealed interface PrizeRedemptionNavigationAction : PrizeRedemptionStateAction

internal fun reducePrizeRedemptionState(state: PrizeRedemptionUiState, action: PrizeRedemptionStateAction): PrizeRedemptionUiState =
    when (action) {
        is PrizeRedemptionLoadingAction -> reducePrizeRedemptionLoadingState(state, action)
        is PrizeRedemptionReceiptAction -> reducePrizeRedemptionReceiptState(state, action)
        is PrizeRedemptionNoticeAction -> reducePrizeRedemptionNoticeState(state, action)
        is PrizeRedemptionChannelAction -> reducePrizeRedemptionChannelState(state, action)
        is PrizeRedemptionSubmissionAction -> reducePrizeRedemptionSubmissionState(state, action)
        is PrizeRedemptionNavigationAction -> reducePrizeRedemptionNavigationState(state, action)
    }

private fun reducePrizeRedemptionLoadingState(state: PrizeRedemptionUiState, action: PrizeRedemptionLoadingAction): PrizeRedemptionUiState =
    when (action) {
        PrizeRedemptionStateAction.DetailsLoading -> state.copy(loading = true, errorMessage = null)

        is PrizeRedemptionStateAction.DetailsReceived -> state.copy(
            stage = PrizeRedemptionStage.OPTIONS,
            details = action.details,
            loading = false
        )

        PrizeRedemptionStateAction.ContestLoading -> state.copy(currentContestLoading = true, currentContestError = null)

        is PrizeRedemptionStateAction.ContestReceived -> state.copy(
            currentContestLoading = false,
            currentContest = action.parameters.games.firstOrNull { game -> game.modality.id == action.modalityId }?.copy(
                hasNextContest = action.parameters.games.any { game ->
                    game.modality.id == action.modalityId &&
                        game.hasNextContest
                }
            )
        )

        is PrizeRedemptionStateAction.ContestFailed -> state.copy(
            currentContestLoading = false,
            currentContestError = action.message
        )

        PrizeRedemptionStateAction.BetEntryStarted -> state.copy(loading = true, errorMessage = null)

        is PrizeRedemptionStateAction.BetEntryPrepared -> state.copy(loading = false, betEntryDecision = action.decision)

        is PrizeRedemptionStateAction.TimerTicked -> state.copy(remainingSeconds = action.seconds)

        PrizeRedemptionStateAction.BetEntryConsumed -> state.copy(betEntryDecision = null)
    }

private fun reducePrizeRedemptionReceiptState(state: PrizeRedemptionUiState, action: PrizeRedemptionReceiptAction): PrizeRedemptionUiState =
    when (action) {
        PrizeRedemptionStateAction.ReceiptLoading -> state.copy(loading = true, errorMessage = null)
        PrizeRedemptionStateAction.ReceiptDelivered -> state.copy(loading = false)
        PrizeRedemptionStateAction.ReceiptRecoveryFailed -> state.copy(loading = false)
    }

private fun reducePrizeRedemptionNoticeState(state: PrizeRedemptionUiState, action: PrizeRedemptionNoticeAction): PrizeRedemptionUiState =
    when (action) {
        is PrizeRedemptionStateAction.NoticeDismissed -> reducePrizeRedemptionNoticeStateNoticeDismissed(state, action)

        is PrizeRedemptionStateAction.NoticeActionConsumed -> reducePrizeRedemptionNoticeStateNoticeActionConsumed(state, action)

        is PrizeRedemptionStateAction.RegistrationNoticeDismissed -> reducePrizeRedemptionNoticeStateRegistrationNoticeDismissed(
            state,
            action
        )

        is PrizeRedemptionStateAction.RegistrationNoticeConsumed -> reducePrizeRedemptionNoticeStateRegistrationNoticeConsumed(
            state,
            action
        )

        PrizeRedemptionStateAction.RegistrationFinished -> state.copy(registrationPending = false)

        is PrizeRedemptionStateAction.RedirectConsumed -> run reduce@{
            if (state.redirect !=
                action.expected
            ) {
                return@reduce state
            }
            state.copy(redirect = null, redirectPaused = true)
        }

        PrizeRedemptionStateAction.RedirectResumed -> state.copy(redirectPaused = false)

        is PrizeRedemptionStateAction.OfflineLoginBlocked -> state.copy(notice = action.notice, redirect = null, redirectPaused = false)
    }

private fun reducePrizeRedemptionChannelState(state: PrizeRedemptionUiState, action: PrizeRedemptionChannelAction): PrizeRedemptionUiState =
    when (action) {
        is PrizeRedemptionInputAction -> reducePrizeRedemptionInputState(state, action)

        PrizeRedemptionStateAction.AgencyCodeLoading -> state.copy(loading = true, errorMessage = null)

        is PrizeRedemptionStateAction.AgencyCodeReceived -> state.copy(
            stage = PrizeRedemptionStage.AGENCY,
            lotteryCode = action.code,
            loading = false
        )

        PrizeRedemptionStateAction.LotteryCodeLoading -> state.copy(
            stage = PrizeRedemptionStage.LOTTERY,
            loading = true,
            errorMessage = null,
            lotteryCode = null
        )

        is PrizeRedemptionStateAction.LotteryCodeReceived -> state.copy(
            lotteryCode = action.code,
            remainingSeconds = action.seconds,
            loading = false
        )

        is PrizeRedemptionStateAction.PixKeyRejected -> state.copy(errorMessage = action.message)

        PrizeRedemptionStateAction.PixConsultStarted -> state.copy(loading = true, errorMessage = null)

        is PrizeRedemptionStateAction.PixBeneficiaryReceived -> state.copy(
            stage = PrizeRedemptionStage.PIX_CONFIRM,
            pixBeneficiary = action.beneficiary,
            loading = false
        )

        PrizeRedemptionStateAction.PixCancellationStarted -> state.copy(loading = true, errorMessage = null)

        is PrizeRedemptionStateAction.EmailRejected -> state.copy(errorMessage = action.message)
    }

private fun reducePrizeRedemptionSubmissionState(
    state: PrizeRedemptionUiState,
    action: PrizeRedemptionSubmissionAction,
): PrizeRedemptionUiState = when (action) {
    PrizeRedemptionStateAction.SubmissionStarted -> state.copy(loading = true, errorMessage = null)

    is PrizeRedemptionStateAction.ConnectionFailed -> state.copy(loading = false, notice = action.notice)

    PrizeRedemptionStateAction.SubmissionUnchanged -> state.copy(loading = false)

    is PrizeRedemptionStateAction.Redeemed -> state.copy(
        stage = PrizeRedemptionStage.SUCCESS,
        selectedChannel = action.channel,
        loading = false,
        successNoticeMessage = action.message
    )

    is PrizeRedemptionStateAction.RegistrationRequired -> state.copy(
        loading = false,
        registrationNotice = PrizeRegistrationNotice(action.message, action.email)
    )

    is PrizeRedemptionStateAction.NoticeRequired -> state.copy(loading = false, notice = action.notice)

    is PrizeRedemptionStateAction.RedirectRequired -> state.copy(loading = false, redirect = action.destination)
}

private fun reducePrizeRedemptionNavigationState(
    state: PrizeRedemptionUiState,
    action: PrizeRedemptionNavigationAction,
): PrizeRedemptionUiState = when (action) {
    is PrizeRedemptionStateAction.ChannelSelected -> state.copy(selectedChannel = action.channel, errorMessage = null)

    PrizeRedemptionStateAction.PixEntryOpened -> state.copy(stage = PrizeRedemptionStage.PIX_ENTRY)

    PrizeRedemptionStateAction.MercadoPagoOpened -> state.copy(stage = PrizeRedemptionStage.MERCADO_PAGO)

    PrizeRedemptionStateAction.ErrorDismissed -> state.copy(errorMessage = null)

    PrizeRedemptionStateAction.SuccessNoticeDismissed -> state.copy(successNoticeMessage = null)

    PrizeRedemptionStateAction.ReturnedToOptions -> state.copy(
        stage = PrizeRedemptionStage.OPTIONS,
        selectedChannel = null,
        pixBeneficiary = null,
        loading = false,
        errorMessage = null
    )

    is PrizeRedemptionStateAction.OperationFailed -> state.copy(
        loading = false,
        errorMessage = action.message
    )
}

private fun reducePrizeRedemptionNoticeStateNoticeDismissed(
    state: PrizeRedemptionUiState,
    action: PrizeRedemptionStateAction.NoticeDismissed,
): PrizeRedemptionUiState = run reduce@{
    if (state.notice !==
        action.expected
    ) {
        return@reduce state
    }
    state.copy(notice = null)
}

private fun reducePrizeRedemptionNoticeStateNoticeActionConsumed(
    state: PrizeRedemptionUiState,
    action: PrizeRedemptionStateAction.NoticeActionConsumed,
): PrizeRedemptionUiState = run reduce@{
    if (state.notice !==
        action.expected
    ) {
        return@reduce state
    }
    state.copy(notice = null, redirectPaused = true)
}

private fun reducePrizeRedemptionNoticeStateRegistrationNoticeDismissed(
    state: PrizeRedemptionUiState,
    action: PrizeRedemptionStateAction.RegistrationNoticeDismissed,
): PrizeRedemptionUiState = run reduce@{
    if (state.registrationNotice !==
        action.expected
    ) {
        return@reduce state
    }
    state.copy(registrationNotice = null)
}

private fun reducePrizeRedemptionNoticeStateRegistrationNoticeConsumed(
    state: PrizeRedemptionUiState,
    action: PrizeRedemptionStateAction.RegistrationNoticeConsumed,
): PrizeRedemptionUiState = run reduce@{
    if (state.registrationNotice !==
        action.expected
    ) {
        return@reduce state
    }
    state.copy(registrationNotice = null, registrationPending = true)
}

private fun reducePrizeRedemptionInputState(state: PrizeRedemptionUiState, action: PrizeRedemptionInputAction): PrizeRedemptionUiState =
    when (action) {
        PrizeRedemptionStateAction.CpfPixKeySelected -> state.copy(pixKeySource = PrizePixKeySource.CPF, errorMessage = null)

        PrizeRedemptionStateAction.OtherPixKeySelected -> state.copy(pixKeySource = PrizePixKeySource.OTHER, errorMessage = null)

        is PrizeRedemptionStateAction.PixKeyChanged -> state.copy(pixKey = action.value.take(PRIZE_INPUT_MAX_LENGTH), errorMessage = null)

        PrizeRedemptionStateAction.PixOwnershipToggled -> state.copy(
            pixOwnershipAccepted = !state.pixOwnershipAccepted,
            errorMessage = null
        )

        is PrizeRedemptionStateAction.EmailChanged -> state.copy(email = action.value.take(PRIZE_INPUT_MAX_LENGTH), errorMessage = null)
    }

private const val PRIZE_INPUT_MAX_LENGTH = 120
