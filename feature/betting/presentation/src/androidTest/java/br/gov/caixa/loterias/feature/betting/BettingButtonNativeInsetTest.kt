package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.R

import android.content.Context
import android.content.res.Configuration
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.SystemClock
import android.view.ContextThemeWrapper
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.presentation.route.BettingGameRoute
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

/** Referência de pixels independente da geometria do Compose: o estilo real do MaterialButton nativo
 * usado em rodape_simula.xml e activity_simula_aposta.xml. Apenas os limites externos
 * não detectavam a regressão: as áreas de toque de 52dp estavam corretas, mas suas pinturas se tocavam. */
class BettingButtonNativeInsetTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun footerPaintBoundsMatchNativeWithoutChangingTheHitFrames() {
        val model = renderMegaSena()
        assertNativePaintBounds("Aposta aleatória", 52, 4)
        assertNativePaintBounds("Limpar", 52, 4)
        assertNativePaintBounds("Adicionar ao carrinho", 52, 4)

        // O estado selecionado/favorito usa outro consumidor do mesmo controle do DS.
        compose.runOnIdle { (1..6).forEach(model::toggleNumber) }
        assertNativePaintBounds("Favoritar aposta", 52, 4)
        assertNativePaintBounds("Limpar", 52, 4)
        assertNativePaintBounds("Adicionar ao carrinho", 52, 4)
    }

    @Test fun selectorPaintBoundsMatchNativeWhileKeepingTheirFortyFiveDpHitFrames() {
        renderMegaSena()
        assertNativePaintBounds("6 números", 45, 12)
        assertNativePaintBounds("Teimosinha", 45, 12)
    }

    @Test fun pressedFooterRippleKeepsTheNativeInsetAndFullHitFrame() {
        renderMegaSena()
        val button = compose.onNodeWithTag("betting-button-Aposta aleatória", useUnmergedTree = true)
        compose.mainClock.autoAdvance = false
        button.performTouchInput { down(center) }
        try {
            compose.mainClock.advanceTimeBy(400)
            // O efeito de onda do Android também anima pelo relógio real de RenderThread. Mantenha o
            // ponteiro pressionado até o raio se expandir antes de capturar os pixels.
            SystemClock.sleep(350)
            assertNativePaintBounds("Aposta aleatória", 52, 4, pressed = true)
        } finally {
            button.performTouchInput { up() }
            compose.mainClock.autoAdvance = true
        }
    }

    @Test fun nativeInsetsKeepAndroidPixelOffsetRoundingAtFractionalDensity() {
        // O Android resolve insetTop/Bottom com getDimensionPixelOffset: em 450dpi,
        // 6dp viram 16px, enquanto o padding comum do Compose arredonda para 17px.
        val fractionalContext = context.createConfigurationContext(
            Configuration(context.resources.configuration).apply {
                densityDpi = 450
            }
        )
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                Box(Modifier.background(androidx.compose.ui.graphics.Color.White)) {
                    CompositionLocalProvider(LocalDensity provides Density(450f / 160f)) {
                        CaixaDSBettingButton(
                            "Fractional inset",
                            {},
                            Modifier.size(130.dp, 52.dp),
                            color = androidx.compose.ui.graphics.Color.White,
                            content = androidx.compose.ui.graphics.Color.Black,
                            stroke = androidx.compose.ui.graphics.Color.Black,
                            verticalInset = CaixaDSBettingButtonDefaults.NativeVerticalInset
                        )
                    }
                }
            }
        }
        val painted = compose.onNodeWithTag("betting-button-Fractional inset", useUnmergedTree = true)
            .captureToImage().toPixelMap()
        val nativeRows = nativePaintedRows(painted.width, painted.height, 4, false, fractionalContext)
        val actualRows = paintedRows(painted)
        assertEquals("Fractional-density drawable top", nativeRows.first(), actualRows.first())
        assertEquals("Fractional-density drawable bottom", nativeRows.last(), actualRows.last())
    }

    private fun assertNativePaintBounds(label: String, frameHeightDp: Int, cornerDp: Int, pressed: Boolean = false) {
        val node = compose.onNodeWithTag("betting-button-$label", useUnmergedTree = true)
            .assertHeightIsEqualTo(frameHeightDp.dp)
        val painted = node.captureToImage().toPixelMap()
        val actualRows = paintedRows(painted)
        assertTrue("$label has visible paint", actualRows.isNotEmpty())
        val nativeRows = nativePaintedRows(painted.width, painted.height, cornerDp, pressed)
        assertEquals("$label drawable top inside its hit frame", nativeRows.first(), actualRows.first())
        assertEquals("$label drawable bottom inside its hit frame", nativeRows.last(), actualRows.last())
    }

    private fun paintedRows(painted: PixelMap) = (0 until painted.height).filter { y ->
        (0 until painted.width).any { x ->
            val pixel = painted[x, y]
            pixel.alpha > .9f && minOf(pixel.red, pixel.green, pixel.blue) < .99f
        }
    }

    private fun nativePaintedRows(width: Int, height: Int, cornerDp: Int, pressed: Boolean, nativeContext: Context = context): List<Int> {
        lateinit var bitmap: Bitmap
        compose.runOnIdle {
            val nativeDensity = nativeContext.resources.displayMetrics.density
            val button = MaterialButton(
                ContextThemeWrapper(
                    nativeContext,
                    com.google.android.material.R.style.Theme_MaterialComponents_Light_NoActionBar
                )
            ).apply {
                text = ""
                backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                strokeColor = ColorStateList.valueOf(Color.BLACK)
                strokeWidth = (nativeDensity + .5f).toInt()
                cornerRadius = (cornerDp * nativeDensity + .5f).toInt()
                stateListAnimator = null
                elevation = 0f
            }
            button.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            )
            button.layout(0, 0, width, height)
            button.isPressed = pressed
            button.jumpDrawablesToCurrentState()
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            button.draw(canvas)
        }
        return (0 until height).filter { y ->
            (0 until width).any { x ->
                val pixel = bitmap.getPixel(x, y)
                minOf(Color.red(pixel), Color.green(pixel), Color.blue(pixel)) < 253
            }
        }.also { bitmap.recycle() }
    }

    private fun renderMegaSena(): BettingGameViewModel {
        val game = LotteryGameParameters(
            modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA"),
            contestNumber = 3068, drawDate = "15/10/2026 21:00:00",
            maximumNumber = 60, minimumSelection = 6, maximumSelection = 20,
            maximumSurprises = 3, recurringOptions = listOf(0, 2, 4),
            prices = listOf(BetPrice(6, 0, BigDecimal("6.00")))
        )
        val model = BettingGameViewModel(
            2,
            GetBettingParametersUseCase { error("Prepared public snapshot") },
            AddBetToCartUseCase { error("Geometry never submits a bet") },
            initialParameters = BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, listOf(game))
        )
        compose.setContent { CaixaDSTheme(darkTheme = false) { BettingGameRoute(model, {}, {}) } }
        return model
    }
}
