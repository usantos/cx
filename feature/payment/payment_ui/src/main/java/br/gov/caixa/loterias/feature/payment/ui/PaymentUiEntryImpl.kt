package br.gov.caixa.loterias.feature.payment.ui

import androidx.annotation.Keep
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentPaymentCardFlowScreenEntry
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentPaymentDataContentEntry
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentPaymentFailureScreenEntry
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentPaymentPixGuidanceScreenEntry
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentPaymentProcessingScreenEntry
import br.gov.caixa.loterias.feature.payment.presentation.delivery.PaymentUiEntry

@Keep
class PaymentUiEntryImpl :
    PaymentUiEntry,
    PaymentPaymentFailureScreenEntry by PaymentPaymentFailureScreenRenderer(),

    PaymentPaymentProcessingScreenEntry by PaymentPaymentProcessingScreenRenderer(),
    PaymentPaymentPixGuidanceScreenEntry by PaymentPaymentPixGuidanceScreenRenderer(),

    PaymentPaymentCardFlowScreenEntry by PaymentPaymentCardFlowScreenRenderer(),

    PaymentPaymentDataContentEntry by PaymentPaymentDataContentRenderer()
