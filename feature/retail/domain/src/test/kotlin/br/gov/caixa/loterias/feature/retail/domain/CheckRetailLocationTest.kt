package br.gov.caixa.loterias.feature.retail.domain

import br.gov.caixa.loterias.feature.retail.domain.port.RetailCountrySource
import br.gov.caixa.loterias.feature.retail.domain.model.RetailLocationEligibility
import br.gov.caixa.loterias.feature.retail.domain.usecase.CheckRetailLocation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CheckRetailLocationTest {
    @Test fun `Brazil permits entry regardless of country code case`() = runTest {
        assertEquals(RetailLocationEligibility.ALLOWED, CheckRetailLocation(RetailCountrySource { "br" })())
    }

    @Test fun `foreign country blocks entry`() = runTest {
        assertEquals(RetailLocationEligibility.OUTSIDE_BRAZIL, CheckRetailLocation(RetailCountrySource { "US" })())
    }

    @Test fun `missing or failed country lookup cannot allow entry`() = runTest {
        assertEquals(RetailLocationEligibility.UNAVAILABLE, CheckRetailLocation(RetailCountrySource { null })())
        assertEquals(RetailLocationEligibility.UNAVAILABLE, CheckRetailLocation(RetailCountrySource { error("unavailable") })())
    }

    @Test fun `mock configuration allows entry without accessing device location`() = runTest {
        assertEquals(RetailLocationEligibility.ALLOWED, CheckRetailLocation(RetailCountrySource { error("device accessed") })(mockInsideBrazil = true))
    }

    @Test(expected = CancellationException::class)
    fun `cancellation propagates instead of becoming unavailable`() = runTest {
        CheckRetailLocation(RetailCountrySource { throw CancellationException("screen left") })()
    }
}
