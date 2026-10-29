package br.gov.caixa.loterias.feature.retail.data.datasource.remote

import br.gov.caixa.loterias.feature.retail.data.localizedMessage as messageText

import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.PrizeBackendApiProvider
import br.gov.caixa.loterias.core.network.error.toBackendHttpFailure
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class RetailRemoteDataSourceImpl(private val provider: BackendApiProvider, private val prizeProvider: PrizeBackendApiProvider) :
    RetailRemoteDataSource,
    PrizeRedemptionRemoteDataSource {
    override suspend fun search(query: String) = provider.current().featureRetailApi().retailersByNameOrCode(query).payload
    override suspend fun favorites() = provider.current().featureRetailApi().favoriteRetailers().payload
    override suspend fun addFavorite(retailerId: Long) {
        provider.current().featureRetailApi().addFavoriteRetailer(retailerId)
    }
    override suspend fun deleteFavorite(retailerId: Long) {
        provider.current().featureRetailApi().deleteFavoriteRetailer(retailerId)
    }
    override suspend fun prizeDetails(betId: Long) = provider.current().featureRetailApi().prizeDetails(betId).payload
    override suspend fun lotteryCode(betId: Long) = provider.current().featureRetailApi().retailerRedemptionCode(betId).payload
    override suspend fun consultPixKey(betId: Long, body: JsonObject) =
        provider.current().featureRetailApi().consultPixKey(betId, body).payload
    override suspend fun redeemPrize(betId: Long, body: JsonObject): JsonElement? {
        val envelope = try {
            prizeProvider.current().featureRetailApi().redeemPrize(betId, body)
        } catch (failure: retrofit2.HttpException) {
            throw failure.toBackendHttpFailure()
        }
        // Um estado HTTP de sucesso contendo erro ou redirecionamento não comprova o resgate.
        check(envelope.code == null && envelope.redirect == null) {
            "Resposta de resgate não concluída"
        }
        return envelope.payload
    }
    override suspend fun cancelPix(betId: Long) {
        provider.current().featureRetailApi().cancelPixRedemption(betId)
    }
    override suspend fun mercadoPagoToken(email: String): String {
        val envelope = try {
            prizeProvider.current().featureRetailApi().mercadoPagoRegistrationToken(email)
        } catch (failure: retrofit2.HttpException) {
            throw failure.toBackendHttpFailure()
        }
        require(envelope.code == null && envelope.redirect == null) { messageText("nao_foi_possivel_iniciar_o_cadas_026de0") }
        return requireNotNull(envelope.payload) { messageText("token_de_cadastro_mercado_pago_a_f82269") }
    }
    override suspend fun betReceiptPdf(betId: Long) =
        provider.current().featureRetailApi().betReceiptPdf(betId).use { decodeReceiptPdf(it.bytes()) }
    override suspend fun prizeReceiptPdf(betId: Long) =
        provider.current().featureRetailApi().prizeReceiptPdf(betId).use { decodeReceiptPdf(it.bytes()) }
}

/** SILCE envia texto Base64; implantações que retornam PDF bruto preservam os bytes originais. */
internal fun decodeReceiptPdf(bytes: ByteArray): ByteArray {
    fun ByteArray.isPdf() = size >= 5 && take(5).toByteArray().contentEquals("%PDF-".toByteArray(Charsets.US_ASCII))
    if (bytes.isPdf()) return bytes
    val encoded = bytes.toString(Charsets.US_ASCII).filterNot(Char::isWhitespace)
    val decoded = java.util.Base64.getDecoder().decode(encoded)
    require(decoded.isPdf()) { messageText("comprovante_pdf_invalido_bede87") }
    return decoded
}
