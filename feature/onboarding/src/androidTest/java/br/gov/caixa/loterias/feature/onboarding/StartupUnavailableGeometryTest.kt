package br.gov.caixa.loterias.feature.onboarding

import android.content.ComponentName
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Expectations come from the installed native activity's real theme and novo XML. */
@RunWith(AndroidJUnit4::class)
class StartupUnavailableGeometryTest {
    @get:Rule val compose = createComposeRule()

    @Test fun unavailableFooterMatchesNativeTextViewsAndLinearLayout() {
        show(StartupStatusKind.UNAVAILABLE)
        assertNativeFooter()
    }

    @Test fun compactOfflineFooterKeepsNativeGeometryAndRetry() {
        var retries = 0
        show(StartupStatusKind.OFFLINE, compact = true) { retries++ }
        compose.onNodeWithText(StartupStatusKind.OFFLINE.message).assertExists()
        compose.onNodeWithText(StartupStatusKind.OFFLINE.detailMessage!!).assertExists()
        assertNativeFooter()
        compose.onNodeWithText("Tentar novamente").performClick()
        assertEquals(1, retries)
    }

    @Test fun unavailableRetryRemainsCallable() {
        var retries = 0
        show(StartupStatusKind.UNAVAILABLE) { retries++ }
        compose.onNodeWithText("Tentar novamente").performClick()
        assertEquals(1, retries)
    }

    @Test fun updateRetainsItsOwnMessageAndAction() {
        var updates = 0
        show(StartupStatusKind.UPDATE_REQUIRED) { updates++ }
        compose.onNodeWithText(StartupStatusKind.UPDATE_REQUIRED.message).assertExists()
        compose.onNodeWithText("Atualizar").performClick()
        assertEquals(1, updates)
        compose.onNodeWithText("Código do erro: LTR-503").assertDoesNotExist()
    }

    private fun show(kind: StartupStatusKind, compact: Boolean = false, action: () -> Unit = {}) {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Box(if (compact) Modifier.width(320.dp).fillMaxSize() else Modifier.fillMaxSize()) {
                    StartupStatusScreen(kind, "Loterias", "1.23.5", action, {}, {})
                }
            }
        }
    }

    private fun assertNativeFooter() {
        val native = NativeUnavailableOracle(InstrumentationRegistry.getInstrumentation().targetContext)
        val button = compose.onNodeWithText("Tentar novamente").fetchSemanticsNode().boundsInRoot
        native.measureFooter(button.width.toInt())
        for ((id, text) in listOf("tv_tentar" to "Tentar novamente", "txtVersao" to "v1.23.5", "tv_erro" to "Código do erro: LTR-503")) {
            val expected = native.text(id).apply { this.text = text }
            expected.measure(0, 0)
            val layouts = mutableListOf<TextLayoutResult>()
            val node = compose.onNodeWithText(text, useUnmergedTree = true)
            node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val layout = layouts.single()
            assertEquals("$id native text height", expected.measuredHeight.toFloat(), layout.size.height.toFloat(), 1f)
            assertEquals("$id native glyph width/font", expected.measuredWidth.toFloat(), layout.size.width.toFloat(), 1f)
            assertEquals("$id native baseline", expected.baseline.toFloat(), layout.firstBaseline, 1f)
        }
        val version = compose.onNodeWithText("v1.23.5", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val error = compose.onNodeWithText("Código do erro: LTR-503", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals("44dp native retry button", native.button.height.toFloat(), button.height, 1f)
        assertEquals("native 36dp version gap", native.text("txtVersao").top - native.button.bottom.toFloat(), version.top - button.bottom, 1f)
        assertEquals("native adjacent footer text", native.text("tv_erro").top - native.text("txtVersao").bottom.toFloat(), error.top - version.bottom, 1f)
        val retryText = compose.onNodeWithText("Tentar novamente", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val expectedRetry = native.text("tv_tentar")
        assertEquals("native centered icon/margin/text group", expectedRetry.left.toFloat(), retryText.left - button.left, 1f)
        val icon = compose.onNodeWithTag("startupRetryIcon", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals("native 20dp refresh width", native.button.getChildAt(0).width.toFloat(), icon.width, 1f)
        assertEquals("native 20dp refresh height", native.button.getChildAt(0).height.toFloat(), icon.height, 1f)
        assertEquals("native icon immediately before text", icon.right, retryText.left, 1f)
        assertEquals("native centered icon start margin", native.button.getChildAt(0).left.toFloat(), icon.left - button.left, 1f)
        val content = compose.onNodeWithTag("startupUnavailableContent").fetchSemanticsNode().boundsInRoot
        assertEquals("native 30dp after error", native.bottomMargin.toFloat(), content.bottom - error.bottom, 1f)
    }
}

private class NativeUnavailableOracle(target: Context) {
    private val packageName = "br.gov.caixa.loterias.apostas.cxv2validation"
    private val native = target.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE)
    private val theme = native.packageManager.getActivityInfo(ComponentName(packageName,
        "br.gov.caixa.loterias.apostas.controllers.AppIndisponivelActivity"), 0).themeResource
    private val context = ContextThemeWrapper(native, theme)
    private fun id(name: String, type: String = "id") = native.resources.getIdentifier(name, type, packageName)
    private val root = LayoutInflater.from(context).inflate(id("activity_app_indisponivel_novo", "layout"), null)
    private val footer = root.findViewById<LinearLayout>(id("container_retry"))
    val button: ViewGroup = root.findViewById(id("tenteNovamenteButton"))
    val bottomMargin get() = (footer.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    fun text(name: String): TextView = footer.findViewById(id(name))
    fun measureFooter(width: Int) {
        (footer.parent as ViewGroup).removeView(footer)
        footer.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), 0)
        footer.layout(0, 0, footer.measuredWidth, footer.measuredHeight)
        assertEquals("native refresh width", (20 * context.resources.displayMetrics.density).toInt(), button.getChildAt(0).width)
        assertEquals("native refresh start margin", (16 * context.resources.displayMetrics.density).toInt(), (button.getChildAt(0).layoutParams as ViewGroup.MarginLayoutParams).marginStart)
        assertEquals("native footer bottom margin", (30 * context.resources.displayMetrics.density).toInt(), (footer.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin)
    }
}
