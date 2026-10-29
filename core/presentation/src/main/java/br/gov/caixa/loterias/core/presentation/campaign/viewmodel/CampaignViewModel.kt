package br.gov.caixa.loterias.core.presentation.campaign.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaignSettings
import br.gov.caixa.loterias.core.domain.configuration.usecase.CampaignConfigurationUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CampaignViewModel(private val configuration: CampaignConfigurationUseCases, private val dispatchers: CoroutineDispatchers) :
    ViewModel() {
    val state = configuration.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(0), CommemorativeCampaignSettings())
    private var active = false
    private var initiallyLoaded = false
    private var refresh: Job? = null
    fun resumed(active: Boolean) {
        if (this.active == active) return
        this.active = active
        refresh?.cancel()
        refresh = if (active) {
            viewModelScope.launch(dispatchers.io) {
                if (initiallyLoaded) {
                    configuration.refresh()
                } else {
                    configuration.load()
                    initiallyLoaded = true
                }
            }
        } else {
            null
        }
    }
}
