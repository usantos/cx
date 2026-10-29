package br.gov.caixa.loterias.feature.home.presentation.state

import br.gov.caixa.loterias.feature.home.domain.model.HomePreferencesSnapshot
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2ModalityOrder

internal sealed interface HomePreferencesStateAction {
    data class Loaded(val snapshot: HomePreferencesSnapshot) : HomePreferencesStateAction
    data class OrderSaved(val order: List<String>) : HomePreferencesStateAction
    data class ShakeToggled(val enabled: Boolean, val hidden: Boolean) : HomePreferencesStateAction
    data object TooltipConsumed : HomePreferencesStateAction
    data object TutorialCompleted : HomePreferencesStateAction
    data object ErrorDismissed : HomePreferencesStateAction
    data class SaveFailed(val message: String) : HomePreferencesStateAction
}

internal fun reduceHomePreferencesState(state: HomePreferencesUiState, action: HomePreferencesStateAction): HomePreferencesUiState =
    when (action) {
        is HomePreferencesStateAction.Loaded -> state.copy(
            loaded = true,
            cardOrder = HomeV2ModalityOrder.restore(action.snapshot.savedOrder, HomeV2ModalityOrder.DEFAULT_ORDER),
            shakeEnabled = action.snapshot.shakeEnabled,
            shakeWarningHidden = action.snapshot.shakeWarningHidden,
            shakeTooltipPending = action.snapshot.shakeTooltipPending,
            shakeTutorialCompleted = action.snapshot.shakeTutorialCompleted,
            errorMessage = null
        )

        is HomePreferencesStateAction.OrderSaved -> state.copy(cardOrder = action.order)

        is HomePreferencesStateAction.ShakeToggled -> state.copy(shakeEnabled = action.enabled, shakeWarningHidden = action.hidden)

        HomePreferencesStateAction.TooltipConsumed -> state.copy(shakeTooltipPending = false)

        HomePreferencesStateAction.TutorialCompleted -> state.copy(shakeTutorialCompleted = true, shakeTooltipPending = true)

        HomePreferencesStateAction.ErrorDismissed -> state.copy(errorMessage = null)

        is HomePreferencesStateAction.SaveFailed -> state.copy(errorMessage = action.message)
    }
