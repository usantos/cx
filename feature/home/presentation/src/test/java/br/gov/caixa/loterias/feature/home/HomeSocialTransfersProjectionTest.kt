package br.gov.caixa.loterias.feature.home

import br.gov.caixa.loterias.feature.home.presentation.mapper.initialHomeTransfers
import br.gov.caixa.loterias.feature.home.presentation.mapper.projectedHomeTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeSocialTransfersProjectionTest {
    private val zone = ZoneId.of("America/Sao_Paulo")
    private val baseMillis = LocalDate.of(2026, 10, 21).atStartOfDay(zone).toInstant().toEpochMilli()

    @Test fun `base date uses 48 percent of revenue rather than full revenue`() {
        assertEquals(480.0, transfers().projectedHomeTransfers(baseMillis, zone), 0.000001)
    }

    @Test fun `projects proportional weekly transfers from midnight in device zone`() {
        assertEquals(816.0, transfers().projectedHomeTransfers(baseMillis + 604_800_000, zone), 0.000001)
        assertEquals(528.0, transfers().projectedHomeTransfers(baseMillis + 86_400_000, zone), 0.000001)
    }

    @Test fun `first animation preserves legacy apportioned starting amount`() {
        assertEquals(230.4, transfers().initialHomeTransfers(), 0.000001)
    }

    @Test fun `missing date starts projection from now without invented growth`() {
        assertEquals(480.0, transfers("").projectedHomeTransfers(baseMillis, zone), 0.000001)
    }

    @Test fun `future base date preserves signed elapsed time from legacy`() {
        assertEquals(432.0, transfers().projectedHomeTransfers(baseMillis - 86_400_000, zone), 0.000001)
    }

    private fun transfers(baseDate: String = "21/10/2026") = SocialTransfers(
        accumulatedRevenue = BigDecimal("1000"),
        lastWeekRevenue = BigDecimal("700"),
        baseDate = baseDate,
        socialProgramsPercentage = BigDecimal.ZERO,
        educationPercentage = BigDecimal.ZERO,
        culturePercentage = BigDecimal.ZERO,
        sportPercentage = BigDecimal.ZERO,
        securityPercentage = BigDecimal.ZERO,
        socialSecurityPercentage = BigDecimal.ZERO,
        otherPercentage = BigDecimal.ZERO,
    )
}
