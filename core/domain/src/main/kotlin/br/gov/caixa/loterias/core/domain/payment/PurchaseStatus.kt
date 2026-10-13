package br.gov.caixa.loterias.core.domain.payment

/** Matches SituacaoCompraUtil.isSituacaoFINALIZADA, not a guarantee that every bet was registered. */
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
    11, // Aguardando devolução Pix (negative outcome in the legacy UI).
    12, // Cancelada: pagamento não identificado no tempo limite.
)

// 1 (carrinho), 2 (débito iniciado), 7/9 (processamento), 10 (aguardando Pix)
// and unknown/missing values must never become a completed payment by default.
