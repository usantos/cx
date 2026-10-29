package br.gov.caixa.loterias.apostas.runtime.launcher

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ShortcutManager
import android.os.Looper
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.MainActivity
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.feature.onboarding.di.createLauncherIconViewModel
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.presentation.state.LauncherIconApplyOutcome
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.ConfigUpdateListenerRegistration
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
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
import org.xmlpull.v1.XmlPullParser

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [35],
    application = Application::class,
    shadows = [CalendarIntegrationRemoteShadow::class, CalendarIntegrationAppShadow::class]
)
class LauncherIconCalendarIntegrationTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Before fun reset() {
        CalendarIntegrationRemoteShadow.listeners.clear()
        CalendarIntegrationRemoteShadow.calls.clear()
        CalendarIntegrationRemoteShadow.readsOnMain.clear()
        CalendarIntegrationRemoteShadow.activated = null
        CalendarIntegrationRemoteShadow.defaults = ""
        CalendarIntegrationAppShadow.app = null
    }

    @Test fun productionCompositionReadsCachedCalendarPublishesFourShortcutsAndSchedulesWithoutFetching() {
        CalendarIntegrationRemoteShadow.activated = """{"timezone":"America/Sao_Paulo","campaigns":[
            {"icon":"outubro_rosa","start":"01-01","end":"12-31"}]}"""
        refreshAndAssert(LauncherIconPreset.OUTUBRO_ROSA)
    }

    @Test fun publishedCalendarChangesSelectionWhileTheSameModelRemainsReady() {
        val options = FirebaseOptions.Builder()
            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
            .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
            .build()
        CalendarIntegrationRemoteShadow.activated = """{"timezone":"America/Sao_Paulo","campaigns":[
            {"icon":"outubro_rosa","start":"01-01","end":"12-31"}]}"""
        val model = createLauncherIconViewModel(context, options)
        model.load()
        model.complete(LauncherIconApplyOutcome.APPLIED)
        assertEquals(LauncherIconPreset.OUTUBRO_ROSA, model.state.value.preset)
        assertTrue("A seleção precisa observar publicações do calendário", CalendarIntegrationRemoteShadow.listeners.isNotEmpty())
        CalendarIntegrationRemoteShadow.activated = """{"timezone":"America/Sao_Paulo","campaigns":[
            {"icon":"novembro_azul","start":"01-01","end":"12-31"}]}"""
        CalendarIntegrationRemoteShadow.listeners.toList().forEach {
            it.onUpdate(ConfigUpdate.create(setOf("app_launcher_icon")))
        }
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(LauncherIconPreset.NOVEMBRO_AZUL, model.state.value.preset)
        assertEquals(LauncherIconApplyOutcome.APPLIED, model.state.value.outcome)
        val preset = requireNotNull(model.state.value.preset)
        assertEquals(LauncherIconApplyResult.APPLIED, LauncherIconController(context).apply(preset))
        assertEquals(setOf(preset), AndroidLauncherIconPlatform(context).enabledPresets())
        assertEquals(EXPECTED_SHORTCUT_COUNT, context.getSystemService(ShortcutManager::class.java).dynamicShortcuts.size)
        assertTrue(AndroidLauncherIconPlatform(context).shortcutsMatch(preset))
    }

    @Test fun freshlyCreatedRuntimeResolvesTheRealXmlDefaultsBeforeApplyingTheIcon() {
        val month = Instant.now().atZone(ZoneId.of("America/Sao_Paulo")).monthValue
        val expected = when (month) {
            AUGUST -> LauncherIconPreset.AGOSTO_LILAS
            SEPTEMBER -> LauncherIconPreset.SETEMBRO_AMARELO
            OCTOBER -> LauncherIconPreset.OUTUBRO_ROSA
            NOVEMBER -> LauncherIconPreset.NOVEMBRO_AZUL
            else -> LauncherIconPreset.DEFAULT
        }
        refreshAndAssert(expected)
    }

    @Test fun defaultReceiverEvaluatesOnMainAndFinishesWithOneAliasFourShortcutsAndAnAlarm() = runBlocking {
        CalendarIntegrationRemoteShadow.activated = """{"timezone":"America/Sao_Paulo","campaigns":[
            {"icon":"outubro_rosa","start":"01-01","end":"12-31"}]}"""
        val receiver = LauncherIconScheduleReceiver()
        context.registerReceiver(receiver, IntentFilter(LauncherIconScheduleReceiver.ACTION_EVALUATE), Context.RECEIVER_NOT_EXPORTED)
        try {
            context.sendBroadcast(Intent(LauncherIconScheduleReceiver.ACTION_EVALUATE).setPackage(context.packageName))
            shadowOf(Looper.getMainLooper()).idle()
            val receiverShadow = shadowOf(receiver)
            assertTrue(receiverShadow.wentAsync())
            val pending = shadowOf(requireNotNull(receiverShadow.originalPendingResult))
            withTimeout(RECEIVER_TIMEOUT_MILLIS) {
                while (!pending.future.isDone) {
                    shadowOf(Looper.getMainLooper()).idle()
                    delay(1)
                }
            }
            assertEquals(listOf(true, true), CalendarIntegrationRemoteShadow.readsOnMain)
            assertEquals(
                listOf("initialize", "defaults:${R.xml.remote_config_defaults}", "read:app_launcher_icon", "read:app_launcher_icon"),
                CalendarIntegrationRemoteShadow.calls
            )
            val launches = context.packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.packageName),
                0
            )
            assertEquals(listOf(launcherAlias(context, LauncherIconPreset.OUTUBRO_ROSA).className), launches.map { it.activityInfo.name })
            val shortcuts = context.getSystemService(ShortcutManager::class.java).dynamicShortcuts
            assertEquals(setOf("result_megasena", "result_lotofacil", "result_loteca", "results_all"), shortcuts.map { it.id }.toSet())
            assertTrue(shortcuts.all { it.activity == launcherAlias(context, LauncherIconPreset.OUTUBRO_ROSA) })
            assertTrue(shortcuts.all { it.intent?.component?.className == MainActivity::class.java.name })
            assertEquals(1, shadowOf(context.getSystemService(AlarmManager::class.java)).scheduledAlarms.size)
            assertTrue(pending.future.isDone)
        } finally {
            context.unregisterReceiver(receiver)
        }
    }

    private fun refreshAndAssert(expected: LauncherIconPreset) = runBlocking {
        val before = Instant.now()
        val pending = async(Dispatchers.Unconfined) { LauncherIconScheduleRuntime(context).refresh() }
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(LauncherIconApplyResult.APPLIED, pending.await())
        assertEquals(
            listOf("initialize", "defaults:${R.xml.remote_config_defaults}", "read:app_launcher_icon", "read:app_launcher_icon"),
            CalendarIntegrationRemoteShadow.calls
        )
        val launches = context.packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.packageName),
            0
        )
        assertEquals(listOf(launcherAlias(context, expected).className), launches.map { it.activityInfo.name })
        val shortcuts = context.getSystemService(ShortcutManager::class.java).dynamicShortcuts
        assertEquals(setOf("result_megasena", "result_lotofacil", "result_loteca", "results_all"), shortcuts.map { it.id }.toSet())
        assertTrue(shortcuts.all { it.activity == launcherAlias(context, expected) })
        assertTrue(shortcuts.all { it.intent?.component?.className == MainActivity::class.java.name })
        val alarm = shadowOf(context.getSystemService(AlarmManager::class.java)).scheduledAlarms.single()
        assertTrue(alarm.triggerAtMs > before.toEpochMilli())
        val next = Instant.ofEpochMilli(alarm.triggerAtMs).atZone(ZoneId.of("America/Sao_Paulo"))
        assertEquals(java.time.LocalTime.MIDNIGHT, next.toLocalTime())
        assertTrue(alarm.isAllowWhileIdle)
        assertTrue(alarm.windowLengthMs != 0L)
        assertTrue(shadowOf(alarm.operation).isImmutable)
        assertEquals(LauncherIconScheduleReceiver.ACTION_EVALUATE, shadowOf(alarm.operation).savedIntent.action)
    }
}

@Implements(FirebaseRemoteConfig::class)
class CalendarIntegrationRemoteShadow {
    @Implementation fun addOnConfigUpdateListener(listener: ConfigUpdateListener): ConfigUpdateListenerRegistration {
        listeners += listener
        return ConfigUpdateListenerRegistration { listeners.remove(listener) }
    }

    @Implementation fun activate(): Task<Boolean> = Tasks.forResult(true)

    @Implementation fun ensureInitialized(): Task<FirebaseRemoteConfigInfo> {
        calls += "initialize"
        return Tasks.forResult(null)
    }

    @Implementation fun setDefaultsAsync(resource: Int): Task<Void> {
        calls += "defaults:$resource"
        val context = RuntimeEnvironment.getApplication()
        context.resources.getXml(resource).use { parser ->
            var key = ""
            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                when {
                    parser.eventType != XmlPullParser.START_TAG -> Unit
                    parser.name == "key" -> key = parser.nextText()
                    parser.name == "value" && key == "app_launcher_icon" -> defaults = parser.nextText()
                }
                parser.next()
            }
        }
        return Tasks.forResult(null)
    }

    @Implementation fun getString(key: String): String {
        calls += "read:$key"
        readsOnMain += Looper.myLooper() === Looper.getMainLooper()
        check(activated != null || defaults.isNotEmpty()) { "Os defaults devem estar disponíveis antes da leitura" }
        return activated ?: defaults
    }

    @Implementation fun fetch(interval: Long): Task<Void> = error("A avaliação local não deve buscar pela rede: $interval")

    @Implementation fun fetchAndActivate(): Task<Boolean> = error("A avaliação local não deve buscar ou ativar pela rede")

    companion object {
        val listeners = mutableListOf<ConfigUpdateListener>()
        val calls = mutableListOf<String>()
        val readsOnMain = mutableListOf<Boolean>()
        var activated: String? = null
        var defaults = ""

        @JvmStatic @Implementation
        fun getInstance(app: FirebaseApp): FirebaseRemoteConfig {
            check(app === CalendarIntegrationAppShadow.app)
            return Shadow.newInstanceOf(FirebaseRemoteConfig::class.java)
        }
    }
}

@Implements(FirebaseApp::class)
class CalendarIntegrationAppShadow {
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
            check(name == FirebaseApp.DEFAULT_APP_NAME)
            check(options.applicationId == BuildConfig.FIREBASE_APP_ID)
            check(options.apiKey == BuildConfig.FIREBASE_API_KEY)
            check(options.projectId == BuildConfig.FIREBASE_PROJECT_ID)
            check(options.gcmSenderId == BuildConfig.FIREBASE_SENDER_ID)
            return Shadow.newInstanceOf(FirebaseApp::class.java).also { app = it }
        }
    }
}

private const val AUGUST = 8
private const val SEPTEMBER = 9
private const val OCTOBER = 10
private const val NOVEMBER = 11

private const val RECEIVER_TIMEOUT_MILLIS = 5_000L

private const val EXPECTED_SHORTCUT_COUNT = 4
