package br.gov.caixa.loterias.feature.retail.data.datasource.remote

internal class RedemptionReceiptLoader(
    private val prizeSource: () -> PrizeRedemptionRemoteDataSource,
) {
    suspend fun betReceiptPdf(betId: Long) = prizeSource().betReceiptPdf(betId)

    suspend fun prizeReceiptPdf(betId: Long) = prizeSource().prizeReceiptPdf(betId)
}
