package br.gov.caixa.loterias.apostas.composition.navigation

import br.gov.caixa.loterias.feature.account.navigation.AccountNavigationDependencies
import br.gov.caixa.loterias.feature.betting.navigation.BettingNavigationDependencies
import br.gov.caixa.loterias.feature.cart.navigation.CartNavigationDependencies
import br.gov.caixa.loterias.feature.home.navigation.HomeNavigationDependencies
import br.gov.caixa.loterias.feature.marketplace.navigation.MarketplaceNavigationDependencies
import br.gov.caixa.loterias.feature.notifications.navigation.NotificationsNavigationDependencies
import br.gov.caixa.loterias.feature.onboarding.navigation.OnboardingNavigationDependencies
import br.gov.caixa.loterias.feature.payment.navigation.PaymentNavigationDependencies
import br.gov.caixa.loterias.feature.responsiblegaming.navigation.ResponsibleGamingNavigationDependencies
import br.gov.caixa.loterias.feature.results.navigation.ResultsNavigationDependencies
import br.gov.caixa.loterias.feature.retail.navigation.RetailNavigationDependencies
import br.gov.caixa.loterias.feature.support.navigation.SupportNavigationDependencies
import br.gov.caixa.loterias.feature.tickets.navigation.TicketsNavigationDependencies
import javax.inject.Inject

/** Dependências para compor, na raiz do app, os grafos de navegação pertencentes às features. */
class AppFeatureGraphDependencies @Inject constructor(
    val account: AccountNavigationDependencies,
    val betting: BettingNavigationDependencies,
    val cart: CartNavigationDependencies,
    val home: HomeNavigationDependencies,
    val marketplace: MarketplaceNavigationDependencies,
    val notifications: NotificationsNavigationDependencies,
    val onboarding: OnboardingNavigationDependencies,
    val payment: PaymentNavigationDependencies,
    val responsibleGaming: ResponsibleGamingNavigationDependencies,
    val results: ResultsNavigationDependencies,
    val retail: RetailNavigationDependencies,
    val support: SupportNavigationDependencies,
    val tickets: TicketsNavigationDependencies,
)
