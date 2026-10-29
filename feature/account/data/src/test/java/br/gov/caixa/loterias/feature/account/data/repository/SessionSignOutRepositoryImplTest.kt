package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.SessionLogoutApi
import br.gov.caixa.loterias.core.network.api.SessionLogoutApiProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response
import br.gov.caixa.loterias.feature.account.data.repository.SessionSignOutRepositoryImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.data.datasource.remote.SessionSignOutRemoteDataSourceImpl
import br.gov.caixa.loterias.feature.account.data.datasource.remote.SessionSignOutRemoteDataSource

class SessionSignOutRepositoryImplTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val binding = AccountVerificationSessionBinding { captured ->
        assertSame(identity, captured)
        AccountVerificationSession({ "access" }, { true })
    }

    @Test fun `raw success and empty 204 confirm same captured session without a payload`() = runTest {
        for (response in listOf(Response.success("ignored response".toResponseBody()), Response.success<ResponseBody>(204, null))) {
            assertEquals(SessionSignOutResult.Confirmed(identity), repository(response).signOut(identity))
        }
    }

    @Test fun `HTTP failures retain typed redirect business and expired login decisions`() = runTest {
        val failures = listOf(
            401 to "{}" to ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED)),
            400 to "{\"codigo\":\"X\",\"mensagem\":\"Falha\"}" to ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Falha", "X")),
            500 to "{\"redirect\":\"aceitar-termo-de-uso\"}" to ServiceFailure.RedirectRequired(ServiceRedirect.ACCEPT_TERMS),
        )
        for ((http, expected) in failures) {
            assertEquals(SessionSignOutResult.Rejected(expected), repository(Response.error(http.first, http.second.toResponseBody())).signOut(identity))
        }
    }

    @Test fun `timeout connection failure and cancellation never become success`() = runTest {
        for ((failure, expected) in listOf(java.net.SocketTimeoutException() to ServiceConnectionFailure.TIMEOUT, java.io.IOException() to ServiceConnectionFailure.CONNECTION)) {
            assertEquals(SessionSignOutResult.Rejected(ServiceFailure.ConnectionFailed(expected)),
                SessionSignOutRepositoryImpl(SessionSignOutRemoteDataSource { throw failure }).signOut(identity))
        }
        val cancelled = CancellationException("cancelled")
        assertSame(cancelled, runCatching { SessionSignOutRepositoryImpl(SessionSignOutRemoteDataSource { throw cancelled }).signOut(identity) }.exceptionOrNull())
    }

    private fun repository(response: Response<ResponseBody>): SessionSignOutRepositoryImpl {
        val provider = SessionLogoutApiProvider().apply { replace(object : SessionLogoutApi {
            override suspend fun signOut(session: AccountVerificationSession) = response
        }) }
        return SessionSignOutRepositoryImpl(SessionSignOutRemoteDataSourceImpl(provider, binding))
    }
}
