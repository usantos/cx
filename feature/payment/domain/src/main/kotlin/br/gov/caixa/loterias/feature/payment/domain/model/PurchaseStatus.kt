package br.gov.caixa.loterias.feature.payment.domain.model

/** Reproduz SituacaoCompraUtil.isSituacaoFINALIZADA, sem garantir que todas as apostas foram registradas. */
internal fun purchasePaymentCompleted(status: Int): Boolean = status in setOf(
    6, // Débito realizado.
    8, // Finalizada.
    13, // Finalizada: todas as apostas efetivadas.
    14, // Finalizada: contém apostas não efetivadas.
)

internal fun purchasePaymentRejected(status: Int): Boolean = status in setOf(
    3, // Débito não autorizado.
    4, // Estornada.
    5, // Cancelada.
    11, // Aguardando devolução Pix (resultado negativo na interface legada).
    12, // Cancelada: pagamento não identificado no tempo limite.
)
