package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult

fun interface DownloadPrizeReceiptUseCase { suspend operator fun invoke(betId: Long): AppResult<ByteArray> }
