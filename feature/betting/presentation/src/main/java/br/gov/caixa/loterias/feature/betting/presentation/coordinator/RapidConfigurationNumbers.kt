package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationAction
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidConfigurationUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceRapidConfigurationState

internal class RapidConfigurationNumbers(private val mutableState: ReduxStateStore<RapidConfigurationUiState, RapidConfigurationAction>) {

    fun toggleRequiredNumber(number: Int) = toggleNumber(number, required = true)

    fun toggleExcludedNumber(number: Int) = toggleNumber(number, required = false)

    fun clearRequiredNumbers() = mutableState.dispatch(RapidConfigurationAction.RequiredNumbersCleared)

    fun clearExcludedNumbers() = mutableState.dispatch(RapidConfigurationAction.ExcludedNumbersCleared)

    private fun toggleNumber(number: Int, required: Boolean) =
        mutableState.dispatch(RapidConfigurationAction.NumberToggled(number, required))
}
