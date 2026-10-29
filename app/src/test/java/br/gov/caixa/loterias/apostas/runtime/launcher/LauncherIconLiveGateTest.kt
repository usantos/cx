package br.gov.caixa.loterias.apostas.runtime.launcher

import android.app.Application
import android.content.Intent
import android.content.pm.ShortcutManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.apostas.BuildConfig
import br.gov.caixa.loterias.apostas.HomeDrawerTestActivity
import br.gov.caixa.loterias.apostas.composition.startup.LauncherIconGate
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.ConfigUpdate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [35],
    application = Application::class,
    shadows = [CalendarIntegrationRemoteShadow::class, CalendarIntegrationAppShadow::class]
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class LauncherIconLiveGateTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private var mounts = 0
    private var disposals = 0

    @Before fun reset() {
        CalendarIntegrationRemoteShadow.listeners.clear()
        CalendarIntegrationRemoteShadow.calls.clear()
        CalendarIntegrationRemoteShadow.defaults = ""
        CalendarIntegrationAppShadow.app = null
        CalendarIntegrationRemoteShadow.activated = calendar("outubro_rosa")
    }

    @Test fun publicationUpdatesTheRealGateWithoutLosingTheOpenScreenOrDuplicatingTheAppEntry() {
        val options = FirebaseOptions.Builder()
            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
            .setGcmSenderId(BuildConfig.FIREBASE_SENDER_ID)
            .build()
        compose.setContent {
            LauncherIconGate(compose.activity, options) {
                var draft by remember { mutableStateOf("Aposta inicial") }
                DisposableEffect(Unit) {
                    mounts++
                    onDispose { disposals++ }
                }
                BasicText(draft, Modifier.clickable { draft = "Aposta preservada" })
            }
        }
        compose.onNodeWithText("Aposta inicial").performClick()
        assertLauncher(LauncherIconPreset.OUTUBRO_ROSA)
        publish(calendar("novembro_azul"))
        assertLauncher(LauncherIconPreset.NOVEMBRO_AZUL)
        publish("""{"timezone":"America/Sao_Paulo","campaigns":[]}""")
        assertLauncher(LauncherIconPreset.DEFAULT)
        compose.onNodeWithText("Aposta preservada").assertExists()
        compose.runOnIdle {
            assertEquals(1, mounts)
            assertEquals(0, disposals)
            assertEquals(1, CalendarIntegrationRemoteShadow.listeners.size)
        }
    }

    private fun publish(value: String) {
        compose.runOnIdle {
            CalendarIntegrationRemoteShadow.activated = value
            CalendarIntegrationRemoteShadow.listeners.toList().forEach {
                it.onUpdate(ConfigUpdate.create(setOf("app_launcher_icon")))
            }
        }
        compose.waitForIdle()
    }

    private fun assertLauncher(preset: LauncherIconPreset) {
        compose.runOnIdle {
            val context = compose.activity
            val entries = context.packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.packageName),
                0
            )
            assertEquals(listOf(launcherAlias(context, preset).className), entries.map { it.activityInfo.name })
            val shortcuts = context.getSystemService(ShortcutManager::class.java).dynamicShortcuts
            assertEquals(setOf("result_megasena", "result_lotofacil", "result_loteca", "results_all"), shortcuts.map { it.id }.toSet())
            assertTrue(shortcuts.all { it.activity == launcherAlias(context, preset) })
        }
    }

    private fun calendar(icon: String) = """{"timezone":"America/Sao_Paulo","campaigns":[
        {"icon":"$icon","start":"01-01","end":"12-31"}]}"""
}
