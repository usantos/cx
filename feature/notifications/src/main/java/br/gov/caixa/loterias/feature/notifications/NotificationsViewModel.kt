package br.gov.caixa.loterias.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.notifications.AppNotification
import br.gov.caixa.loterias.core.domain.notifications.DeleteAllNotificationsUseCase
import br.gov.caixa.loterias.core.domain.notifications.DeleteNotificationUseCase
import br.gov.caixa.loterias.core.domain.notifications.GetNotificationPreferencesUseCase
import br.gov.caixa.loterias.core.domain.notifications.GetNotificationsUseCase
import br.gov.caixa.loterias.core.domain.notifications.MarkAllNotificationsReadUseCase
import br.gov.caixa.loterias.core.domain.notifications.MarkNotificationReadUseCase
import br.gov.caixa.loterias.core.domain.notifications.NotificationCategory
import br.gov.caixa.loterias.core.domain.notifications.NotificationPreference
import br.gov.caixa.loterias.core.domain.notifications.SetNotificationPreferenceUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NotificationFilter { ALL, UNREAD }

sealed interface NotificationConfirmation {
    data object DeleteAll : NotificationConfirmation
    data object MarkAllRead : NotificationConfirmation
    data class DeleteOne(val id: Long) : NotificationConfirmation
    data class MarkOneRead(val id: Long) : NotificationConfirmation
}

sealed interface NotificationEffect {
    data class Share(val title: String, val content: String) : NotificationEffect
}

data class NotificationsUiState(
    val loading: Boolean = true,
    val messages: List<AppNotification> = emptyList(),
    val preferences: List<NotificationPreference> = emptyList(),
    val query: String = "",
    val filter: NotificationFilter = NotificationFilter.ALL,
    val expandedMessageId: Long? = null,
    val readWhileVisible: Set<Long> = emptySet(),
    val projectionRevision: Long = 0,
    val pendingConfirmation: NotificationConfirmation? = null,
    val categoryActionMessage: String? = null,
    val errorMessage: String? = null,
) {
    val visibleMessages: List<AppNotification>
        get() = filterMessages(messages, query, filter == NotificationFilter.UNREAD, readWhileVisible)

    val emptyMessage: String
        get() = if (filter == NotificationFilter.UNREAD) {
            "Você não possui mensagens não lidas"
        } else {
            "Você não possui mensagens"
        }
}

class NotificationsViewModel(
    private val getNotifications: GetNotificationsUseCase,
    private val markRead: MarkNotificationReadUseCase,
    private val deleteNotification: DeleteNotificationUseCase,
    private val deleteAll: DeleteAllNotificationsUseCase,
    private val markAllRead: MarkAllNotificationsReadUseCase,
    private val getPreferences: GetNotificationPreferencesUseCase,
    private val setPreference: SetNotificationPreferenceUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val mutableState = MutableStateFlow(NotificationsUiState())
    val state = mutableState.asStateFlow()

    private val mutableEffects = MutableSharedFlow<NotificationEffect>()
    val effects = mutableEffects.asSharedFlow()

    init {
        load()
    }

    fun load() = viewModelScope.launch(dispatchers.io) {
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        val messages = getNotifications()
        val preferences = getPreferences()
        mutableState.update { current ->
            current.copy(
                loading = false,
                messages = (messages as? AppResult.Success)?.value ?: current.messages,
                preferences = (preferences as? AppResult.Success)?.value ?: current.preferences,
                errorMessage = listOf(messages, preferences).firstNotNullOfOrNull { result ->
                    (result as? AppResult.Failure)?.error?.userMessage()
                },
            )
        }
    }

    fun updateQuery(query: String) = mutableState.update {
        it.copy(query = query, readWhileVisible = if (query.isEmpty()) emptySet() else it.readWhileVisible,
            projectionRevision = it.projectionRevision + 1)
    }

    fun selectFilter(filter: NotificationFilter) = mutableState.update {
        it.copy(filter = filter, expandedMessageId = null, readWhileVisible = emptySet(),
            projectionRevision = it.projectionRevision + 1)
    }

    fun toggleExpanded(message: AppNotification) {
        val current = state.value.visibleMessages.firstOrNull { it.id == message.id } ?: return
        val willExpand = state.value.expandedMessageId != message.id
        mutableState.update { it.copy(expandedMessageId = if (willExpand) message.id else null) }
        if (willExpand && !current.isRead) markOneAsRead(message.id, keepExpanded = true)
    }

    fun requestDeleteAll() {
        mutableState.update { it.copy(pendingConfirmation = NotificationConfirmation.DeleteAll) }
    }

    fun requestMarkAllRead() {
        mutableState.update { it.copy(pendingConfirmation = NotificationConfirmation.MarkAllRead) }
    }

    fun requestDelete(id: Long) = mutableState.update {
        it.copy(pendingConfirmation = NotificationConfirmation.DeleteOne(id))
    }

    fun requestMarkRead(id: Long) = mutableState.update {
        it.copy(pendingConfirmation = NotificationConfirmation.MarkOneRead(id))
    }

    fun dismissDialog() = mutableState.update {
        it.copy(pendingConfirmation = null, categoryActionMessage = null, errorMessage = null)
    }

    fun confirmDialog() {
        when (val confirmation = state.value.pendingConfirmation) {
            NotificationConfirmation.DeleteAll -> mutate({ deleteAll() }) {
                it.copy(messages = emptyList(), expandedMessageId = null, readWhileVisible = emptySet())
            }
            NotificationConfirmation.MarkAllRead -> mutate({ markAllRead() }) {
                it.copy(messages = it.messages.map { message -> message.copy(isRead = true) },
                    readWhileVisible = emptySet(), expandedMessageId = null)
            }
            is NotificationConfirmation.DeleteOne -> mutate({ deleteNotification(confirmation.id) }) {
                it.copy(messages = it.messages.filterNot { message -> message.id == confirmation.id },
                    readWhileVisible = it.readWhileVisible - confirmation.id,
                    expandedMessageId = it.expandedMessageId?.takeUnless { id -> id == confirmation.id })
            }
            is NotificationConfirmation.MarkOneRead -> markOneAsRead(confirmation.id, keepExpanded = false)
            null -> Unit
        }
    }

    fun share(message: AppNotification) {
        if (!message.category.canShare()) return
        viewModelScope.launch { mutableEffects.emit(NotificationEffect.Share(message.title, message.content)) }
    }

    fun performCategoryAction(message: AppNotification) {
        if (message.category.primaryAction() != null) {
            mutableState.update { it.copy(categoryActionMessage = "Realiza ação da categoria") }
        }
    }

    fun togglePreference(category: NotificationCategory?, enabled: Boolean) =
        viewModelScope.launch(dispatchers.io) {
            when (val result = setPreference(category, enabled)) {
                is AppResult.Success -> reloadPreferences()
                is AppResult.Failure -> mutableState.update { it.copy(errorMessage = result.error.userMessage()) }
            }
        }

    private fun markOneAsRead(id: Long, keepExpanded: Boolean) {
        val projection = state.value.projectionRevision
        mutate({ markRead(id) }) {
            val retain = keepExpanded && it.filter == NotificationFilter.UNREAD && projection == it.projectionRevision &&
                it.messages.any { message -> message.id == id }
            it.copy(messages = it.messages.map { message -> if (message.id == id) message.copy(isRead = true) else message },
                readWhileVisible = if (retain) it.readWhileVisible + id else it.readWhileVisible - id,
                expandedMessageId = if (keepExpanded) it.expandedMessageId else null)
        }
    }

    private fun mutate(
        operation: suspend () -> AppResult<Unit>,
        updateState: (NotificationsUiState) -> NotificationsUiState,
    ) = viewModelScope.launch(dispatchers.io) {
        when (val result = operation()) {
            is AppResult.Success -> mutableState.update {
                updateState(it).copy(
                    pendingConfirmation = null,
                    errorMessage = null,
                )
            }
            is AppResult.Failure -> mutableState.update {
                it.copy(pendingConfirmation = null, errorMessage = result.error.userMessage())
            }
        }
    }

    private suspend fun reloadPreferences() {
        when (val result = getPreferences()) {
            is AppResult.Success -> mutableState.update {
                it.copy(preferences = result.value, errorMessage = null)
            }
            is AppResult.Failure -> mutableState.update { it.copy(errorMessage = result.error.userMessage()) }
        }
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> "Não foi possível atualizar as notificações. Verifique sua internet."
        is AppError.Validation -> reason.ifBlank { "Não foi possível atualizar as notificações." }
        AppError.Unexpected -> "Ocorreu um erro inesperado ao atualizar as notificações."
    }
}
