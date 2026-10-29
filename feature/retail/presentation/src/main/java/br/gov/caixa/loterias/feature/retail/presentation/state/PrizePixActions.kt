package br.gov.caixa.loterias.feature.retail.presentation.state

internal data class PrizePixActions(
    val cancelPixAndExit: () -> Unit,
    val consultPix: () -> Unit,
    val confirmPix: () -> Unit,
    val selectCpfPixKey: () -> Unit,
    val selectOtherPixKey: () -> Unit,
    val changePixKey: (String) -> Unit,
    val togglePixOwnership: () -> Unit,
)
