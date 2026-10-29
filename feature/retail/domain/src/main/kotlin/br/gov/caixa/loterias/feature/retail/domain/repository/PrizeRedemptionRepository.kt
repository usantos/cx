package br.gov.caixa.loterias.feature.retail.domain.repository

import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode

interface PrizeRedemptionRepository {
    suspend fun details(betId: Long): PrizeDetails
    suspend fun lotteryCode(betId: Long): LotteryRedemptionCode
    suspend fun consultPixKey(betId: Long, key: String): PixBeneficiary
    suspend fun redeemPix(betId: Long, key: String): PrizeRedemptionResult
    suspend fun cancelPix(betId: Long)
    suspend fun redeemMercadoPago(betId: Long, email: String): PrizeRedemptionResult
    suspend fun betReceiptPdf(betId: Long): ByteArray
    suspend fun prizeReceiptPdf(betId: Long): ByteArray
}
