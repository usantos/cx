package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider
import br.gov.caixa.loterias.core.network.PrizeBackendApiProvider
import br.gov.caixa.loterias.core.network.toBackendHttpFailure
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class RetrofitRetailRemoteDataSource(
    private val provider: LegacyBackendApiProvider,
    private val prizeProvider: PrizeBackendApiProvider,
) :
    RetailRemoteDataSource,
    PrizeRedemptionRemoteDataSource {
    override suspend fun search(query: String) = provider.current().retailersByNameOrCode(query).payload
    override suspend fun favorites() = provider.current().favoriteRetailers().payload
    override suspend fun addFavorite(retailerId: Long) { provider.current().addFavoriteRetailer(retailerId) }
    override suspend fun deleteFavorite(retailerId: Long) { provider.current().deleteFavoriteRetailer(retailerId) }
    override suspend fun prizeDetails(betId: Long) = provider.current().prizeDetails(betId).payload
    override suspend fun lotteryCode(betId: Long) = provider.current().retailerRedemptionCode(betId).payload
    override suspend fun consultPixKey(betId: Long, body: JsonObject) = provider.current().consultPixKey(betId, body).payload
    override suspend fun redeemPrize(betId: Long, body: JsonObject): JsonElement? {
        val envelope = try {
            prizeProvider.current().redeemPrize(betId, body)
        } catch (failure: retrofit2.HttpException) {
            throw failure.toBackendHttpFailure()
        }
        // A successful HTTP status containing an error/redirect is not proof of redemption.
        check(envelope.code == null && envelope.redirect == null) {
            "Resposta de resgate não concluída"
        }
        return envelope.payload
    }
    override suspend fun cancelPix(betId: Long) { provider.current().cancelPixRedemption(betId) }
    override suspend fun mercadoPagoToken(email: String): String {
        val envelope = try {
            prizeProvider.current().mercadoPagoRegistrationToken(email)
        } catch (failure: retrofit2.HttpException) {
            throw failure.toBackendHttpFailure()
        }
        require(envelope.code == null && envelope.redirect == null) { "Não foi possível iniciar o cadastro Mercado Pago." }
        return requireNotNull(envelope.payload) { "Token de cadastro Mercado Pago ausente." }
    }
    override suspend fun betReceiptPdf(betId: Long) = provider.current().betReceiptPdf(betId).use { decodeReceiptPdf(it.bytes()) }
    override suspend fun prizeReceiptPdf(betId: Long) = provider.current().prizeReceiptPdf(betId).use { decodeReceiptPdf(it.bytes()) }
}

/** SILCE sends Base64 text; deployments returning raw PDF keep their original bytes. */
internal fun decodeReceiptPdf(bytes: ByteArray): ByteArray {
    fun ByteArray.isPdf() = size >= 5 && take(5).toByteArray().contentEquals("%PDF-".toByteArray(Charsets.US_ASCII))
    if (bytes.isPdf()) return bytes
    val encoded = bytes.toString(Charsets.US_ASCII).filterNot(Char::isWhitespace)
    val decoded = java.util.Base64.getDecoder().decode(encoded)
    require(decoded.isPdf()) { "Comprovante PDF inválido" }
    return decoded
}
