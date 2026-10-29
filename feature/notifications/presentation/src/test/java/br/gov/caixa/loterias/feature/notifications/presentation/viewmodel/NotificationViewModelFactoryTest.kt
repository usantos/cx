package br.gov.caixa.loterias.feature.notifications.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.notifications.di.NotificationViewModelFactory
import br.gov.caixa.loterias.feature.notifications.di.NotificationsModule
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.repository.NotificationRepository
import javax.inject.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelFactoryTest {
    @Test fun `each destination receives a fresh repository while all of its use cases share that instance`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val created = mutableListOf<NotificationRepository>()
        val factory = NotificationViewModelFactory(
            Provider {
                NotificationsModule.notificationRepository().also { created += it }
            },
            FixedCoroutineDispatchers(dispatcher)
        )
        val first = factory.create()
        val second = factory.create()
        try {
            advanceUntilIdle()
            assertEquals(2, created.size)
            assertNotSame(created[0], created[1])
            first.requestMarkAllRead()
            first.confirmDialog()
            advanceUntilIdle()
            first.togglePreference(NotificationCategory.TIPS_NEWS, true)
            advanceUntilIdle()
            first.load()
            advanceUntilIdle()
            assertTrue(first.state.value.messages.all { it.isRead })
            assertTrue(first.state.value.preferences.first { it.category == NotificationCategory.TIPS_NEWS }.enabled)
            assertTrue(second.state.value.messages.none { it.isRead })
            assertTrue(second.state.value.preferences.none { it.enabled })
            first.requestDeleteAll()
            first.confirmDialog()
            advanceUntilIdle()
            first.load()
            advanceUntilIdle()
            assertTrue(first.state.value.messages.isEmpty())
            assertEquals(5, second.state.value.messages.size)
        } finally {
            first.viewModelScope.cancel()
            second.viewModelScope.cancel()
            Dispatchers.resetMain()
        }
    }
}
