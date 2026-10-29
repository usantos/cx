package br.gov.caixa.loterias.feature.payment.domain.repository

import br.gov.caixa.loterias.feature.payment.domain.model.PixCharge
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.DamValidation
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.domain.model.CardPaymentRequest
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome

interface PaymentRepository {
    suspend fun methods(): List<PaymentMethod>
    suspend fun cards(paymentMethodId: Long): List<SavedPaymentCard>
    suspend fun deleteCard(cardId: String, paymentMethodId: Long): List<SavedPaymentCard>
    suspend fun generatePix(): PixCharge
    suspend fun purchaseProgress(purchaseId: Long): PurchaseProgress
    suspend fun validateDam(): DamValidation
    suspend fun submitCardPayment(request: CardPaymentRequest): CardSubmissionOutcome
}
