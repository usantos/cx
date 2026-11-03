package br.gov.caixa.loterias.feature.retail.presentation.screen

import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixActions

data class PrizeRedemptionScreenActions(
    val select: (PrizeRedemptionChannel) -> Unit,
    val betNow: () -> Unit,
    val loadCurrentContest: () -> Unit,
    val refreshLotteryCode: () -> Unit,
    val downloadBetReceipt: () -> Unit,
    val confirmMercadoPago: () -> Unit,
    val changeEmail: (String) -> Unit,
    val downloadPrizeReceipt: () -> Unit,
    val pix: PrizePixActions,
)
