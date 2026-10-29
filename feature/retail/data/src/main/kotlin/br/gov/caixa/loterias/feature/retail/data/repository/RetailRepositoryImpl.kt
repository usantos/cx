package br.gov.caixa.loterias.feature.retail.data.repository

import br.gov.caixa.loterias.feature.retail.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationToken
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.repository.PrizeRedemptionRepository
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.feature.retail.domain.repository.RetailRepository
import br.gov.caixa.loterias.feature.retail.domain.model.Retailer
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.MercadoPagoRegistrationLoader
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.PrizeRedemptionOperations
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.PrizeRedemptionRemoteDataSource
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.RedemptionReceiptLoader
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.RetailFavorites
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.RetailLotterySearch
import br.gov.caixa.loterias.feature.retail.data.datasource.remote.RetailRemoteDataSource

class RetailRepositoryImpl(
    private val remote: RetailRemoteDataSource,
    private val prizeRemote: PrizeRedemptionRemoteDataSource? = remote as? PrizeRedemptionRemoteDataSource,
) : RetailRepository,
    PrizeRedemptionRepository,
    br.gov.caixa.loterias.feature.payment.domain.repository.MercadoPagoRegistrationRepository {
    private val favoriteOperations = RetailFavorites(remote)
    private val lotteries = RetailLotterySearch(remote, favoriteOperations)
    private val redemptions = PrizeRedemptionOperations(::prizeSource)
    private val registration = MercadoPagoRegistrationLoader(::prizeSource)
    private val receipts = RedemptionReceiptLoader(::prizeSource)

    override suspend fun search(query: String): List<Retailer> = lotteries.search(query)

    override suspend fun favorites(): List<Retailer> = favoriteOperations.favorites()

    override suspend fun setFavorite(retailerId: Long, favorite: Boolean) = favoriteOperations.setFavorite(retailerId, favorite)

    override suspend fun details(betId: Long): PrizeDetails = redemptions.details(betId)

    override suspend fun lotteryCode(betId: Long): LotteryRedemptionCode = redemptions.lotteryCode(betId)

    override suspend fun consultPixKey(betId: Long, key: String): PixBeneficiary = redemptions.consultPixKey(betId, key)

    override suspend fun redeemPix(betId: Long, key: String): PrizeRedemptionResult = redemptions.redeemPix(betId, key)

    override suspend fun cancelPix(betId: Long) = redemptions.cancelPix(betId)

    override suspend fun redeemMercadoPago(betId: Long, email: String): PrizeRedemptionResult = redemptions.redeemMercadoPago(betId, email)

    override suspend fun mercadoPagoRegistrationToken(email: String): MercadoPagoRegistrationToken =
        registration.mercadoPagoRegistrationToken(email)

    override suspend fun betReceiptPdf(betId: Long) = receipts.betReceiptPdf(betId)

    override suspend fun prizeReceiptPdf(betId: Long) = receipts.prizeReceiptPdf(betId)

    private fun prizeSource() = requireNotNull(prizeRemote) { messageText("fonte_de_resgate_de_premio_nao_c_0f199c") }
}
