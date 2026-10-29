package br.gov.caixa.loterias.feature.retail.data.datasource.remote

import com.google.gson.JsonElement
import com.google.gson.JsonObject

interface PrizeRedemptionRemoteDataSource {
    suspend fun prizeDetails(betId: Long): JsonElement?
    suspend fun lotteryCode(betId: Long): JsonElement?
    suspend fun consultPixKey(betId: Long, body: JsonObject): JsonElement?
    suspend fun redeemPrize(betId: Long, body: JsonObject): JsonElement?
    suspend fun cancelPix(betId: Long)
    suspend fun mercadoPagoToken(email: String): String
    suspend fun betReceiptPdf(betId: Long): ByteArray
    suspend fun prizeReceiptPdf(betId: Long): ByteArray
}
