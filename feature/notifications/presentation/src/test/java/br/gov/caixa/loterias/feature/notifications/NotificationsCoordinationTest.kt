package br.gov.caixa.loterias.feature.notifications

import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteAllNotificationsUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteNotificationUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationPreferencesUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationsUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkAllNotificationsReadUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkNotificationReadUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.SetNotificationPreferenceUseCase
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationFilter
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.NotificationsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsCoordinationTest {
    private val dispatcher = StandardTestDispatcher()
    private val message = AppNotification(1, NotificationCategory.RESULTS_AVAILABLE, "Resultado", "Mensagem", "Hoje", false)

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `clearing viewmodel cancels a suspended read without committing its local projection`() = runTest(dispatcher) {
        var cancelled = false
        val vm = viewModel(
            MarkNotificationReadUseCase {
                try {
                    awaitCancellation()
                } finally {
                    cancelled = true
                }
            }
        )
        val store = ViewModelStore().apply { put("notifications", vm) }
        val state = vm.state
        advanceUntilIdle()
        vm.selectFilter(NotificationFilter.UNREAD)
        vm.toggleExpanded(message)
        runCurrent()
        store.clear()
        advanceUntilIdle()
        assertTrue(cancelled)
        assertFalse(state.value.messages.single().isRead)
        assertTrue(state.value.readWhileVisible.isEmpty())
        assertEquals(1L, state.value.expandedMessageId)
        assertSame(state, vm.state)
    }

    @Test fun `initial load finishes messages before requesting preferences`() = runTest(dispatcher) {
        val calls = mutableListOf<String>()
        val vm = viewModel(
            MarkNotificationReadUseCase { AppResult.Success(Unit) },
            GetNotificationsUseCase {
                calls += "messages"
                AppResult.Success(listOf(message))
            },
            GetNotificationPreferencesUseCase {
                calls += "preferences"
                AppResult.Success(emptyList())
            }
        )
        advanceUntilIdle()
        assertEquals(listOf("messages", "preferences"), calls)
        assertFalse(vm.state.value.loading)
        assertEquals(listOf(message), vm.state.value.messages)
    }

    private fun viewModel(
        markRead: MarkNotificationReadUseCase,
        getNotifications: GetNotificationsUseCase = GetNotificationsUseCase { AppResult.Success(listOf(message)) },
        getPreferences: GetNotificationPreferencesUseCase = GetNotificationPreferencesUseCase { AppResult.Success(emptyList()) },
    ) = NotificationsViewModel(
        getNotifications,
        markRead,
        DeleteNotificationUseCase { error("Unexpected deletion") },
        DeleteAllNotificationsUseCase { error("Unexpected deletion") },
        MarkAllNotificationsReadUseCase { error("Unexpected bulk read") },
        getPreferences,
        SetNotificationPreferenceUseCase { _, _ -> error("Unexpected preference update") },
        FixedCoroutineDispatchers(dispatcher)
    )
}
