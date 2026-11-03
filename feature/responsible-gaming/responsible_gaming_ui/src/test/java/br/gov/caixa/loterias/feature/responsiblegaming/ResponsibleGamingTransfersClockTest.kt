package br.gov.caixa.loterias.feature.responsiblegaming

import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.presentation.mapper.socialTransferredAmount
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class ResponsibleGamingTransfersClockTest {
    private val zone = ZoneId.of("America/Sao_Paulo")
    private val transfers = SocialTransfers(
        accumulatedRevenue = BigDecimal("1000"), lastWeekRevenue = BigDecimal("700"), baseDate = "14/10/2026",
        socialProgramsPercentage = BigDecimal("99"), educationPercentage = BigDecimal.TEN,
        culturePercentage = BigDecimal.TEN, sportPercentage = BigDecimal.TEN, securityPercentage = BigDecimal.TEN,
        socialSecurityPercentage = BigDecimal.TEN, otherPercentage = BigDecimal.TEN
    )

    @Test fun headerUsesFixedFortyEightPercentAndWeeklyRevenueSinceBaseDate() {
        assertEquals(0, BigDecimal("480").compareTo(socialTransferredAmount(transfers, Instant.parse("2026-10-14T03:00:00Z"), zone)))
        assertEquals(0, BigDecimal("816").compareTo(socialTransferredAmount(transfers, Instant.parse("2026-10-21T03:00:00Z"), zone)))
        assertEquals(0, BigDecimal("528").compareTo(socialTransferredAmount(transfers, Instant.parse("2026-10-15T03:00:00Z"), zone)))
    }

    @Test fun absentOrInvalidBaseDateUsesCurrentTimeAsLegacyFallback() {
        val now = Instant.parse("2026-10-21T03:00:00Z")
        listOf("", "data inválida").forEach { date ->
            assertEquals(0, BigDecimal("480").compareTo(socialTransferredAmount(transfers.copy(baseDate = date), now, zone)))
        }
        assertEquals(0, BigDecimal.ZERO.compareTo(socialTransferredAmount(null, now, zone)))
    }
}
