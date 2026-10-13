package br.gov.caixa.loterias.feature.retail

internal fun locationMessage(status: LocationGateStatus): String = when (status) {
    LocationGateStatus.PERMISSION_REQUIRED,
    LocationGateStatus.PERMISSION_PERMANENTLY_DENIED,
    LocationGateStatus.PROVIDER_DISABLED -> "Precisamos conferir se você está em território brasileiro.\nHabilite sua localização."
    LocationGateStatus.OUTSIDE_BRAZIL -> "As apostas de Loterias só podem ser realizadas em território brasileiro."
    LocationGateStatus.ERROR -> "Não foi possível recuperar sua localização. Por favor, tente novamente"
    LocationGateStatus.CHECKING -> ""
}

internal fun locationActionLabel(status: LocationGateStatus): String? = when (status) {
    LocationGateStatus.PERMISSION_REQUIRED,
    LocationGateStatus.PERMISSION_PERMANENTLY_DENIED -> "Permitir acesso a localização"
    LocationGateStatus.PROVIDER_DISABLED -> "Ir para configurações"
    LocationGateStatus.ERROR -> "Tentar novamente"
    LocationGateStatus.CHECKING,
    LocationGateStatus.OUTSIDE_BRAZIL -> null
}
