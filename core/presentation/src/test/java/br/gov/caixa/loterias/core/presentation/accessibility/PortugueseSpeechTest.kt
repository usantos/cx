package br.gov.caixa.loterias.core.presentation.accessibility

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class PortugueseSpeechTest {
    @Test fun cardinalsKeepThousandsHundredsAndMillionsInPortuguese() {
        val examples = mapOf(
            "0" to "zero", "1" to "um", "2" to "dois", "19" to "dezenove", "100" to "cem",
            "101" to "cento e um", "121" to "cento e vinte e um", "1000" to "mil",
            "1001" to "mil e um", "1100" to "mil e cem", "3026" to "três mil e vinte e seis",
            "1000000" to "um milhão", "2000001" to "dois milhões e um",
            "1234567" to "um milhão duzentos e trinta e quatro mil quinhentos e sessenta e sete",
            "-123" to "menos cento e vinte e três"
        )
        examples.forEach { (number, expected) -> assertEquals(number, expected, PortugueseSpeech.integer(number.toBigInteger())) }
    }

    @Test fun feminineQuantitiesRetainGrammaticalAgreementAcrossScales() {
        val examples = mapOf(
            "1" to "uma",
            "2" to "duas",
            "201" to "duzentas e uma",
            "2000" to "duas mil",
            "2000201" to "dois milhões duzentas e uma"
        )
        examples.forEach { (number, expected) -> assertEquals(number, expected, PortugueseSpeech.integer(number.toBigInteger(), true)) }
    }

    @Test fun currencySeparatesReaisAndCentavosWithoutDecimalDigitSpeech() {
        val examples = mapOf(
            "0" to "zero reais",
            "1" to "um real",
            "0.01" to "um centavo",
            "1.01" to "um real e um centavo",
            "3.50" to "três reais e cinquenta centavos",
            "1000000" to "um milhão de reais",
            "-1.50" to "menos um real e cinquenta centavos"
        )
        examples.forEach { (number, expected) -> assertEquals(number, expected, PortugueseSpeech.currency(BigDecimal(number))) }
    }

    @Test fun opaqueIdentifiersPreserveTheirLeadingZeros() {
        assertEquals("A zero zero um", PortugueseSpeech.identifier("A001"))
        assertEquals("Concurso três mil e vinte e seis", PortugueseSpeech.expand("Conc. 3026"))
    }

    @Test fun resultDatesAndOrdinalsKeepTheirMeaningInPortuguese() {
        assertEquals("vinte e sete de outubro de dois mil e vinte e seis", PortugueseSpeech.date("27/10/2026"))
        assertEquals("vinte e seis de outubro de dois mil e vinte e seis", PortugueseSpeech.date("26 out 2026"))
        assertEquals("primeiro de janeiro de dois mil e vinte e seis", PortugueseSpeech.date("2026-01-01"))
        assertEquals("primeiro sorteio", PortugueseSpeech.expand("1º sorteio"))
        assertEquals("trinta e um/dois/dois mil e vinte e seis", PortugueseSpeech.date("31/02/2026"))
    }
}
