package br.gov.caixa.loterias.apostas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.feature.home.presentation.components.HomeDrawer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DrawerNativeMenuParityTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun menuRowAndLabelMatchInstalledNativeLayoutDimensions() {
        val target = InstrumentationRegistry.getInstrumentation().targetContext
        val pkg = "br.gov.caixa.loterias.apostas.cxv2validation"
        val native = target.createPackageContext(pkg, Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE)
        val theme = native.resources.getIdentifier("AppTheme", "style", pkg)
        val view = LayoutInflater.from(ContextThemeWrapper(native, theme))
            .inflate(native.resources.getIdentifier("list_group_side_menu", "layout", pkg), null)
        val density = target.resources.displayMetrics.density
        val width = (300 * density).toInt()
        val label = view.findViewById<TextView>(native.resources.getIdentifier("titleItemGroupSideMenu", "id", pkg))
        label.text = "Minha área"
        label.typeface = native.resources.getFont(native.resources.getIdentifier("caixa_std_semi_bold", "font", pkg))
        view.measure(View.MeasureSpec.makeMeasureSpec(width - (48 * density).toInt(), View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        compose.setContent { CaixaDSTheme {
            HomeDrawer(features = HomeFeatureConfiguration(), userName = null, environmentLabel = null, onOpen = {}, onLogout = {}, onClose = {}, onExternalFailure = {})
        } }
        val row = compose.onNodeWithText("Minha área").fetchSemanticsNode().boundsInRoot
        val text = compose.onNodeWithText("Minha área", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertEquals("Original row width inside the 24dp list padding", view.measuredWidth.toFloat(), row.width, 1f)
        assertEquals("Original group row pitch", view.measuredHeight.toFloat(), row.height, 1f)
        assertEquals("Original label start", 24 * density + label.left, text.left, 1f)
        assertEquals("Original font line height", label.measuredHeight.toFloat(), text.height, 1f)
    }
}
