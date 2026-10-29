package br.gov.caixa.loterias.feature.retail.navigation

/** Hospedeiro real do controlador do fluxo misto de prêmio, independente do token ou da situação da resposta. */
fun legacyPrizeForegroundHost(stage: br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage): Boolean = when (stage) {
    br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.LOADING,
    br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.OPTIONS,
    -> false

    br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.LOTTERY,
    br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.AGENCY,
    br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.PIX_ENTRY,
    br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.PIX_CONFIRM,
    br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.MERCADO_PAGO,
    br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.SUCCESS,
    -> true
}
