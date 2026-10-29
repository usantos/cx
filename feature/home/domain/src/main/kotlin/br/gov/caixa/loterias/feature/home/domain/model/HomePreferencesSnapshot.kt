package br.gov.caixa.loterias.feature.home.domain.model

data class HomePreferencesSnapshot(
    val savedOrder: String?, val shakeEnabled: Boolean, val shakeWarningHidden: Boolean,
    val shakeTooltipPending: Boolean, val shakeTutorialCompleted: Boolean,
)
