package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard

fun interface GetSavedCardsUseCase { suspend operator fun invoke(paymentMethodId: Long): AppResult<List<SavedPaymentCard>> }
