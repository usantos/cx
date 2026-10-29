package br.gov.caixa.loterias.feature.home.presentation.state

import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.feature.account.domain.model.AccountNotification
import br.gov.caixa.loterias.feature.betting.domain.model.BetCombo
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import java.math.BigDecimal

class HomeNotificationRedirect(val destination: ServiceRedirect)

data class HomeUiState(
    val results: HomeResultsState = HomeResultsState(),
    val loading: Boolean = true,
    val headerActionsRevealed: Boolean = false,
    val parameters: BettingParameters? = null,
    val features: HomeFeatureConfiguration = HomeFeatureConfiguration(),
    val combos: List<BetCombo> = emptyList(),
    val resaleOffers: List<br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability> = emptyList(),
    val socialTransfers: SocialTransfers? = null,
    val loadingSocialTransfers: Boolean = false,
    val cartTotal: BigDecimal = BigDecimal.ZERO,
    val cartItemCount: Int = 0,
    val loadingCart: Boolean = false,
    val errorMessage: String? = null,
    val parametersUnavailable: Boolean = false,
    val parameterRetryMessage: String? = null,
    val preparingBet: Boolean = false,
    val betEntryDecision: BetEntryDecision? = null,
    val betEntryError: String? = null,
    val poolEntryDecision: PoolEntryDecision? = null,
    val otherBettingDecision: HomeBettingDecision? = null,
    val localCartReviewRequired: Boolean = false,
    val localCartReviewAcknowledged: Boolean = false,
    val unidentifiedPayment: Boolean = false,
    val paymentNoticeAcknowledged: Boolean = false,
    val latestNotification: AccountNotification? = null,
    val notificationDismissed: Boolean = false,
    val notificationReadError: String? = null,
    val notificationNotice: ServiceNoticeUiState? = null,
    val notificationRedirect: HomeNotificationRedirect? = null,
    val unreadNotificationCount: Int = 0,
)
