package br.gov.caixa.loterias.feature.onboarding.presentation.state

data class HostStartupState(val minimumSplashElapsed: Boolean = false,
    val securityReady: Boolean = false, val securityFailed: Boolean = false, val attempt: Int = 0,
)
