package br.gov.caixa.loterias.sample.designsystem

import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class CatalogSavedEntryTest {
    @Test
    fun restoresCurrentAndLegacySavedIdsWithoutInventingUnknownEntries() {
        val currentId = "br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme"
        val legacyId = "br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme"
        val expected = CatalogRegistry.entries.single { it.id == currentId }

        assertSame(expected, CatalogRegistry.findEntry(currentId))
        assertNull(CatalogRegistry.findEntry(null))
        assertNull(CatalogRegistry.findEntry("br.gov.caixa.loterias.core.designsystem.theme.UnknownTheme"))
        assertSame(expected, CatalogRegistry.findEntry(legacyId))
        assertNull(CatalogRegistry.findEntry("br.gov.caixa.loterias.core.designsystem.theme.CaixaDSUnknown"))
        assertNull(CatalogRegistry.findEntry("unknown.package.CaixaDSTheme"))
    }
}
