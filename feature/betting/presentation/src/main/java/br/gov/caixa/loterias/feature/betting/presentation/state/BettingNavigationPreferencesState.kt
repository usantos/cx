package br.gov.caixa.loterias.feature.betting.presentation.state

enum class NavigationPreferenceMode { RAPID, SHAKE }
enum class NavigationPreferenceFailure { READ, WRITE }
enum class RapidCompletion { POP_BACK, OPEN_LIST }

data class BettingNavigationPreferencesState(
    val loading: Boolean = false,
    val tutorialDismissed: Boolean? = null,
    val shakeEnabled: Boolean = false,
    val failure: NavigationPreferenceFailure? = null,
    val rapidCompletion: RapidCompletion? = null,
    val requestId: Long = 0,
)

sealed interface BettingNavigationPreferencesAction {
    data class Started(val requestId: Long) : BettingNavigationPreferencesAction
    data class TutorialLoaded(val requestId: Long, val dismissed: Boolean) : BettingNavigationPreferencesAction
    data class ShakeLoaded(val requestId: Long, val enabled: Boolean) : BettingNavigationPreferencesAction
    data class Failed(val requestId: Long, val failure: NavigationPreferenceFailure) : BettingNavigationPreferencesAction
    data class Completed(val requestId: Long, val completion: RapidCompletion) : BettingNavigationPreferencesAction
    data class Deactivated(val requestId: Long) : BettingNavigationPreferencesAction
    data object CompletionAcknowledged : BettingNavigationPreferencesAction
}
