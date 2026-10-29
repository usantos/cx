package br.gov.caixa.loterias.feature.retail.data.datasource.remote

import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.network.error.BackendHttpFailure
import br.gov.caixa.loterias.feature.retail.data.mapper.mercadoPagoRedemptionBody
import br.gov.caixa.loterias.feature.retail.data.mapper.prizeNoticeCode
import br.gov.caixa.loterias.feature.retail.data.mapper.prizePixBody
import br.gov.caixa.loterias.feature.retail.data.mapper.toLotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.data.mapper.toPixBeneficiary
import br.gov.caixa.loterias.feature.retail.data.mapper.toPrizeDetails
import br.gov.caixa.loterias.feature.retail.data.mapper.toRetailPrizeResult
import br.gov.caixa.loterias.core.data.mapper.toServiceConnectionFailure
import br.gov.caixa.loterias.core.data.mapper.toServiceFailure

internal class PrizeRedemptionOperations(
    private val prizeSource: () -> PrizeRedemptionRemoteDataSource,
) {
    suspend fun details(betId: Long): PrizeDetails = prizeSource().prizeDetails(betId).toPrizeDetails(betId)

    suspend fun lotteryCode(betId: Long): LotteryRedemptionCode = prizeSource().lotteryCode(betId).toLotteryRedemptionCode()

    suspend fun consultPixKey(betId: Long, key: String): PixBeneficiary =
        prizeSource().consultPixKey(betId, prizePixBody(key)).toPixBeneficiary()

    suspend fun redeemPix(betId: Long, key: String): PrizeRedemptionResult = try {
        PrizeRedemptionResult.Redeemed(prizeSource().redeemPrize(betId, prizePixBody(key)).prizeNoticeCode())
    } catch (failure: BackendHttpFailure) {
        failure.toServiceFailure(allowRegistration = false).toRetailPrizeResult()
    } catch (failure: java.io.IOException) {
        ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()).toRetailPrizeResult()
    }

    suspend fun cancelPix(betId: Long) = prizeSource().cancelPix(betId)

    suspend fun redeemMercadoPago(betId: Long, email: String): PrizeRedemptionResult = try {
        PrizeRedemptionResult.Redeemed(prizeSource().redeemPrize(betId, mercadoPagoRedemptionBody(email)).prizeNoticeCode())
    } catch (failure: BackendHttpFailure) {
        failure.toServiceFailure(allowRegistration = true).toRetailPrizeResult()
    } catch (failure: java.io.IOException) {
        ServiceFailure.ConnectionFailed(failure.toServiceConnectionFailure()).toRetailPrizeResult()
    }
}
