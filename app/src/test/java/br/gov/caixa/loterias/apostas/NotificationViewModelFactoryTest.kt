package br.gov.caixa.loterias.apostas

import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.data.LocalNotificationRepository
import br.gov.caixa.loterias.core.domain.notifications.NotificationCategory
import br.gov.caixa.loterias.core.domain.notifications.NotificationRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import javax.inject.Provider

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelFactoryTest {
    @Test fun `each destination receives a fresh repository while all of its use cases share that instance`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val created = mutableListOf<NotificationRepository>()
        val factory = NotificationViewModelFactory(Provider {
            LocalNotificationRepository().also { created += it }
        }, FixedCoroutineDispatchers(dispatcher))
        val first = factory.create()
        val second = factory.create()
        try {
            advanceUntilIdle()
            assertEquals(2, created.size); assertNotSame(created[0], created[1])
            first.requestMarkAllRead(); first.confirmDialog(); advanceUntilIdle()
            first.togglePreference(NotificationCategory.TIPS_NEWS, true); advanceUntilIdle()
            first.load(); advanceUntilIdle()
            assertTrue(first.state.value.messages.all { it.isRead })
            assertTrue(first.state.value.preferences.first { it.category == NotificationCategory.TIPS_NEWS }.enabled)
            assertTrue(second.state.value.messages.none { it.isRead })
            assertTrue(second.state.value.preferences.none { it.enabled })
            first.requestDeleteAll(); first.confirmDialog(); advanceUntilIdle()
            first.load(); advanceUntilIdle()
            assertTrue(first.state.value.messages.isEmpty()); assertEquals(5, second.state.value.messages.size)
        } finally {
            first.viewModelScope.cancel(); second.viewModelScope.cancel(); Dispatchers.resetMain()
        }
    }
}
