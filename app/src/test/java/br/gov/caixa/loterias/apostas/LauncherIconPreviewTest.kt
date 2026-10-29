package br.gov.caixa.loterias.apostas

import android.app.Application
import android.content.Intent
import br.gov.caixa.loterias.apostas.composition.startup.launcherIconPreviewValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class LauncherIconPreviewTest {
    @Test fun releaseIgnoresPreviewExtras() {
        val intent = Intent().putExtra("launcher_icon_preview", "outubro_rosa")
        assertNull(launcherIconPreviewValue(intent, debug = false))
    }

    @Test fun debugPassesTheExactConfigValueIncludingBlankForFallback() {
        assertEquals("agosto_lilas", launcherIconPreviewValue(Intent().putExtra("launcher_icon_preview", "agosto_lilas"), true))
        assertEquals("", launcherIconPreviewValue(Intent().putExtra("launcher_icon_preview", ""), true))
        assertNull(launcherIconPreviewValue(Intent(), true))
    }
}
