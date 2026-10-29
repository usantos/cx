package br.gov.caixa.loterias.feature.home

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.home.presentation.mapper.formatCompactPrize
import br.gov.caixa.loterias.feature.home.presentation.mapper.formatHomeDrawDate
import br.gov.caixa.loterias.feature.home.presentation.mapper.remainingTimeLabel

class HomeFormattingTest {
    @Test fun `formats currency compact prize and supported dates`() {
        assertTrue(formatBrazilianCurrency(BigDecimal("35000000")).contains("35.000.000"))
        assertEquals("R$ 35 Milhões", formatCompactPrize(BigDecimal("35000000")))
        assertEquals("R$ 850 Mil", formatCompactPrize(BigDecimal("850000")))
        assertTrue(formatCompactPrize(BigDecimal("50")).contains("50"))
        assertTrue(formatHomeDrawDate("10/09/2026 20:00:00").contains("10/09/2026 às 20:00:00"))
        assertTrue(formatHomeDrawDate("10/09/2026").contains("10/09/2026 às 00:00:00"))
        assertTrue(formatHomeDrawDate("2026-09-10T20:00:00").contains("10/09/2026 às 20:00:00"))
        assertEquals("inválida", formatHomeDrawDate("inválida"))
    }

    @Test fun `formats remaining duration with legacy plural rules`() {
        val zone = ZoneId.systemDefault()
        fun millis(date: LocalDateTime) = date.atZone(zone).toInstant().toEpochMilli()
        val closing = "10/09/2026 20:00:00"
        assertEquals("1 dia e 2 horas", remainingTimeLabel(closing, millis(LocalDateTime.of(2026, 9, 9, 17, 59, 59))))
        assertEquals("2 horas e 1 minuto", remainingTimeLabel(closing, millis(LocalDateTime.of(2026, 9, 10, 17, 58, 59))))
        assertEquals("1 minuto", remainingTimeLabel(closing, millis(LocalDateTime.of(2026, 9, 10, 19, 58, 59))))
        assertEquals("1 segundo", remainingTimeLabel(closing, millis(LocalDateTime.of(2026, 9, 10, 19, 59, 59))))
        assertEquals("Encerradas", remainingTimeLabel(closing, millis(LocalDateTime.of(2026, 9, 10, 20, 0))))
        assertEquals("Horário indisponível", remainingTimeLabel("", 0))
    }
}
