package br.gov.caixa.loterias.feature.responsiblegaming.domain.model

import java.math.BigDecimal

data class SocialTransfers(
    val accumulatedRevenue: BigDecimal,
    val lastWeekRevenue: BigDecimal,
    val baseDate: String,
    val socialProgramsPercentage: BigDecimal,
    val educationPercentage: BigDecimal,
    val culturePercentage: BigDecimal,
    val sportPercentage: BigDecimal,
    val securityPercentage: BigDecimal,
    val socialSecurityPercentage: BigDecimal,
    val otherPercentage: BigDecimal,
) {
    fun amountFor(percentage: BigDecimal): BigDecimal =
        accumulatedRevenue.multiply(percentage).divide(BigDecimal.valueOf(100))
}
