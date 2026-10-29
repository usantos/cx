package br.gov.caixa.loterias.feature.home.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBetCombosUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.home.presentation.mapper.toHomeLotteries
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class HomeLoadingCoordinator(
    private val mutableState: ReduxStateStore<HomeUiState, HomeStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getBettingParameters: GetBettingParametersUseCase,
    private val getHomeFeatureConfiguration: GetHomeFeatureConfigurationUseCase,
    private val getBetCombos: GetBetCombosUseCase,
    private val selectHomeResaleOffers: br.gov.caixa.loterias.feature.home.domain.usecase.SelectHomeResaleOffersUseCase,
) {
    private var parameterRequestRunning = false

    fun dismissParameterRetryMessage() = mutableState.dispatch(HomeStateAction.ParameterRetryDismissed)

    fun dismissParametersUnavailable() {
        if (!mutableState.value.loading) mutableState.dispatch(HomeStateAction.UnavailableParametersDismissed)
    }

    fun load(refreshConfiguration: Boolean = false) {
        if (parameterRequestRunning) return
        parameterRequestRunning = true
        val recovering = mutableState.value.parametersUnavailable
        mutableState.dispatch(HomeStateAction.ParametersLoading)
        scope.launch(dispatchers.io) {
            try {
                when (val result = getBettingParameters()) {
                    is AppResult.Success -> {
                        val featureResult = if (refreshConfiguration) {
                            getHomeFeatureConfiguration.refresh()
                        } else {
                            getHomeFeatureConfiguration()
                        }
                        val features = when (featureResult) {
                            is AppResult.Success -> featureResult.value
                            is AppResult.Failure -> HomeFeatureConfiguration()
                        }
                        val resaleOffers = selectHomeResaleOffers(result.value.availablePoolModalities, features.poolSalesEnabled)
                        if (result.value.games.isEmpty() && result.value.toHomeLotteries(
                                hasCombos = false,
                                specialContests = features.specialContests,
                                poolSalesEnabled = features.poolSalesEnabled,
                                resaleOffers = resaleOffers
                            ).isEmpty()
                        ) {
                            mutableState.dispatch(
                                HomeStateAction.EmptyParametersReceived(if (recovering) PARAMETER_RETRY_FAILURE else null)
                            )
                            return@launch
                        }
                        val combos = if (features.comboBettingEnabled) {
                            when (val comboResult = getBetCombos()) {
                                is AppResult.Success -> comboResult.value.filter { combo -> combo.id > 0 }
                                is AppResult.Failure -> emptyList()
                            }
                        } else {
                            emptyList()
                        }
                        mutableState.dispatch(HomeStateAction.ParametersReceived(result.value, features, combos, resaleOffers))
                    }

                    is AppResult.Failure -> mutableState.dispatch(
                        HomeStateAction.ParametersFailed(if (recovering) PARAMETER_RETRY_FAILURE else null)
                    )
                }
            } finally {
                parameterRequestRunning = false
                mutableState.dispatch(HomeStateAction.ParametersLoadingFinished)
            }
        }
    }
}

private val PARAMETER_RETRY_FAILURE get() = messageText("nao_foi_possivel_concluir_esta_o_3c7db3")
