package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress

fun interface GetPurchaseProgressUseCase { suspend operator fun invoke(purchaseId: Long): AppResult<PurchaseProgress> }
