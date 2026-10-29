package br.gov.caixa.loterias.feature.payment.data.repository

import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.repository.PaymentRepository
import br.gov.caixa.loterias.feature.payment.domain.model.DamValidation
import br.gov.caixa.loterias.feature.payment.domain.model.CardPaymentRequest
import br.gov.caixa.loterias.feature.payment.domain.model.CardSubmissionOutcome
import br.gov.caixa.loterias.feature.payment.domain.model.PixCharge
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.core.network.api.PurchaseHttpException
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import kotlinx.coroutines.CancellationException
import br.gov.caixa.loterias.feature.payment.data.datasource.remote.CardPaymentTokenizer
import br.gov.caixa.loterias.feature.payment.data.mapper.CardPurchaseRequestMapper
import br.gov.caixa.loterias.feature.payment.data.mapper.PaymentPayloadMapper
import br.gov.caixa.loterias.feature.payment.data.datasource.remote.PaymentRemoteDataSource
import br.gov.caixa.loterias.feature.payment.data.mapper.PurchaseResponseMapper

class PaymentRepositoryImpl(private val remote: PaymentRemoteDataSource) : PaymentRepository {
    private val cardTokenizer = CardPaymentTokenizer(remote)

    override suspend fun methods(): List<PaymentMethod> = PaymentPayloadMapper.methods(remote.methods())

    override suspend fun cards(paymentMethodId: Long): List<SavedPaymentCard> =
        PaymentPayloadMapper.cards(remote.cards(paymentMethodId), paymentMethodId)

    override suspend fun deleteCard(cardId: String, paymentMethodId: Long): List<SavedPaymentCard> =
        PaymentPayloadMapper.cards(remote.deleteCard(cardId, paymentMethodId), paymentMethodId)

    override suspend fun generatePix(): PixCharge =
        PaymentPayloadMapper.pix(remote.generatePix(PaymentPayloadMapper.pixRequest()))

    override suspend fun purchaseProgress(purchaseId: Long): PurchaseProgress {
        val response = try {
            remote.purchaseProgress(purchaseId)
        } catch (failure: PurchaseHttpException) {
            return PurchaseResponseMapper.failedProgress(failure, purchaseId) ?: throw failure
        }
        return PurchaseResponseMapper.progress(response, purchaseId)
    }

    override suspend fun validateDam(): DamValidation = PaymentPayloadMapper.dam(remote.validateDam())

    override suspend fun submitCardPayment(request: CardPaymentRequest): CardSubmissionOutcome {
        val tokenAndMethod = cardTokenizer.tokenize(request)
        val body = CardPurchaseRequestMapper.map(request, tokenAndMethod)
        // A tokenização e a preparação acima não podem registrar uma compra por este endpoint.
        // Após o início do envio, uma resposta perdida ou inválida não comprova que a compra falhou.
        var knownPurchaseId: Long? = null
        return try {
            val response = remote.registerCardPurchase(body)
            knownPurchaseId = response.envelope.payload?.id?.takeIf { it > 0 }
            PurchaseResponseMapper.submission(response, knownPurchaseId)
        } catch (failure: PurchaseHttpException) {
            PurchaseResponseMapper.failedSubmission(failure, knownPurchaseId)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            // Nunca registra ou persiste dados de cartão, tokens de pagamento, corpos de erro ou exceções brutas aqui.
            CardSubmissionOutcome.ReviewRequired(knownPurchaseId)
        }
    }
}
