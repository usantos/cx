package br.gov.caixa.loterias.feature.home.domain.usecase

import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import br.gov.caixa.loterias.feature.home.domain.model.HomePreferencesSnapshot
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository

/** Agrupa as gravações ordenadas de preferências sem expor o armazenamento à apresentação. */
class HomePreferencesUseCases(private val layout: HomeLayoutRepository, private val shake: ShakeSettingsRepository) {
    suspend fun load(ownerKey: String? = null) = HomePreferencesSnapshot(
        layout.loadOrder(),
        shake.enabled(),
        shake.warningHidden(),
        shake.tooltipPending(ownerKey),
        shake.tutorialCompleted(ownerKey)
    )
    suspend fun saveOrder(order: List<String>) = layout.saveOrder(order.joinToString(","))
    suspend fun toggleShake(hideWarning: Boolean? = null): Pair<Boolean, Boolean> {
        if (hideWarning != null) shake.setWarningHidden(hideWarning)
        shake.setEnabled(!shake.enabled())
        return shake.enabled() to shake.warningHidden()
    }
    suspend fun consumeShakeTooltip(ownerKey: String? = null) {
        shake.consumeTooltip(ownerKey)
    }
    suspend fun completeShakeTutorial(ownerKey: String? = null) {
        shake.completeTutorial(ownerKey)
    }
}
