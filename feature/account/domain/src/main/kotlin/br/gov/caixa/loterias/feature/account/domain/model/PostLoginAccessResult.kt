package br.gov.caixa.loterias.feature.account.domain.model

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity

sealed interface PostLoginAccessResult {
    data class Verified(val access: AccountAccess, val identity: AuthenticatedSessionIdentity? = null) : PostLoginAccessResult
    data object SessionSuperseded : PostLoginAccessResult
    data class BlockedAccountCheckFailedForSession(val identity: AuthenticatedSessionIdentity) : PostLoginAccessResult
    data object BlockedAccountCheckFailed : PostLoginAccessResult
    data class AccountCheckFailed(val error: AppError, val identity: AuthenticatedSessionIdentity? = null) : PostLoginAccessResult
}
