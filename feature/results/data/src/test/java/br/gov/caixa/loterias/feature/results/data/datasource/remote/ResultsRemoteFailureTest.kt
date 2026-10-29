package br.gov.caixa.loterias.feature.results.data.datasource.remote

import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import java.io.IOException
import java.lang.reflect.Proxy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ResultsRemoteFailureTest {
    @Test fun `shield HTTP failure is translated at the remote boundary`() = runTest {
        val httpFailure = HttpException(Response.error<Unit>(503, "unavailable".toResponseBody()))
        try {
            source(httpFailure).sportsTeamShields()
            fail("Expected an IO failure independent of Retrofit")
        } catch (failure: IOException) {
            assertSame(httpFailure, failure.cause)
        }
    }

    @Test fun `shield IO failure retains its original identity`() = runTest {
        val offline = IOException("offline")
        try {
            source(offline).sportsTeamShields()
            fail("Expected network failure")
        } catch (failure: IOException) {
            assertSame(offline, failure)
        }
    }

    @Test fun `shield cancellation is never converted to a network failure`() = runTest {
        val cancelled = CancellationException("cancelled")
        try {
            source(cancelled).sportsTeamShields()
            fail("Expected cancellation")
        } catch (failure: CancellationException) {
            assertSame(cancelled, failure)
        }
    }

    @Test fun `shield programming errors are never hidden`() = runTest {
        val bug = IllegalStateException("invalid configuration")
        try {
            source(bug).sportsTeamShields()
            fail("Expected programming error")
        } catch (failure: IllegalStateException) {
            assertSame(bug, failure)
        }
    }

    private fun source(failure: Exception): ResultsRemoteDataSourceImpl {
        val api = Proxy.newProxyInstance(BackendApi::class.java.classLoader, arrayOf(BackendApi::class.java)) { _, method, args ->
            check(method.name == "sportsTeams") { "Unexpected call ${method.name}" }
            // Retoma a chamada suspensa para que falhas verificadas de IO não sejam encapsuladas pelo Proxy Java.
            @Suppress("UNCHECKED_CAST")
            val continuation = args.last() as kotlin.coroutines.Continuation<Any?>
            continuation.resumeWith(Result.failure(failure))
            kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
        } as BackendApi
        return ResultsRemoteDataSourceImpl(LoteriasApiProvider(), BackendApiProvider().apply { replace(api) })
    }
}
