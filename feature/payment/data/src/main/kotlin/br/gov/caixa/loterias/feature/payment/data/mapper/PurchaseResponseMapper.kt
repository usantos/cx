package br.gov.caixa.loterias.feature.payment.data.mapper

import br.gov.caixa.loterias.core.network.api.PurchaseBackendResponse
import br.gov.caixa.loterias.core.network.api.PurchaseHttpException
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseEnvelopeDto
import br.gov.caixa.loterias.feature.payment.data.dto.PurchaseErrorEnvelopeDto
import br.gov.caixa.loterias.feature.payment.data.localizedMessage as messageText
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNotice
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseNoticeReason
import br.gov.caixa.loterias.feature.payment.domain.model.PurchasePolling
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseSubmission

/** Interpretação pura das respostas do protocolo de compra e da precedência dos erros de negócio legados. */
internal object PurchaseResponseMapper {
    fun progress(response: PurchaseBackendResponse, purchaseId: Long): PurchaseProgress {
        purchaseRedirect(response.envelope.redirect)?.let { destination ->
            return PurchaseProgress(response.envelope.payload?.id?.takeIf { it > 0 } ?: purchaseId, 0, redirect = destination)
        }
        response.registrationNotice()?.let { return PurchaseProgress(purchaseId, 0, notice = it) }
        val envelope = response.envelope.validated()
        val value = envelope.payload
        if (value?.id?.takeIf { it > 0 }?.let { it != purchaseId } == true) {
            // Uma resposta alheia não confirma nem rejeita esta compra. Mantém a cobrança
            // atual pendente e permite uma nova consulta, sem repetir o pagamento.
            return PurchaseProgress(purchaseId, 0)
        }
        val nextPoll = if (response.processing) {
            requireNotNull(value) { messageText("compra_sem_payload_de_processame_78e664") }
            PurchasePolling(
                requireNotNull(value.intervalMillis) { messageText("compra_sem_periodicidade_de_cons_b180c8") }.coerceAtLeast(0),
                requireNotNull(value.repetitions) { messageText("compra_sem_limite_de_consultas_5dbe00") }.coerceAtLeast(0)
            )
        } else {
            null
        }
        return PurchaseProgress(value?.id?.takeIf { it > 0 } ?: purchaseId, value?.status ?: 0, nextPoll)
    }

    fun failedProgress(failure: PurchaseHttpException, purchaseId: Long): PurchaseProgress? {
        failure.purchaseErrorRedirect()?.let { return PurchaseProgress(purchaseId, 0, redirect = it) }
        failure.purchaseErrorNotice()?.let { return PurchaseProgress(purchaseId, 0, notice = it) }
        return null
    }

    fun submission(response: PurchaseBackendResponse, knownPurchaseId: Long?): CardSubmissionOutcome {
        val envelope = response.envelope
        purchaseRedirect(envelope.redirect)?.let { destination ->
            return CardSubmissionOutcome.RedirectRequired(destination, knownPurchaseId)
        }
        response.registrationNotice()?.let { return CardSubmissionOutcome.NoticeRequired(it) }
        val value = requireNotNull(envelope.validated().payload) { messageText("compra_sem_payload_de_processame_78e664") }
        return CardSubmissionOutcome.Submitted(
            PurchaseSubmission(
                purchaseId = requireNotNull(knownPurchaseId) { messageText("compra_sem_identificador_valido_7f1e28") },
                pollingIntervalMillis = requireNotNull(value.intervalMillis) {
                    messageText("compra_sem_periodicidade_de_cons_b180c8")
                }.coerceAtLeast(0),
                pollingAttempts = requireNotNull(value.repetitions) {
                    messageText("compra_sem_limite_de_consultas_5dbe00")
                }.coerceAtLeast(0),
                status = value.status ?: 0
            )
        )
    }

    fun failedSubmission(failure: PurchaseHttpException, knownPurchaseId: Long?): CardSubmissionOutcome =
        failure.purchaseErrorRedirect()?.let { CardSubmissionOutcome.RedirectRequired(it, knownPurchaseId) }
            ?: failure.purchaseErrorNotice()?.let(CardSubmissionOutcome::NoticeRequired)
            ?: CardSubmissionOutcome.ReviewRequired(knownPurchaseId)

    private fun PurchaseEnvelopeDto.validated(): PurchaseEnvelopeDto {
        // Um redirecionamento de negócio não é uma URL HTTP e nunca deve ser tratado como sucesso do pagamento.
        // A navegação de diretivas sem processamento é auditada separadamente deste transporte.
        require(redirect == null || redirect == "/carrinhos/verifica-compra-processamento") {
            messageText("consulte_minhas_compras_para_aco_1dc19c")
        }
        require(code == null) { message?.takeIf(String::isNotBlank) ?: messageText("nao_foi_possivel_processar_a_com_fb8518") }
        return this
    }

    private fun purchaseRedirect(value: String?): PurchaseRedirect? = when (value) {
        "login" -> PurchaseRedirect.LOGIN
        "aceitar-termo-de-uso" -> PurchaseRedirect.ACCEPT_TERMS
        "cadastrar-apostador" -> PurchaseRedirect.REGISTER_ACCOUNT
        "indisponibilidade" -> PurchaseRedirect.UNAVAILABLE
        else -> null
    }

    /** Redirecionamentos de erro têm prioridade sobre a alternativa de situação, como em checkRedirectCompraAsync. */
    private fun PurchaseHttpException.purchaseErrorRedirect(): PurchaseRedirect? {
        val directive = error?.redirect
        return when (directive) {
            "carrinho" -> PurchaseRedirect.CART
            "carrinhos-favoritos" -> PurchaseRedirect.FAVORITE_CARTS
            null -> PurchaseRedirect.LOGIN.takeIf { statusCode == 401 }
            else -> purchaseRedirect(directive)
        }
    }

    private fun PurchaseHttpException.purchaseErrorNotice(): PurchaseNotice? {
        val metadata = error ?: return null
        if (metadata.redirect != null) return null
        val reason = when (statusCode) {
            400, 404 -> when (metadata.code) {
                "011010", "011011", "011012" -> PurchaseNoticeReason.MORE_BETS_REQUIRED
                "018003" -> PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION
                else -> PurchaseNoticeReason.BUSINESS_RULE
            }

            500 -> when {
                metadata.code == "2067" -> PurchaseNoticeReason.INVALID_IDENTIFICATION
                metadata.type == "1" -> PurchaseNoticeReason.SERVER_MESSAGE
                else -> PurchaseNoticeReason.SERVER_FAILURE
            }

            else -> if (metadata.code == "018003") PurchaseNoticeReason.MERCADO_PAGO_REGISTRATION else return null
        }
        val message = if (statusCode == 408) messageText("tempo_de_resposta_esgotou_18ae0a") else metadata.message
        return PurchaseNotice(reason, message, metadata.code)
    }

    private fun PurchaseBackendResponse.registrationNotice(): PurchaseNotice? =
        if (processing && envelope.code == "018003" && envelope.redirect == null) {
            PurchaseHttpException(303, PurchaseErrorEnvelopeDto(code = envelope.code, message = envelope.message)).purchaseErrorNotice()
        } else {
            null
        }
}
