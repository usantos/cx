package br.gov.caixa.loterias.feature.payment

import br.gov.caixa.loterias.core.domain.payment.PaymentMethod

internal const val PIX_RECOVERY_GUIDANCE =
    "Caso precise recuperar este código ou consultar o status da compra acesse o menu \"Minhas Compras\"."

internal const val PIX_EXPIRED_GUIDANCE =
    "A validade do seu código de pagamento Pix expirou.\n" +
        "Caso tenha realizado o pagamento, aguarde enquanto realizamos o processamento.\n" +
        "Pagamentos identificados fora do tempo limite para efetivação das apostas serão estornados.\n" +
        "Acompanhe o status da sua compra acessando o menu Minhas Compras."

internal const val PAYMENT_CARD_PROCESSOR_GUIDANCE =
    "Escolha um dos processadores abaixo e cadastre um cartão de crédito de sua titularidade ou escolha um cartão salvo para continuar."

internal fun acceptedBrands(paymentMethodId: Long): List<String> =
    if (paymentMethodId == PaymentMethod.RECARGA_PAY_ID) {
        listOf("ELO", "MASTERCARD", "VISA")
    } else {
        listOf("AMERICANEXPRESS", "ELO", "HIPERCARD", "MASTERCARD", "VISA")
    }

internal fun pixCopyDisplayValue(value: String): String = value.take(19) + if (value.length > 19) "..." else ""
internal fun formatRemaining(seconds: Long) = "%02d:%02d".format(seconds / 60, seconds % 60)

/** IDs de cartão só são únicos dentro de cada processador de pagamento. */
internal fun savedCardListKey(paymentMethodId: Long, cardId: String): String = "$paymentMethodId:$cardId"

internal fun formatCardNumber(value: String): String =
    value.filter(Char::isDigit).take(16).chunked(4).joinToString(" ")

internal const val PURCHASE_PROCESSING_TITLE = "Estamos processando sua compra."

internal const val PURCHASE_UPDATED_TERMS_GUIDANCE = "O termo de Uso foi Atualizado. É necessário aceitar novamente."

internal const val PURCHASE_REVIEW_GUIDANCE =
    "Verificamos que sua compra está em processamento e está demorando mais que o esperado. " +
        "Aguarde alguns instantes e verifique na área Minhas compras se a compra foi processada antes de tentar novamente."

internal val purchaseProcessingMessages = listOf(
    "Para participar dos sorteios, a aposta precisa estar EFETIVADA.",
    "O pagamento realizado não garante automaticamente a efetivação da aposta.",
    "Se a aposta não for registrada, o valor será devolvido.",
    "Você pode acompanhar tudo em Minhas Compras.",
)

internal const val PIX_PENDING_EXIT_MESSAGE =
    "Pagamento ainda não identificado.\n" +
        "Caso tenha realizado o pagamento, aguarde enquanto realizamos o processamento.\n" +
        "Pagamentos identificados fora do tempo limite para efetivação de apostas serão estornados.\n" +
        "Acompanhe o processamento do seu pedido na opção Minhas Compras.\n\n" +
        "Você deseja aguardar a confirmação de pagamento?"

internal fun purchaseProcessingDestination(): String = "tickets/purchases"

internal fun prePurchaseMessage(formattedAmount: String): String =
    "O Valor total da sua compra é de $formattedAmount. Confirma?"
