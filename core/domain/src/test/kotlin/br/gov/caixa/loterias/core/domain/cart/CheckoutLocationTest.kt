package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class CheckoutLocationTest {
    @Test fun `disabled provider precedes permissions and permission history controls settings`() = runTest {
        val source = Source()
        val check = CheckCheckoutLocation(source)
        for (fine in listOf(false, true)) for (coarse in listOf(false, true)) {
            source.access = CheckoutLocationAccess(false, fine, coarse, 5)
            assertEquals(AppResult.Success(CheckoutLocationStatus.PROVIDER_DISABLED), check())
            if (!fine || !coarse) for (count in listOf(0, 1, 2, 10)) {
                source.access = CheckoutLocationAccess(true, fine, coarse, count)
                assertEquals(AppResult.Success(if (count < 2) CheckoutLocationStatus.PERMISSION_REQUIRED else CheckoutLocationStatus.PERMISSION_SETTINGS_REQUIRED), check())
            }
        }
        assertEquals(0, source.countryCalls)
    }

    @Test fun `only geocoded Brazil with both permissions allows checkout`() = runTest {
        val source = Source()
        val check = CheckCheckoutLocation(source)
        for ((country, expected) in listOf(
            "BR" to CheckoutLocationStatus.IN_BRAZIL,
            "br" to CheckoutLocationStatus.IN_BRAZIL,
            "AR" to CheckoutLocationStatus.OUTSIDE_BRAZIL,
            null to CheckoutLocationStatus.UNAVAILABLE,
            "" to CheckoutLocationStatus.UNAVAILABLE,
        )) {
            source.country = country
            assertEquals(AppResult.Success(expected), check())
        }
        assertEquals(5, source.countryCalls)
    }

    @Test fun `records requests and propagates failures and cancellation without granting access`() = runTest {
        val source = Source()
        val record = RecordCheckoutLocationRequest(source)
        assertEquals(AppResult.Success(Unit), record())
        assertEquals(1, source.recorded)
        source.failure = SecurityException("revoked")
        assertEquals(AppResult.Failure(AppError.Unexpected), CheckCheckoutLocation(source)())
        assertEquals(AppResult.Failure(AppError.Unexpected), record())
        source.failure = CancellationException("cancelled")
        for (operation in listOf<suspend () -> Any>({ CheckCheckoutLocation(source)() }, { record() })) {
            try { operation(); fail("Cancellation swallowed") } catch (_: CancellationException) { }
        }
    }

    private class Source : CheckoutLocationSource {
        var access = CheckoutLocationAccess(true, true, true, 0)
        var country: String? = "BR"
        var countryCalls = 0
        var recorded = 0
        var failure: Exception? = null
        override fun access() = access
        override suspend fun countryCode(): String? {
            failure?.let { throw it }
            countryCalls++
            return country
        }
        override fun recordPermissionRequest() {
            failure?.let { throw it }
            recorded++
        }
    }
}
