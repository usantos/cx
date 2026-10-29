package br.gov.caixa.loterias.feature.notifications

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationInbox
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationInboxRepository
import br.gov.caixa.loterias.feature.notifications.domain.usecase.NotificationInboxActions
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.CentralNotificationsViewModel
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CentralNotificationsViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private var current: AuthenticatedSessionIdentity? = identity
    private val sessions = object : AuthenticatedSessionIdentityProvider {
        override fun captureIdentity() = current
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = identity === current
    }
    private val repo = FakeInbox()

    @Before fun before() = Dispatchers.setMain(dispatcher)

    @After fun after() = Dispatchers.resetMain()
    private fun model() = CentralNotificationsViewModel(NotificationInboxActions(repo), sessions, FixedCoroutineDispatchers(dispatcher))

    @Test fun `anonymous never fetches private or synthetic messages`() = runTest(dispatcher) {
        current = null
        val vm = model()
        advanceUntilIdle()
        assertTrue(vm.state.value.isAnonymous)
        assertTrue(vm.state.value.visible.isEmpty())
        assertFalse(vm.state.value.canMarkAll)
        assertEquals(0, repo.loads)
    }

    @Test fun `server ordering unread count and tabs are preserved`() = runTest(dispatcher) {
        val vm = model()
        advanceUntilIdle()
        assertEquals(listOf(2L, 1L), vm.state.value.visible.map { it.id })
        assertEquals(1, vm.state.value.unread.size)
        vm.selectUnread(true)
        assertEquals(listOf(2L), vm.state.value.visible.map { it.id })
        assertFalse(vm.state.value.canDeleteAll)
    }

    @Test fun `mark all requires confirmation and marks only unread IDs`() = runTest(dispatcher) {
        val vm = model()
        advanceUntilIdle()
        vm.requestMarkAll()
        assertTrue(repo.reads.isEmpty())
        vm.confirm()
        advanceUntilIdle()
        assertEquals(listOf(2L), repo.reads)
        assertTrue(vm.state.value.unread.isEmpty())
        assertFalse(vm.state.value.canMarkAll)
    }

    @Test fun `cancel and failed deletion retain the server content`() = runTest(dispatcher) {
        val vm = model()
        advanceUntilIdle()
        vm.requestClear()
        vm.dismissDialog()
        assertEquals(0, repo.clears)
        repo.failMutations = true
        vm.requestRemove(vm.state.value.all.first())
        vm.confirm()
        advanceUntilIdle()
        assertEquals(2, vm.state.value.all.size)
        assertNotNull(vm.state.value.error)
    }

    @Test fun `partial mark all refreshes authoritative unread list and reports failure`() = runTest(dispatcher) {
        repo.rows = repo.rows + InboxNotification(PARTIAL_FAILURE_ID, "Outra", "Corpo", false)
        repo.failReadId = PARTIAL_FAILURE_ID
        val vm = model()
        advanceUntilIdle()
        vm.requestMarkAll()
        vm.confirm()
        advanceUntilIdle()
        assertEquals(listOf(2L), repo.reads)
        assertEquals(listOf(PARTIAL_FAILURE_ID), vm.state.value.unread.map { it.id })
        assertNotNull(vm.state.value.error)
    }

    @Test fun `confirmation from previous login cannot mutate replacement account`() = runTest(dispatcher) {
        val vm = model()
        advanceUntilIdle()
        vm.requestClear()
        current = AuthenticatedSessionIdentity("b".repeat(IDENTITY_LENGTH))
        vm.confirm()
        advanceUntilIdle()
        assertEquals(0, repo.clears)
        assertTrue(vm.state.value.all.isEmpty())
    }

    @Test fun `late response after logout is discarded`() = runTest(dispatcher) {
        repo.gate = CompletableDeferred()
        val vm = model()
        current = null
        repo.gate!!.complete(Unit)
        advanceUntilIdle()
        assertTrue(vm.state.value.all.isEmpty())
        assertTrue(vm.state.value.isAnonymous)
    }

    @Test fun `first resume does not repeat initial query and later resume refreshes`() = runTest(dispatcher) {
        val vm = model()
        advanceUntilIdle()
        vm.onResume()
        advanceUntilIdle()
        assertEquals(1, repo.loads)
        vm.onResume()
        advanceUntilIdle()
        assertEquals(2, repo.loads)
    }

    private class FakeInbox : NotificationInboxRepository {
        var rows = listOf(InboxNotification(2, "Nova", "Corpo", false), InboxNotification(1, "Anterior", "Corpo", true))
        var loads = 0
        var clears = 0
        var failMutations = false
        var failReadId: Long? = null
        val reads = mutableListOf<Long>()
        var gate: CompletableDeferred<Unit>? = null
        override suspend fun load(identity: AuthenticatedSessionIdentity): NotificationInbox {
            loads++
            gate?.await()
            return NotificationInbox(rows, rows.filterNot { it.read })
        }
        override suspend fun markRead(identity: AuthenticatedSessionIdentity, id: Long) {
            if (failMutations || failReadId == id) throw IOException()
            reads += id
            rows = rows.map { if (it.id == id) it.copy(read = true) else it }
        }
        override suspend fun remove(identity: AuthenticatedSessionIdentity, id: Long) {
            if (failMutations) throw IOException()
            rows = rows.filterNot { it.id == id }
        }
        override suspend fun clear(identity: AuthenticatedSessionIdentity) {
            if (failMutations) throw IOException()
            clears++
            rows = emptyList()
        }
    }
    private companion object {
        const val PARTIAL_FAILURE_ID = 3L
        const val IDENTITY_LENGTH = 64
    }
}
