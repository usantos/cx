package br.gov.caixa.loterias.sample.designsystem

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CatalogRegistryTest {
    @Test
    fun everyEntryHasDistinctIdentityRunnablePreviewAndLocalizedUsage() {
        val resources = ApplicationProvider.getApplicationContext<android.content.Context>().resources
        val entries = CatalogRegistry.entries
        assertTrue(entries.size >= EXPECTED_COMPOSABLE_COUNT)
        assertEquals(entries.size, entries.map { it.id }.toSet().size)
        assertEquals(entries.map { it.id }.toSet(), catalogPreviews.keys)
        entries.forEach { entry ->
            assertTrue(entry.id.endsWith(entry.name))
            assertFalse(resources.getString(entry.category).isBlank())
            assertFalse(resources.getString(entry.description).isBlank())
            assertTrue(resources.getString(entry.signatures).contains(entry.name))
            assertTrue(resources.getString(entry.snippet).contains(entry.name))
            assertTrue(CatalogRegistry.search(entry.id).contains(entry))
        }
    }

    @Test
    fun searchesNamesPackagesAndParametersIgnoringCaseAndWhitespace() {
        assertEquals(CatalogRegistry.entries, CatalogRegistry.search("  "))
        assertTrue(CatalogRegistry.search("textFIELD").all { it.name.contains("TextField") })
        assertTrue(CatalogRegistry.search(" onValueChange ").any { it.name == "CaixaDSTextField" })
        assertTrue(CatalogRegistry.search("navigation").any { it.name == "CaixaDSTopBar" })
        assertTrue(CatalogRegistry.search("unfindablecomponent").isEmpty())
    }

    @Test
    fun previewStateKeepsCallbacksLocalAndExplicit() {
        val state = PreviewState()
        state.action()
        assertEquals(1, state.count)
        state.toggle()
        assertTrue(state.checked)
        assertEquals(2, state.count)
        state.show = true
        state.dismiss()
        assertFalse(state.show)
        assertEquals(CALLBACK_COUNT, state.count)
        state.text = "CAIXA"
        state.selection = 1
        assertEquals("CAIXA", state.text)
        assertEquals(1, state.selection)
    }
}

private const val EXPECTED_COMPOSABLE_COUNT = 121
private const val CALLBACK_COUNT = 3
