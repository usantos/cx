package br.gov.caixa.loterias.core.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.network.adapter.ServerTimeAdapterFactory
import br.gov.caixa.loterias.core.network.model.ApiEnvelope

class ServerTimeAdapterFactoryTest {
    private val observed = mutableListOf<String>()
    private val gson = GsonBuilder().registerTypeAdapterFactory(ServerTimeAdapterFactory { observed += it }).create()
    private val type = object : TypeToken<ApiEnvelope<List<Int>>>() {}.type

    @Test fun `envelope observer preserves generic payload and metadata`() {
        val result = gson.fromJson<ApiEnvelope<List<Int>>>(
            """{"payload":[1,2],"dataHoraServidor":"2026-09-06T18:00:00.000","redirect":"url","mensagem":"ok"}""", type)
        assertEquals(listOf(1, 2), result.payload)
        assertEquals("url", result.redirect)
        assertEquals("ok", result.message)
        assertEquals(listOf("2026-09-06T18:00:00.000"), observed)
    }

    @Test fun `null missing timestamps and unrelated payloads do not notify`() {
        assertNull(gson.fromJson<ApiEnvelope<List<Int>>>("null", type))
        assertNull(gson.fromJson<ApiEnvelope<List<Int>>>("{}", type).payload)
        assertNull(gson.fromJson<ApiEnvelope<List<Int>>>("""{"dataHoraServidor":null}""", type).serverDateTime)
        gson.fromJson("""{"dataHoraServidor":"arbitrary"}""", Map::class.java)
        assertTrue(observed.isEmpty())
    }

    @Test fun `serialization never changes clock and invalid payloads still fail`() {
        assertTrue(gson.toJson(ApiEnvelope(payload = listOf(1), serverDateTime = "server"), type).contains("server"))
        assertEquals("null", gson.toJson(null, type))
        assertThrows(JsonSyntaxException::class.java) {
            gson.fromJson<ApiEnvelope<List<Int>>>("""{"dataHoraServidor":"server","payload":["oops"]}""", type)
        }
        assertTrue(observed.isEmpty())
    }
}
