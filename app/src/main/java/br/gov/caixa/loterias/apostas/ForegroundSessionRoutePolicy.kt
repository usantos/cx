package br.gov.caixa.loterias.apostas

/**
 * CX active manifest + controller inheritance, mapped through LEGACY_UI_INVENTORY.tsv.
 * SettingToolbar/LoteriasApp/Marketplace descendants inherit LoteriasBaseAppActivity.
 * Additional catalogue routes are states of those hosts, not new foreground lifecycles.
 */
internal object ForegroundSessionRoutePolicy {
    // NavController reports registered templates, not the catalogue's simplified labels.
    // Query-only patterns normalize below; these exact path templates need explicit hosts.
    private val registeredHosts = mapOf(
        "betting/simulation/{modalityId}" to "betting/simulation",
        "betting/combo-detail/{comboId}" to "betting/combo-detail",
        "betting/pool-detail/{poolCode}" to "betting/pool-detail",
        "cart/favorite-detail/{cartId}" to "cart/favorite-detail",
        "cart/local-review" to "cart/current",
        "payment/card-flow/{methodId}" to "payment/add-card",
    )
    private val baseHosts = setOf(
        "account/change-login",
        "account/personal-data",
        "account/personal-data-confirmation",
        "account/register",
        "account/register-confirmation",
        "betting/combo-detail",
        "betting/combos",
        "betting/favorites",
        "betting/home",
        "betting/modalities",
        "betting/pool-detail",
        "betting/pool-view",
        "betting/pools",
        "betting/rapid",
        "betting/rapid-list",
        "betting/simulation",
        "betting/tutorial",
        "betting/tutorial-plus",
        "betting/tutorial-super-seven",
        "cart/current",
        "cart/favorite-carts",
        "cart/favorite-detail",
        "cart/included",
        "cart/pool-included",
        "home",
        "marketplace/filter",
        "notifications/messages",
        "notifications/preferences",
        "onboarding/age",
        "onboarding/tutorial",
        "onboarding/unavailable",
        "onboarding/update",
        "payment/add-card",
        "payment/card-preview",
        "payment/cards",
        "payment/confirm-card",
        "payment/confirmation",
        "payment/cvv",
        "payment/failure",
        "payment/method",
        "payment/pix-copy",
        "payment/pix-expired",
        "payment/pix-guidance",
        "payment/success",
        "responsible/form",
        "responsible/social",
        "responsible/start",
        "responsible/start/required",
        "responsible/suspension",
        "results/confirmed-bet",
        "results/detail",
        "results/home/{modality}",
        "results/modalities",
        "retail/location",
        "retail/prize-agency",
        "retail/prize-confirmation",
        "retail/prize-guidance",
        "retail/prize-lottery",
        "retail/prize-mercado-pago",
        "retail/prize-mercado-pago-success",
        "retail/prize-pix",
        "retail/search",
        "support",
        "support/game-help",
        "support/terms",
        "support/terms-acceptance",
        "support/terms-view",
        "tickets/manual-code",
        "tickets/purchase-detail",
        "tickets/purchases",
        "tickets/read",
        "tickets/read-game-choice",
        "tickets/read-result",
    )

    fun eligible(route: String?, historicalBetDetail: Boolean = false, prizeChannelHost: Boolean = false): Boolean {
        val base = route?.substringBefore('?') ?: return false
        // Current confirmed details use DetalheMinhasApostasActivity (AppCompatActivity).
        // The historical ApostaConfirmadaDetalhesActivity is Base but currently unreachable:
        // CX disables the history tab. Only an explicit tracked origin may enable it.
        if (base == "tickets/bet-detail") return historicalBetDetail
        // Options remain on DetalheMinhasApostasActivity; selected channel screens are Base.
        // This exact registered host changes internally without a NavController transition.
        if (base == "retail/prize/{betId}") return prizeChannelHost
        // No Activity owns the catalogue-only retail/prize options alias; actual channel
        // screens are listed above. New or unresolved hosts must not consume background.
        return (registeredHosts[base] ?: base) in baseHosts
    }
}

/** Actual controller host of the mixed prize flow, independent of token or response status. */
internal fun legacyPrizeForegroundHost(stage: br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage): Boolean = when (stage) {
    br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.LOADING,
    br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.OPTIONS -> false
    br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.LOTTERY,
    br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.AGENCY,
    br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.PIX_ENTRY,
    br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.PIX_CONFIRM,
    br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.MERCADO_PAGO,
    br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.SUCCESS -> true
}
