package br.gov.caixa.loterias.feature.retail.presentation.mapper

import br.gov.caixa.loterias.feature.retail.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStatus

internal fun locationMessage(status: LocationGateStatus): String = when (status) {
    LocationGateStatus.PERMISSION_REQUIRED,
    LocationGateStatus.PERMISSION_PERMANENTLY_DENIED,
    LocationGateStatus.PROVIDER_DISABLED,
    -> messageText("precisamos_conferir_se_voce_esta_0dfcfe")

    LocationGateStatus.OUTSIDE_BRAZIL -> messageText("as_apostas_de_loterias_so_podem_27508d")

    LocationGateStatus.ERROR -> messageText("nao_foi_possivel_recuperar_sua_l_9fee10")

    LocationGateStatus.CHECKING -> ""
}

internal fun locationActionLabel(status: LocationGateStatus): String? = when (status) {
    LocationGateStatus.PERMISSION_REQUIRED,
    LocationGateStatus.PERMISSION_PERMANENTLY_DENIED,
    -> messageText("permitir_acesso_a_localizacao_9eea5f")

    LocationGateStatus.PROVIDER_DISABLED -> messageText("ir_para_configuracoes_00f494")

    LocationGateStatus.ERROR -> "Tentar novamente"

    LocationGateStatus.CHECKING,
    LocationGateStatus.OUTSIDE_BRAZIL,
    -> null
}
