package br.gov.caixa.loterias.feature.retail.navigation

import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrizeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SearchRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase

/** Dependências deste grafo de destinos, fornecidas pela raiz de composição do aplicativo. */
interface RetailNavigationDependencies : br.gov.caixa.loterias.feature.payment.navigation.MercadoPagoRegistrationDependencies {
    val checkRetailLocation: br.gov.caixa.loterias.feature.retail.domain.usecase.CheckRetailLocationUseCase
    val appVersionName: String
    val bettingParameters: GetBettingParametersUseCase
    val cancelPixRedemption: CancelPixRedemptionUseCase
    val consultPixRedemptionKey: ConsultPixRedemptionKeyUseCase
    val currentUserIdentity: CurrentUserIdentityProvider
    val downloadBetReceipt: DownloadBetReceiptUseCase
    val downloadPrizeReceipt: DownloadPrizeReceiptUseCase
    val lotteryRedemptionCode: GenerateLotteryRedemptionCodeUseCase
    val prepareBetEntry: br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
    val prizeDetails: GetPrizeDetailsUseCase
    val redeemPrize: RedeemPrizeUseCase
    val searchRetailers: SearchRetailersUseCase
    val setRetailerFavorite: SetRetailerFavoriteUseCase
}
