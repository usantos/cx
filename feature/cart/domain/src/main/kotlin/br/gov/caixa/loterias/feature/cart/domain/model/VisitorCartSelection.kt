package br.gov.caixa.loterias.feature.cart.domain.model

/** Seleções que tornam duas apostas de visitantes idênticas, independentemente dos identificadores de armazenamento e do preço. */
data class VisitorCartSelection(
    val modality: CartModality,
    val targetContest: Int,
    val contestType: String?,
    val numbers: CartNumbers?,
    val clovers: List<Int>,
    val repeatCount: Int,
    val favoriteTeamNumber: Int?,
    val luckyMonthNumber: Int?,
    val loteca: List<CartLotecaMatch>,
    val lotogol: List<CartLotogolMatch>,
)
