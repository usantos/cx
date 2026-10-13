package br.gov.caixa.loterias.core.domain.betting

interface ShakeSettingsRepository {
    fun enabled(): Boolean
    fun setEnabled(enabled: Boolean)
    fun warningHidden(): Boolean
    fun setWarningHidden(hidden: Boolean)
    fun tutorialCompleted(): Boolean
    fun completeTutorial()
    fun tooltipPending(): Boolean
    fun consumeTooltip()
}
