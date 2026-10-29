package br.gov.caixa.loterias.core.domain

import org.junit.Assert.*
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import br.gov.caixa.loterias.feature.home.domain.model.HomePreferencesSnapshot
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import org.junit.Test

class HomePreferencesUseCasesTest {
    @Test fun `snapshot and writes preserve order encodings and durable shake call order`() = kotlinx.coroutines.test.runTest {
        val calls = mutableListOf<String>()
        val layout = object : HomeLayoutRepository {
            override suspend fun loadOrder() = "QUINA,MEGA_SENA"
            override suspend fun saveOrder(order: String) {
                calls += order
            }
        }
        var enabled = false
        var hidden = false
        val shake = object : ShakeSettingsRepository {
            override suspend fun enabled() = enabled
            override suspend fun setEnabled(enabled: Boolean) {
                calls += "enabled:$enabled"
                toggle(enabled)
            }
            private fun toggle(value: Boolean) {
                enabled = value
            }
            override suspend fun warningHidden() = hidden
            override suspend fun setWarningHidden(hidden: Boolean) {
                calls += "hidden:$hidden"
                setHiddenValue(hidden)
            }
            private fun setHiddenValue(value: Boolean) {
                hidden = value
            }
            override suspend fun tooltipPending(ownerKey: String?) = true
            override suspend fun consumeTooltip(ownerKey: String?) {
                calls += "tooltip"
            }
            override suspend fun tutorialCompleted(ownerKey: String?) = false
            override suspend fun completeTutorial(ownerKey: String?) {
                calls += "tutorial"
            }
        }
        val useCases = HomePreferencesUseCases(layout, shake)
        assertEquals(HomePreferencesSnapshot("QUINA,MEGA_SENA", false, false, true, false), useCases.load())
        useCases.saveOrder(listOf("MEGA_SENA", "QUINA"))
        assertEquals(true to true, useCases.toggleShake(true))
        assertEquals(false to true, useCases.toggleShake())
        useCases.consumeShakeTooltip()
        useCases.completeShakeTutorial()
        assertEquals(listOf("MEGA_SENA,QUINA", "hidden:true", "enabled:true", "enabled:false", "tooltip", "tutorial"), calls)
    }
}
