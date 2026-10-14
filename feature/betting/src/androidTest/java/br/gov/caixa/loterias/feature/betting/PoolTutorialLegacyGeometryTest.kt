package br.gov.caixa.loterias.feature.betting

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.onboarding.BettingTutorial
import br.gov.caixa.loterias.core.domain.onboarding.SetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.core.presentation.WorkflowSpec
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import kotlin.math.min
import kotlin.math.roundToInt

class PoolTutorialLegacyGeometryTest {
    @get:Rule val compose = createComposeRule()
    private val spec = WorkflowSpec("betting/tutorial", "Tutorial", "")
    private fun bounds(tag: String): Rect = compose.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
    private fun px(dp: Int): Float = with(compose.density) { dp.dp.toPx() }

    @Test fun introductionUsesNativeInsideImagePagerAndLowerQuarterButtonConstraints() {
        val vm = BettingFlowViewModel(spec)
        compose.setContent { CaixaDSTheme { BettingRoute(vm, {}, {}) } }
        compose.waitForIdle()
        val viewport = bounds("pool-tutorial-viewport")
        val pager = bounds("pool-tutorial-pager")
        assertEquals("CX ci_tutorial reserves 40dp below pager", viewport.height - px(40), pager.height, 1f)
        val media = bounds("pool-tutorial-media-tutorial_bolao_introducao.json")
        val inside = min(1f, min(pager.width / px(360), pager.height / px(640)))
        assertEquals("centerInside must not upscale the authored 360dp image", px(360) * inside, media.width, 1f)
        assertEquals(px(640) * inside, media.height, 1f)
        assertEquals(pager.center.x, media.center.x, 1f)
        assertEquals(pager.center.y, media.center.y, 1f)
        val primary = bounds("pool-tutorial-primary")
        assertEquals(px(164), primary.width, 1f)
        assertEquals(nativeButtonHeight(false).toFloat(), primary.height, 2f)
        // Native center_view_pager + bottom constraint; BOLAO controller margin is 200 RAW pixels.
        assertEquals(pager.top + (pager.height - px(1)) * .25f + pager.height * .5f - 100f, primary.center.y, 1.5f)
        val close = bounds("pool-tutorial-close")
        assertEquals(viewport.top, close.top, 1f)
        assertEquals(px(88), close.width, 1f)
        assertEquals(px(48), close.height, 1f)
        compose.onNodeWithText("Pular para\no final").assertIsDisplayed()
        assertEquals(px(40), bounds("pool-tutorial-indicator-strip").height, 1f)
        assertEquals(px(5), bounds("pool-tutorial-dot-1").width, 1f)
        assertEquals(px(9), bounds("pool-tutorial-dot-0").width, 1f)
        val definition = tutorialDefinition("betting/tutorial")
        for (page in 1..definition.pages.lastIndex) {
            compose.runOnIdle { vm.selectPage(page) }
            compose.waitForIdle()
            val image = bounds("pool-tutorial-media-${definition.pages[page].media}")
            assertEquals("Every page preserves the authored aspect and centerInside limit", px(360) * inside, image.width, 1f)
            assertEquals(px(640) * inside, image.height, 1f)
            assertEquals(pager.center.y, image.center.y, 1f)
        }
    }

    @Test fun firstIntroIsBoldButReturningIntroUsesRegularQuestionAndNativeTextAppearance() {
        val vm = BettingFlowViewModel(spec)
        compose.setContent { CaixaDSTheme { BettingRoute(vm, {}, {}) } }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText("Vamos começar", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(1, layouts.single().lineCount)
        assertEquals("Initial CX spannable uses Futura bold", br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyFuturaFontFamily,
            layouts.single().layoutInput.text.spanStyles.single().item.fontFamily)
        assertEquals(nativeButtonHeight(false).toFloat(), bounds("pool-tutorial-primary").height, 2f)
        compose.runOnIdle { vm.selectPage(1) }
        compose.waitForIdle()
        compose.runOnIdle { vm.selectPage(0) }
        compose.waitForIdle()
        layouts.clear()
        compose.onNodeWithText("Vamos começar?", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(1, layouts.single().lineCount)
        assertTrue("Revisited intro is the plain CX string resource", layouts.single().layoutInput.text.spanStyles.isEmpty())
        assertEquals(nativeButtonHeight(false, true).toFloat(), bounds("pool-tutorial-primary").height, 2f)
    }

    @Test fun skipSettlesDirectlyOnFinalInsteadOfPublishingIntermediateAnimationPages() {
        val vm = BettingFlowViewModel(spec)
        compose.setContent { CaixaDSTheme { BettingRoute(vm, {}, {}) } }
        compose.onNodeWithTag("pool-tutorial-skip").performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals("Pular para o final must settle on page 5", 4, vm.state.value.page) }
        compose.onNodeWithTag("pool-tutorial-media-tutorial_bolao_final.json").assertIsDisplayed()
        compose.onNodeWithTag("pool-tutorial-close").assertDoesNotExist()
        compose.onNodeWithText("Escolher\nmeu bolão").assertIsDisplayed()
    }

    @Test fun finalKeepsNativeButtonTypographyCheckboxGapAndDismissPreference() {
        val dismissed = mutableListOf<Pair<BettingTutorial, Boolean>>()
        val vm = BettingFlowViewModel(spec, SetBettingTutorialDismissedUseCase { kind, value -> dismissed += kind to value })
        vm.selectPage(4)
        var completed = 0
        compose.setContent { CaixaDSTheme { BettingRoute(vm, {}, { completed++ }) } }
        compose.waitForIdle()
        val pager = bounds("pool-tutorial-pager")
        val primary = bounds("pool-tutorial-primary")
        assertEquals(nativeButtonHeight(true).toFloat(), primary.height, 2f)
        assertEquals(pager.top + (pager.height - px(1)) * .25f + pager.height * .5f - 100f, primary.center.y, 1.5f)
        val check = bounds("pool-tutorial-checkbox-row")
        assertEquals("Native checkbox starts 8dp after the independently centered button", px(8), check.top - primary.bottom, 1f)
        compose.onNodeWithTag("pool-tutorial-checkbox").performClick()
        compose.onNodeWithTag("pool-tutorial-primary").performClick()
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(listOf(BettingTutorial.POOL to true), dismissed)
            assertEquals(1, completed)
        }
    }

    private fun nativeButtonHeight(final: Boolean, returnedIntro: Boolean = false): Int {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bold = ResourcesCompat.getFont(context, br.gov.caixa.loterias.core.designsystem.R.font.futura_std_bold)!!
        val label = if (final) "Escolher\nmeu bolão" else if (returnedIntro) "Vamos começar?" else "Vamos começar"
        val styled = SpannableString(label).apply {
            if (final || !returnedIntro) setSpan(object : MetricAffectingSpan() {
                override fun updateDrawState(paint: TextPaint) { paint.typeface = bold }
                override fun updateMeasureState(paint: TextPaint) { paint.typeface = bold }
            }, 0, if (final) 8 else label.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        }
        return TextView(context).apply {
            setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Light_Widget_PopupMenu_Large)
            // XML fontFamily is an explicit override of the inherited TextAppearance.
            typeface = ResourcesCompat.getFont(context, br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular)
            includeFontPadding = true
            setPadding(px(16).roundToInt(), px(16).roundToInt(), px(16).roundToInt(), px(16).roundToInt())
            text = styled
            measure(View.MeasureSpec.makeMeasureSpec(px(164).roundToInt(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        }.measuredHeight
    }
}
