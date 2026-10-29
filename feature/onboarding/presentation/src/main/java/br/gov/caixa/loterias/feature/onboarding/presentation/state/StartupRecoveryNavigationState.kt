package br.gov.caixa.loterias.feature.onboarding.presentation.state

enum class StartupRecoveryTarget { INTRODUCTION, LOGIN }

data class StartupRecoveryNavigationState(
    val loading: Boolean = false,
    val storageFailed: Boolean = false,
    val target: StartupRecoveryTarget? = null,
    val requestId: Long = 0,
)

sealed interface StartupRecoveryNavigationAction {
    data class Started(val requestId: Long) : StartupRecoveryNavigationAction
    data class Recovered(val requestId: Long, val target: StartupRecoveryTarget) : StartupRecoveryNavigationAction
    data class Failed(val requestId: Long) : StartupRecoveryNavigationAction
    data class Deactivated(val requestId: Long) : StartupRecoveryNavigationAction
    data object TargetAcknowledged : StartupRecoveryNavigationAction
}
