package br.gov.caixa.loterias.feature.onboarding.domain.model

import java.time.Instant
import java.time.MonthDay
import java.time.ZoneId

/** Calendário anual; a ordem das campanhas define a prioridade nas sobreposições. */
data class LauncherIconSchedule(
    val timezone: ZoneId = ZoneId.of("America/Sao_Paulo"),
    val campaigns: List<LauncherIconCampaign> = emptyList(),
) {
    fun presetAt(instant: Instant): LauncherIconPreset {
        val date = MonthDay.from(instant.atZone(timezone))
        return campaigns.firstOrNull { it.contains(date) }?.icon ?: LauncherIconPreset.DEFAULT
    }

    fun nextEvaluationAt(instant: Instant): Instant = instant.atZone(timezone).toLocalDate().plusDays(1).atStartOfDay(timezone).toInstant()
}

data class LauncherIconCampaign(val icon: LauncherIconPreset, val start: MonthDay, val end: MonthDay) {
    internal fun contains(date: MonthDay): Boolean = if (start <= end) {
        date >= start && date <= end
    } else {
        date >= start || date <= end
    }
}
