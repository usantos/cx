package br.gov.caixa.loterias.feature.retail.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.rule.connectionFailureNotice
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrizeUseCase
import br.gov.caixa.loterias.feature.retail.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.presentation.mapper.prizeNoticeUi
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import java.time.Clock
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PrizeRedemptionSubmission(
    private val betId: Long,
    private val mutableState: ReduxStateStore<PrizeRedemptionUiState, PrizeRedemptionStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val redeemPrize: RedeemPrizeUseCase,
    private val localClock: Clock,
    private val networkAvailability: NetworkAvailability,
    private val interactionBlocked: () -> Boolean,
    private val fail: (AppError) -> Unit
) {
    fun submit(channel: PrizeRedemptionChannel, value: String) {
        if (mutableState.value.loading || interactionBlocked() || mutableState.value.stage == PrizeRedemptionStage.SUCCESS) return
        mutableState.dispatch(PrizeRedemptionStateAction.SubmissionStarted)
        scope.launch(dispatchers.io) {
            when (val result = redeemPrize(betId, channel, value)) {
                is AppResult.Success -> when (val outcome = result.value) {
                    is PrizeRedemptionResult.ConnectionFailed -> mutableState.dispatch(
                        PrizeRedemptionStateAction.ConnectionFailed(connectionFailureNotice(outcome.reason, networkAvailability.current()))
                    )

                    PrizeRedemptionResult.Unchanged -> mutableState.dispatch(PrizeRedemptionStateAction.SubmissionUnchanged)

                    is PrizeRedemptionResult.Redeemed -> mutableState.dispatch(
                        PrizeRedemptionStateAction.Redeemed(channel, outcome.noticeCode.toLegacyNoticeMessage())
                    )

                    is PrizeRedemptionResult.RegistrationRequired -> {
                        if (channel != PrizeRedemptionChannel.MERCADO_PAGO) {
                            fail(AppError.Unexpected)
                        } else {
                            mutableState.dispatch(PrizeRedemptionStateAction.RegistrationRequired(outcome.message, value))
                        }
                    }

                    is PrizeRedemptionResult.NoticeRequired -> mutableState.dispatch(
                        PrizeRedemptionStateAction.NoticeRequired(prizeNoticeUi(outcome.notice, LocalDateTime.now(localClock)))
                    )

                    is PrizeRedemptionResult.RedirectRequired -> mutableState.dispatch(
                        PrizeRedemptionStateAction.RedirectRequired(outcome.destination)
                    )
                }

                is AppResult.Failure -> fail(result.error)
            }
        }
    }
}

private fun String?.toLegacyNoticeMessage(): String? = when (this?.uppercase()) {
    "MI053", "MIO53" -> messageText("premio_resgatado_com_sucesso_sua_de7645")
    else -> null
}
