package br.gov.caixa.loterias.feature.notifications

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationCategory
import br.gov.caixa.loterias.feature.notifications.domain.model.NotificationPreference
import br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationPreferencesUseCase
import br.gov.caixa.loterias.feature.notifications.domain.usecase.SetNotificationPreferenceUseCase
import br.gov.caixa.loterias.feature.notifications.presentation.coordinator.NotificationsPreferences
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsAction
import br.gov.caixa.loterias.feature.notifications.presentation.state.NotificationsUiState
import br.gov.caixa.loterias.feature.notifications.presentation.state.reduceNotificationsState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsPreferencesOrderingTest {
    @Test fun delayedEarlierSnapshotCannotOverwriteALaterPreferenceMutation() = runTest {
        val releaseFirstRead = CompletableDeferred<Unit>()
        var reads = 0
        val values = linkedMapOf(NotificationCategory.TIPS_NEWS to false, NotificationCategory.SECURITY_ALERTS to false)
        val store = reduxStateStore<NotificationsUiState, NotificationsAction>(NotificationsUiState(), ::reduceNotificationsState)
        val coordinator = NotificationsPreferences(
            store,
            this,
            FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler)),
            GetNotificationPreferencesUseCase {
                val snapshot = values.map { (category, enabled) -> NotificationPreference(category, "", "", enabled) }
                reads++
                if (reads == 1) releaseFirstRead.await()
                AppResult.Success(snapshot)
            },
            SetNotificationPreferenceUseCase { category, enabled ->
                if (category != null) values[category] = enabled
                AppResult.Success(Unit)
            }
        )
        try {
            coordinator.togglePreference(NotificationCategory.TIPS_NEWS, true)
            runCurrent()
            coordinator.togglePreference(NotificationCategory.SECURITY_ALERTS, true)
            runCurrent()
            releaseFirstRead.complete(Unit)
            advanceUntilIdle()
            assertEquals(2, store.value.preferences.size)
            assertTrue("Both committed preferences must survive delayed publication", store.value.preferences.all { it.enabled })
        } finally {
            store.close()
        }
    }

    @Test fun initialPreferenceReadAndSubsequentWritesShareInvocationOrder() = runTest {
        val releaseInitial = CompletableDeferred<Unit>()
        var reads = 0
        var writes = 0
        var enabled = false
        val store = reduxStateStore<NotificationsUiState, NotificationsAction>(NotificationsUiState(), ::reduceNotificationsState)
        val operations = br.gov.caixa.loterias.feature.notifications.presentation.coordinator.NotificationPreferenceOperations()
        val dispatchers = FixedCoroutineDispatchers(StandardTestDispatcher(testScheduler))
        val get = GetNotificationPreferencesUseCase {
            val snapshot = listOf(NotificationPreference(NotificationCategory.TIPS_NEWS, "", "", enabled))
            reads++
            if (reads == 1) releaseInitial.await()
            AppResult.Success(snapshot)
        }
        val loading = br.gov.caixa.loterias.feature.notifications.presentation.coordinator.NotificationsLoading(
            store,
            this,
            dispatchers,
            br.gov.caixa.loterias.feature.notifications.domain.usecase.GetNotificationsUseCase { AppResult.Success(emptyList()) },
            get,
            operations
        )
        val mutations = NotificationsPreferences(
            store,
            this,
            dispatchers,
            get,
            SetNotificationPreferenceUseCase { _, value ->
                writes++
                enabled = value
                AppResult.Success(Unit)
            },
            operations
        )
        try {
            loading.load()
            runCurrent()
            mutations.togglePreference(NotificationCategory.TIPS_NEWS, true)
            runCurrent()
            assertEquals("Mutation waits for the earlier authoritative read", 0, writes)
            releaseInitial.complete(Unit)
            advanceUntilIdle()
            assertEquals(1, writes)
            assertTrue(store.value.preferences.single().enabled)
        } finally {
            releaseInitial.complete(Unit)
            store.close()
        }
    }
}
