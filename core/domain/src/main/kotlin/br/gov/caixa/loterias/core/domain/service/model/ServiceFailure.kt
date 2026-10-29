package br.gov.caixa.loterias.core.domain.service.model

enum class ServiceRedirect { LOGIN, ACCEPT_TERMS, REGISTER_ACCOUNT, UNAVAILABLE, CART, FAVORITE_CARTS, HOME }

enum class ServiceConnectionFailure { TIMEOUT, CONNECTION, INVALID_RESPONSE }

enum class ServiceNoticeReason {
    BUSINESS_RULE, MORE_BETS_REQUIRED, SERVER_MESSAGE, SERVER_FAILURE,
    INVALID_IDENTIFICATION, LOGIN_REQUIRED, OPERATION_FAILED,
}

data class ServiceNotice(val reason: ServiceNoticeReason, val message: String? = null, val code: String? = null)

/** Decisões do tratamento compartilhado de erros do serviço legado, independentes de HTTP e Android. */
sealed interface ServiceFailure {
    data class ConnectionFailed(val reason: ServiceConnectionFailure) : ServiceFailure
    data object Unchanged : ServiceFailure
    data class NoticeRequired(val notice: ServiceNotice) : ServiceFailure
    data class RedirectRequired(val destination: ServiceRedirect) : ServiceFailure
    data class RegistrationRequired(val message: String) : ServiceFailure
}
