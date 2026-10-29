package br.gov.caixa.loterias.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2ModalityOrder
import br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState
import br.gov.caixa.loterias.feature.home.presentation.state.reduceHomePreferencesState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

/** Serializa o trabalho de armazenamento fora da thread da interface; o estado existente da Home permanece independente. */
class HomePreferencesViewModel(
    private val preferences: HomePreferencesUseCases,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    private val ownerKey: String? = null,
) : ViewModel() {
    private val mutableState = reduxStateStore<HomePreferencesUiState, HomePreferencesStateAction>(
        HomePreferencesUiState(),
        ::reduceHomePreferencesState
    ).also { addCloseable(it) }
    val state = mutableState.state
    private var pendingCommand: Job? = null
    init {
        load()
    }

    fun load() = command {
        val snapshot = preferences.load(ownerKey)
        currentCoroutineContext().ensureActive()
        mutableState.dispatch(HomePreferencesStateAction.Loaded(snapshot))
    }

    fun moveModality(from: String, to: String) = command {
        val updated = HomeV2ModalityOrder.move(state.value.cardOrder, from, to)
        if (updated != state.value.cardOrder) {
            preferences.saveOrder(updated)
            currentCoroutineContext().ensureActive()
            mutableState.dispatch(HomePreferencesStateAction.OrderSaved(updated))
        }
    }

    fun toggleShake(hideWarning: Boolean? = null) = command {
        val (enabled, hidden) = preferences.toggleShake(hideWarning)
        currentCoroutineContext().ensureActive()
        mutableState.dispatch(HomePreferencesStateAction.ShakeToggled(enabled, hidden))
    }

    fun consumeShakeTooltip() = command {
        preferences.consumeShakeTooltip(ownerKey)
        currentCoroutineContext().ensureActive()
        mutableState.dispatch(HomePreferencesStateAction.TooltipConsumed)
    }

    fun completeShakeTutorial() = command {
        preferences.completeShakeTutorial(ownerKey)
        currentCoroutineContext().ensureActive()
        mutableState.dispatch(HomePreferencesStateAction.TutorialCompleted)
    }

    fun dismissError() {
        mutableState.dispatch(HomePreferencesStateAction.ErrorDismissed)
    }

    @Synchronized
    private fun command(action: suspend () -> Unit): Job {
        val previous = pendingCommand
        return viewModelScope.launch(dispatchers.io) {
            previous?.join()
            currentCoroutineContext().ensureActive()
            try {
                action()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                currentCoroutineContext().ensureActive()
                mutableState.dispatch(HomePreferencesStateAction.SaveFailed(messageText("nao_foi_possivel_atualizar_as_pr_0951be")))
            }
        }.also { pendingCommand = it }
    }

    companion object {
        val DEFAULT_ORDER = HomeV2ModalityOrder.DEFAULT_ORDER
    }
}
