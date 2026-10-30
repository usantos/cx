package br.gov.caixa.loterias.apostas.runtime.launcher

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import br.gov.caixa.loterias.apostas.MainActivity
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [32, 35], application = Application::class)
class LauncherEntryActivityTest {
    @Test fun forwardsLaunchExtrasToStableActivityWithoutClearingTheExistingSession() {
        val context = RuntimeEnvironment.getApplication()
        val incoming = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(launcherAlias(context, LauncherIconPreset.DEFAULT))
            .putExtra("launcher_icon_preview", "outubro_rosa")
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        val entry = Robolectric.buildActivity(LauncherEntryActivity::class.java, incoming).create().get()
        val forwarded = shadowOf(entry).nextStartedActivity
        assertEquals(ComponentName(context, MainActivity::class.java), forwarded.component)
        assertEquals(Intent.ACTION_MAIN, forwarded.action)
        assertTrue(Intent.CATEGORY_LAUNCHER in forwarded.categories)
        assertEquals("outubro_rosa", forwarded.getStringExtra("launcher_icon_preview"))
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP, forwarded.flags)
        assertTrue(forwarded.flags and Intent.FLAG_ACTIVITY_SINGLE_TOP != 0)
        assertEquals(0, forwarded.flags and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        assertTrue(entry.isFinishing)
        assertEquals(launcherAlias(context, LauncherIconPreset.DEFAULT), incoming.component)
    }

    @Test fun aliasesUseATemporaryTaskAndLeaveMainActivityStable() {
        val context = RuntimeEnvironment.getApplication()
        val manager = context.packageManager
        val entryName = "br.gov.caixa.loterias.apostas.runtime.launcher.LauncherEntryActivity"
        LauncherIconPreset.entries.forEach { preset ->
            val alias = manager.getActivityInfo(launcherAlias(context, preset), PackageManager.MATCH_DISABLED_COMPONENTS)
            assertEquals(entryName, alias.targetActivity)
        }
        val entry = manager.getActivityInfo(ComponentName(context.packageName, entryName), 0)
        assertTrue(entry.taskAffinity.isNullOrEmpty())
        assertTrue(entry.flags and ActivityInfo.FLAG_NO_HISTORY != 0)
        assertTrue(entry.flags and ActivityInfo.FLAG_EXCLUDE_FROM_RECENTS != 0)
        val main = manager.getActivityInfo(ComponentName(context, MainActivity::class.java), 0)
        assertTrue(main.enabled)
        assertEquals(null, main.targetActivity)
    }
}
