package br.gov.caixa.loterias.feature.betting

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BettingWheelSwipeTest {
    @get:Rule val compose = createComposeRule()

    @Test fun actualTouchSwipeSnapsCenterAndConfirmsThatOptionOnce() {
        val visible = mutableStateOf(true)
        val options = listOf(0, 2, 3, 4, 6, 8, 9, 12)
        var commits = 0
        var chosen = -1
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                if (visible.value) BettingOptionDialog(
                    "Teimosinhas", options, 0,
                    { if (it == 0) "Nenhum concurso" else "$it concursos" },
                    { chosen = it; commits++ }, { visible.value = false },
                    "Escolha em quantos concursos você quer participar com este mesmo jogo:",
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithTag("betting-option-wheel").performTouchInput { swipeUp() }

        fun selectedIndex(): Int? = options.indices.firstOrNull { index ->
            compose.onAllNodes(hasTestTag("betting-option-item-$index") and isSelected())
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.waitUntil(10_000) { selectedIndex()?.let { it > 0 } == true }
        val centerPosition = requireNotNull(selectedIndex())
        assertTrue("A real swipe must change the centered option", centerPosition > 0)

        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = File(requireNotNull(instrumentation.targetContext.externalCacheDir), "codex-parity")
            .apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(directory, "compose-mega-recurrence-swiped.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
        File(directory, "compose-mega-recurrence-swiped.txt").writeText(
            "fixture=true\nnetworkSends=0\ninput=actual-touch-swipe\noptions=$options\n" +
                "centerPosition=$centerPosition\ncenterText=${options[centerPosition]} concursos\n",
        )

        compose.onNodeWithTag("betting-option-confirm").performClick()
        compose.waitForIdle()
        assertEquals(options[centerPosition], chosen)
        assertEquals(1, commits)
    }
}
