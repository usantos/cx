package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.navigation.CaixaDSDrawerItem
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xhdpi")
class CaixaDSDrawerTextLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `long menu labels wrap without ellipsis or clipping`() {
        val labels = listOf("Política de jogo responsável", "Autoavaliação do apostador", "Lotéricas Favoritas")
        compose.setContent {
            CaixaDSTheme {
                Column(Modifier.width(220.dp)) {
                    labels.forEach { CaixaDSDrawerItem(it, {}, child = true) }
                }
            }
        }
        labels.forEach { label ->
            val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
            compose.onNodeWithText(label).performSemanticsAction(
                androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult
            ) { it(layouts) }
            val layout = layouts.single()
            assertFalse(layout.hasVisualOverflow)
            assertTrue(layout.getLineEnd(layout.lineCount - 1) == label.length)
        }
    }
}
