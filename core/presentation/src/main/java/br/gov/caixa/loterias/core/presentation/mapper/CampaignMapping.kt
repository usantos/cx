package br.gov.caixa.loterias.core.presentation.mapper

import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign

fun CommemorativeCampaign?.toDSCampaign(): CaixaDSCampaign = when (this) {
    CommemorativeCampaign.OUTUBRO_ROSA -> CaixaDSCampaign.OCTOBER_ROSE
    CommemorativeCampaign.SETEMBRO_AZUL -> CaixaDSCampaign.SEPTEMBER_BLUE
    null -> CaixaDSCampaign.DEFAULT
}
