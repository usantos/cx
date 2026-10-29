package br.gov.caixa.loterias.core.domain.configuration.model

import java.time.LocalDate
import java.time.ZoneId

enum class CommemorativeCampaign { SETEMBRO_AZUL, OUTUBRO_ROSA }
enum class CampaignArea { HOME, DRAWER, MEGA_SENA }

data class CampaignActivation(
    val campaign: CommemorativeCampaign,
    val areas: Set<CampaignArea>,
    val enabled: Boolean = true,
    val startsOn: LocalDate? = null,
    val endsOn: LocalDate? = null,
    val priority: Int = 0,
) {
    fun isActive(date: LocalDate): Boolean = enabled &&
        (startsOn == null || !date.isBefore(startsOn)) &&
        (endsOn == null || !date.isAfter(endsOn))
}

/** A ativação pelo servidor é a autoridade. Um mês do calendário, sozinho, nunca ativa uma campanha. */
data class CommemorativeCampaignSettings(val activations: List<CampaignActivation> = emptyList()) {
    fun active(
        area: CampaignArea,
        date: LocalDate = LocalDate.now(ZoneId.of("America/Sao_Paulo")),
    ): CommemorativeCampaign? = activations.asSequence()
        .filter { area in it.areas && it.isActive(date) }
        .maxByOrNull { it.priority }?.campaign
}
