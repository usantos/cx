// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.icon.DrawerChevron
import br.gov.caixa.loterias.core.designsystem.icon.DrawerLink
import br.gov.caixa.loterias.core.designsystem.icon.DrawerLogout
import br.gov.caixa.loterias.core.designsystem.icon.DrawerQuestions
import br.gov.caixa.loterias.core.designsystem.icon.DrawerResponsible
import br.gov.caixa.loterias.core.designsystem.icon.DrawerTerms
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSCurrentCartDeleteIcon
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSCurrentCartIcon
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSDeleteIcon
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSDetailClover
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSMarketplaceNumberRing
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSNotificationSwipeDelete
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSNotificationSwipeRead
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSRecurringIcon
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.testing.captureToImage
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSHomeV2Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class LoteriasCaixaDSAssetContractsJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun drawerIconsKeepOriginalNativeViewportsAndNonemptyPaths() {
        listOf(
            Triple(LoteriasCaixaDSIcons.DrawerResponsible, 25f, 24f),
            Triple(LoteriasCaixaDSIcons.DrawerTerms, 13f, 16f),
            Triple(LoteriasCaixaDSIcons.DrawerQuestions, 9f, 12f),
            Triple(LoteriasCaixaDSIcons.DrawerLogout, 29f, 29f),
            Triple(LoteriasCaixaDSIcons.DrawerChevron, 16f, 10f),
            Triple(LoteriasCaixaDSIcons.DrawerLink, 20f, 10f)
        ).forEach { (vector, width, height) -> assertViewport(vector, width, height) }
    }

    @Test
    fun cartAndNotificationArtworkRetainsViewportIndependentOfDisplaySize() {
        listOf(
            Triple(LoteriasCaixaDSCurrentCartIcon, 36f, 32f),
            Triple(LoteriasCaixaDSCurrentCartDeleteIcon, 20f, 20f),
            Triple(LoteriasCaixaDSDeleteIcon, 17f, 20f),
            Triple(LoteriasCaixaDSRecurringIcon, 384.9f, 345.9f),
            Triple(LoteriasCaixaDSDetailClover, 119f, 119f),
            Triple(LoteriasCaixaDSMarketplaceNumberRing, 41f, 41f),
            Triple(LoteriasCaixaDSNotificationSwipeRead, 26f, 26f),
            Triple(LoteriasCaixaDSNotificationSwipeDelete, 24f, 24f)
        ).forEach { (vector, width, height) -> assertViewport(vector, width, height) }
        assertEquals(24.dp, LoteriasCaixaDSRecurringIcon.defaultWidth)
        assertEquals(35.dp, LoteriasCaixaDSDetailClover.defaultHeight)
        assertEquals(45.dp, LoteriasCaixaDSMarketplaceNumberRing.defaultWidth)
        assertEquals(29.dp, LoteriasCaixaDSNotificationSwipeDelete.defaultWidth)
    }

    @Test
    fun legacyDrawablePainterProducesSamePixelsAsNativeDrawableAtRequestedBounds() {
        lateinit var context: android.content.Context
        compose.setContent {
            context = LocalContext.current
            Image(
                loteriasCaixaDSDrawablePainter(R.drawable.legacy_marketplace_filter_add),
                "Adicionar",
                Modifier.size(48.dp).background(Color.White).testTag("drawable")
            )
        }
        val actual = compose.onNodeWithTag("drawable").captureToImage().asAndroidBitmap()
        val expected = Bitmap.createBitmap(actual.width, actual.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(expected)
        canvas.drawColor(android.graphics.Color.WHITE)
        val drawable = requireNotNull(context.getDrawable(R.drawable.legacy_marketplace_filter_add))
        drawable.setBounds(0, 0, expected.width, expected.height)
        drawable.draw(canvas)
        assertTrue("Legacy layer/rotation drawing must match the native drawable", expected.sameAs(actual))
    }

    @Test
    fun homeV2ThemeRespectsFontScaleWhileKeepingPhysicalDensityAndRestoringParentScope() {
        val requestedScale = androidx.compose.runtime.mutableStateOf(1f)
        var innerDensity = 0f
        var innerScale = 0f
        var outerScale = 0f
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(2.5f, requestedScale.value)) {
                LoteriasCaixaDSHomeV2Theme {
                    innerDensity = LocalDensity.current.density
                    innerScale = LocalDensity.current.fontScale
                }
                outerScale = LocalDensity.current.fontScale
            }
        }
        compose.runOnIdle {
            assertEquals(2.5f, innerDensity)
            assertEquals(1f, innerScale)
            assertEquals(1f, outerScale)
            requestedScale.value = 2f
        }
        compose.runOnIdle {
            assertEquals(2.5f, innerDensity)
            assertEquals(2f, innerScale)
            assertEquals(2f, outerScale)
        }
    }

    private fun assertViewport(vector: ImageVector, width: Float, height: Float) {
        assertEquals(vector.name, width, vector.viewportWidth, 0f)
        assertEquals(vector.name, height, vector.viewportHeight, 0f)
        assertTrue("${vector.name} requires authored paths", vector.root.size > 0)
        assertTrue("${vector.name} needs a positive display width", vector.defaultWidth.value > 0)
        assertTrue("${vector.name} needs a positive display height", vector.defaultHeight.value > 0)
    }
}
