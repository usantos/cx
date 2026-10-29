package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.mapper.formatRapidAmount
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationAction
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceRapidConfigurationState
import java.math.BigDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class RapidConfigurationAmounts(
    private val mutableState: ReduxStateStore<RapidConfigurationUiState, RapidConfigurationAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val getParameters: GetBettingParametersUseCase?,
    private val verifyAccess: VerifyAccountAccessUseCase?,
    private val isSessionCurrent: () -> Boolean = { true },
) {

    fun loadAmountLimits() = scope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        val parametersUseCase = getParameters ?: return@launch
        val accessUseCase = verifyAccess ?: return@launch
        if (mutableState.value.amountLimitsLoading) return@launch
        mutableState.dispatch(RapidConfigurationAction.AmountLimitsLoading)
        val parameters = parametersUseCase()
        if (!isSessionCurrent()) return@launch
        val access = accessUseCase()
        if (!isSessionCurrent()) return@launch
        if (parameters is AppResult.Success && access is AppResult.Success &&
            parameters.value.minimumCartAmount >= BigDecimal.ZERO && access.value.dailyLimit >= parameters.value.minimumCartAmount
        ) {
            mutableState.dispatch(RapidConfigurationAction.AmountLimitsLoaded(parameters.value.minimumCartAmount, access.value.dailyLimit))
        } else {
            mutableState.dispatch(RapidConfigurationAction.AmountLimitsFailed())
        }
    }

    fun setMinimumPrize(value: BigDecimal) = mutableState.dispatch(RapidConfigurationAction.MinimumPrizeChanged(value))

    fun setMinimumAmount(value: BigDecimal) = mutableState.dispatch(RapidConfigurationAction.MinimumAmountChanged(value))

    fun setMaximumAmount(value: BigDecimal) = mutableState.dispatch(RapidConfigurationAction.MaximumAmountChanged(value))

    fun setCustomAmount(value: BigDecimal?, minimum: Boolean): Boolean {
        val current = mutableState.value
        val floor = current.minimumAllowedAmount ?: return false
        val ceiling = current.maximumAllowedAmount ?: return false
        if (value == null || value < floor || value > ceiling) return false
        if (minimum) {
            setMinimumAmount(value)
        } else if (value <= floor.add(BigDecimal("5"))) {
            val maximum = floor.add(BigDecimal("5"))
            mutableState.dispatch(RapidConfigurationAction.MinimumIntervalCorrected(floor, maximum))
        } else {
            setMaximumAmount(value)
        }
        return true
    }
}
