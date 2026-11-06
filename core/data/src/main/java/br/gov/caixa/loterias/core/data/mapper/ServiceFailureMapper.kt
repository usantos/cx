package br.gov.caixa.loterias.core.data.mapper

import br.gov.caixa.loterias.core.data.localizedMessage as messageText

import br.gov.caixa.loterias.core.network.error.BackendHttpFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect

/** Decisões compartilhadas de checkRedirect para falhas de notificação, resgate e token de cadastro. */
fun BackendHttpFailure.toServiceFailure(allowRegistration: Boolean): ServiceFailure {
    metadata?.redirect?.let { directive ->
        if (directive in ignoredServiceRedirects) return ServiceFailure.Unchanged
        val destination = when (directive) {
            "login" -> ServiceRedirect.LOGIN
            "aceitar-termo-de-uso" -> ServiceRedirect.ACCEPT_TERMS
            "cadastrar-apostador" -> ServiceRedirect.REGISTER_ACCOUNT
            "indisponibilidade" -> ServiceRedirect.UNAVAILABLE
            "carrinho" -> ServiceRedirect.CART
            "carrinhos-favoritos" -> ServiceRedirect.FAVORITE_CARTS
            else -> null
        }
        return destination?.let { ServiceFailure.RedirectRequired(it) }
            ?: ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))
    }
    if (statusCode == 401) return ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.LOGIN_REQUIRED))
    if (statusCode == 500) {
        return ServiceFailure.NoticeRequired(
            ServiceNotice(
                reason = when {
                    metadata?.code == "2067" -> ServiceNoticeReason.INVALID_IDENTIFICATION
                    metadata?.type == "1" -> ServiceNoticeReason.SERVER_MESSAGE
                    else -> ServiceNoticeReason.SERVER_FAILURE
                },
                message = metadata?.message,
                code = metadata?.code
            )
        )
    }
    val message = if (statusCode == 408) messageText("tempo_de_resposta_esgotou_18ae0a") else metadata?.message
    if (metadata?.code == "018003") {
        return if (allowRegistration) {
            ServiceFailure.RegistrationRequired(
                message?.takeIf { it.isNotEmpty() } ?: "Não foi possível concluir esta operação. Por favor, tente novamente."
            )
        } else {
            ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.OPERATION_FAILED))
        }
    }
    return ServiceFailure.NoticeRequired(
        ServiceNotice(
            reason = if (statusCode == 400 && metadata?.code in setOf("011010", "011011", "011012")) {
                ServiceNoticeReason.MORE_BETS_REQUIRED
            } else {
                ServiceNoticeReason.BUSINESS_RULE
            },
            message = message,
            code = metadata?.code
        )
    )
}

// RedirectEnum reconhece estes valores, mas trataRedict intencionalmente não executa ações para eles.
private val ignoredServiceRedirects = setOf(
    "home", "ativar-cadastro", "mega-sena", "surpresinhaMegaSena", "quina", "surpresinhaQuina",
    "dupla-sena", "surpresinhaDuplaSena", "lotomania", "surpresinhaLotomania",
    "lotofacil", "surpresinhaLotofacil", "timemania", "surpresinhaTimemania", "lotogol", "loteca",
    "aposta", "validar-cpf", "selecionar-loterica", "/carrinhos/verifica-compra-processamento"
)
