package br.gov.caixa.loterias.feature.betting.domain.repository

interface ShakeSettingsRepository {
    suspend fun enabled(): Boolean
    suspend fun setEnabled(enabled: Boolean)
    suspend fun warningHidden(): Boolean
    suspend fun setWarningHidden(hidden: Boolean)
    suspend fun tutorialCompleted(ownerKey: String? = null): Boolean
    suspend fun completeTutorial(ownerKey: String? = null)
    suspend fun tooltipPending(ownerKey: String? = null): Boolean
    suspend fun consumeTooltip(ownerKey: String? = null)
}
