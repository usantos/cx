package br.gov.caixa.loterias.feature.onboarding.domain

import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconCampaign
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconSchedule
import java.time.Instant
import java.time.MonthDay
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class LauncherIconScheduleTest {
    @Test fun annualCampaignIncludesBothLimitsAndReturnsToDefaultOutsideThem() {
        val schedule = schedule("10-01", "10-31")
        listOf(
            "2026-10-01T02:59:59Z" to LauncherIconPreset.DEFAULT,
            "2026-10-01T03:00:00Z" to LauncherIconPreset.OUTUBRO_ROSA,
            "2026-11-01T02:59:59Z" to LauncherIconPreset.OUTUBRO_ROSA,
            "2026-11-01T03:00:00Z" to LauncherIconPreset.DEFAULT,
            "2030-10-17T12:00:00Z" to LauncherIconPreset.OUTUBRO_ROSA
        ).forEach { (time, expected) -> assertEquals(expected, schedule.presetAt(Instant.parse(time))) }
    }

    @Test fun crossingNewYearAndSingleDayCampaignsRemainAnnual() {
        val schedule = schedule("12-20", "01-10")
        listOf(
            "2026-12-19T12:00:00Z" to LauncherIconPreset.DEFAULT,
            "2026-12-20T12:00:00Z" to LauncherIconPreset.OUTUBRO_ROSA,
            "2027-01-10T12:00:00Z" to LauncherIconPreset.OUTUBRO_ROSA,
            "2027-01-11T12:00:00Z" to LauncherIconPreset.DEFAULT
        ).forEach { (time, expected) -> assertEquals(expected, schedule.presetAt(Instant.parse(time))) }
        assertEquals(LauncherIconPreset.OUTUBRO_ROSA, schedule("02-29", "02-29").presetAt(Instant.parse("2028-02-29T12:00:00Z")))
        assertEquals(LauncherIconPreset.DEFAULT, schedule("02-29", "02-29").presetAt(Instant.parse("2027-02-28T12:00:00Z")))
    }

    @Test fun firstMatchingCampaignHasPriority() {
        val schedule = LauncherIconSchedule(
            campaigns = listOf(
                campaign("10-01", "10-31", LauncherIconPreset.OUTUBRO_ROSA),
                campaign("10-01", "10-31", LauncherIconPreset.AGOSTO_LILAS)
            )
        )
        assertEquals(LauncherIconPreset.OUTUBRO_ROSA, schedule.presetAt(Instant.parse("2026-10-10T12:00:00Z")))
    }

    @Test fun nextEvaluationIsStrictlyNextLocalMidnightIncludingDaylightSavingChanges() {
        val schedule = LauncherIconSchedule(timezone = ZoneId.of("America/New_York"))
        assertEquals(Instant.parse("2026-03-09T04:00:00Z"), schedule.nextEvaluationAt(Instant.parse("2026-03-08T05:00:00Z")))
        assertEquals(Instant.parse("2026-11-02T05:00:00Z"), schedule.nextEvaluationAt(Instant.parse("2026-11-01T04:00:00Z")))
        assertEquals(LauncherIconPreset.DEFAULT, schedule.presetAt(Instant.parse("2026-10-10T12:00:00Z")))
    }

    private fun schedule(start: String, end: String) = LauncherIconSchedule(campaigns = listOf(campaign(start, end)))

    private fun campaign(start: String, end: String, icon: LauncherIconPreset = LauncherIconPreset.OUTUBRO_ROSA) =
        LauncherIconCampaign(icon, MonthDay.parse("--$start"), MonthDay.parse("--$end"))
}
