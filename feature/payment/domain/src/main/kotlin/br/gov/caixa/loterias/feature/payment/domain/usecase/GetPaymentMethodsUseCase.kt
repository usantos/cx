package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod

fun interface GetPaymentMethodsUseCase { suspend operator fun invoke(): AppResult<List<PaymentMethod>> }
