package br.gov.caixa.loterias.core.domain.cart

import java.math.BigDecimal

/** Read-only migration snapshot. Reading never acknowledges, uploads, or removes local bets. */
fun interface LegacyLocalCartRepository { suspend fun snapshot(): List<LegacyLocalCartBet> }

enum class LegacyCartModality {
    DIA_DE_SORTE, DUPLA_SENA, LOTECA, LOTOFACIL, LOTOGOL, LOTOMANIA, MEGA_SENA,
    QUINA, TIMEMANIA, SUPER_7, MAIS_MILIONARIA, INSTANTANEA, COMBO, BOLAO,
}

sealed interface LegacyCartNumbers {
    data class Flat(val values: List<Int>) : LegacyCartNumbers {
        override fun toString() = "LegacyCartNumbers.Flat(redacted)"
    }
    data class Columns(val values: List<List<Int>>) : LegacyCartNumbers {
        override fun toString() = "LegacyCartNumbers.Columns(redacted)"
    }
}

data class LegacyCartCode<T>(val value: T, val description: String?)

data class LegacyCartTeam(
    val number: Int,
    val name: String?,
    val countryNumber: Int,
    val country: String?,
    val countryCode: String?,
    val state: String?,
    val className: String?,
    val isNationalTeam: Boolean,
    val shortDescription: String?,
    val longDescription: String?,
)

data class LegacyCartMatchTeam(val team: LegacyCartTeam, val parameter: LegacyCartTeam?, val score: String?, val winner: Boolean)
data class LegacyCartLotecaMatch(val first: LegacyCartMatchTeam, val second: LegacyCartMatchTeam, val draw: Boolean)
data class LegacyCartLotogolMatch(val first: LegacyCartMatchTeam, val second: LegacyCartMatchTeam, val firstScoreIndex: Int, val secondScoreIndex: Int)
data class LegacyCartMonth(val number: Int, val name: String?, val abbreviation: String?)

data class LegacyLocalCartBet(
    val localId: Long,
    val serverId: Long?,
    val modality: LegacyCartModality,
    val contestType: LegacyCartCode<String?>,
    val numbers: LegacyCartNumbers?,
    val clovers: List<Int>?,
    val quickPickIndicator: LegacyCartCode<Int>,
    val amount: BigDecimal,
    val targetContest: Int,
    val repeatCount: Int,
    val betCount: Int,
    val mirrored: Boolean,
    val generateMirror: Boolean,
    val initialContest: Int,
    val status: LegacyCartCode<Long>,
    val replacement: Boolean,
    val effectiveDate: String?,
    val effectiveTime: String?,
    val mirrorLink: Long,
    val numberCount: Int,
    val cloverCount: Int,
    val quickPick: Boolean,
    val quickPickCount: Int,
    val favoriteTeam: LegacyCartTeam?,
    val luckyMonth: LegacyCartMonth?,
    val loteca: List<LegacyCartLotecaMatch>,
    val lotogol: List<LegacyCartLotogolMatch>,
) {
    override fun toString() = "LegacyLocalCartBet(redacted)"
}
