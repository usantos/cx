package br.gov.caixa.loterias.core.network

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.core.network.logging.NetworkTrafficSanitizer

class NetworkTrafficSanitizerTest {
    @Test fun `payment and redemption secrets are masked while service error codes remain visible`() {
        val safe = NetworkTrafficSanitizer.body("""{"security_code":"private-cvv","chavePix":"private-pix","beneficiario":{"chave":"private-key"},"comprovante":{"codigo":"private-redemption","qrCode":"private-qr","nsb":"private-nsb"},"erro":{"codigo":"LTR-503","mensagem":"Indisponível"}}""")
        assertFalse(safe.contains("private-"))
        assertTrue(safe.contains("LTR-503"))
    }

    @Test fun `redacts nested credentials while retaining useful lottery data`() {
        val safe = NetworkTrafficSanitizer.body("""{"resultado":[{"cpf":"private-cpf","nome":"private-name","concurso":3070,"valorPremio":20}],"refresh_token":"private-token"}""")
        assertFalse(safe.contains("private-"))
        assertTrue(safe.contains("\"concurso\":3070"))
        assertTrue(safe.contains("\"valorPremio\":20"))
        assertFalse(NetworkTrafficSanitizer.body("broken private-payload").contains("private-payload"))
    }

    @Test fun `inspection urls hide personal values but retain the actual query shape`() {
        val safe = NetworkTrafficSanitizer.url("https://example.test/apostas?mes=45&cpf=private-cpf&token=private-token".toHttpUrl()).toString()
        assertTrue(safe.contains("mes=45"))
        assertTrue(safe.contains("cpf="))
        assertFalse(safe.contains("private-"))
    }
}
