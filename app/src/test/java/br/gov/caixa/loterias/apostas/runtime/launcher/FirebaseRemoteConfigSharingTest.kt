package br.gov.caixa.loterias.apostas.runtime.launcher

import android.app.Application
import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.security.keyset.FirebaseKeySetOptions
import br.gov.caixa.loterias.core.security.keyset.FirebaseRemotePublicKeySetSource
import br.gov.caixa.loterias.core.testing.coroutines.MainDispatcherRule
import br.gov.caixa.loterias.feature.onboarding.data.repository.FirebaseForceUpdateRepositoryImpl
import br.gov.caixa.loterias.feature.onboarding.data.repository.FirebaseLauncherIconConfigurationRepositoryImpl
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetActiveForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.GetLauncherIconPresetUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ObserveForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ObserveLauncherIconCalendarUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.RefreshForceUpdateUseCase
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.ForceUpdateViewModel
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.LauncherIconViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.ConfigUpdateListenerRegistration
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.IdentityHashMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.annotation.RealObject
import org.robolectric.shadow.api.Shadow

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class, shadows = [SharedFirebaseAppShadow::class, SharedRemoteConfigShadow::class])
class FirebaseRemoteConfigSharingTest {
    @get:Rule val main = MainDispatcherRule(UnconfinedTestDispatcher())

    private val context get() = RuntimeEnvironment.getApplication()
    private val options = FirebaseOptions.Builder().setApplicationId("shared-app").setApiKey("test-key")
        .setProjectId("test-project").setGcmSenderId("test-sender").build()

    @Before fun reset() {
        SharedFirebaseAppShadow.apps.clear()
        SharedFirebaseAppShadow.names.clear()
        SharedRemoteConfigShadow.apps.clear()
        SharedRemoteConfigShadow.instances.clear()
        SharedRemoteConfigShadow.listeners.clear()
        SharedRemoteConfigShadow.encoded = calendar("outubro_rosa")
        SharedRemoteConfigShadow.required = false
        SharedRemoteConfigShadow.keysetActivation = TaskCompletionSource()
    }

    @Test fun pushKeysetsAndIconsShareTheDefaultFirebaseClient() {
        FirebaseApp.initializeApp(context, options, FirebaseApp.DEFAULT_APP_NAME)
        keysets()
        icons()
        assertSharedClient()
    }

    @Test fun iconsBeforeKeysetsStillCreateOnlyOneFirebaseClient() {
        icons()
        keysets()
        assertSharedClient()
    }

    @Test fun keysetsBeforeIconsStillCreateOnlyOneFirebaseClient() {
        keysets()
        icons()
        assertSharedClient()
    }

    @Test fun keysetActivationAfterResumeUpdatesBothObserversWithoutAStreamEvent() = runBlocking {
        val force = FirebaseForceUpdateRepositoryImpl(context, options, 1)
        val launcher = FirebaseLauncherIconConfigurationRepositoryImpl(
            context,
            options,
            Clock.fixed(Instant.parse("2026-10-15T12:00:00Z"), ZoneOffset.UTC)
        )
        val flags = mutableListOf<Boolean>()
        val icons = mutableListOf<LauncherIconPreset>()
        force.observe(flags::add).use {
            launcher.observe { icons += launcher.activePreset }.use {
                val refresh = async(Dispatchers.Unconfined) { force.refresh() }
                shadowOf(Looper.getMainLooper()).idle()
                refresh.await()
                val pending = async(Dispatchers.Unconfined) { keysets().fetch() }
                shadowOf(Looper.getMainLooper()).idle()
                SharedRemoteConfigShadow.required = true
                SharedRemoteConfigShadow.encoded = calendar("novembro_azul")
                SharedRemoteConfigShadow.keysetActivation.setResult(false)
                shadowOf(Looper.getMainLooper()).idle()
                pending.await()
                assertEquals(listOf(true), flags)
                assertEquals(listOf(LauncherIconPreset.NOVEMBRO_AZUL), icons)
            }
        }
    }

    @Test fun foregroundRecoveryUpdatesTheCalendarTwiceWithoutStreamEvents() {
        val force = FirebaseForceUpdateRepositoryImpl(context, options, 1)
        val launcher = FirebaseLauncherIconConfigurationRepositoryImpl(
            context,
            options,
            Clock.fixed(Instant.parse("2026-10-15T12:00:00Z"), ZoneOffset.UTC)
        )
        val forceModel = ForceUpdateViewModel(
            GetActiveForceUpdateUseCase(force),
            RefreshForceUpdateUseCase(force),
            ObserveForceUpdateUseCase(force)
        )
        val launcherModel = LauncherIconViewModel(GetLauncherIconPresetUseCase(launcher), ObserveLauncherIconCalendarUseCase(launcher))
        val store = ViewModelStore().apply {
            put("force-update", forceModel)
            put("launcher-icon", launcherModel)
        }
        try {
            launcherModel.load()
            forceModel.startForegroundUpdates()
            shadowOf(Looper.getMainLooper()).idle()
            assertEquals(LauncherIconPreset.OUTUBRO_ROSA, launcherModel.state.value.preset)
            SharedRemoteConfigShadow.encoded = """{"timezone":"America/Sao_Paulo","campaigns":[]}"""
            main.dispatcher.scheduler.advanceTimeBy(FOREGROUND_RECOVERY_INTERVAL_MILLIS)
            main.dispatcher.scheduler.runCurrent()
            shadowOf(Looper.getMainLooper()).idle()
            assertEquals(LauncherIconPreset.DEFAULT, launcherModel.state.value.preset)
            SharedRemoteConfigShadow.encoded = calendar("novembro_azul")
            main.dispatcher.scheduler.advanceTimeBy(FOREGROUND_RECOVERY_INTERVAL_MILLIS)
            main.dispatcher.scheduler.runCurrent()
            shadowOf(Looper.getMainLooper()).idle()
            assertEquals(LauncherIconPreset.NOVEMBRO_AZUL, launcherModel.state.value.preset)
            assertEquals(2, SharedRemoteConfigShadow.listeners.size)
            forceModel.stopForegroundUpdates()
            SharedRemoteConfigShadow.encoded = calendar("outubro_rosa")
            main.dispatcher.scheduler.advanceTimeBy(FOREGROUND_RECOVERY_INTERVAL_MILLIS * BACKGROUND_RECOVERY_INTERVALS)
            main.dispatcher.scheduler.runCurrent()
            shadowOf(Looper.getMainLooper()).idle()
            assertEquals(LauncherIconPreset.NOVEMBRO_AZUL, launcherModel.state.value.preset)
            forceModel.startForegroundUpdates()
            shadowOf(Looper.getMainLooper()).idle()
            assertEquals(LauncherIconPreset.OUTUBRO_ROSA, launcherModel.state.value.preset)
        } finally {
            store.clear()
        }
        assertEquals(0, SharedRemoteConfigShadow.listeners.size)
    }

    private fun calendar(icon: String) = """{"timezone":"America/Sao_Paulo","campaigns":[
        {"icon":"$icon","start":"01-01","end":"12-31"}]}"""

    private fun keysets() = FirebaseRemotePublicKeySetSource.create(
        context,
        FirebaseKeySetOptions(
            options.applicationId,
            requireNotNull(options.apiKey),
            requireNotNull(options.projectId),
            requireNotNull(options.gcmSenderId)
        )
    )

    private fun icons() = FirebaseLauncherIconConfigurationRepositoryImpl(context, options).activePreset

    private fun assertSharedClient() {
        assertEquals(setOf(FirebaseApp.DEFAULT_APP_NAME), SharedFirebaseAppShadow.apps.keys)
        SharedRemoteConfigShadow.apps.forEach { assertSame(SharedFirebaseAppShadow.apps.getValue(FirebaseApp.DEFAULT_APP_NAME), it) }
    }
}

/** Modela somente a fronteira externa do SDK; os dois adaptadores de produção são executados integralmente. */
@Implements(FirebaseApp::class)
class SharedFirebaseAppShadow {
    @RealObject lateinit var realApp: FirebaseApp

    @Implementation fun getName(): String = names.getValue(realApp)

    companion object {
        val apps = linkedMapOf<String, FirebaseApp>()
        val names = IdentityHashMap<FirebaseApp, String>()

        @JvmStatic @Implementation
        fun getApps(context: Context): List<FirebaseApp> {
            check(context.packageName.isNotBlank())
            return apps.values.toList()
        }

        @JvmStatic @Implementation
        fun initializeApp(context: Context, options: FirebaseOptions, name: String): FirebaseApp {
            check(context.packageName.isNotBlank() && options.applicationId.isNotBlank())
            check(name !in apps)
            return Shadow.newInstanceOf(FirebaseApp::class.java).also {
                apps[name] = it
                names[it] = name
            }
        }
    }
}

@Implements(FirebaseRemoteConfig::class)
class SharedRemoteConfigShadow {
    @Implementation fun getString(key: String): String {
        check(key == "app_launcher_icon" || key == "tls_pinning_keyset")
        return if (key == "app_launcher_icon") encoded else ""
    }

    @Implementation fun getBoolean(key: String): Boolean {
        check(key == "force_update")
        return required
    }

    @Implementation fun setDefaultsAsync(resource: Int): Task<Void> {
        check(resource > 0)
        return Tasks.forResult(null)
    }

    @Implementation fun setConfigSettingsAsync(settings: FirebaseRemoteConfigSettings): Task<Void> {
        check(settings.fetchTimeoutInSeconds > 0)
        return Tasks.forResult(null)
    }

    @Implementation fun fetch(interval: Long): Task<Void> {
        check(interval >= 0)
        return Tasks.forResult(null)
    }

    @Implementation fun activate(): Task<Boolean> = Tasks.forResult(false)

    @Implementation fun fetchAndActivate(): Task<Boolean> = keysetActivation.task

    @Implementation fun addOnConfigUpdateListener(listener: ConfigUpdateListener): ConfigUpdateListenerRegistration {
        listeners += listener
        return ConfigUpdateListenerRegistration { listeners.remove(listener) }
    }
    companion object {
        val apps = mutableListOf<FirebaseApp>()
        val instances = IdentityHashMap<FirebaseApp, FirebaseRemoteConfig>()
        val listeners = mutableListOf<ConfigUpdateListener>()
        var encoded = ""
        var required = false
        var keysetActivation = TaskCompletionSource<Boolean>()

        @JvmStatic @Implementation
        fun getInstance(app: FirebaseApp): FirebaseRemoteConfig {
            apps += app
            return instances.getOrPut(app) { Shadow.newInstanceOf(FirebaseRemoteConfig::class.java) }
        }
    }
}

private const val FOREGROUND_RECOVERY_INTERVAL_MILLIS = 30_000L

private const val BACKGROUND_RECOVERY_INTERVALS = 3L
