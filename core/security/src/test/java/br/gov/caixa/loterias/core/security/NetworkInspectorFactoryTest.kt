// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.security

import br.gov.caixa.loterias.core.security.inspection.createNetworkInspector
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NetworkInspectorFactoryTest {
    @Test fun `configured inspector preserves original transport request response and credentials`() {
        val request = Request.Builder().url("https://api.example.test/read?cpf=synthetic-private-document")
            .header("Authorization", "Bearer synthetic-private-token")
            .post("{\"cpf\":\"synthetic-private-document\"}".toRequestBody("application/json".toMediaType())).build()
        var proceeds = 0
        val client = OkHttpClient.Builder()
            .addInterceptor(createNetworkInspector(RuntimeEnvironment.getApplication()))
            .addInterceptor { chain ->
                val actual = chain.request()
                proceeds++
                assertSame(request, actual)
                Response.Builder().request(actual).protocol(Protocol.HTTP_1_1).code(200).message("OK")
                    .body("{\"payload\":\"original response\"}".toResponseBody("application/json".toMediaType())).build()
            }.build()
        client.newCall(request).execute().use {
            assertEquals("{\"payload\":\"original response\"}", it.body.string())
            assertSame(request, it.request)
            assertEquals("Bearer synthetic-private-token", it.request.header("Authorization"))
        }
        assertEquals(1, proceeds)
    }
}
