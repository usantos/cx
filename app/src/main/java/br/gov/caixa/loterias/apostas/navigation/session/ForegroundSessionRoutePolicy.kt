package br.gov.caixa.loterias.apostas.navigation.session

/**
 * Manifesto ativo do CX e herança dos controladores, mapeados por LEGACY_UI_INVENTORY.tsv.
 * Descendentes de SettingToolbar/LoteriasApp/Marketplace herdam LoteriasBaseAppActivity.
 * Rotas adicionais do catálogo são estados desses hospedeiros, não novos ciclos de vida em primeiro plano.
 */
internal object ForegroundSessionRoutePolicy {
    // NavController informa os modelos registrados, não os rótulos simplificados do catálogo.
    // Padrões compostos apenas por consulta são normalizados abaixo; estes modelos exatos de caminho exigem hospedeiros explícitos.
    private val registeredHosts = mapOf(
        "betting/simulation/{modalityId}" to "betting/simulation",
        "betting/rapid-config" to "betting/rapid",
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
        // Os detalhes confirmados atuais usam DetalheMinhasApostasActivity (AppCompatActivity).
        // A ApostaConfirmadaDetalhesActivity histórica é Base, mas atualmente está inacessível:
        // O CX desativa a aba de histórico. Somente uma origem explícita e rastreada pode ativá-la.
        if (base == "tickets/bet-detail") return historicalBetDetail
        // As opções permanecem em DetalheMinhasApostasActivity; as telas dos canais selecionados são Base.
        // Este hospedeiro registrado específico muda internamente sem uma transição do NavController.
        if (base == "retail/prize/{betId}") return prizeChannelHost
        // Nenhuma Activity corresponde ao alias de opções de lotérica/prêmio exclusivo do catálogo; as telas reais
        // dos canais estão listadas acima. Hospedeiros novos ou não resolvidos não devem consumir o sinal de segundo plano.
        return (registeredHosts[base] ?: base) in baseHosts
    }
}

