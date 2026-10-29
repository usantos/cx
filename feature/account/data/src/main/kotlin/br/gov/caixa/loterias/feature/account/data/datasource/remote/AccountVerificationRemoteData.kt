package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.feature.account.data.dto.AccountSessionDto

data class AccountVerificationRemoteData(
    val session: AccountSessionDto?,
    val redirect: String?,
)
