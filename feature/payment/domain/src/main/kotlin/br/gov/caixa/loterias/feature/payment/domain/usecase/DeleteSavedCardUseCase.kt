package br.gov.caixa.loterias.feature.payment.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard

fun interface DeleteSavedCardUseCase { suspend operator fun invoke(cardId: String, paymentMethodId: Long): AppResult<List<SavedPaymentCard>> }
