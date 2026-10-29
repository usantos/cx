package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.PrepareBetEntryUseCase
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionOption
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import br.gov.caixa.loterias.feature.retail.domain.usecase.CancelPixRedemptionUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.ConsultPixRedemptionKeyUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadBetReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.DownloadPrizeReceiptUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GenerateLotteryRedemptionCodeUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetPrizeDetailsUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.RedeemPrizeUseCase
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeReceiptUseCases
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionInput
import br.gov.caixa.loterias.feature.retail.presentation.viewmodel.PrizeRedemptionViewModel
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
abstract class PrizeRedemptionTestFixture {
    protected var connection = br.gov.caixa.loterias.core.common.network.ConnectionAvailability.CONNECTED
    protected var connectionReads = 0

    protected val dispatcher = StandardTestDispatcher()
    protected val details = PrizeDetails(
        7,
        BigDecimal.TEN,
        "nsbi",
        PrizeRedemptionChannel.entries.map {
            PrizeRedemptionOption(it, it.name, "description")
        }
    )
    protected var detailsResult: AppResult<PrizeDetails> = AppResult.Success(details)
    protected var codeResult: AppResult<LotteryRedemptionCode> = AppResult.Success(LotteryRedemptionCode("qr", "code", "nsb", 1))
    protected var consultResult: AppResult<PixBeneficiary> = AppResult.Success(PixBeneficiary("cpf", "Pessoa", "key", "Banco"))
    protected var redeemResult: AppResult<PrizeRedemptionResult> = AppResult.Success(PrizeRedemptionResult.Redeemed())
    protected var cancelResult: AppResult<Unit> = AppResult.Success(Unit)
    protected var betPdfResult: AppResult<ByteArray> = AppResult.Success(byteArrayOf(1))
    protected var prizePdfResult: AppResult<ByteArray> = AppResult.Success(byteArrayOf(2))
    protected var redemption: Triple<Long, PrizeRedemptionChannel, String>? = null
    protected var redemptionCalls = 0
    protected var cancelCalls = 0
    protected var codeCalls = 0
    protected val game =
        LotteryGameParameters(
            LotteryModality(
                2,
                "Mega-Sena",
                "MEGA_SENA"
            ),
            3000, "", 60, 6, 20, 10, emptyList(), emptyList(), hasNextContest = true
        )
    protected var parametersResult: AppResult<BettingParameters> = AppResult.Success(
        BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, listOf(game))
    )
    protected var entryResult: AppResult<BetEntryDecision> = AppResult.Success(BetEntryDecision.Ready(game))
    protected var entryCalls = 0
    protected var requestedModality = 0

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    protected fun viewModel(betReceipt: DownloadBetReceiptUseCase = DownloadBetReceiptUseCase { betPdfResult }) = PrizeRedemptionViewModel(
        PrizeRedemptionInput(REDEMPTION_BET_ID, "initial@caixa.gov.br", "12345678901"),
        GetPrizeDetailsUseCase { detailsResult },
        GenerateLotteryRedemptionCodeUseCase {
            codeCalls++
            codeResult
        },
        ConsultPixRedemptionKeyUseCase { _, _ -> consultResult },
        RedeemPrizeUseCase { id, channel, value ->
            redemptionCalls++
            redemption = Triple(id, channel, value)
            redeemResult
        },
        CancelPixRedemptionUseCase {
            cancelCalls++
            cancelResult
        },
        PrizeReceiptUseCases(betReceipt, DownloadPrizeReceiptUseCase { prizePdfResult }),
        GetBettingParametersUseCase { parametersResult },
        PrepareBetEntryUseCase { request ->
            entryCalls++
            requestedModality = request.modalityId
            entryResult
        },
        java.time.Clock.fixed(java.time.Instant.parse("2026-09-07T15:30:00Z"), java.time.ZoneId.of("America/Sao_Paulo")),
        br.gov.caixa.loterias.core.common.network.NetworkAvailability {
            connectionReads++
            connection
        },
        FixedCoroutineDispatchers(dispatcher)
    )
}

private const val REDEMPTION_BET_ID = 7L
