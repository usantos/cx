package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CatalogFieldLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun variantsAndBasicOverloadsHaveSeparateVisibleNonOverlappingInputBounds() {
        val ids = listOf(
            "br.gov.caixa.loterias.core.designsystem.components.CaixaDSTextField" to VARIANT_COUNT,
            "br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSBasicTextField" to 2
        )
        val selected = mutableStateOf(CatalogRegistry.entries.first { it.id == ids.first().first })
        compose.setContent { CaixaDSTheme { Column { CatalogDemo(selected.value) } } }
        ids.forEach { (id, count) ->
            compose.runOnIdle { selected.value = CatalogRegistry.entries.first { it.id == id } }
            val fields = compose.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().sortedBy { it.boundsInRoot.top }
            assertEquals(id, count, fields.size)
            fields.forEach { field -> assertTrue(id, field.boundsInRoot.width > 0 && field.boundsInRoot.height > 0) }
            fields.zipWithNext().forEach { (first, second) ->
                assertTrue("$id input bounds must not overlap", first.boundsInRoot.bottom <= second.boundsInRoot.top)
            }
        }
    }
}

private const val VARIANT_COUNT = 3
