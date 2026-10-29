package br.gov.caixa.loterias.feature.cart.domain.model

import java.math.BigDecimal

data class LocalCartBet(
    val localId: Long,
    val serverId: Long?,
    val modality: CartModality,
    val contestType: CartCode<String?>,
    val numbers: CartNumbers?,
    val clovers: List<Int>?,
    val quickPickIndicator: CartCode<Int>,
    val amount: BigDecimal,
    val targetContest: Int,
    val repeatCount: Int,
    val betCount: Int,
    val mirrored: Boolean,
    val generateMirror: Boolean,
    val initialContest: Int,
    val status: CartCode<Long>,
    val replacement: Boolean,
    val effectiveDate: String?,
    val effectiveTime: String?,
    val mirrorLink: Long,
    val numberCount: Int,
    val cloverCount: Int,
    val quickPick: Boolean,
    val quickPickCount: Int,
    val favoriteTeam: CartTeam?,
    val luckyMonth: CartMonth?,
    val loteca: List<CartLotecaMatch>,
    val lotogol: List<CartLotogolMatch>,
) {
    override fun toString() = "LocalCartBet(redacted)"
}
