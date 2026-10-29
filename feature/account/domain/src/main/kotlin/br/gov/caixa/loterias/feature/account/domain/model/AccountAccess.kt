package br.gov.caixa.loterias.feature.account.domain.model

import java.math.BigDecimal

data class AccountAccess(
    val destination: PostLoginDestination,
    val temporarySuspension: Boolean,
    val dailyLimit: BigDecimal,
    val status: AccountStatus? = null,
)
