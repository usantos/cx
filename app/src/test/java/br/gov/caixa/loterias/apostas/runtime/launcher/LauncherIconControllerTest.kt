package br.gov.caixa.loterias.apostas.runtime.launcher

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ShortcutManager
import br.gov.caixa.loterias.apostas.MainActivity
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [32, 35], application = Application::class)
class LauncherIconControllerTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Test fun switchingEveryPresetKeepsOneLauncherAndStableMainActivityTargets() {
        val controller = LauncherIconController(context)
        for (preset in LauncherIconPreset.entries + LauncherIconPreset.DEFAULT) {
            assertEquals(LauncherIconApplyResult.APPLIED, controller.apply(preset))
            val launches = context.packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setPackage(context.packageName),
                0
            )
            assertEquals(listOf(launcherAlias(context, preset).className), launches.map { it.activityInfo.name })
            val main = context.packageManager.getActivityInfo(ComponentName(context, MainActivity::class.java), 0)
            assertTrue(main.enabled)
            val shortcuts = context.getSystemService(ShortcutManager::class.java).dynamicShortcuts
            assertEquals(SHORTCUT_COUNT, shortcuts.size)
            assertTrue(shortcuts.all { it.activity == launcherAlias(context, preset) })
            assertTrue(shortcuts.all { it.intent?.component?.className == MainActivity::class.java.name })
        }
    }

    @Test fun repeatingTheActivePresetDoesNotRepublish() {
        val platform = FakePlatform()
        val controller = LauncherIconController(platform)
        assertEquals(LauncherIconApplyResult.APPLIED, controller.apply(LauncherIconPreset.OUTUBRO_ROSA))
        assertEquals(LauncherIconApplyResult.UNCHANGED, controller.apply(LauncherIconPreset.OUTUBRO_ROSA))
        assertEquals(1, platform.publications)
        assertEquals(listOf(LauncherIconPreset.OUTUBRO_ROSA), platform.activations)
    }

    @Test fun throttlingDefersBeforeChangingTheLauncherOrItsShortcuts() {
        val platform = FakePlatform().apply { ready = false }
        assertEquals(
            LauncherIconApplyResult.DEFERRED,
            LauncherIconController(platform).apply(LauncherIconPreset.AGOSTO_LILAS)
        )
        assertEquals(setOf(LauncherIconPreset.DEFAULT), platform.enabledPresets())
        assertEquals(0, platform.publications)
    }

    @Test fun rejectedPublicationRestoresThePreviousLauncherAndLeavesShortcuts() {
        val platform = FakePlatform().apply { accepted = false }
        assertEquals(
            LauncherIconApplyResult.FAILED,
            LauncherIconController(platform).apply(LauncherIconPreset.NOVEMBRO_AZUL)
        )
        assertEquals(setOf(LauncherIconPreset.DEFAULT), platform.enabledPresets())
        assertEquals(LauncherIconPreset.DEFAULT, platform.shortcutOwner)
    }

    @Test fun publicationExceptionAlsoRestoresThePreviousLauncher() {
        val platform = FakePlatform().apply { publicationThrows = true }
        assertEquals(
            LauncherIconApplyResult.FAILED,
            LauncherIconController(platform).apply(LauncherIconPreset.SETEMBRO_AMARELO)
        )
        assertEquals(setOf(LauncherIconPreset.DEFAULT), platform.enabledPresets())
    }

    @Test fun failedFinalActivationRebindsShortcutsBeforeDisablingTheirNewOwner() {
        val platform = FakePlatform().apply { failNextActivation = true }
        assertEquals(
            LauncherIconApplyResult.FAILED,
            LauncherIconController(platform).apply(LauncherIconPreset.OUTUBRO_ROSA)
        )
        assertEquals(setOf(LauncherIconPreset.DEFAULT), platform.enabledPresets())
        assertEquals(LauncherIconPreset.DEFAULT, platform.shortcutOwner)
        assertEquals(listOf(LauncherIconPreset.OUTUBRO_ROSA, LauncherIconPreset.DEFAULT), platform.publishedOwners)
    }

    private class FakePlatform : LauncherIconPlatform {
        private val enabled = linkedSetOf(LauncherIconPreset.DEFAULT)
        var failNextActivation = false
        var ready = true
        var accepted = true
        var publicationThrows = false
        var publications = 0
        var shortcutOwner: LauncherIconPreset? = LauncherIconPreset.DEFAULT
        val activations = mutableListOf<LauncherIconPreset>()
        val publishedOwners = mutableListOf<LauncherIconPreset>()
        override fun enabledPresets() = enabled.toSet()
        override fun shortcutsMatch(preset: LauncherIconPreset) = shortcutOwner == preset
        override fun canPublishShortcuts() = ready
        override fun prepare(preset: LauncherIconPreset) {
            enabled += preset
        }
        override fun activate(preset: LauncherIconPreset) {
            activations += preset
            if (failNextActivation) {
                failNextActivation = false
                throw IllegalStateException("Alias update rejected")
            }
            if (shortcutOwner != preset) shortcutOwner = null
            enabled.clear()
            enabled += preset
        }
        override fun publishShortcuts(preset: LauncherIconPreset): Boolean {
            publications++
            if (publicationThrows) throw IllegalStateException("Launcher unavailable")
            check(preset in enabled) { "Shortcut owner must be enabled" }
            if (accepted) {
                publishedOwners += preset
                shortcutOwner = preset
            }
            return accepted
        }
    }
}

private const val SHORTCUT_COUNT = 4
