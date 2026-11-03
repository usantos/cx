package br.gov.caixa.loterias.core.domain.configuration.model

/** Capacidades locais; a disponibilidade remota não concede autorização de negócio. */
enum class AppModule(val key: String, val splitName: String, val entryClassName: String, val bundled: Boolean = false) {
    ACCOUNT("account", "account_ui", "br.gov.caixa.loterias.feature.account.ui.AccountUiEntryImpl", true),
    HOME("home", "home_ui", "br.gov.caixa.loterias.feature.home.ui.HomeUiEntryImpl", true),
    ONBOARDING("onboarding", "onboarding_ui", "br.gov.caixa.loterias.feature.onboarding.ui.OnboardingUiEntryImpl"),
    BETTING("betting", "betting_ui", "br.gov.caixa.loterias.feature.betting.ui.BettingUiEntryImpl"),
    MARKETPLACE("marketplace", "marketplace_ui", "br.gov.caixa.loterias.feature.marketplace.ui.MarketplaceUiEntryImpl"),
    MARKETPLACE_FILTER(
        "marketplace_filter",
        "marketplace_filter_ui",
        "br.gov.caixa.loterias.feature.marketplacefilter.ui.MarketplaceFilterUiEntryImpl"
    ),
    CART("cart", "cart_ui", "br.gov.caixa.loterias.feature.cart.ui.CartUiEntryImpl"),
    PAYMENT("payment", "payment_ui", "br.gov.caixa.loterias.feature.payment.ui.PaymentUiEntryImpl"),
    TICKETS("tickets", "tickets_ui", "br.gov.caixa.loterias.feature.tickets.ui.TicketsUiEntryImpl"),
    RESULTS("results", "results_ui", "br.gov.caixa.loterias.feature.results.ui.ResultsUiEntryImpl"),
    NOTIFICATIONS("notifications", "notifications_ui", "br.gov.caixa.loterias.feature.notifications.ui.NotificationsUiEntryImpl"),
    RESPONSIBLE_GAMING(
        "responsible_gaming",
        "responsible_gaming_ui",
        "br.gov.caixa.loterias.feature.responsiblegaming.ui.ResponsibleGamingUiEntryImpl",
        true
    ),
    RETAIL("retail", "retail_ui", "br.gov.caixa.loterias.feature.retail.ui.RetailUiEntryImpl"),
    SUPPORT_HELP("support_help", "support_ui", "br.gov.caixa.loterias.feature.support.ui.SupportUiEntryImpl"),
    SUPPORT_FAQ("support_faq", "faq", "br.gov.caixa.loterias.feature.support.faq.SupportFaqEntryImpl"),
    ;

    val defaultEnabled: Boolean get() = this != SUPPORT_FAQ
}
