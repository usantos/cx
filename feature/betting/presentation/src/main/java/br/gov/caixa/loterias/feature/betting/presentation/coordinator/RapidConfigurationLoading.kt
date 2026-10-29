package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetRapidBetConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationAction
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceRapidConfigurationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class RapidConfigurationLoading(
    private val mutableState: ReduxStateStore<RapidConfigurationUiState, RapidConfigurationAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getConfiguration: GetRapidBetConfigurationUseCase,
    private val getModalities: GetLotteryModalitiesUseCase,
    private val isSessionCurrent: () -> Boolean = { true },
    private val applyServiceFailure: (ServiceFailure) -> Unit = {},
) {

    fun load() = scope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        mutableState.dispatch(RapidConfigurationAction.ConfigurationLoading)
        val configurationResult = getConfiguration()
        if (!isSessionCurrent()) return@launch
        if (configurationResult is ServiceResult.Rejected) {
            applyServiceFailure(configurationResult.failure)
            return@launch
        }
        val modalitiesResult = getModalities()
        if (!isSessionCurrent()) return@launch
        if (configurationResult is ServiceResult.Failed) {
            applyFailure(configurationResult.error)
            return@launch
        }
        if (modalitiesResult is AppResult.Failure) {
            applyFailure(modalitiesResult.error)
            return@launch
        }
        val configuration = (configurationResult as ServiceResult.Success).value
        val available = (modalitiesResult as AppResult.Success).value.filter {
            it.id > 0 && it.id !in setOf(19, 14) &&
                !it.name.equals("Federal", ignoreCase = true) && !it.code.equals("FEDERAL", ignoreCase = true)
        }
        val selected = configuration.modalities.map(LotteryModality::id).toSet()
            .intersect(available.map(LotteryModality::id).toSet())
        mutableState.dispatch(RapidConfigurationAction.ConfigurationLoaded(configuration, available, selected))
    }

    fun applyFailure(error: AppError) = mutableState.dispatch(RapidConfigurationAction.Failed(error))
}
