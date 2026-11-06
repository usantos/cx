package br.gov.caixa.loterias.core.moduledelivery.data

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.delivery.ModuleDeliveryState
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
class PlayModuleInstallationRepositoryImplTest {
    @Test fun installedFaqIsReadyWithoutDownloadOrListener() = runTest {
        val sdk = DeliveryManager()
        sdk.installed = setOf("faq")
        val states = mutableListOf<ModuleDeliveryState>()
        PlayModuleInstallationRepositoryImpl(sdk.manager).install(AppModule.SUPPORT_FAQ).collect(states::add)
        assertEquals(listOf(ModuleDeliveryState.Checking, ModuleDeliveryState.Ready), states)
        assertEquals(0, sdk.starts)
        assertTrue(sdk.listeners.isEmpty())
    }

    @Test fun reusesActiveSessionAndRejectsUnrelatedEvents() = runTest {
        val sdk = DeliveryManager()
        sdk.session = session(FAQ_SESSION_ID, SplitInstallSessionStatus.DOWNLOADING)
        sdk.active = listOf(sdk.session)
        val states = mutableListOf<ModuleDeliveryState>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            PlayModuleInstallationRepositoryImpl(sdk.manager).install(AppModule.SUPPORT_FAQ).collect(states::add)
        }
        assertEquals(ModuleDeliveryState.Downloading(DOWNLOADED_BYTES, TOTAL_BYTES), states.last())
        assertEquals(0, sdk.starts)
        sdk.publish(session(OTHER_SESSION_ID, SplitInstallSessionStatus.INSTALLED))
        sdk.publish(session(FAQ_SESSION_ID, SplitInstallSessionStatus.INSTALLED, "other"))
        assertFalse(collection.isCompleted)
        sdk.publish(session(FAQ_SESSION_ID, SplitInstallSessionStatus.INSTALLED))
        assertEquals(ModuleDeliveryState.Ready, states.last())
        assertTrue(collection.isCompleted)
        assertEquals(1, sdk.removals)
    }

    @Test fun requestsOnlyFaqAndStopsObservingWhenUserLeaves() = runTest {
        val sdk = DeliveryManager()
        val states = mutableListOf<ModuleDeliveryState>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            PlayModuleInstallationRepositoryImpl(sdk.manager).install(AppModule.SUPPORT_FAQ).collect(states::add)
        }
        assertEquals(1, sdk.starts)
        assertEquals(ModuleDeliveryState.Pending, states.last())
        sdk.publish(confirmationSession(FAQ_SESSION_ID))
        assertEquals(ModuleDeliveryState.Confirmation(FAQ_SESSION_ID), states.last())
        collection.cancel()
        collection.join()
        assertEquals(1, sdk.removals)
        assertEquals(0, sdk.cancellations)
        assertEquals(0, sdk.uninstalls)
    }

    @Test fun downloadFailureAllowsExplicitRetryWithNewSession() = runTest {
        val sdk = DeliveryManager()
        val delivery = PlayModuleInstallationRepositoryImpl(sdk.manager)
        val states = mutableListOf<ModuleDeliveryState>()
        val first = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install(AppModule.SUPPORT_FAQ).collect(states::add) }
        sdk.publish(session(FAQ_SESSION_ID, SplitInstallSessionStatus.FAILED))
        assertTrue(first.isCompleted)
        assertEquals(ModuleDeliveryState.Failed, states.last())
        sdk.session = session(FAQ_SESSION_ID, SplitInstallSessionStatus.FAILED)
        val second = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install(AppModule.SUPPORT_FAQ).collect(states::add) }
        assertEquals(2, sdk.starts)
        second.cancel()
        second.join()
    }

    @Test fun twoCollectorsReuseOneInstallationRequest() = runTest {
        val sdk = DeliveryManager()
        val delivery = PlayModuleInstallationRepositoryImpl(sdk.manager)
        val first = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install(AppModule.SUPPORT_FAQ).collect {} }
        val second = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install(AppModule.SUPPORT_FAQ).collect {} }
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
        val states = mutableListOf<ModuleDeliveryState>()
        PlayModuleInstallationRepositoryImpl(sdk.manager).install(AppModule.SUPPORT_FAQ).collect(states::add)
        assertEquals(ModuleDeliveryState.Failed, states.last())
        assertEquals(1, sdk.removals)
    }

    @Test fun leavingWhileRequestIsPendingReusesTheSameTaskOnReturn() = runTest {
        val sdk = DeliveryManager()
        val pending = TaskCompletionSource<Int>()
        sdk.startTask = pending.task
        val delivery = PlayModuleInstallationRepositoryImpl(sdk.manager)
        val first = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install(AppModule.SUPPORT_FAQ).collect {} }
        first.cancel()
        first.join()
        val states = mutableListOf<ModuleDeliveryState>()
        val second = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install(AppModule.SUPPORT_FAQ).collect(states::add) }
        assertEquals(1, sdk.starts)
        pending.setResult(FAQ_SESSION_ID)
        assertEquals(ModuleDeliveryState.Pending, states.last())
        second.cancel()
        second.join()
        assertEquals(2, sdk.removals)
    }

    @Test fun completionBeforeStartTaskReturnsIsRecoveredAfterSessionSelection() = runTest {
        val sdk = DeliveryManager()
        val pending = TaskCompletionSource<Int>()
        sdk.startTask = pending.task
        val states = mutableListOf<ModuleDeliveryState>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            PlayModuleInstallationRepositoryImpl(sdk.manager).install(AppModule.SUPPORT_FAQ).collect(states::add)
        }
        sdk.publish(session(FAQ_SESSION_ID, SplitInstallSessionStatus.INSTALLED))
        pending.setResult(FAQ_SESSION_ID)
        assertEquals(ModuleDeliveryState.Ready, states.last())
        assertTrue(collection.isCompleted)
        assertEquals(1, sdk.removals)
    }

    @Test fun installationCompletingDuringRequestUsesTheInstalledModuleWithoutSessionZero() = runTest {
        val sdk = DeliveryManager()
        val pending = TaskCompletionSource<Int>()
        sdk.startTask = pending.task
        val states = mutableListOf<ModuleDeliveryState>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            PlayModuleInstallationRepositoryImpl(sdk.manager).install(AppModule.SUPPORT_FAQ).collect(states::add)
        }
        sdk.installed = setOf("faq")
        pending.setResult(0)
        assertEquals(ModuleDeliveryState.Ready, states.last())
        assertTrue(collection.isCompleted)
        assertEquals(1, sdk.removals)
    }

    @Test fun completedRequestRemainsReusableWhenCollectorIsCancelledBeforeResumption() = runTest {
        val sdk = DeliveryManager()
        val pending = TaskCompletionSource<Int>()
        sdk.startTask = pending.task
        val delivery = PlayModuleInstallationRepositoryImpl(sdk.manager)
        val first = launch(StandardTestDispatcher(testScheduler)) { delivery.install(AppModule.SUPPORT_FAQ).collect {} }
        runCurrent()
        pending.setResult(FAQ_SESSION_ID)
        first.cancel()
        runCurrent()
        first.join()
        val second = launch(UnconfinedTestDispatcher(testScheduler)) { delivery.install(AppModule.SUPPORT_FAQ).collect {} }
        second.cancel()
        second.join()
        assertEquals(1, sdk.starts)
        assertEquals(2, sdk.removals)
    }

    @Test fun simultaneousModulesUseSeparateRequestsAndRejectEachOthersCompletion() = runTest {
        val sdk = DeliveryManager().apply { strictFaq = false }
        val delivery = PlayModuleInstallationRepositoryImpl(sdk.manager)
        val faqStates = mutableListOf<ModuleDeliveryState>()
        val resultStates = mutableListOf<ModuleDeliveryState>()
        val faq = launch(UnconfinedTestDispatcher(testScheduler)) {
            delivery.install(AppModule.SUPPORT_FAQ).collect(faqStates::add)
        }
        val results = launch(UnconfinedTestDispatcher(testScheduler)) {
            delivery.install(AppModule.RESULTS).collect(resultStates::add)
        }
        assertEquals(listOf("faq", "results_ui"), sdk.requestedModules)
        assertEquals(ModuleDeliveryState.Pending, faqStates.last())
        assertEquals(ModuleDeliveryState.Pending, resultStates.last())
        sdk.publish(session(OTHER_SESSION_ID, SplitInstallSessionStatus.INSTALLED, "results_ui"))
        assertEquals(ModuleDeliveryState.Ready, resultStates.last())
        assertTrue(results.isCompleted)
        assertFalse(faq.isCompleted)
        sdk.publish(session(FAQ_SESSION_ID, SplitInstallSessionStatus.INSTALLED))
        assertEquals(ModuleDeliveryState.Ready, faqStates.last())
        assertTrue(faq.isCompleted)
        assertEquals(2, sdk.removals)
        assertEquals(0, sdk.cancellations)
    }

    private class DeliveryManager {
        var strictFaq = true
        val requestedModules = mutableListOf<String>()
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

                "getSessionState" -> if (strictFaq) {
                    Tasks.forResult(session)
                } else {
                    val id = arguments!![0] as Int
                    Tasks.forResult(session(id, SplitInstallSessionStatus.PENDING, if (id == FAQ_SESSION_ID) "faq" else "results_ui"))
                }

                "startInstall" -> {
                    val names = (arguments!![0] as SplitInstallRequest).moduleNames
                    if (strictFaq) assertEquals(listOf("faq"), names)
                    requestedModules += names.single()
                    starts++
                    if (strictFaq) startTask else Tasks.forResult(if (names.single() == "faq") FAQ_SESSION_ID else OTHER_SESSION_ID)
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
