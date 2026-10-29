package br.gov.caixa.loterias.feature.retail.domain.model

import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect

sealed interface PrizeRedemptionResult {
    data class ConnectionFailed(val reason: br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure) : PrizeRedemptionResult
    data object Unchanged : PrizeRedemptionResult
    data class Redeemed(val noticeCode: String? = null) : PrizeRedemptionResult
    data class RegistrationRequired(val message: String) : PrizeRedemptionResult
    data class NoticeRequired(val notice: ServiceNotice) : PrizeRedemptionResult
    data class RedirectRequired(val destination: ServiceRedirect) : PrizeRedemptionResult
}
