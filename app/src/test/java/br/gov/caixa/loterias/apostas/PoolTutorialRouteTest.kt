package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.betting.PoolEntryRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class PoolTutorialRouteTest {
    @Test fun `tutorial keeps pool selection and finishes in marketplace`() {
        assertEquals("betting/tutorial?modalityId=2&contestTypeId=2", PoolTutorialRoute.from(PoolEntryRequest(2, "ESPECIAL")))
        assertEquals("betting/tutorial?modalityId=3&contestTypeId=1", PoolTutorialRoute.from(PoolEntryRequest(3, "NORMAL")))
        assertEquals("betting/pools", nextDestination("betting/tutorial"))
    }
}
