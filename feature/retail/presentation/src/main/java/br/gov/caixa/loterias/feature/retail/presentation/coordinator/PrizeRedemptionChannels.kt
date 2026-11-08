package br.gov.caixa.loterias.feature.retail.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.usecase.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.feature.retail.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.presentation.mapper.canContinuePix
import br.gov.caixa.loterias.feature.retail.presentation.mapper.isValidPrizeEmail
import br.gov.caixa.loterias.feature.retail.presentation.mapper.selectedPixKey
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionInputAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PrizeLotteryChannel(
    private val betId: Long,
    private val mutableState: ReduxStateStore<PrizeRedemptionUiState, PrizeRedemptionStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val generateLotteryCode: GenerateLotteryRedemptionCodeUseCase,
    private val timer: PrizeRedemptionTimer,
    private val interactionBlocked: () -> Boolean,
    private val fail: (AppError) -> Unit,
) {
    fun openAgencyGuidance() {
        mutableState.dispatch(PrizeRedemptionStateAction.AgencyCodeLoading)
        scope.launch(dispatchers.io) {
            when (val result = generateLotteryCode(betId)) {
                is AppResult.Success -> mutableState.dispatch(PrizeRedemptionStateAction.AgencyCodeReceived(result.value))
                is AppResult.Failure -> fail(result.error)
            }
        }
    }

    fun refreshLotteryCode() {
        if (interactionBlocked()) return
        timer.cancel()
        scope.launch(dispatchers.io) {
            mutableState.dispatch(PrizeRedemptionStateAction.LotteryCodeLoading)
            when (val result = generateLotteryCode(betId)) {
                is AppResult.Success -> {
                    // A orientação para agência usa o mesmo serviço, mas somente a tela de lotérica
                    // exige conteúdo QR. Não rejeita uma resposta de agência por sua ausência.
                    if (result.value.qrCode.isBlank()) {
                        fail(AppError.Validation(messageText("codigo_de_resgate_indisponivel_e3a861")))
                        return@launch
                    }
                    val seconds = result.value.validityMinutes * 60
                    mutableState.dispatch(PrizeRedemptionStateAction.LotteryCodeReceived(result.value, seconds))
                    timer.start(seconds)
                }

                is AppResult.Failure -> fail(result.error)
            }
        }
    }
}

internal class PrizePixChannel(
    private val betId: Long,
    private val mutableState: ReduxStateStore<PrizeRedemptionUiState, PrizeRedemptionStateAction>,
    private val scope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val consultPixKey: ConsultPixRedemptionKeyUseCase,
    private val cancelPix: CancelPixRedemptionUseCase,
    private val submit: (PrizeRedemptionChannel, String) -> Unit,
    private val returnToOptions: () -> Unit,
    private val interactionBlocked: () -> Boolean,
    private val fail: (AppError) -> Unit,
) {
    fun selectCpfPixKey() = editKey(PrizeRedemptionStateAction.CpfPixKeySelected)

    fun selectOtherPixKey() = editKey(PrizeRedemptionStateAction.OtherPixKeySelected)

    fun changePixKey(value: String) = editKey(PrizeRedemptionStateAction.PixKeyChanged(value))

    fun togglePixOwnership() = editKey(PrizeRedemptionStateAction.PixOwnershipToggled)

    fun consultPix() {
        if (!canAct(PrizeRedemptionStage.PIX_ENTRY)) return
        val state = mutableState.value
        if (!state.canContinuePix()) {
            mutableState.dispatch(PrizeRedemptionStateAction.PixKeyRejected())
            return
        }
        val key = state.selectedPixKey()
        // Reserva a etapa antes de agendar a consulta para impedir cliques repetidos.
        mutableState.dispatch(PrizeRedemptionStateAction.PixConsultStarted)
        scope.launch(dispatchers.io) {
            when (val result = consultPixKey(betId, key)) {
                is AppResult.Success -> mutableState.dispatch(PrizeRedemptionStateAction.PixBeneficiaryReceived(result.value))
                is AppResult.Failure -> fail(result.error)
            }
        }
    }

    fun confirmPix() {
        if (!canAct(PrizeRedemptionStage.PIX_CONFIRM)) return
        // O legado envia a chave escolhida; a chave do favorecido é apenas apresentação.
        submit(PrizeRedemptionChannel.PIX, mutableState.value.selectedPixKey())
    }

    fun cancelPixAndExit() {
        if (!canAct(PrizeRedemptionStage.PIX_CONFIRM)) return
        mutableState.dispatch(PrizeRedemptionStateAction.PixCancellationStarted)
        scope.launch(dispatchers.io) {
            when (val result = cancelPix(betId)) {
                is AppResult.Success -> returnToOptions()
                is AppResult.Failure -> fail(result.error)
            }
        }
    }

    private fun editKey(action: PrizeRedemptionInputAction) {
        if (canAct(PrizeRedemptionStage.PIX_ENTRY)) mutableState.dispatch(action)
    }

    private fun canAct(stage: PrizeRedemptionStage): Boolean =
        mutableState.value.stage == stage && !mutableState.value.loading && !interactionBlocked()
}

internal class PrizeMercadoPagoChannel(
    private val mutableState: ReduxStateStore<PrizeRedemptionUiState, PrizeRedemptionStateAction>,
    private val interactionBlocked: () -> Boolean,
    private val submit: (PrizeRedemptionChannel, String) -> Unit,
) {
    fun changeEmail(value: String) {
        if (mutableState.value.loading || interactionBlocked()) return
        mutableState.dispatch(PrizeRedemptionStateAction.EmailChanged(value))
    }

    fun confirmMercadoPago() {
        if (mutableState.value.loading || interactionBlocked()) return
        if (!isValidPrizeEmail(mutableState.value.email)) {
            mutableState.dispatch(PrizeRedemptionStateAction.EmailRejected())
            return
        }
        submit(PrizeRedemptionChannel.MERCADO_PAGO, mutableState.value.email)
    }
}
