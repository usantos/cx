package br.gov.caixa.loterias.feature.onboarding.data.repository

import android.content.Context
import android.os.Looper
import br.gov.caixa.loterias.feature.onboarding.data.config.publishFeatureConfigActivations
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
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
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
@Config(sdk = [35], shadows = [ForceUpdateConfigShadow::class, ForceUpdateAppShadow::class])
class FirebaseForceUpdateRepositoryImplTest {
    private val defaultsResource = 1
    private val options = FirebaseOptions.Builder()
        .setApplicationId("force-update-test")
        .setApiKey("test-api-key")
        .setProjectId("test-project")
        .build()

    @Before fun reset() {
        ForceUpdateAppShadow.existing = null
        ForceUpdateAppShadow.suppliedOptions = null
        ForceUpdateAppShadow.initializations = 0
        ForceUpdateAppShadow.failInitialization = false
        ForceUpdateConfigShadow.reset()
    }

    @Test fun `active reads current value and reuses the named Firebase application`() {
        val repository = repository()
        assertFalse(repository.active)
        ForceUpdateConfigShadow.activeValue = true
        assertTrue(repository.active)
        assertTrue(repository().active)
        assertEquals(listOf("force_update", "force_update", "force_update"), ForceUpdateConfigShadow.keys)
        assertEquals(1, ForceUpdateAppShadow.initializations)
        assertSame(options, ForceUpdateAppShadow.suppliedOptions)
        assertSame(ForceUpdateAppShadow.existing, ForceUpdateConfigShadow.selectedApp)
    }

    @Test fun `unavailable Firebase and unreadable active value fall back to false`() {
        ForceUpdateAppShadow.failInitialization = true
        assertFalse(repository().active)
        ForceUpdateAppShadow.failInitialization = false
        ForceUpdateConfigShadow.failRead = true
        assertFalse(repository().active)
    }

    @Test fun `refresh installs defaults and settings before fetching immediately and activating`() {
        ForceUpdateConfigShadow.activeValue = true
        assertTrue(refresh(repository()))
        assertEquals(listOf("defaults", "settings", "fetch", "activate", "read"), ForceUpdateConfigShadow.calls)
        assertEquals(defaultsResource, ForceUpdateConfigShadow.defaultsResource)
        assertEquals(0L, ForceUpdateConfigShadow.fetchInterval)
        assertEquals(EXPECTED_FETCH_TIMEOUT_SECONDS, ForceUpdateConfigShadow.settings!!.fetchTimeoutInSeconds)
        assertEquals(EXPECTED_FETCH_INTERVAL_SECONDS, ForceUpdateConfigShadow.settings!!.minimumFetchIntervalInSeconds)
    }

    @Test fun `failed fetch preserves active configuration without activating`() {
        ForceUpdateConfigShadow.activeValue = true
        ForceUpdateConfigShadow.fetchTask = Tasks.forException(IllegalStateException("offline"))
        assertTrue(refresh(repository()))
        assertEquals(listOf("defaults", "settings", "fetch", "read"), ForceUpdateConfigShadow.calls)
    }

    @Test fun `failed defaults settings and activation still return the current active value`() {
        ForceUpdateConfigShadow.defaultsTask = Tasks.forException(IllegalStateException("defaults"))
        ForceUpdateConfigShadow.settingsTask = Tasks.forException(IllegalStateException("settings"))
        ForceUpdateConfigShadow.activationTask = Tasks.forException(IllegalStateException("activation"))
        ForceUpdateConfigShadow.activeValue = true
        assertTrue(refresh(repository()))
        assertEquals(listOf("defaults", "settings", "fetch", "activate", "read"), ForceUpdateConfigShadow.calls)
    }

    @Test fun `relevant live update waits for activation then reports the latest active value`() {
        val activation = TaskCompletionSource<Boolean>()
        ForceUpdateConfigShadow.activationTask = activation.task
        val values = mutableListOf<Boolean>()
        val observation = repository().observe(values::add)
        ForceUpdateConfigShadow.calls.clear()
        assertNotNull(ForceUpdateConfigShadow.listener)
        ForceUpdateConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("force_update")))
        idle()
        assertTrue(values.isEmpty())
        ForceUpdateConfigShadow.activeValue = true
        activation.setResult(false)
        idle()
        assertEquals(listOf(true), values)
        assertEquals(listOf("activate", "read"), ForceUpdateConfigShadow.calls)
        observation.close()
        assertEquals(1, ForceUpdateConfigShadow.removals)
    }

    @Test fun `unrelated updates and stream errors do not activate or notify`() {
        val values = mutableListOf<Boolean>()
        repository().observe(values::add).use {
            ForceUpdateConfigShadow.calls.clear()
            ForceUpdateConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("launcher_icon")))
            ForceUpdateConfigShadow.listener!!.onError(FirebaseRemoteConfigException("offline"))
            idle()
            assertTrue(values.isEmpty())
            assertTrue(ForceUpdateConfigShadow.calls.isEmpty())
        }
        assertEquals(1, ForceUpdateConfigShadow.removals)
    }

    @Test fun `failed live activation does not read or emit the active configuration`() {
        ForceUpdateConfigShadow.activationTask = Tasks.forException(IllegalStateException("activation"))
        val values = mutableListOf<Boolean>()
        repository().observe(values::add).use {
            ForceUpdateConfigShadow.calls.clear()
            ForceUpdateConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("force_update", "other")))
            idle()
            assertTrue(values.isEmpty())
            assertEquals(listOf("activate"), ForceUpdateConfigShadow.calls)
        }
    }

    @Test fun `cancelled refresh ignores the late fetch completion`() = runBlocking {
        val fetch = TaskCompletionSource<Void>()
        ForceUpdateConfigShadow.fetchTask = fetch.task
        val repository = repository()
        val pending = async(Dispatchers.Unconfined) { repository.refresh() }
        idle()
        assertEquals(listOf("defaults", "settings", "fetch"), ForceUpdateConfigShadow.calls)
        pending.cancel()
        pending.join()
        fetch.setResult(null)
        idle()
        assertTrue(pending.isCancelled)
        assertEquals(listOf("defaults", "settings", "fetch"), ForceUpdateConfigShadow.calls)
    }

    @Test fun failedLiveReadDoesNotClearAPreviouslyRequiredUpdate() {
        ForceUpdateConfigShadow.activeValue = true
        val values = mutableListOf<Boolean>()
        repository().observe(values::add).use {
            ForceUpdateConfigShadow.failRead = true
            ForceUpdateConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("force_update")))
            idle()
            assertTrue(values.isEmpty())
        }
    }

    @Test fun overlappingActivationsCannotPublishAnOlderFalseAfterANewerTrue() {
        ForceUpdateConfigShadow.activeValue = true
        val values = CopyOnWriteArrayList<Boolean>()
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)
        repository().observe { value ->
            if (!value) {
                entered.countDown()
                check(release.await(CONCURRENT_TIMEOUT_SECONDS, TimeUnit.SECONDS))
            }
            values += value
        }.use {
            try {
                val remote = requireNotNull(ForceUpdateConfigShadow.selectedRemote)
                ForceUpdateConfigShadow.activeValue = false
                val older = executor.submit { publishFeatureConfigActivations(remote) }
                assertTrue(entered.await(CONCURRENT_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                ForceUpdateConfigShadow.activeValue = true
                val newer = executor.submit { publishFeatureConfigActivations(remote) }
                try {
                    newer.get(1, TimeUnit.SECONDS)
                } catch (_: TimeoutException) {
                    assertFalse(newer.isDone)
                }
                release.countDown()
                older.get(CONCURRENT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                newer.get(CONCURRENT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                assertEquals(true, values.last())
            } finally {
                release.countDown()
                executor.shutdownNow()
            }
        }
    }

    private fun repository() = FirebaseForceUpdateRepositoryImpl(
        RuntimeEnvironment.getApplication(),
        options,
        defaultsResource
    )

    private fun refresh(repository: FirebaseForceUpdateRepositoryImpl): Boolean = runBlocking {
        val pending = async(Dispatchers.Unconfined) { repository.refresh() }
        idle()
        pending.await()
    }

    private fun idle() = shadowOf(Looper.getMainLooper()).idle()
}

@Implements(FirebaseRemoteConfig::class)
class ForceUpdateConfigShadow {
    @Implementation fun getBoolean(key: String): Boolean {
        calls += "read"
        keys += key
        check(!failRead) { "unreadable" }
        return activeValue
    }

    @Implementation fun setDefaultsAsync(resource: Int): Task<Void> {
        calls += "defaults"
        defaultsResource = resource
        return defaultsTask
    }

    @Implementation fun setConfigSettingsAsync(value: FirebaseRemoteConfigSettings): Task<Void> {
        calls += "settings"
        settings = value
        return settingsTask
    }

    @Implementation fun fetch(interval: Long): Task<Void> {
        calls += "fetch"
        fetchInterval = interval
        return fetchTask
    }

    @Implementation fun activate(): Task<Boolean> {
        calls += "activate"
        return activationTask
    }

    @Implementation fun addOnConfigUpdateListener(value: ConfigUpdateListener): ConfigUpdateListenerRegistration {
        listener = value
        return ConfigUpdateListenerRegistration { removals++ }
    }

    companion object {
        val calls = mutableListOf<String>()
        val keys = mutableListOf<String>()
        var activeValue = false
        var failRead = false
        var defaultsResource: Int? = null
        var fetchInterval: Long? = null
        var settings: FirebaseRemoteConfigSettings? = null
        var listener: ConfigUpdateListener? = null
        var selectedRemote: FirebaseRemoteConfig? = null
        var selectedApp: FirebaseApp? = null
        var removals = 0
        var defaultsTask: Task<Void> = Tasks.forResult(null)
        var settingsTask: Task<Void> = Tasks.forResult(null)
        var fetchTask: Task<Void> = Tasks.forResult(null)
        var activationTask: Task<Boolean> = Tasks.forResult(false)

        fun reset() {
            calls.clear()
            keys.clear()
            activeValue = false
            failRead = false
            defaultsResource = null
            fetchInterval = null
            settings = null
            listener = null
            selectedRemote = null
            selectedApp = null
            removals = 0
            defaultsTask = Tasks.forResult(null)
            settingsTask = Tasks.forResult(null)
            fetchTask = Tasks.forResult(null)
            activationTask = Tasks.forResult(false)
        }

        @JvmStatic @Implementation
        fun getInstance(app: FirebaseApp): FirebaseRemoteConfig {
            selectedApp = app
            return Shadow.newInstanceOf(FirebaseRemoteConfig::class.java).also { selectedRemote = it }
        }
    }
}

@Implements(FirebaseApp::class)
class ForceUpdateAppShadow {
    @Implementation fun getName() = FirebaseApp.DEFAULT_APP_NAME

    companion object {
        var existing: FirebaseApp? = null
        var suppliedOptions: FirebaseOptions? = null
        var initializations = 0
        var failInitialization = false

        @JvmStatic @Implementation
        fun getApps(context: Context): List<FirebaseApp> {
            require(context.packageName.isNotBlank())
            return listOfNotNull(existing)
        }

        @JvmStatic @Implementation
        fun initializeApp(context: Context, options: FirebaseOptions, name: String): FirebaseApp {
            require(context.packageName.isNotBlank())
            check(!failInitialization) { "Firebase unavailable" }
            check(name == FirebaseApp.DEFAULT_APP_NAME)
            initializations++
            suppliedOptions = options
            return Shadow.newInstanceOf(FirebaseApp::class.java).also { existing = it }
        }
    }
}

private const val EXPECTED_FETCH_TIMEOUT_SECONDS = 10L
private const val EXPECTED_FETCH_INTERVAL_SECONDS = 3_600L

private const val CONCURRENT_TIMEOUT_SECONDS = 5L
