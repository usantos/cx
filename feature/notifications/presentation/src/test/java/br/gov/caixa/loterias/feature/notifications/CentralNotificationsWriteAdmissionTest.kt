package br.gov.caixa.loterias.feature.notifications

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.contract.account.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.notifications.domain.model.InboxNotification
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationInbox
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationInboxRepository
import br.gov.caixa.loterias.feature.notifications.domain.usecase.NotificationInboxActions
import br.gov.caixa.loterias.feature.notifications.presentation.viewmodel.CentralNotificationsViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CentralNotificationsWriteAdmissionTest {
    private val main = StandardTestDispatcher()
    private val io = StandardTestDispatcher(TestCoroutineScheduler())
    private val identity = AuthenticatedSessionIdentity("a".repeat(IDENTITY_LENGTH))
    private var enabled = true
    private val repository = Inbox()
    private val identities = object : AuthenticatedSessionIdentityProvider {
        override fun captureIdentity() = identity
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = identity === this@CentralNotificationsWriteAdmissionTest.identity
    }

    @After fun resetDispatcher() = Dispatchers.resetMain()

    @Test fun `clear enfileirado nao executa quando a capacidade desliga antes da coroutine`() = runTest(main) {
        val model = loadedModel()
        model.requestClear()
        model.confirm()
        enabled = false
        drain()
        assertEquals(0, repository.clears)
        assertEquals(listOf(FIRST_ID, SECOND_ID), model.state.value.all.map { it.id })
        assertFalse(model.state.value.loading)
    }

    @Test fun `remove nao executa quando a capacidade desliga entre main e IO`() = runTest(main) {
        val model = loadedModel()
        model.requestRemove(model.state.value.all.first())
        model.confirm()
        main.scheduler.runCurrent()
        enabled = false
        drain()
        assertTrue(repository.removed.isEmpty())
        assertEquals(listOf(FIRST_ID, SECOND_ID), model.state.value.all.map { it.id })
        assertFalse(model.state.value.loading)
    }

    @Test fun `mark all termina escrita iniciada mas nao inicia proxima apos desativacao`() = runTest(main) {
        val model = loadedModel()
        repository.firstReadGate = CompletableDeferred()
        model.requestMarkAll()
        model.confirm()
        drain()
        assertEquals(listOf(FIRST_ID), repository.reads)
        enabled = false
        repository.firstReadGate!!.complete(Unit)
        drain()
        assertEquals(listOf(FIRST_ID), repository.reads)
        assertEquals(listOf(SECOND_ID), model.state.value.unread.map { it.id })
        assertFalse(model.state.value.loading)
    }

    @Test fun `clear em andamento conclui apos desativacao sem repetir a escrita`() = runTest(main) {
        val model = loadedModel()
        repository.clearGate = CompletableDeferred()
        model.requestClear()
        model.confirm()
        drain()
        assertEquals(1, repository.clears)
        enabled = false
        repository.clearGate!!.complete(Unit)
        drain()
        assertEquals(1, repository.clears)
        assertTrue(model.state.value.all.isEmpty())
        assertFalse(model.state.value.loading)
    }

    private fun loadedModel(): CentralNotificationsViewModel {
        Dispatchers.setMain(main)
        return CentralNotificationsViewModel(
            NotificationInboxActions(repository),
            identities,
            FixedCoroutineDispatchers(io, main = main),
            { enabled }
        ).also { drain() }
    }

    private fun drain() {
        repeat(DRAIN_PASSES) {
            main.scheduler.runCurrent()
            io.scheduler.runCurrent()
        }
    }

    private class Inbox : NotificationInboxRepository {
        var rows = listOf(InboxNotification(FIRST_ID, "Primeira", "Corpo", false), InboxNotification(SECOND_ID, "Segunda", "Corpo", false))
        val reads = mutableListOf<Long>()
        val removed = mutableListOf<Long>()
        var clears = 0
        var firstReadGate: CompletableDeferred<Unit>? = null
        var clearGate: CompletableDeferred<Unit>? = null

        override suspend fun load(identity: AuthenticatedSessionIdentity) = NotificationInbox(rows, rows.filterNot { it.read })
        override suspend fun markRead(identity: AuthenticatedSessionIdentity, id: Long) {
            reads += id
            if (id == FIRST_ID) firstReadGate?.await()
            rows = rows.map { if (it.id == id) it.copy(read = true) else it }
        }
        override suspend fun remove(identity: AuthenticatedSessionIdentity, id: Long) {
            removed += id
            rows = rows.filterNot { it.id == id }
        }
        override suspend fun clear(identity: AuthenticatedSessionIdentity) {
            clears++
            clearGate?.await()
            rows = emptyList()
        }
    }

    private companion object {
        const val IDENTITY_LENGTH = 64
        const val FIRST_ID = 1L
        const val SECOND_ID = 2L
        const val DRAIN_PASSES = 8
    }
}
