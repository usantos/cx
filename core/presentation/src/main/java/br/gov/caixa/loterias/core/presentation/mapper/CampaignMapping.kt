package br.gov.caixa.loterias.core.presentation.mapper

import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign

fun CommemorativeCampaign?.toDSCampaign(): LoteriasCaixaDSCampaign = when (this) {
    CommemorativeCampaign.OUTUBRO_ROSA -> LoteriasCaixaDSCampaign.OCTOBER_ROSE
    CommemorativeCampaign.SETEMBRO_AZUL -> LoteriasCaixaDSCampaign.SEPTEMBER_BLUE
    null -> LoteriasCaixaDSCampaign.DEFAULT
}
