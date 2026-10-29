package br.gov.caixa.loterias.core.domain.marketplace

import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MarketplaceSpecialContestsTest {
    private val contests = MarketplaceSpecialContests(megaThirtyYearsContest = 3000, lotecaFathersDayContest = 1200)

    @Test fun `mega anniversary label requires configured contest identity and special type`() {
        assertEquals("mega-sena\n30 anos", contests.label("MEGA_SENA", 2, 3000))
        assertEquals("mega-sena\n30 anos", contests.label("mega-sena", 2, 3000))
        assertNull(contests.label("MEGA_SENA", 1, 3000))
        assertNull(contests.label("MEGA_SENA", 2, 3001))
        assertNull(contests.label("QUINA", 2, 3000))
    }

    @Test fun `loteca fathers day label requires configured contest identity and special type`() {
        assertEquals("loteca\ndos pais", contests.label("LOTECA", 2, 1200))
        assertNull(contests.label("LOTECA", 1, 1200))
        assertNull(contests.label("LOTECA", 2, 1201))
        assertNull(contests.label("MEGA_SENA", 2, 1200))
    }

    @Test fun `missing zero and negative configuration keep standard special labels as fallback`() {
        for (config in listOf(MarketplaceSpecialContests(), MarketplaceSpecialContests(0, 0), MarketplaceSpecialContests(-1, -1))) {
            assertNull(config.label("MEGA_SENA", 2, 3000))
            assertNull(config.label("LOTECA", 2, 1200))
            assertNull(config.label("MEGA_SENA", 2, 0))
            assertNull(config.label("LOTECA", 2, -1))
        }
        assertNull(contests.label("MEGA_SENA", 0, 3000))
        assertNull(contests.label("LOTECA", 3, 1200))
    }
}
