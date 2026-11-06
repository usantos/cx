// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.data.mapper.serviceResultOf
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ServiceResultBoundaryTest {
    @Test fun `HTTP exception metadata becomes a business notice without enabling registration implicitly`() = runBlocking {
        val login = serviceResultOf<String> { throw HttpException(Response.error<String>(401, "{}".toResponseBody())) }
        assertEquals(ServiceResult.Rejected(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED))), login)
        val registration =
            serviceResultOf<String> {
                throw HttpException(Response.error<String>(400, "{\"codigo\":\"018003\",\"mensagem\":\"register\"}".toResponseBody()))
            }
        assertEquals(
            ServiceResult.Rejected(ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))),
            registration
        )
    }

    @Test fun `unexpected service errors produce a safe failure while cancellation remains cancellation`() = runBlocking {
        val result = serviceResultOf<String> { throw IllegalStateException("private backend details") }
        assertEquals(ServiceResult.Failed(AppError.Unexpected), result)
        val cancellation = CancellationException("cancelled by caller")
        assertSame(cancellation, runCatching { serviceResultOf<String> { throw cancellation } }.exceptionOrNull())
    }
}
