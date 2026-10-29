package br.gov.caixa.loterias.feature.payment.presentation.mapper

import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.presentation.formatting.localizedMessage as messageText

internal val PIX_RECOVERY_GUIDANCE get() = messageText("caso_precise_recuperar_este_codi_8f24e6")

internal val PIX_EXPIRED_GUIDANCE get() = messageText("a_validade_do_seu_codigo_de_paga_9fd6f9") +
    "Caso tenha realizado o pagamento, aguarde enquanto realizamos o processamento.\n" +
    messageText("pagamentos_identificados_fora_do_9a0a5a") +
    messageText("acompanhe_o_status_da_sua_compra_0013f7")

internal val PAYMENT_CARD_PROCESSOR_GUIDANCE get() = messageText("escolha_um_dos_processadores_aba_c619e4")

internal fun acceptedBrands(paymentMethodId: Long): List<String> = if (paymentMethodId == PaymentMethod.RECARGA_PAY_ID) {
    listOf("ELO", "MASTERCARD", "VISA")
} else {
    listOf("AMERICANEXPRESS", "ELO", "HIPERCARD", "MASTERCARD", "VISA")
}

internal fun pixCopyDisplayValue(value: String): String = value.take(19) + if (value.length > 19) "..." else ""
internal fun formatRemaining(seconds: Long) = "%02d:%02d".format(seconds / 60, seconds % 60)

/** IDs de cartão só são únicos dentro de cada processador de pagamento. */
internal fun savedCardListKey(paymentMethodId: Long, cardId: String): String = "$paymentMethodId:$cardId"

internal fun formatCardNumber(value: String): String = value.filter(Char::isDigit).take(16).chunked(4).joinToString(" ")

internal val PURCHASE_PROCESSING_TITLE get() = messageText("estamos_processando_sua_compra_952655")

internal val PURCHASE_UPDATED_TERMS_GUIDANCE get() = messageText("o_termo_de_uso_foi_atualizado_e_959939")

internal val PURCHASE_REVIEW_GUIDANCE get() = messageText("verificamos_que_sua_compra_esta_6e10b2") +
    messageText("aguarde_alguns_instantes_e_verif_ab3d34")

internal val purchaseProcessingMessages = listOf(
    messageText("para_participar_dos_sorteios_a_a_a7b029"),
    messageText("o_pagamento_realizado_nao_garant_2daf0a"),
    messageText("se_a_aposta_nao_for_registrada_o_cd21b6"),
    messageText("voce_pode_acompanhar_tudo_em_min_ea338a")
)

internal val PIX_PENDING_EXIT_MESSAGE get() = messageText("pagamento_ainda_nao_identificado_a85b53") +
    "Caso tenha realizado o pagamento, aguarde enquanto realizamos o processamento.\n" +
    messageText("pagamentos_identificados_fora_do_a6ac3d") +
    messageText("acompanhe_o_processamento_do_seu_ce74e7") +
    messageText("voce_deseja_aguardar_a_confirmac_b26135")

internal fun purchaseProcessingDestination(): String = "tickets/purchases"

internal fun prePurchaseMessage(formattedAmount: String): String = messageText("o_valor_total_da_sua_compra_e_de_776a2e", formattedAmount)
