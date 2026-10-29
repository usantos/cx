package br.gov.caixa.loterias.feature.support

import br.gov.caixa.loterias.feature.support.presentation.route.supportAnswerUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SupportAnswerLinksTest {
    @Test fun `explicit web URL preserves case path query and fragment`() {
        val url = "HTTPS://www.caixa.gov.br/portal?a=1#termo"
        assertEquals(url, supportAnswerUrl(url, 0, url, "HTTPS", "www.caixa.gov.br"))
    }

    @Test fun `domain without scheme opens over HTTPS without changing displayed answer`() {
        val answer = "Consulte www.caixa.gov.br/portal."
        assertEquals(
            "https://www.caixa.gov.br/portal",
            supportAnswerUrl("www.caixa.gov.br/portal", 9, answer, null, "www.caixa.gov.br")
        )
    }

    @Test fun `email domain and non web schemes are not clickable`() {
        val answer = "cliente@caixa.gov.br"
        assertNull(supportAnswerUrl("caixa.gov.br", 8, answer, null, "caixa.gov.br"))
        listOf("ftp", "file", "javascript", "mailto").forEach { scheme ->
            val candidate = "$scheme://caixa.gov.br"
            assertNull(supportAnswerUrl(candidate, 0, candidate, scheme, "caixa.gov.br"))
        }
    }

    @Test fun `legacy hostname rules reject IP localhost malformed label and overlong host`() {
        listOf(
            "127.0.0.1",
            "localhost",
            "-caixa.gov.br",
            "caixa-.gov.br",
            "${"a".repeat(64)}.gov.br",
            "caixa.123",
            ""
        ).forEach { host ->
            val url = "https://$host"
            assertNull(supportAnswerUrl(url, 0, url, "https", host))
        }
        assertNull(supportAnswerUrl("https://", 0, "https://", "https", null))
        val numericStart = "http://1caixa.gov.br"
        assertEquals(numericStart, supportAnswerUrl(numericStart, 0, numericStart, "http", "1caixa.gov.br"))
    }

    @Test fun `validation uses Android parsed host without extra restrictions on path`() {
        val url = "https://caixa.gov.br/caminho com espaço"
        assertEquals(url, supportAnswerUrl(url, 0, url, "https", "caixa.gov.br"))
        val encoded = "https://%63aixa.gov.br/portal"
        assertEquals(encoded, supportAnswerUrl(encoded, 0, encoded, "https", "caixa.gov.br"))
    }
}
