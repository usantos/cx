package br.gov.caixa.loterias.feature.onboarding.data.repository

import br.gov.caixa.loterias.feature.onboarding.domain.model.LauncherIconPreset
import java.nio.file.Files
import java.nio.file.Path
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Test

class FirebaseLauncherIconConfigurationRepositoryImplTest {
    private val clock = Clock.fixed(Instant.parse("2026-10-01T02:59:59Z"), ZoneOffset.UTC)

    @Test fun legacyTextWithoutDatesDoesNotActivateCampaign() {
        val repository = FirebaseLauncherIconConfigurationRepositoryImpl({ "outubro_rosa" }, clock)
        assertEquals(LauncherIconPreset.DEFAULT, repository.activePreset)
    }

    @Test fun activatedJsonIsReadAgainAndUsesTheConfiguredTimezone() {
        var activated = calendar("""{"icon":"setembro_amarelo","start":"09-01","end":"09-30"}""")
        val repository = FirebaseLauncherIconConfigurationRepositoryImpl({ activated }, clock)
        assertEquals(LauncherIconPreset.SETEMBRO_AMARELO, repository.activePreset)
        assertEquals(Instant.parse("2026-10-01T03:00:00Z"), repository.nextEvaluationAt())
        activated = calendar("""{"icon":"outubro_rosa","start":"10-01","end":"10-31"}""")
        assertEquals(LauncherIconPreset.DEFAULT, repository.activePreset)
        assertEquals(
            LauncherIconPreset.OUTUBRO_ROSA,
            repository.activeSchedule.presetAt(Instant.parse("2026-10-01T03:00:00Z"))
        )
    }

    @Test fun malformedJsonTimezoneAndUnavailableValuesUseTheDefaultSchedule() {
        val invalid = listOf(
            "",
            "null",
            "[]",
            "{broken",
            "42",
            "true",
            "\"outubro_rosa\"",
            """{"timezone":"Unknown/Zone","campaigns":[]}"""
        )
        invalid.forEach { value ->
            val repository = FirebaseLauncherIconConfigurationRepositoryImpl({ value }, clock)
            assertEquals(value, LauncherIconPreset.DEFAULT, repository.activePreset)
            assertEquals(value, Instant.parse("2026-10-01T03:00:00Z"), repository.nextEvaluationAt())
        }
        val unavailable = FirebaseLauncherIconConfigurationRepositoryImpl({ error("Firebase unavailable") }, clock)
        assertEquals(LauncherIconPreset.DEFAULT, unavailable.activePreset)
    }

    @Test fun invalidCampaignsAreSkippedAndTheFirstValidMatchingCampaignWins() {
        val json = calendar(
            """null,42,{"icon":"unknown","start":"09-01","end":"09-30"},
                {"icon":"outubro_rosa","start":"09-01"},
                {"icon":"outubro_rosa","start":"9-01","end":"09-30"},
                {"icon":"outubro_rosa","start":"02-30","end":"09-30"},
                {"icon":"outubro_rosa","start":901,"end":"09-30"},
                {"icon":"setembro_amarelo","start":"09-01","end":"09-30"},
                {"icon":"outubro_rosa","start":"09-01","end":"09-30"}"""
        )
        assertEquals(
            LauncherIconPreset.SETEMBRO_AMARELO,
            FirebaseLauncherIconConfigurationRepositoryImpl({ json }, clock).activePreset
        )
    }

    @Test fun campaignsWithoutDatesAndCustomDefaultsCannotForceAnUndatedIcon() {
        val json = """{"default_icon":"outubro_rosa","timezone":"America/Sao_Paulo",
            "campaigns":[{"icon":"outubro_rosa"}]}"""
        assertEquals(LauncherIconPreset.DEFAULT, FirebaseLauncherIconConfigurationRepositoryImpl({ json }, clock).activePreset)
    }

    @Test fun allCampaignPresetsAreAcceptedWithInclusiveAnnualDates() {
        val json =
            calendar(
                """{"icon":"agosto_lilas","start":"08-01","end":"08-31"},
            {"icon":"setembro_amarelo","start":"09-01","end":"09-30"},
            {"icon":"outubro_rosa","start":"10-01","end":"10-31"},
            {"icon":"novembro_azul","start":"11-01","end":"11-30"}"""
            )
        val schedule = FirebaseLauncherIconConfigurationRepositoryImpl({ json }, clock).activeSchedule
        listOf(
            "2027-08-01T03:00:00Z" to LauncherIconPreset.AGOSTO_LILAS,
            "2027-09-30T23:00:00Z" to LauncherIconPreset.SETEMBRO_AMARELO,
            "2027-10-31T23:00:00Z" to LauncherIconPreset.OUTUBRO_ROSA,
            "2027-11-30T23:00:00Z" to LauncherIconPreset.NOVEMBRO_AZUL,
            "2027-12-01T03:00:00Z" to LauncherIconPreset.DEFAULT
        ).forEach { (instant, preset) -> assertEquals(preset, schedule.presetAt(Instant.parse(instant))) }
    }

    @Test fun invalidJsonWithUnquotedKeysCannotActivateCampaign() {
        val value = """{timezone:"America/Sao_Paulo",campaigns:[{icon:"setembro_amarelo",start:"09-01",end:"09-30"}]}"""
        assertEquals(LauncherIconPreset.DEFAULT, FirebaseLauncherIconConfigurationRepositoryImpl({ value }, clock).activePreset)
    }

    @Test fun publishedExampleResolvesAnnualCampaignsAndTheDefaultOutsideTheirDates() {
        val root = generateSequence(Path.of(System.getProperty("user.dir"))) { it.parent }
            .first { Files.isRegularFile(it.resolve("config/firebase/app_launcher_icon.json")) }
        val json = Files.readString(root.resolve("config/firebase/app_launcher_icon.json"))
        val schedule = FirebaseLauncherIconConfigurationRepositoryImpl({ json }, clock).activeSchedule
        listOf(
            "2027-07-31T12:00:00Z" to LauncherIconPreset.DEFAULT,
            "2027-08-01T12:00:00Z" to LauncherIconPreset.AGOSTO_LILAS,
            "2027-09-01T12:00:00Z" to LauncherIconPreset.SETEMBRO_AMARELO,
            "2027-10-01T12:00:00Z" to LauncherIconPreset.OUTUBRO_ROSA,
            "2027-11-01T12:00:00Z" to LauncherIconPreset.NOVEMBRO_AZUL,
            "2027-12-01T12:00:00Z" to LauncherIconPreset.DEFAULT
        ).forEach { (instant, preset) -> assertEquals(preset, schedule.presetAt(Instant.parse(instant))) }
    }

    private fun calendar(campaigns: String) = """{"default_icon":"default","timezone":"America/Sao_Paulo","campaigns":[$campaigns]}"""
}
