package br.gov.caixa.loterias.feature.payment.presentation.state

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome

private const val CARD_NUMBER_LENGTH = 16
private const val HOLDER_NAME_LENGTH = 40
private const val EXPIRATION_YEAR_LENGTH = 4

internal fun reducePaymentCardState(state: PaymentCardUiState, action: PaymentCardAction): PaymentCardUiState = when (action) {
    is PaymentCardAction.NumberChanged,
    is PaymentCardAction.HolderNameChanged,
    is PaymentCardAction.MonthChanged,
    is PaymentCardAction.YearChanged,
    PaymentCardAction.SaveToggled,
    PaymentCardAction.CvvEntryStarted,
    is PaymentCardAction.InvalidCardEntered,
    is PaymentCardAction.CvvChanged,
    PaymentCardAction.ConfirmationStarted,
    is PaymentCardAction.InvalidCvvEntered,
    -> reducePaymentCardForm(state, action)

    PaymentCardAction.ProcessingStarted,
    PaymentCardAction.PurchaseTrackingStarted,
    PaymentCardAction.PurchaseCompleted,
    PaymentCardAction.FormStarted,
    is PaymentCardAction.SavedCardPreviewed,
    is PaymentCardAction.SavedCardMissing,
    is PaymentCardAction.SavedCardLoaded,
    PaymentCardAction.ErrorDismissed,
    is PaymentCardAction.Failed,
    -> reducePaymentCardPurchase(state, action)

    PaymentCardAction.RegistrationFinished,
    is PaymentCardAction.NoticeConsumed,
    is PaymentCardAction.NoticeRequired,
    is PaymentCardAction.RedirectConsumed,
    PaymentCardAction.RedirectResumed,
    is PaymentCardAction.PurchaseReviewRequired,
    is PaymentCardAction.RedirectRequired,
    -> reducePaymentCardNavigation(state, action)
}

private fun reducePaymentCardForm(state: PaymentCardUiState, action: PaymentCardAction): PaymentCardUiState = when (action) {
    is PaymentCardAction.NumberChanged -> state.copy(number = action.value.filter(Char::isDigit).take(CARD_NUMBER_LENGTH), cvv = "")
    is PaymentCardAction.HolderNameChanged -> state.copy(holderName = action.value.uppercase().take(HOLDER_NAME_LENGTH))
    is PaymentCardAction.MonthChanged -> state.copy(month = action.value.filter(Char::isDigit).take(2))
    is PaymentCardAction.YearChanged -> state.copy(year = action.value.filter(Char::isDigit).take(EXPIRATION_YEAR_LENGTH))
    PaymentCardAction.SaveToggled -> state.copy(save = !state.save)
    PaymentCardAction.CvvEntryStarted -> state.copy(stage = PaymentCardStage.CVV, errorMessage = null)
    is PaymentCardAction.InvalidCardEntered -> state.copy(errorMessage = action.message)
    is PaymentCardAction.CvvChanged -> state.copy(cvv = action.value.filter(Char::isDigit).take(state.cvvLength))
    PaymentCardAction.ConfirmationStarted -> state.copy(stage = PaymentCardStage.CONFIRM, errorMessage = null)
    is PaymentCardAction.InvalidCvvEntered -> state.copy(errorMessage = action.message)
    else -> state
}

private fun reducePaymentCardPurchase(state: PaymentCardUiState, action: PaymentCardAction): PaymentCardUiState = when (action) {
    PaymentCardAction.ProcessingStarted -> state.copy(stage = PaymentCardStage.PROCESSING, errorMessage = null)

    PaymentCardAction.PurchaseTrackingStarted -> state.copy(trackingPurchase = true, cvv = "")

    PaymentCardAction.PurchaseCompleted -> state.copy(stage = PaymentCardStage.SUCCESS)

    PaymentCardAction.FormStarted -> state.copy(stage = PaymentCardStage.FORM, errorMessage = null)

    is PaymentCardAction.SavedCardPreviewed -> state.copy(stage = PaymentCardStage.PREVIEW, savedCard = action.card, errorMessage = null)

    is PaymentCardAction.SavedCardMissing -> state.copy(
        stage = PaymentCardStage.FORM,
        errorMessage = action.message
    )

    is PaymentCardAction.SavedCardLoaded -> state.copy(stage = action.initialStage, savedCard = action.card, errorMessage = null)

    PaymentCardAction.ErrorDismissed -> state.copy(errorMessage = null)

    is PaymentCardAction.Failed -> failedPaymentCardState(state, action.error, action.messages)

    else -> state
}

private fun reducePaymentCardNavigation(state: PaymentCardUiState, action: PaymentCardAction): PaymentCardUiState = when (action) {
    PaymentCardAction.RegistrationFinished -> state.copy(registrationPending = false)

    is PaymentCardAction.NoticeConsumed -> state.copy(
        notice = null,
        stage = if (!action.tracking) PaymentCardStage.CONFIRM else PaymentCardStage.FAILURE,
        errorMessage = if (action.tracking) action.message else null,
        redirectPaused = action.moreBets,
        registrationPending = action.registration
    )

    is PaymentCardAction.NoticeRequired -> state.copy(
        stage = PaymentCardStage.NOTICE_REQUIRED,
        notice = action.presentation,
        errorMessage = null
    )

    is PaymentCardAction.RedirectConsumed -> when {
        action.tracking -> state.copy(
            stage = PaymentCardStage.FAILURE,
            redirect = null,
            redirectPurchaseId = null,
            redirectPaused = true
        )

        state.redirectPurchaseId != null -> state.copy(
            stage = PaymentCardStage.REVIEW_REQUIRED,
            redirect = null,
            redirectPurchaseId = null,
            redirectPaused = true,
            purchaseReview = CardSubmissionOutcome.ReviewRequired(state.redirectPurchaseId),
            cvv = ""
        )

        else -> state.copy(stage = PaymentCardStage.CONFIRM, redirect = null, redirectPurchaseId = null, redirectPaused = true)
    }

    PaymentCardAction.RedirectResumed -> state.copy(redirectPaused = false)

    is PaymentCardAction.PurchaseReviewRequired -> state.copy(
        stage = PaymentCardStage.REVIEW_REQUIRED,
        purchaseReview = action.outcome,
        cvv = "",
        errorMessage = null
    )

    is PaymentCardAction.RedirectRequired -> state.copy(
        stage = PaymentCardStage.REDIRECT_REQUIRED,
        redirect = action.destination,
        redirectPurchaseId = action.purchaseId,
        errorMessage = null
    )

    else -> state
}

private fun failedPaymentCardState(state: PaymentCardUiState, error: AppError, messages: PaymentCardFailureMessages): PaymentCardUiState =
    state.copy(
        stage = PaymentCardStage.FAILURE,
        errorMessage = when (error) {
            AppError.Network -> if (state.trackingPurchase) {
                messages.networkTracking
            } else {
                messages.networkPayment
            }

            is AppError.Validation -> error.reason.ifBlank { messages.validation }

            AppError.Unexpected -> if (state.trackingPurchase) {
                messages.unexpectedTracking
            } else {
                messages.unexpectedPayment
            }
        }
    )
