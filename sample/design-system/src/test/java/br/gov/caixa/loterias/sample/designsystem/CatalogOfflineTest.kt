package br.gov.caixa.loterias.sample.designsystem

import android.Manifest
import android.content.pm.PackageManager
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/** Verifica o contrato do adaptador Android; ShadowWebView não renderiza pixels do Chromium. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CatalogOfflineTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun sampleWebViewLoadsOnlyPackagedHtmlWithoutJavaScriptOrInternetPermission() {
        val key = "br.gov.caixa.loterias.core.designsystem.web.CaixaDSWebPage"
        compose.setContent { CaixaDSTheme { catalogPreviews.getValue(key)(PreviewState()) } }
        compose.runOnIdle {
            val view = compose.activity.window.decorView.descendants().filterIsInstance<WebView>().single()
            assertEquals("file:///android_asset/catalog.html", shadowOf(view).lastLoadedUrl)
            assertFalse(view.settings.javaScriptEnabled)
            assertTrue(view.webViewClient.shouldOverrideUrlLoading(view, "https://example.org/"))
            val context = compose.activity.applicationContext
            val permissions = context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions.orEmpty()
            assertFalse(permissions.contains(Manifest.permission.INTERNET))
            val html = context.assets.open("catalog.html").bufferedReader().use { it.readText() }
            assertTrue(html.contains("CAIXA Design System"))
            assertFalse(html.contains("https://"))
        }
    }

    private fun View.descendants(): Sequence<View> = sequence {
        yield(this@descendants)
        val group = this@descendants as? ViewGroup
        if (group != null) {
            for (index in 0 until group.childCount) yieldAll(group.getChildAt(index).descendants())
        }
    }
}
