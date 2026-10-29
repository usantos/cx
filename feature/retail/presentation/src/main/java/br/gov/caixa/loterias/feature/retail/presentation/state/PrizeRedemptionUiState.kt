package br.gov.caixa.loterias.feature.retail.presentation.state

import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState

enum class PrizeRedemptionStage { LOADING, OPTIONS, LOTTERY, AGENCY, PIX_ENTRY, PIX_CONFIRM, MERCADO_PAGO, SUCCESS }
enum class PrizePixKeySource { CPF, OTHER }

data class PrizeRegistrationNotice(val message: String, val email: String) {
    override fun toString(): String = "PrizeRegistrationNotice(redacted)"
}

data class PrizeRedemptionUiState(
    val stage: PrizeRedemptionStage = PrizeRedemptionStage.LOADING,
    val details: PrizeDetails? = null,
    val selectedChannel: PrizeRedemptionChannel? = null,
    val lotteryCode: LotteryRedemptionCode? = null,
    val remainingSeconds: Int = 0,
    val cpf: String = "",
    val pixKeySource: PrizePixKeySource = PrizePixKeySource.CPF,
    val pixKey: String = "",
    val pixOwnershipAccepted: Boolean = false,
    val pixBeneficiary: PixBeneficiary? = null,
    val email: String = "",
    val loading: Boolean = true,
    val errorMessage: String? = null,
    val successNoticeMessage: String? = null,
    val currentContest: LotteryGameParameters? = null,
    val currentContestLoading: Boolean = false,
    val currentContestError: String? = null,
    val betEntryDecision: BetEntryDecision? = null,
    val registrationNotice: PrizeRegistrationNotice? = null,
    val registrationPending: Boolean = false,
    val notice: ServiceNoticeUiState? = null,
    val redirect: ServiceRedirect? = null,
    val redirectPaused: Boolean = false,
)

data class PrizeReceipt(val bytes: ByteArray, val fileName: String)
