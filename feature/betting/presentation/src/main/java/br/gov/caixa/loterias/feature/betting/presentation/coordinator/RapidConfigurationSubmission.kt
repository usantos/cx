package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.domain.usecase.SaveRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationAction
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class RapidConfigurationSubmission(
    private val mutableState: ReduxStateStore<RapidConfigurationUiState, RapidConfigurationAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val saveConfiguration: SaveRapidBetConfigurationUseCase,
    private val applyFailure: (AppError) -> Unit,
    private val isSessionCurrent: () -> Boolean = { true },
    private val applyServiceFailure: (ServiceFailure) -> Unit = {},
    private val canStartOperation: () -> Boolean,
) {

    fun save() {
        if (!isSessionCurrent()) return
        val current = mutableState.value
        if (!current.canSave) {
            mutableState.dispatch(RapidConfigurationAction.InvalidConfiguration())
            return
        }
        val selectedModalities = current.availableModalities.filter { it.id in current.selectedModalityIds }
        val configuration = RapidBetConfiguration(
            minimumAmount = current.minimumAmount,
            maximumAmount = current.maximumAmount,
            minimumPrize = current.minimumPrize,
            requiredNumbers = current.requiredNumbers,
            excludedNumbers = current.excludedNumbers,
            modalities = selectedModalities,
            favoriteBetIds = current.favoriteBetIds
        )
        scope.launch(dispatchers.io) {
            if (!isSessionCurrent() || !canStartOperation()) return@launch
            mutableState.dispatch(RapidConfigurationAction.SavingStarted)
            val result = saveConfiguration(configuration)
            if (!isSessionCurrent()) return@launch
            when (result) {
                is ServiceResult.Success -> mutableState.dispatch(RapidConfigurationAction.ConfigurationSaved)
                is ServiceResult.Failed -> applyFailure(result.error)
                is ServiceResult.Rejected -> applyServiceFailure(result.failure)
            }
        }
    }
}
