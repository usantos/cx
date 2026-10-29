package br.gov.caixa.loterias.feature.payment.presentation.state

import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.PixCharge
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState
import java.math.BigDecimal

enum class PaymentMode { METHODS, CARDS, PIX }

data class PaymentUiState(
    val loading: Boolean = true,
    val methods: List<PaymentMethod> = emptyList(),
    val cards: Map<Long, List<SavedPaymentCard>> = emptyMap(),
    val cartTotal: BigDecimal = BigDecimal.ZERO,
    val showPrePurchaseConfirmation: Boolean = false,
    val pendingAddMethodId: Long? = null,
    val deleteCandidate: SavedPaymentCard? = null,
    val pix: PixCharge? = null,
    val pixApproved: Boolean = false,
    val pixRejected: Boolean = false,
    val checkingPix: Boolean = false,
    val damMessage: String? = null,
    val errorMessage: String? = null,
    val redirect: PurchaseRedirect? = null,
    val redirectPaused: Boolean = false,
    val notice: PurchaseNoticeUiState? = null,
    val registrationPending: Boolean = false,
    val pixRecoveryRequired: Boolean = false,
    val pixSessionMismatch: Boolean = false
)
