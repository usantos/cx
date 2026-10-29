package br.gov.caixa.loterias.feature.betting.presentation.coordinator

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareHomeBettingActionUseCase
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidEntryAction
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceRapidEntryState

/** A autorização de entrada pertence ao mesmo vínculo de sessão da tela privada de Rapidão que ela habilita. */
internal class RapidEntryCoordinator(
    private val prepareEntry: PrepareHomeBettingActionUseCase,
    private val isAuthenticated: Boolean,
    showList: Boolean,
    private val isSessionCurrent: () -> Boolean = { true },
) {
    private val mutableState = reduxStateStore<AppResult<HomeBettingDecision>?, RapidEntryAction>(
        if (isAuthenticated) {
            null
        } else {
            AppResult.Success(
                HomeBettingDecision.Ready(if (showList) HomeBettingTarget.RAPID_LIST else HomeBettingTarget.RAPID_CONFIGURATION)
            )
        },
        ::reduceRapidEntryState
    )
    val state = mutableState.state

    fun close() = mutableState.close()

    suspend fun prepare() {
        if (!isAuthenticated || !isSessionCurrent()) return
        val result = prepareEntry(HomeBettingAction.RAPID)
        if (isSessionCurrent()) mutableState.dispatch(RapidEntryAction.Prepared(result))
    }
}
