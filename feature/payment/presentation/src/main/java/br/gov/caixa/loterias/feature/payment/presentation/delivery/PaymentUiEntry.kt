package br.gov.caixa.loterias.feature.payment.presentation.delivery

import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry

interface PaymentUiEntry :
    FeatureUiEntry,
    PaymentPaymentProcessingScreenEntry,
    PaymentPaymentFailureScreenEntry,
    PaymentPaymentPixGuidanceScreenEntry,
    PaymentPaymentCardFlowScreenEntry,
    PaymentPaymentDataContentEntry
