package br.gov.caixa.loterias.feature.account.domain.model

import br.gov.caixa.loterias.core.common.result.AppError

sealed interface PostLoginAccessResult {
    data class Verified(val access: AccountAccess) : PostLoginAccessResult
    data object BlockedAccountCheckFailed : PostLoginAccessResult
    data class AccountCheckFailed(val error: AppError) : PostLoginAccessResult
}
