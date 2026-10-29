package br.gov.caixa.loterias.core.domain.payment

import org.junit.Assert.*
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import java.net.URI
import java.net.URLDecoder
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Test
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistration
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationResult
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationToken
import br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistration

class MercadoPagoRegistrationTest {
    @Test fun `service decisions pass through without constructing a browser URL or requesting another token`() = runTest {
        val decisions = listOf(
            br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.Unchanged,
            br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RedirectRequired(br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect.LOGIN),
            br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.NoticeRequired(br.gov.caixa.loterias.core.domain.service.model.ServiceNotice(br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason.LOGIN_REQUIRED)),
            br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RegistrationRequired("Confirme novamente"),
        )
        for (decision in decisions) {
            var calls = 0
            val outcome = PrepareMercadoPagoRegistration { calls++; MercadoPagoRegistrationToken.Rejected(decision) }("pessoa@example.test")
            assertEquals(AppResult.Success(MercadoPagoRegistrationResult.Rejected(decision)), outcome)
            assertEquals(1, calls)
        }
        assertFalse(MercadoPagoRegistrationToken.Available("private-token").toString().contains("private-token"))
    }
    @Test fun `opaque tokens are encoded without changing their contents or destination`() {
        for (token in listOf("opaque", "a&b/+?=#%'\"<>\\", "https://untrusted.invalid/?x=1", "token com espaço ç", "x".repeat(4096))) {
            val request = MercadoPagoRegistration.fromToken(token)
            val uri = URI(request.url)
            assertEquals("https", uri.scheme)
            assertEquals("registration-br.mercadopago.com", uri.host)
            assertEquals("/registration-mp", uri.path)
            assertNull(uri.userInfo); assertNull(uri.fragment)
            assertEquals(-1, uri.port)
            val query = uri.rawQuery.split('&')
            assertEquals(2, query.size)
            assertEquals("mode=mp", query[0])
            assertEquals(token, URLDecoder.decode(query[1].substringAfter('='), "UTF-8"))
            assertEquals("MercadoPagoRegistration(redacted)", request.toString())
        }
    }

    @Test fun `blank oversized and control containing tokens cannot form a browser request`() {
        for (token in listOf("", "   ", "\t", "secret\nvalue", "secret\rvalue", "secret\u0000value", "x".repeat(4097))) {
            val failure = assertThrows(IllegalArgumentException::class.java) { MercadoPagoRegistration.fromToken(token) }
            assertEquals("Token de cadastro Mercado Pago inválido.", failure.message)
        }
    }

    @Test fun `preparation validates and normalizes email before requesting one token`() = runTest {
        val emails = mutableListOf<String>()
        val prepare = PrepareMercadoPagoRegistration { emails += it; MercadoPagoRegistrationToken.Available("secret-token") }
        for (email in listOf("", "invalid", "@example.com", "x@y", "x@y z.com", "x\u0000@example.com", "x".repeat(255) + "@example.com")) {
            assertTrue(prepare(email) is AppResult.Failure)
        }
        assertTrue(emails.isEmpty())
        val result = prepare("  pessoa@example.com  ") as AppResult.Success
        assertEquals(listOf("pessoa@example.com"), emails)
        assertFalse(result.toString().contains("secret-token"))
    }

    @Test fun `repository errors and invalid tokens do not become browser requests`() = runTest {
        assertEquals(AppResult.Failure(AppError.Network), PrepareMercadoPagoRegistration { throw IOException("private") }("pessoa@example.com"))
        val result = PrepareMercadoPagoRegistration { MercadoPagoRegistrationToken.Available("secret\nvalue") }("pessoa@example.com")
        assertTrue(result is AppResult.Failure)
        assertFalse(result.toString().contains("secret"))
    }

    @Test fun `cancellation propagates instead of requesting another token`() = runTest {
        var calls = 0
        val cancelled = CancellationException("cancelled")
        try {
            PrepareMercadoPagoRegistration { calls++; throw cancelled }("pessoa@example.com")
            fail("Expected cancellation")
        } catch (actual: CancellationException) { assertSame(cancelled, actual) }
        assertEquals(1, calls)
    }
}
