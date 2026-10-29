package br.gov.caixa.loterias.feature.account.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.environment.ChangeEnvironment
import br.gov.caixa.loterias.feature.account.presentation.state.EnvironmentAction
import br.gov.caixa.loterias.feature.account.presentation.state.EnvironmentUiState
import br.gov.caixa.loterias.feature.account.presentation.state.reduceEnvironmentState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EnvironmentViewModel(
    private val handle: SavedStateHandle,
    private var selected: TestEnvironment,
    private var change: ChangeEnvironment,
) : ViewModel() {
    private val mutableState = reduxStateStore<EnvironmentUiState, EnvironmentAction>(
        EnvironmentUiState(
            pending = handle.get<String>("pending")?.let { name -> TestEnvironment.entries.firstOrNull { it.name == name } },
            persisted = handle["persisted"] ?: false
        ),
        ::reduceEnvironmentState
    ).also { addCloseable(it) }
    val state = mutableState.state
    private var operation: Job? = null
    private var generation = 0L

    fun bind(selected: TestEnvironment, operation: ChangeEnvironment = change) {
        this.selected = selected
        change = operation
        if (state.value.pending == selected) {
            generation++
            this.operation?.cancel()
            this.operation = null
            publish(EnvironmentAction.SelectionApplied)
        } else if (state.value.pending != null && !state.value.persisted && !state.value.failed && this.operation?.isActive != true) {
            save()
        }
    }

    fun select(environment: TestEnvironment) {
        if (environment == selected || state.value.pending != null) return
        publish(EnvironmentAction.Selected(environment))
        save()
    }
    fun retry() {
        if (state.value.failed && operation?.isActive != true) save()
    }
    fun unbind() {
        generation++
        operation?.cancel()
        // Mantém a tarefa até concluir sua gravação síncrona persistente antes de uma repetição restaurada.
        publish(EnvironmentAction.Unbound)
    }
    private fun save() {
        val pending = state.value.pending ?: return
        val current = ++generation
        val previous = operation
        publish(EnvironmentAction.SaveStarted)
        operation = viewModelScope.launch {
            try {
                previous?.join()
                val persisted = change(pending)
                if (current == generation && state.value.pending == pending) publish(EnvironmentAction.Saved(persisted))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (current == generation && state.value.pending == pending) publish(EnvironmentAction.SaveFailed)
            } finally {
                if (current == generation && state.value.pending == pending) publish(EnvironmentAction.SaveFinished)
            }
        }
    }
    private fun publish(action: EnvironmentAction) {
        mutableState.dispatch(action)
        val value = mutableState.value
        handle["pending"] = value.pending?.name
        handle["persisted"] = value.persisted
    }
}
