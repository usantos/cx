package br.gov.caixa.loterias.feature.payment.data.datasource.remote

import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.provider.PaymentApiProvider
import br.gov.caixa.loterias.core.network.api.PurchaseBackendApiProvider
import br.gov.caixa.loterias.core.network.api.PurchaseBackendResponse
import br.gov.caixa.loterias.feature.account.data.dto.UserProfileDto
import com.google.gson.JsonElement

interface PaymentRemoteDataSource {
    suspend fun methods(): JsonElement?
    suspend fun cards(paymentMethodId: Long): JsonElement?
    suspend fun deleteCard(cardId: String, paymentMethodId: Long): JsonElement?
    suspend fun generatePix(body: JsonElement): JsonElement?
    suspend fun purchaseProgress(purchaseId: Long): PurchaseBackendResponse
    suspend fun validateDam(): JsonElement?
    suspend fun paymentMethod(paymentMethodId: Long): JsonElement?
    suspend fun profile(): UserProfileDto?
    suspend fun recargaPayCustomerToken(): String?
    suspend fun mercadoPagoInstallments(bin: String, publicKey: String): JsonElement
    suspend fun mercadoPagoCardToken(publicKey: String, body: JsonElement): JsonElement
    suspend fun recargaPayCreateCard(token: String, body: JsonElement): JsonElement
    suspend fun recargaPayAuthorizeCard(cardToken: String, token: String, body: JsonElement): JsonElement
    suspend fun registerCardPurchase(body: JsonElement): PurchaseBackendResponse
}

class PaymentRemoteDataSourceImpl(
    private val provider: BackendApiProvider,
    private val loteriasProvider: LoteriasApiProvider,
    private val paymentProvider: PaymentApiProvider,
    private val purchaseProvider: PurchaseBackendApiProvider,
) : PaymentRemoteDataSource {
    override suspend fun methods() = provider.current().featurePaymentApi().paymentMethods().payload
    override suspend fun cards(paymentMethodId: Long) = provider.current().featurePaymentApi().cards(paymentMethodId.toString()).payload
    override suspend fun deleteCard(cardId: String, paymentMethodId: Long) =
        provider.current().featurePaymentApi().deleteCard(cardId, paymentMethodId.toString()).payload
    override suspend fun generatePix(body: JsonElement) = provider.current().featurePaymentApi().generatePixCharge(body).payload
    override suspend fun purchaseProgress(purchaseId: Long) = purchaseProvider.current().progress(purchaseId)
    override suspend fun validateDam() = provider.current().featurePaymentApi().validateDam().payload
    override suspend fun paymentMethod(paymentMethodId: Long) = provider.current().featurePaymentApi().paymentMethod(paymentMethodId).payload
    override suspend fun profile() = loteriasProvider.current().featurePaymentApi().currentUserProfile().payload
    override suspend fun recargaPayCustomerToken() = provider.current().featurePaymentApi().recargaPayCustomerToken().payload
    override suspend fun mercadoPagoInstallments(bin: String, publicKey: String) =
        paymentProvider.current().mercadoPago.featurePaymentApi().installments(bin, publicKey)
    override suspend fun mercadoPagoCardToken(publicKey: String, body: JsonElement) =
        paymentProvider.current().mercadoPago.featurePaymentApi().createCardToken(publicKey, body)
    override suspend fun recargaPayCreateCard(token: String, body: JsonElement) =
        paymentProvider.current().recargaPay.featurePaymentApi().createCard("Bearer $token", body = body)
    override suspend fun recargaPayAuthorizeCard(cardToken: String, token: String, body: JsonElement) =
        paymentProvider.current().recargaPay.featurePaymentApi().authorizeSavedCard(cardToken, "Bearer $token", body = body)
    override suspend fun registerCardPurchase(body: JsonElement) = purchaseProvider.current().submit(body)
}

