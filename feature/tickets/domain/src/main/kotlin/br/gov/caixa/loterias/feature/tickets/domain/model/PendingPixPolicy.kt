package br.gov.caixa.loterias.feature.tickets.domain.model

/** IDs do servidor têm preferência; contratos legados só de exibição mantêm sua alternativa histórica. */
fun Purchase.isPendingPix(): Boolean = (paymentMethodId == PIX_PAYMENT_METHOD_ID || paymentMethod.contains("pix", true)) &&
    (statusId == AWAITING_PAYMENT_STATUS_ID || status.contains("aguardando", true)) && pixCopyPaste.isNotBlank()

private const val PIX_PAYMENT_METHOD_ID = 6L
private const val AWAITING_PAYMENT_STATUS_ID = 10L
