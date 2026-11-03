package br.gov.caixa.loterias.feature.onboarding.data.config

import android.os.Looper
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.ConfigUpdateListenerRegistration
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], shadows = [AppModulesRemoteConfigShadow::class])
class FirebaseAppModulesConfigurationSourceTest {
    @Before fun reset() {
        AppModulesRemoteConfigShadow.calls.clear()
        AppModulesRemoteConfigShadow.removals = 0
        AppModulesRemoteConfigShadow.failFetch = false
        AppModulesRemoteConfigShadow.failActivation = false
    }

    @Test fun readsOnlyTheModulesParameterAndInitializesWithoutChangingSettings() = runBlocking {
        val source = source()
        val pending = async(Dispatchers.Unconfined) { source.initialize() }
        idle()
        pending.await()
        assertEquals("encoded", source.read())
        assertEquals(listOf("initialize", "read:app_modules"), AppModulesRemoteConfigShadow.calls)
    }

    @Test fun refreshPublishesActivationAndDoesNotChangeSharedSettingsOrDefaults() = runBlocking {
        val remote = remote()
        val source = FirebaseAppModulesConfigurationSource { remote }
        val changes = AtomicInteger()
        source.observe { changes.incrementAndGet() }.use {
            val pending = async(Dispatchers.Unconfined) { source.refresh() }
            idle()
            pending.await()
            assertEquals(listOf("fetch:0", "activate"), AppModulesRemoteConfigShadow.calls)
            assertEquals(1, changes.get())
        }
    }

    @Test fun realtimeIgnoresUnrelatedKeysAndRemovesListener() {
        val source = source()
        var changes = 0
        val observation = source.observe { changes++ }
        AppModulesRemoteConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("force_update")))
        idle()
        assertTrue(AppModulesRemoteConfigShadow.calls.isEmpty())
        AppModulesRemoteConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("app_modules")))
        idle()
        assertEquals(1, changes)
        observation.close()
        observation.close()
        AppModulesRemoteConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("app_modules")))
        idle()
        assertEquals(1, changes)
        assertEquals(1, AppModulesRemoteConfigShadow.removals)
    }

    @Test fun failedFetchDoesNotActivateOrPublish() = runBlocking {
        AppModulesRemoteConfigShadow.failFetch = true
        val source = source()
        var changes = 0
        source.observe { changes++ }.use {
            val pending = async(Dispatchers.Unconfined) { runCatching { source.refresh() } }
            idle()
            assertTrue(pending.await().isFailure)
            assertEquals(listOf("fetch:0"), AppModulesRemoteConfigShadow.calls)
            assertEquals(0, changes)
        }
    }

    @Test fun failedRealtimeActivationAndStreamErrorKeepTheCurrentConfiguration() {
        AppModulesRemoteConfigShadow.failActivation = true
        var changes = 0
        source().observe { changes++ }.use {
            AppModulesRemoteConfigShadow.listener!!.onError(FirebaseRemoteConfigException("offline"))
            AppModulesRemoteConfigShadow.listener!!.onUpdate(ConfigUpdate.create(setOf("app_modules")))
            idle()
            assertEquals(0, changes)
        }
    }

    private fun source() = FirebaseAppModulesConfigurationSource { remote() }
    private fun remote(): FirebaseRemoteConfig = Shadow.newInstanceOf(FirebaseRemoteConfig::class.java)
    private fun idle() = shadowOf(Looper.getMainLooper()).idle()
}

@Implements(FirebaseRemoteConfig::class)
class AppModulesRemoteConfigShadow {
    @Implementation fun getString(key: String): String {
        calls += "read:$key"
        return "encoded"
    }

    @Implementation fun ensureInitialized(): Task<FirebaseRemoteConfigInfo> {
        calls += "initialize"
        return Tasks.forResult(null)
    }

    @Implementation fun fetch(interval: Long): Task<Void> {
        calls += "fetch:$interval"
        return if (failFetch) Tasks.forException(IllegalStateException("offline")) else Tasks.forResult(null)
    }

    @Implementation fun activate(): Task<Boolean> {
        calls += "activate"
        return if (failActivation) Tasks.forException(IllegalStateException("activation")) else Tasks.forResult(false)
    }

    @Implementation fun addOnConfigUpdateListener(value: ConfigUpdateListener): ConfigUpdateListenerRegistration {
        listener = value
        return ConfigUpdateListenerRegistration { removals++ }
    }

    companion object {
        val calls = mutableListOf<String>()
        var removals = 0
        var listener: ConfigUpdateListener? = null
        var failFetch = false
        var failActivation = false
    }
}
