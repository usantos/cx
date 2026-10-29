package br.gov.caixa.loterias.feature.retail.presentation.state

internal fun reducePrizeReceiptDelivery(state: PrizeReceiptDeliveryState, action: PrizeReceiptDeliveryAction): PrizeReceiptDeliveryState =
    when (action) {
        is PrizeReceiptDeliveryAction.ReceiptReceived -> state.copy(
            pendingFileName = action.fileName,
            recovering = false,
            recoveryFailure = false
        )

        PrizeReceiptDeliveryAction.RecoveryRequested -> state.copy(recovering = true, recoveryFailure = false)

        PrizeReceiptDeliveryAction.RecoveryFailed -> state.copy(recovering = false, recoveryFailure = true)

        is PrizeReceiptDeliveryAction.WriteStarted -> state.copy(
            generation = action.claim,
            activeWrite = action.claim,
            saveFailure = null
        )

        is PrizeReceiptDeliveryAction.WriteSucceeded -> if (state.activeWrite == action.claim) {
            state.copy(
                pendingFileName = null,
                activeWrite = null,
                saved = state.saved + (action.fileName to action.destination),
                completion = PrizeReceiptCompletion(action.claim, action.destination)
            )
        } else {
            state
        }

        is PrizeReceiptDeliveryAction.WriteFailed -> if (state.activeWrite == action.claim) {
            state.copy(activeWrite = null, saveFailure = action.destination)
        } else {
            state
        }

        is PrizeReceiptDeliveryAction.CompletionClaimed -> if (state.completion == action.completion) {
            state.copy(completion = null)
        } else {
            state
        }

        is PrizeReceiptDeliveryAction.ViewerFailed -> state.copy(viewerFailure = action.destination)

        PrizeReceiptDeliveryAction.ViewerDismissed -> state.copy(viewerFailure = null)

        PrizeReceiptDeliveryAction.Cancelled -> state.copy(
            pendingFileName = null,
            generation = state.generation + 1,
            activeWrite = null,
            recovering = false,
            recoveryFailure = false,
            completion = null,
            saveFailure = null
        )
    }
