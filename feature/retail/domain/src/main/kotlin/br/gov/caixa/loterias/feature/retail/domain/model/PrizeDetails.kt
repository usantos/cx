package br.gov.caixa.loterias.feature.retail.domain.model

import java.math.BigDecimal

data class PrizeDetails(
    val betId: Long,
    val netValue: BigDecimal,
    val nsbi: String,
    val options: List<PrizeRedemptionOption>,
    val incomeTaxMessage: String = "",
    val modalityId: Int = 2,
    val modalityName: String = "Mega-Sena",
    val initialContest: Int = 0,
    val targetContest: Int = 0,
    val awards: List<PrizeAward> = emptyList(),
    val isPool: Boolean = false,
    val exchange: Boolean = false,
    val combo: Boolean = false,
    val mirror: Boolean = false,
    val surprise: Boolean = false,
    val recurringDraws: Int = 0,
)
