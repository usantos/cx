package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.core.network.dto.account.AccountSessionDto

data class AccountVerificationRemoteData(
    val session: AccountSessionDto?,
    val redirect: String?,
)
