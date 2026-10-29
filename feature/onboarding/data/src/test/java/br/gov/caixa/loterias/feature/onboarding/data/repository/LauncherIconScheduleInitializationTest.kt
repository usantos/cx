package br.gov.caixa.loterias.feature.onboarding.data.repository

import android.content.Context
import android.os.Looper
import br.gov.caixa.loterias.feature.onboarding.data.config.observeFeatureConfigActivations
import br.gov.caixa.loterias.feature.onboarding.data.config.publishFeatureConfigActivations
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.ConfigUpdateListenerRegistration
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], shadows = [LauncherScheduleConfigShadow::class, LauncherScheduleAppShadow::class])
class LauncherIconScheduleInitializationTest {
    @Before fun reset() {
        LauncherScheduleConfigShadow.instance = null
        LauncherScheduleConfigShadow.encoded = calendar("outubro_rosa")
        LauncherScheduleConfigShadow.listener = null
        LauncherScheduleConfigShadow.activation = TaskCompletionSource()
        LauncherScheduleConfigShadow.removals = 0
        LauncherScheduleConfigShadow.calls.clear()
        LauncherScheduleConfigShadow.initialization = TaskCompletionSource()
        LauncherScheduleConfigShadow.defaults = TaskCompletionSource()
        LauncherScheduleAppShadow.app = null
    }

    @Test fun initializationWaitsForCachedValuesAndDefaultsWithoutFetching() = runBlocking {
        val repository = repository()
        val pending = async(Dispatchers.Unconfined) { repository.initialize(1) }
        idle()
        assertFalse(pending.isCompleted)
        assertEquals(listOf("initialize"), LauncherScheduleConfigShadow.calls)
        LauncherScheduleConfigShadow.initialization.setResult(null)
        idle()
        assertFalse(pending.isCompleted)
        assertEquals(listOf("initialize", "defaults:1"), LauncherScheduleConfigShadow.calls)
        LauncherScheduleConfigShadow.defaults.setResult(null)
        idle()
        pending.await()
        assertEquals(LauncherIconPreset.OUTUBRO_ROSA, repository.activePreset)
        assertEquals(listOf("initialize", "defaults:1", "read:app_launcher_icon"), LauncherScheduleConfigShadow.calls)
    }

    @Test fun failedCacheAndDefaultsTasksKeepTheActivatedValueAvailable() = runBlocking {
        LauncherScheduleConfigShadow.initialization.setException(IllegalStateException("cache"))
        LauncherScheduleConfigShadow.defaults.setException(IllegalStateException("defaults"))
        val repository = repository()
        val pending = async(Dispatchers.Unconfined) { repository.initialize(1) }
        idle()
        pending.await()
        assertTrue(pending.isCompleted)
        assertEquals(LauncherIconPreset.OUTUBRO_ROSA, repository.activePreset)
    }

    @Test fun publishedCalendarWaitsForSuccessfulActivationBeforeNotifying() {
        var changes = 0
        repository().observe { changes++ }.use {
            LauncherScheduleConfigShadow.calls.clear()
            LauncherScheduleConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("app_launcher_icon")))
            idle()
            assertEquals(0, changes)
            assertEquals(listOf("activate"), LauncherScheduleConfigShadow.calls)
            LauncherScheduleConfigShadow.encoded = calendar("novembro_azul")
            LauncherScheduleConfigShadow.activation.setResult(false)
            idle()
            assertEquals(1, changes)
        }
        assertEquals(1, LauncherScheduleConfigShadow.removals)
    }

    @Test fun unrelatedChangesAndStreamErrorsKeepTheCurrentIcon() {
        var changes = 0
        repository().observe { changes++ }.use {
            LauncherScheduleConfigShadow.calls.clear()
            LauncherScheduleConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("force_update")))
            LauncherScheduleConfigShadow.listener!!.onError(FirebaseRemoteConfigException("offline"))
            idle()
            assertTrue(LauncherScheduleConfigShadow.calls.isEmpty())
            assertEquals(0, changes)
        }
    }

    @Test fun failedActivationDoesNotNotifyTheCalendar() {
        var changes = 0
        repository().observe { changes++ }.use {
            LauncherScheduleConfigShadow.calls.clear()
            LauncherScheduleConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("app_launcher_icon", "force_update")))
            LauncherScheduleConfigShadow.activation.setException(IllegalStateException("offline"))
            idle()
            assertEquals(0, changes)
        }
    }

    @Test fun closingTheObservationSuppressesLateActivationAndRemovesOnlyOnce() {
        var changes = 0
        val observation = repository().observe { changes++ }
        LauncherScheduleConfigShadow.calls.clear()
        val listener = requireNotNull(LauncherScheduleConfigShadow.listener)
        listener.onUpdate(ConfigUpdate.create(setOf("app_launcher_icon")))
        observation.close()
        observation.close()
        LauncherScheduleConfigShadow.activation.setResult(true)
        idle()
        listener.onUpdate(ConfigUpdate.create(setOf("app_launcher_icon")))
        idle()
        assertEquals(0, changes)
        assertEquals(1, LauncherScheduleConfigShadow.removals)
        assertEquals(listOf("activate"), LauncherScheduleConfigShadow.calls)
    }

    @Test fun successfulResumeFetchNotifiesCalendarWithoutARealtimeEvent() = runBlocking {
        LauncherScheduleConfigShadow.defaults.setResult(null)
        var changes = 0
        repository().observe { changes++ }.use {
            val options = FirebaseOptions.Builder().setApplicationId("test-launcher").setApiKey("test-key").build()
            val force = FirebaseForceUpdateRepositoryImpl(RuntimeEnvironment.getApplication(), options, 1)
            val pending = async(Dispatchers.Unconfined) { force.refresh() }
            idle()
            assertEquals(0, changes)
            LauncherScheduleConfigShadow.encoded = calendar("novembro_azul")
            LauncherScheduleConfigShadow.activation.setResult(false)
            idle()
            pending.await()
            assertEquals("Uma ativação da busca ao retomar deve atualizar o calendário", 1, changes)
            LauncherScheduleConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("app_launcher_icon")))
            idle()
            assertEquals("O mesmo valor não deve ser aplicado duas vezes", 1, changes)
        }
    }

    @Test fun closingOneActivationObserverKeepsTheOtherUntilItAlsoCloses() {
        val remote = Shadow.newInstanceOf(FirebaseRemoteConfig::class.java)
        var first = 0
        var second = 0
        val one = observeFeatureConfigActivations(remote) { first++ }
        val two = observeFeatureConfigActivations(remote) { second++ }
        publishFeatureConfigActivations(remote)
        one.close()
        one.close()
        publishFeatureConfigActivations(remote)
        two.close()
        publishFeatureConfigActivations(remote)
        assertEquals(1, first)
        assertEquals(2, second)
    }

    private fun calendar(icon: String) = """{"timezone":"America/Sao_Paulo","campaigns":[
        {"icon":"$icon","start":"10-01","end":"10-31"}]}"""

    private fun repository() = FirebaseLauncherIconConfigurationRepositoryImpl(
        RuntimeEnvironment.getApplication(),
        FirebaseOptions.Builder().setApplicationId("test-launcher").setApiKey("test-key").build(),
        Clock.fixed(Instant.parse("2026-10-15T12:00:00Z"), ZoneOffset.UTC)
    )

    private fun idle() = shadowOf(Looper.getMainLooper()).idle()
}

@Implements(FirebaseRemoteConfig::class)
class LauncherScheduleConfigShadow {
    @Implementation fun addOnConfigUpdateListener(value: ConfigUpdateListener): ConfigUpdateListenerRegistration {
        listener = value
        return ConfigUpdateListenerRegistration { removals++ }
    }

    @Implementation fun activate(): Task<Boolean> {
        calls += "activate"
        return activation.task
    }

    @Implementation fun ensureInitialized(): Task<FirebaseRemoteConfigInfo> {
        calls += "initialize"
        return initialization.task
    }

    @Implementation fun setDefaultsAsync(resource: Int): Task<Void> {
        calls += "defaults:$resource"
        return defaults.task
    }

    @Implementation fun getBoolean(key: String): Boolean {
        check(key == "force_update")
        return false
    }

    @Implementation fun setConfigSettingsAsync(settings: FirebaseRemoteConfigSettings): Task<Void> {
        check(settings.fetchTimeoutInSeconds > 0)
        return Tasks.forResult(null)
    }

    @Implementation fun getString(key: String): String {
        calls += "read:$key"
        return encoded
    }

    @Implementation fun fetch(interval: Long): Task<Void> {
        calls += "fetch:$interval"
        return Tasks.forResult(null)
    }

    companion object {
        var instance: FirebaseRemoteConfig? = null
        var encoded = ""
        var listener: ConfigUpdateListener? = null
        var activation = TaskCompletionSource<Boolean>()
        var removals = 0
        val calls = mutableListOf<String>()
        var initialization = TaskCompletionSource<FirebaseRemoteConfigInfo>()
        var defaults = TaskCompletionSource<Void>()

        @JvmStatic @Implementation
        fun getInstance(app: FirebaseApp): FirebaseRemoteConfig {
            check(app === LauncherScheduleAppShadow.app)
            return instance ?: Shadow.newInstanceOf(FirebaseRemoteConfig::class.java).also { instance = it }
        }
    }
}

@Implements(FirebaseApp::class)
class LauncherScheduleAppShadow {
    @Implementation fun getName() = FirebaseApp.DEFAULT_APP_NAME

    companion object {
        var app: FirebaseApp? = null

        @JvmStatic @Implementation
        fun getApps(context: Context): List<FirebaseApp> {
            require(context.packageName.isNotBlank())
            return listOfNotNull(app)
        }

        @JvmStatic @Implementation
        fun initializeApp(context: Context, options: FirebaseOptions, name: String): FirebaseApp {
            require(context.packageName.isNotBlank())
            check(options.applicationId == "test-launcher")
            check(name == FirebaseApp.DEFAULT_APP_NAME)
            return Shadow.newInstanceOf(FirebaseApp::class.java).also { app = it }
        }
    }
}
