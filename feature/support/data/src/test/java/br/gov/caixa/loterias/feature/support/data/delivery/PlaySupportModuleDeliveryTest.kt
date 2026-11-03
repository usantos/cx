package br.gov.caixa.loterias.feature.support.data.delivery

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import br.gov.caixa.loterias.feature.support.domain.delivery.SupportDeliveryState
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import java.lang.reflect.Proxy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@OptIn(ExperimentalCoroutinesApi::class)
class PlaySupportModuleDeliveryTest {
    @Test fun installedFaqIsReadyWithoutDownloadOrListener() = runTest {
        val sdk = DeliveryManager()
        sdk.installed = setOf("faq")
        val states = mutableListOf<SupportDeliveryState>()
        PlaySupportModuleDelivery(sdk.manager).install().collect(states::add)
        assertEquals(listOf(SupportDeliveryState.Checking, SupportDeliveryState.Ready), states)
        assertEquals(0, sdk.starts)
        assertTrue(sdk.listeners.isEmpty())
    }

    @Test fun reusesActiveSessionAndRejectsUnrelatedEvents() = runTest {
        val sdk = DeliveryManager()
        sdk.session = session(FAQ_SESSION_ID, SplitInstallSessionStatus.DOWNLOADING)
        sdk.active = listOf(sdk.session)
        val states = mutableListOf<SupportDeliveryState>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            PlaySupportModuleDelivery(sdk.manager).install().collect(states::add)
        }
        assertEquals(SupportDeliveryState.Downloading(DOWNLOADED_BYTES, TOTAL_BYTES), states.last())
        assertEquals(0, sdk.starts)
        sdk.publish(session(OTHER_SESSION_ID, SplitInstallSessionStatus.INSTALLED))
        sdk.publish(session(FAQ_SESSION_ID, SplitInstallSessionStatus.INSTALLED, "other"))
        assertFalse(collection.isCompleted)
        sdk.publish(session(FAQ_SESSION_ID, SplitInstallSessionStatus.INSTALLED))
        assertEquals(SupportDeliveryState.Ready, states.last())
        assertTrue(collection.isCompleted)
        assertEquals(1, sdk.removals)
    }

    @Test fun requestsOnlyFaqAndStopsObservingWhenUserLeaves() = runTest {
        val sdk = DeliveryManager()
        val states = mutableListOf<SupportDeliveryState>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            PlaySupportModuleDelivery(sdk.manager).install().collect(states::add)
        }
        assertEquals(1, sdk.starts)
        assertEquals(SupportDeliveryState.Pending, states.last())
        sdk.publish(confirmationSession(FAQ_SESSION_ID))
        assertEquals(SupportDeliveryState.Confirmation(FAQ_SESSION_ID), states.last())
        collection.cancel()
        collection.join()
        assertEquals(1, sdk.removals)
        assertEquals(0, sdk.cancellations)
        assertEquals(0, sdk.uninstalls)
    }

    @Test fun downloadFailureAllowsExplicitRetryWithNewSession() = runTest {
        val sdk = DeliveryManager()
        val delivery = PlaySupportModuleDelivery(sdk.manager)
        val states = mutableListOf<SupportDeliveryState>()
        val first = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install().collect(states::add) }
        sdk.publish(session(FAQ_SESSION_ID, SplitInstallSessionStatus.FAILED))
        assertTrue(first.isCompleted)
        assertEquals(SupportDeliveryState.Failed, states.last())
        sdk.session = session(FAQ_SESSION_ID, SplitInstallSessionStatus.FAILED)
        val second = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install().collect(states::add) }
        assertEquals(2, sdk.starts)
        second.cancel()
        second.join()
    }

    @Test fun twoCollectorsReuseOneInstallationRequest() = runTest {
        val sdk = DeliveryManager()
        val delivery = PlaySupportModuleDelivery(sdk.manager)
        val first = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install().collect {} }
        val second = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install().collect {} }
        assertEquals(1, sdk.starts)
        first.cancel()
        second.cancel()
        first.join()
        second.join()
        assertEquals(2, sdk.removals)
    }

    @Test fun sdkFailureIsRecoverableAndDoesNotLeaveAListener() = runTest {
        val sdk = DeliveryManager()
        sdk.startTask = Tasks.forException(IllegalStateException("unavailable"))
        val states = mutableListOf<SupportDeliveryState>()
        PlaySupportModuleDelivery(sdk.manager).install().collect(states::add)
        assertEquals(SupportDeliveryState.Failed, states.last())
        assertEquals(1, sdk.removals)
    }

    @Test fun leavingWhileRequestIsPendingReusesTheSameTaskOnReturn() = runTest {
        val sdk = DeliveryManager()
        val pending = TaskCompletionSource<Int>()
        sdk.startTask = pending.task
        val delivery = PlaySupportModuleDelivery(sdk.manager)
        val first = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install().collect {} }
        first.cancel()
        first.join()
        val states = mutableListOf<SupportDeliveryState>()
        val second = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install().collect(states::add) }
        assertEquals(1, sdk.starts)
        pending.setResult(FAQ_SESSION_ID)
        assertEquals(SupportDeliveryState.Pending, states.last())
        second.cancel()
        second.join()
        assertEquals(2, sdk.removals)
    }

    @Test fun completionBeforeStartTaskReturnsIsRecoveredAfterSessionSelection() = runTest {
        val sdk = DeliveryManager()
        val pending = TaskCompletionSource<Int>()
        sdk.startTask = pending.task
        val states = mutableListOf<SupportDeliveryState>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            PlaySupportModuleDelivery(sdk.manager).install().collect(states::add)
        }
        sdk.publish(session(FAQ_SESSION_ID, SplitInstallSessionStatus.INSTALLED))
        pending.setResult(FAQ_SESSION_ID)
        assertEquals(SupportDeliveryState.Ready, states.last())
        assertTrue(collection.isCompleted)
        assertEquals(1, sdk.removals)
    }

    @Test fun installationCompletingDuringRequestUsesTheInstalledModuleWithoutSessionZero() = runTest {
        val sdk = DeliveryManager()
        val pending = TaskCompletionSource<Int>()
        sdk.startTask = pending.task
        val states = mutableListOf<SupportDeliveryState>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            PlaySupportModuleDelivery(sdk.manager).install().collect(states::add)
        }
        sdk.installed = setOf("faq")
        pending.setResult(0)
        assertEquals(SupportDeliveryState.Ready, states.last())
        assertTrue(collection.isCompleted)
        assertEquals(1, sdk.removals)
    }

    @Test fun completedRequestRemainsReusableWhenCollectorIsCancelledBeforeResumption() = runTest {
        val sdk = DeliveryManager()
        val pending = TaskCompletionSource<Int>()
        sdk.startTask = pending.task
        val delivery = PlaySupportModuleDelivery(sdk.manager)
        val first = launch(StandardTestDispatcher(testScheduler)) { delivery.install().collect {} }
        runCurrent()
        pending.setResult(FAQ_SESSION_ID)
        first.cancel()
        runCurrent()
        first.join()
        val second = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install().collect {} }
        second.cancel()
        second.join()
        assertEquals(1, sdk.starts)
        assertEquals(2, sdk.removals)
    }

    private class DeliveryManager {
        var installed = emptySet<String>()
        var active = emptyList<SplitInstallSessionState>()
        var session = session(FAQ_SESSION_ID, SplitInstallSessionStatus.PENDING)
        var starts = 0
        var removals = 0
        var cancellations = 0
        var uninstalls = 0
        var startTask: Task<Int> = Tasks.forResult(FAQ_SESSION_ID)
        val listeners = mutableSetOf<SplitInstallStateUpdatedListener>()
        val manager = Proxy.newProxyInstance(
            SplitInstallManager::class.java.classLoader,
            arrayOf(SplitInstallManager::class.java)
        ) { _, method, arguments ->
            when (method.name) {
                "getInstalledModules" -> installed

                "getSessionStates" -> Tasks.forResult(active)

                "getSessionState" -> Tasks.forResult(session)

                "startInstall" -> {
                    assertEquals(listOf("faq"), (arguments!![0] as SplitInstallRequest).moduleNames)
                    starts++
                    startTask
                }

                "registerListener" -> {
                    listeners.add(arguments!![0] as SplitInstallStateUpdatedListener)
                    null
                }

                "unregisterListener" -> {
                    assertTrue(listeners.remove(arguments!![0] as SplitInstallStateUpdatedListener))
                    removals++
                    null
                }

                "cancelInstall" -> {
                    cancellations++
                    Tasks.forResult<Void>(null)
                }

                "deferredUninstall" -> {
                    uninstalls++
                    Tasks.forResult<Void>(null)
                }

                else -> error("Método inesperado: ${method.name}")
            }
        } as SplitInstallManager

        fun publish(value: SplitInstallSessionState) = listeners.toList().forEach { it.onStateUpdate(value) }
    }

    private companion object {
        const val FAQ_SESSION_ID = 7
        const val OTHER_SESSION_ID = 8
        const val DOWNLOADED_BYTES = 3L
        const val TOTAL_BYTES = 10L

        // O SDK 2.1.0 rejeita confirmação em create; a fixture reproduz seu Bundle com resolução real.
        fun confirmationSession(id: Int): SplitInstallSessionState {
            val confirmation = PendingIntent.getActivity(
                RuntimeEnvironment.getApplication(),
                0,
                Intent(),
                PendingIntent.FLAG_IMMUTABLE
            )
            val encoded = Bundle().apply {
                putInt("session_id", id)
                putInt("status", SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION)
                putStringArrayList("module_names", arrayListOf("faq"))
                putParcelable("user_confirmation_intent", confirmation)
            }
            return SplitInstallSessionState.zzd(encoded)
        }

        fun session(id: Int, status: Int, module: String = "faq"): SplitInstallSessionState =
            SplitInstallSessionState.create(id, status, 0, DOWNLOADED_BYTES, TOTAL_BYTES, listOf(module), emptyList())
    }
}
