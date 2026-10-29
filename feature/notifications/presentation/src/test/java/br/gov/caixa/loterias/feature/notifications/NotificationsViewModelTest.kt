package br.gov.caixa.loterias.feature.notifications

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.notifications.R
import br.gov.caixa.loterias.feature.notifications.domain.model.AppNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteAllNotifications
import br.gov.caixa.loterias.feature.notifications.domain.usecase.DeleteNotification
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationPreferences
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotifications
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkAllNotificationsRead
import br.gov.caixa.loterias.feature.notifications.domain.usecase.MarkNotificationRead
import br.gov.caixa.loterias.feature.notifications.domain.usecase.SetNotificationPreference
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationConfirmation
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationEffect
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationFilter
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationText
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.NotificationsViewModel
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {
    private val repository = FakeRepository()

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `loads content and applies search and unread filters`() = runTest {
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.loading)
        assertEquals(3, viewModel.state.value.messages.size)
        assertEquals(2, viewModel.state.value.preferences.size)

        viewModel.updateQuery("resultado")
        assertEquals(listOf(2L), viewModel.state.value.visibleMessages.map { it.id })
        viewModel.updateQuery("")
        viewModel.selectFilter(NotificationFilter.UNREAD)
        assertEquals(listOf(1L, 2L), viewModel.state.value.visibleMessages.map { it.id })
        assertEquals(R.string.notifications_voce_nao_possui_mensagens_nao_lidas, viewModel.state.value.emptyMessage)
        viewModel.selectFilter(NotificationFilter.ALL)
        assertEquals(R.string.notifications_voce_nao_possui_mensagens, viewModel.state.value.emptyMessage)
    }

    @Test fun `expanding closes the previous item and marks unread item as read`() = runTest {
        val viewModel = viewModel(repository)
        advanceUntilIdle()
        val first = viewModel.state.value.messages[0]
        val second = viewModel.state.value.messages[1]

        viewModel.toggleExpanded(first)
        advanceUntilIdle()
        assertEquals(first.id, viewModel.state.value.expandedMessageId)
        assertTrue(viewModel.state.value.messages.first { it.id == first.id }.isRead)

        viewModel.toggleExpanded(second)
        advanceUntilIdle()
        assertEquals(second.id, viewModel.state.value.expandedMessageId)
        viewModel.toggleExpanded(viewModel.state.value.messages.first { it.id == second.id })
        assertNull(viewModel.state.value.expandedMessageId)
    }

    @Test fun `bulk confirmation remains available for empty and already read lists like legacy`() = runTest {
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.requestDeleteAll()
        assertEquals(NotificationConfirmation.DeleteAll, viewModel.state.value.pendingConfirmation)
        viewModel.dismissDialog()
        viewModel.requestMarkAllRead()
        assertEquals(NotificationConfirmation.MarkAllRead, viewModel.state.value.pendingConfirmation)
        viewModel.dismissDialog()

        repository.messages.clear()
        viewModel.load()
        advanceUntilIdle()
        viewModel.requestDeleteAll()
        assertEquals(NotificationConfirmation.DeleteAll, viewModel.state.value.pendingConfirmation)
        viewModel.requestMarkAllRead()
        assertEquals(NotificationConfirmation.MarkAllRead, viewModel.state.value.pendingConfirmation)
        viewModel.dismissDialog()
        viewModel.confirmDialog()
    }

    @Test fun `confirms item read item delete mark all and delete all`() = runTest {
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.requestMarkRead(1L)
        assertEquals(NotificationConfirmation.MarkOneRead(1L), viewModel.state.value.pendingConfirmation)
        viewModel.confirmDialog()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.messages.first { it.id == 1L }.isRead)

        viewModel.requestDelete(2L)
        assertEquals(NotificationConfirmation.DeleteOne(2L), viewModel.state.value.pendingConfirmation)
        viewModel.confirmDialog()
        advanceUntilIdle()
        assertEquals(listOf(1L, 3L), viewModel.state.value.messages.map { it.id })

        viewModel.requestMarkAllRead()
        viewModel.confirmDialog()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.messages.all { it.isRead })

        viewModel.requestDeleteAll()
        viewModel.confirmDialog()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.messages.isEmpty())
    }

    @Test fun `preferences toggle independently without implying remote subscriptions`() = runTest {
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.togglePreference(null, true)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.preferences.first().enabled)
        assertFalse(viewModel.state.value.preferences.last().enabled)
        viewModel.togglePreference(NotificationCategory.TIPS_NEWS, true)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.preferences.first { it.category == NotificationCategory.TIPS_NEWS }.enabled)

        viewModel.togglePreference(null, true)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.preferences.all { it.enabled })
    }

    @Test fun `late expansion read cannot retain a deleted message or a refreshed unread projection`() = runTest {
        for (delete in listOf(false, true)) {
            val source = FakeRepository()
            val vm = viewModel(source)
            advanceUntilIdle()
            vm.selectFilter(NotificationFilter.UNREAD)
            val pending = kotlinx.coroutines.CompletableDeferred<Unit>()
            source.readBarrier = pending
            val item = vm.state.value.visibleMessages.first()
            vm.toggleExpanded(item)
            advanceUntilIdle()
            if (delete) {
                vm.requestDelete(item.id)
                vm.confirmDialog()
                advanceUntilIdle()
            } else {
                vm.selectFilter(NotificationFilter.ALL)
                vm.selectFilter(NotificationFilter.UNREAD)
            }
            pending.complete(Unit)
            advanceUntilIdle()
            assertTrue(vm.state.value.readWhileVisible.isEmpty())
            assertFalse(vm.state.value.visibleMessages.any { it.id == item.id })
            vm.toggleExpanded(item)
            assertNull(vm.state.value.expandedMessageId)
        }
    }

    @Test fun `stale unread snapshot does not submit another read when reopening an already read item`() = runTest {
        val vm = viewModel(repository)
        advanceUntilIdle()
        val snapshot = vm.state.value.messages.first()
        vm.toggleExpanded(snapshot)
        advanceUntilIdle()
        vm.toggleExpanded(snapshot)
        repository.failure = IllegalStateException("No second mutation")
        vm.toggleExpanded(snapshot)
        advanceUntilIdle()
        assertNull(vm.state.value.errorMessage)
        assertEquals(snapshot.id, vm.state.value.expandedMessageId)
    }

    @Test fun `opening unread messages retains their content until the projection is refreshed`() = runTest {
        val vm = viewModel(repository)
        advanceUntilIdle()
        vm.selectFilter(NotificationFilter.UNREAD)
        vm.toggleExpanded(vm.state.value.visibleMessages.first())
        advanceUntilIdle()
        assertEquals(listOf(1L, 2L), vm.state.value.visibleMessages.map { it.id })
        assertEquals(setOf(1L), vm.state.value.readWhileVisible)
        assertTrue(vm.state.value.visibleMessages.first().isRead)
        vm.toggleExpanded(vm.state.value.visibleMessages.last())
        advanceUntilIdle()
        assertEquals(setOf(1L, 2L), vm.state.value.readWhileVisible)
        assertEquals(2L, vm.state.value.expandedMessageId)
        vm.toggleExpanded(vm.state.value.visibleMessages.last())
        assertNull(vm.state.value.expandedMessageId)
        assertEquals(2, vm.state.value.visibleMessages.size)
        vm.selectFilter(NotificationFilter.ALL)
        vm.selectFilter(NotificationFilter.UNREAD)
        assertTrue(vm.state.value.visibleMessages.isEmpty())
        assertTrue(vm.state.value.readWhileVisible.isEmpty())
    }

    @Test fun `explicit read removes retained unread item and deleting expanded item clears selection`() = runTest {
        val vm = viewModel(repository)
        advanceUntilIdle()
        vm.selectFilter(NotificationFilter.UNREAD)
        vm.toggleExpanded(vm.state.value.visibleMessages.first())
        advanceUntilIdle()
        vm.requestMarkRead(1)
        vm.confirmDialog()
        advanceUntilIdle()
        assertEquals(listOf(2L), vm.state.value.visibleMessages.map { it.id })
        assertTrue(vm.state.value.readWhileVisible.isEmpty())
        assertNull(vm.state.value.expandedMessageId)
        vm.toggleExpanded(vm.state.value.visibleMessages.first())
        advanceUntilIdle()
        vm.requestDelete(2)
        vm.confirmDialog()
        advanceUntilIdle()
        assertTrue(vm.state.value.visibleMessages.isEmpty())
        assertTrue(vm.state.value.readWhileVisible.isEmpty())
        assertNull(vm.state.value.expandedMessageId)
    }

    @Test fun `clearing search and marking all read refresh the unread projection`() = runTest {
        val vm = viewModel(repository)
        advanceUntilIdle()
        vm.selectFilter(NotificationFilter.UNREAD)
        vm.toggleExpanded(vm.state.value.visibleMessages.first())
        advanceUntilIdle()
        vm.updateQuery("Novidade")
        assertEquals(listOf(1L), vm.state.value.visibleMessages.map { it.id })
        vm.updateQuery("")
        assertEquals(listOf(2L), vm.state.value.visibleMessages.map { it.id })
        vm.toggleExpanded(vm.state.value.visibleMessages.first())
        advanceUntilIdle()
        vm.requestMarkAllRead()
        vm.confirmDialog()
        advanceUntilIdle()
        assertTrue(vm.state.value.visibleMessages.isEmpty())
        assertNull(vm.state.value.expandedMessageId)
    }

    @Test fun `share and category actions follow category rules`() = runTest {
        val viewModel = viewModel(repository)
        advanceUntilIdle()
        val share = async(UnconfinedTestDispatcher(testScheduler)) { viewModel.effects.first() }

        viewModel.share(repository.messages[0])
        assertEquals(NotificationEffect.Share("Novidade", "Conteúdo"), share.await())

        viewModel.share(repository.messages[2])
        viewModel.performCategoryAction(repository.messages[0])
        assertNull(viewModel.state.value.categoryActionMessage)
        viewModel.performCategoryAction(repository.messages[1])
        assertEquals(
            NotificationText.Resource(R.string.notifications_realiza_acao_da_categoria),
            viewModel.state.value.categoryActionMessage
        )
        viewModel.dismissDialog()
        assertNull(viewModel.state.value.categoryActionMessage)
    }

    @Test fun `load and mutation failures expose mapped messages and can be dismissed`() = runTest {
        repository.failure = IOException("offline")
        val failingLoad = viewModel(repository)
        advanceUntilIdle()
        assertEquals(
            NotificationText.Resource(R.string.notifications_nao_foi_possivel_atualizar_as_notificacoes_verifique_sua_internet),
            failingLoad.state.value.errorMessage
        )
        assertFalse(failingLoad.state.value.loading)
        failingLoad.dismissDialog()
        assertNull(failingLoad.state.value.errorMessage)

        repository.failure = null
        val viewModel = viewModel(repository)
        advanceUntilIdle()
        repository.failure = IllegalArgumentException("")
        viewModel.requestDelete(1L)
        viewModel.confirmDialog()
        advanceUntilIdle()
        assertEquals(
            NotificationText.Resource(R.string.notifications_nao_foi_possivel_atualizar_as_notificacoes),
            viewModel.state.value.errorMessage
        )

        repository.failure = IllegalStateException("boom")
        viewModel.togglePreference(NotificationCategory.TIPS_NEWS, true)
        advanceUntilIdle()
        assertEquals(
            NotificationText.Resource(R.string.notifications_ocorreu_um_erro_inesperado_ao_atualizar_as_notificacoes),
            viewModel.state.value.errorMessage
        )
    }

    @Test fun `preference reload failure is reported after a successful update`() = runTest {
        val viewModel = viewModel(repository)
        advanceUntilIdle()
        repository.failPreferenceRead = true

        viewModel.togglePreference(NotificationCategory.TIPS_NEWS, true)
        advanceUntilIdle()

        assertEquals(
            NotificationText.Resource(R.string.notifications_ocorreu_um_erro_inesperado_ao_atualizar_as_notificacoes),
            viewModel.state.value.errorMessage
        )
    }

    private fun viewModel(repository: FakeRepository): NotificationsViewModel {
        val dispatcher = UnconfinedTestDispatcher()
        return NotificationsViewModel(
            GetNotifications(repository),
            MarkNotificationRead(repository),
            DeleteNotification(repository),
            DeleteAllNotifications(repository),
            MarkAllNotificationsRead(repository),
            GetNotificationPreferences(repository),
            SetNotificationPreference(repository),
            FixedCoroutineDispatchers(dispatcher)
        )
    }

    private class FakeRepository : NotificationRepository {
        val messages = mutableListOf(
            message(1, NotificationCategory.TIPS_NEWS, "Novidade", "Conteúdo"),
            message(2, NotificationCategory.RESULTS_AVAILABLE, "Resultado", "Confira"),
            message(3, NotificationCategory.SECURITY_ALERTS, "Segurança", "Proteja-se", true)
        )
        private val enabled = mutableSetOf<NotificationCategory>()
        private var allEnabled = false
        var failure: Throwable? = null
        var readBarrier: kotlinx.coroutines.CompletableDeferred<Unit>? = null
        var failPreferenceRead = false

        private fun checkFailure() {
            failure?.let { throw it }
        }

        override suspend fun getNotifications(): List<AppNotification> {
            checkFailure()
            return messages.toList()
        }

        override suspend fun markAsRead(id: Long) {
            checkFailure()
            readBarrier?.await()
            messages.replaceAll { if (it.id == id) it.copy(isRead = true) else it }
        }

        override suspend fun delete(id: Long) {
            checkFailure()
            messages.removeAll { it.id == id }
        }

        override suspend fun deleteAll() {
            checkFailure()
            messages.clear()
        }

        override suspend fun markAllAsRead() {
            checkFailure()
            messages.replaceAll { it.copy(isRead = true) }
        }

        override suspend fun getPreferences(): List<NotificationPreference> {
            checkFailure()
            if (failPreferenceRead) error("preferences")
            return listOf(
                NotificationPreference(null, "Todas", "", allEnabled),
                NotificationPreference(
                    NotificationCategory.TIPS_NEWS,
                    "Novidades",
                    "Descrição",
                    NotificationCategory.TIPS_NEWS in enabled
                )
            )
        }

        override suspend fun setPreference(category: NotificationCategory?, enabled: Boolean) {
            checkFailure()
            if (category == null) {
                allEnabled = enabled
            } else if (enabled) {
                this.enabled += category
            } else {
                this.enabled -= category
            }
        }
    }
}
