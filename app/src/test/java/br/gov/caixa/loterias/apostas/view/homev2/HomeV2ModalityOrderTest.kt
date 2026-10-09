package br.gov.caixa.loterias.apostas.view.homev2

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeV2ModalityOrderTest {
    private val defaults = listOf("MEGA_SENA", "MAIS_MILIONARIA", "LOTECA", "QUINA")

    @Test fun restoresSavedPositionsAndAppendsNewModalities() {
        assertEquals(listOf("QUINA", "MEGA_SENA", "MAIS_MILIONARIA", "LOTECA"),
            HomeV2ModalityOrder.restore("QUINA,MEGA_SENA", defaults))
    }
    @Test fun ignoresInvalidAndRepeatedEntriesWithoutLosingModalities() {
        assertEquals(listOf("LOTECA", "MEGA_SENA", "MAIS_MILIONARIA", "QUINA"),
            HomeV2ModalityOrder.restore("UNKNOWN,LOTECA,LOTECA", defaults))
        assertEquals(defaults, HomeV2ModalityOrder.restore(null, defaults))
    }
    @Test fun insertsInBothDirectionsAndSurvivesReload() {
        val forward = HomeV2ModalityOrder.move(defaults, "MEGA_SENA", "LOTECA")
        assertEquals(listOf("MAIS_MILIONARIA", "LOTECA", "MEGA_SENA", "QUINA"), forward)
        assertEquals(forward, HomeV2ModalityOrder.restore(forward.joinToString(","), defaults))
        assertEquals(listOf("QUINA", "MEGA_SENA", "MAIS_MILIONARIA", "LOTECA"),
            HomeV2ModalityOrder.move(defaults, "QUINA", "MEGA_SENA"))
    }
    @Test fun invalidOrUnchangedDropKeepsOrder() {
        assertEquals(defaults, HomeV2ModalityOrder.move(defaults, "UNKNOWN", "QUINA"))
        assertEquals(defaults, HomeV2ModalityOrder.move(defaults, "QUINA", "UNKNOWN"))
        assertEquals(defaults, HomeV2ModalityOrder.move(defaults, "QUINA", "QUINA"))
    }
}
