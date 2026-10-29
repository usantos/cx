package br.gov.caixa.loterias.feature.betting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetShakeEnabledUseCase
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingNavigationPreferencesAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingNavigationPreferencesState
import br.gov.caixa.loterias.feature.betting.presentation.state.NavigationPreferenceFailure
import br.gov.caixa.loterias.feature.betting.presentation.state.NavigationPreferenceMode
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidCompletion
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceBettingNavigationPreferencesState
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Controla as preferências necessárias para entrar no destino e concluir seu tutorial. */
class BettingNavigationPreferencesViewModel(
    private val mode: NavigationPreferenceMode,
    private val readTutorial: GetBettingTutorialDismissedUseCase,
    private val writeTutorial: SetBettingTutorialDismissedUseCase,
    private val readShake: GetShakeEnabledUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val isSessionCurrent: () -> Boolean,
) : ViewModel() {
    private val mutableState = reduxStateStore<BettingNavigationPreferencesState, BettingNavigationPreferencesAction>(
        BettingNavigationPreferencesState(),
        ::reduceBettingNavigationPreferencesState
    ).also { addCloseable(it) }
    val state = mutableState.state
    private var request: Job? = null
    private var generation = 0L
    private var active = true
    private var pendingReturnToPrevious: Boolean? = null

    init {
        load()
    }

    fun activate() {
        if (active) return
        active = true
        if (state.value.failure == null &&
            (mode == NavigationPreferenceMode.SHAKE || state.value.tutorialDismissed == null)
        ) {
            load()
        }
    }

    fun deactivate() {
        active = false
        generation++
        request?.cancel()
        mutableState.dispatch(BettingNavigationPreferencesAction.Deactivated(generation))
    }

    fun retry() {
        val pending = pendingReturnToPrevious
        if (pending != null) completeRapidConfiguration(pending) else load()
    }

    fun completeRapidConfiguration(returnToPrevious: Boolean) {
        if (mode != NavigationPreferenceMode.RAPID || !canStart() || state.value.rapidCompletion != null) return
        pendingReturnToPrevious = returnToPrevious
        val id = begin()
        request = viewModelScope.launch {
            val previous = withContext(dispatchers.io) { appResultOf { readTutorial(BettingTutorial.RAPID) } }
            if (!isCurrent(id)) return@launch
            val dismissed = when (previous) {
                is AppResult.Success -> previous.value

                is AppResult.Failure -> {
                    mutableState.dispatch(BettingNavigationPreferencesAction.Failed(id, NavigationPreferenceFailure.READ))
                    return@launch
                }
            }
            val written = withContext(dispatchers.io) { appResultOf { writeTutorial(BettingTutorial.RAPID, true) } }
            if (!isCurrent(id)) return@launch
            when (written) {
                is AppResult.Success -> {
                    pendingReturnToPrevious = null
                    mutableState.dispatch(
                        BettingNavigationPreferencesAction.Completed(
                            id,
                            if (returnToPrevious && dismissed) RapidCompletion.POP_BACK else RapidCompletion.OPEN_LIST
                        )
                    )
                }

                is AppResult.Failure -> mutableState.dispatch(
                    BettingNavigationPreferencesAction.Failed(id, NavigationPreferenceFailure.WRITE)
                )
            }
        }
    }

    fun acknowledgeCompletion() = mutableState.dispatch(BettingNavigationPreferencesAction.CompletionAcknowledged)

    private fun load() {
        if (!canStart()) return
        val id = begin()
        request = viewModelScope.launch {
            val result = withContext(dispatchers.io) {
                appResultOf {
                    when (mode) {
                        NavigationPreferenceMode.RAPID -> BettingNavigationPreferencesAction.TutorialLoaded(
                            id,
                            readTutorial(BettingTutorial.RAPID)
                        )

                        NavigationPreferenceMode.SHAKE -> BettingNavigationPreferencesAction.ShakeLoaded(id, readShake())
                    }
                }
            }
            if (!isCurrent(id)) return@launch
            when (result) {
                is AppResult.Success -> mutableState.dispatch(result.value)

                is AppResult.Failure -> mutableState.dispatch(
                    BettingNavigationPreferencesAction.Failed(id, NavigationPreferenceFailure.READ)
                )
            }
        }
    }

    private fun canStart(): Boolean = active && isSessionCurrent() && request?.isActive != true
    private fun isCurrent(id: Long): Boolean = active && id == generation && isSessionCurrent()
    private fun begin(): Long = (++generation).also { mutableState.dispatch(BettingNavigationPreferencesAction.Started(it)) }

    override fun onCleared() {
        deactivate()
    }
}
