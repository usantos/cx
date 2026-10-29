package br.gov.caixa.loterias.feature.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.domain.usecase.NotificationInboxActions
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.CentralNotificationsState
import br.gov.caixa.loterias.feature.notifications.presentation.state.InboxConfirmation
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationText
import br.gov.caixa.loterias.feature.notifications.presentation.state.reduceCentralNotificationsState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CentralNotificationsViewModel(
    private val actions: NotificationInboxActions,
    private val identities: AuthenticatedSessionIdentityProvider,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = reduxStateStore<CentralNotificationsState, CentralNotificationsAction>(
        CentralNotificationsState(),
        ::reduceCentralNotificationsState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    private var identity: AuthenticatedSessionIdentity? = null
    private var confirmationIdentity: AuthenticatedSessionIdentity? = null
    private var job: Job? = null
    private var resumed = false

    init {
        load()
    }

    fun onResume() {
        if (resumed && !state.value.loading) load()
        resumed = true
    }

    fun load() {
        job?.cancel()
        val captured = identities.captureIdentity()
        if (captured !== identity) mutableState.dispatch(CentralNotificationsAction.IdentityChanged)
        identity = captured
        confirmationIdentity = null
        if (captured == null) {
            clearPrivateState()
            return
        }
        mutableState.dispatch(CentralNotificationsAction.LoadStarted)
        job = viewModelScope.launch(dispatchers.main) { refresh(captured) }
    }

    private fun current(captured: AuthenticatedSessionIdentity): Boolean {
        if (captured !== identity) return false
        if (identities.isCurrent(captured)) return true
        clearPrivateState()
        return false
    }

    private fun clearPrivateState() {
        identity = null
        confirmationIdentity = null
        mutableState.dispatch(CentralNotificationsAction.PrivateStateCleared)
    }

    private suspend fun refresh(captured: AuthenticatedSessionIdentity, mutationFailed: Boolean = false) {
        try {
            if (!current(captured)) return
            val inbox = withContext(dispatchers.io) { actions.load(captured) }
            if (!current(captured)) return
            mutableState.dispatch(CentralNotificationsAction.Loaded(inbox, if (mutationFailed) OPERATION_ERROR else null))
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            if (current(captured)) mutableState.dispatch(CentralNotificationsAction.LoadFailed(LOAD_ERROR))
        }
    }

    fun selectUnread(value: Boolean) {
        mutableState.dispatch(CentralNotificationsAction.UnreadSelected(value))
    }
    fun requestMarkAll() {
        if (state.value.canMarkAll) request(InboxConfirmation.MarkAll)
    }
    fun requestClear() {
        if (state.value.canDeleteAll) request(InboxConfirmation.Clear)
    }
    fun requestRemove(notification: InboxNotification) {
        if (!state.value.loading && state.value.all.any { it.id == notification.id }) request(InboxConfirmation.Remove(notification))
    }
    private fun request(confirmation: InboxConfirmation) {
        val captured = identity ?: return
        if (!current(captured)) return
        confirmationIdentity = captured
        mutableState.dispatch(CentralNotificationsAction.ConfirmationRequested(confirmation))
    }
    fun dismissDialog() {
        confirmationIdentity = null
        mutableState.dispatch(CentralNotificationsAction.DialogDismissed)
    }

    fun confirm() {
        val pending = state.value.pending ?: return
        val captured = confirmationIdentity ?: return
        if (state.value.loading || !current(captured)) return
        val unreadIds = state.value.unread.map { it.id }.distinct()
        dismissDialog()
        mutableState.dispatch(CentralNotificationsAction.MutationStarted)
        job = viewModelScope.launch(dispatchers.main) {
            val failed = applyConfirmation(pending, captured, unreadIds)
            if (current(captured)) refresh(captured, failed)
        }
    }

    private suspend fun applyConfirmation(
        pending: InboxConfirmation,
        captured: AuthenticatedSessionIdentity,
        unreadIds: List<Long>,
    ): Boolean = try {
        when (pending) {
            InboxConfirmation.MarkAll -> markUnread(captured, unreadIds)

            InboxConfirmation.Clear -> {
                withContext(dispatchers.io) { actions.clear(captured) }
                false
            }

            is InboxConfirmation.Remove -> {
                withContext(dispatchers.io) { actions.remove(captured, pending.notification.id) }
                false
            }
        }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        true
    }

    private suspend fun markUnread(captured: AuthenticatedSessionIdentity, unreadIds: List<Long>): Boolean {
        var failed = false
        for (id in unreadIds) {
            if (!current(captured)) return failed
            try {
                withContext(dispatchers.io) { actions.markRead(captured, id) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                failed = true
            }
        }
        return failed
    }

    private companion object {
        val LOAD_ERROR = NotificationText.Resource(R.string.notifications_nao_foi_possivel_carregar_as_notificacoes_tente_novamente)
        val OPERATION_ERROR = NotificationText.Resource(
            R.string.notifications_nao_foi_possivel_concluir_todas_as_alteracoes_verifique_as_notifi
        )
    }
}
