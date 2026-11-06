package br.gov.caixa.loterias.core.common.text

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class TextCatalogTest {
    @Test fun `carrega acentos e preserva quebras de linha dos recursos JVM`() {
        val catalog = TextCatalog("textfixture.messages", TextCatalogTest::class.java.classLoader)
        assertEquals("Atenção\nTente novamente.", catalog.text("notice", locale = Locale("pt", "BR")))
    }

    @Test fun `formata argumentos e consulta o idioma de cada chamada`() {
        val catalog = TextCatalog("textfixture.messages", TextCatalogTest::class.java.classLoader)
        assertEquals("Concurso 1278", catalog.text("contest", CONTEST_NUMBER, locale = Locale("pt", "BR")))
        assertEquals("Contest 1278", catalog.text("contest", CONTEST_NUMBER, locale = Locale.ENGLISH))
        assertEquals("Concurso 1278", catalog.text("contest", CONTEST_NUMBER, locale = Locale("pt", "BR")))
    }
}

private const val CONTEST_NUMBER = 1278
