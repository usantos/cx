package br.gov.caixa.loterias.core.data.mapper

import br.gov.caixa.loterias.core.domain.configuration.model.CampaignActivation
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaignSettings
import br.gov.caixa.loterias.core.network.model.AppParametersDto
import java.time.LocalDate
import java.util.Locale

internal fun AppParametersDto?.toCommemorativeCampaigns(): CommemorativeCampaignSettings {
    val configured = this?.commemorativeCampaigns
    if (configured != null) {
        // Uma lista explícita, inclusive [], substitui as opções legadas.
        return CommemorativeCampaignSettings(configured.mapNotNull { rule ->
            rule ?: return@mapNotNull null
            val campaign = CommemorativeCampaign.entries.firstOrNull { it.name == rule.code?.trim()?.uppercase(Locale.ROOT) }
                ?: return@mapNotNull null
            val areas = rule.areas?.mapNotNull { area ->
                CampaignArea.entries.firstOrNull { it.name == area?.trim()?.uppercase(Locale.ROOT) }
            }?.toSet() ?: setOf(CampaignArea.HOME, CampaignArea.DRAWER)
            if (areas.isEmpty()) return@mapNotNull null
            val start = parseCampaignDate(rule.startsOn)
            val end = parseCampaignDate(rule.endsOn)
            if ((rule.startsOn != null && start == null) || (rule.endsOn != null && end == null)) return@mapNotNull null
            CampaignActivation(campaign, areas, rule.active == true, start, end, rule.priority ?: 0)
        })
    }
    if (this?.octoberRoseEnabled != true) return CommemorativeCampaignSettings()
    val areas = mutableSetOf(CampaignArea.HOME, CampaignArea.DRAWER)
    if (octoberRoseMegaSenaEnabled == true) areas += CampaignArea.MEGA_SENA
    return CommemorativeCampaignSettings(listOf(CampaignActivation(CommemorativeCampaign.OUTUBRO_ROSA, areas)))
}

private fun parseCampaignDate(value: String?): LocalDate? =
    value?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
