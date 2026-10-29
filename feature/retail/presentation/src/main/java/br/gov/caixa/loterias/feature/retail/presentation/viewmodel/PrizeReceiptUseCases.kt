package br.gov.caixa.loterias.feature.retail.presentation.viewmodel

import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase

/** As duas origens de documentos disponíveis no fluxo de resgate. */
class PrizeReceiptUseCases(val downloadBetReceipt: DownloadBetReceiptUseCase, val downloadPrizeReceipt: DownloadPrizeReceiptUseCase)
