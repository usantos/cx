package br.gov.caixa.loterias.feature.home.presentation.delivery

/** Ações do menu mantidas pelo coordenador base e entregues ao renderer instalado. */
data class HomeDrawerActions(
    val toggleShake: (Boolean?) -> Unit,
    val consumeShakeTooltip: () -> Unit,
    val open: (String) -> Unit,
    val logout: () -> Unit,
    val close: () -> Unit,
    val externalFailure: () -> Unit,
)
