package br.gov.caixa.loterias.feature.payment.presentation.screen

import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard

data class PaymentDataActions(
    val requestAddCard: (Long) -> Unit,
    val requestDelete: (SavedPaymentCard) -> Unit,
    val checkPixStatus: () -> Unit,
)
