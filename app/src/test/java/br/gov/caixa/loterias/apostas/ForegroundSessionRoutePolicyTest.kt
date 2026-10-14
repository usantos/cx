package br.gov.caixa.loterias.apostas

import org.junit.Assert.*
import org.junit.Test

class ForegroundSessionRoutePolicyTest {
    @Test fun nonBaseControllersAndProviderEntryNeverConsumeBackground() {
        for (route in listOf("tickets/my-bets", "tickets/bet-detail", "account/login", "account/token",
            "onboarding/splash", "onboarding/introduction", "retail/prize", "unknown/future")) {
            assertFalse("Route must preserve pending background: $route", ForegroundSessionRoutePolicy.eligible(route))
        }
        assertFalse(ForegroundSessionRoutePolicy.eligible(null))
    }

    @Test fun auditedBaseHostsAndSameActivityStatesConsumeBackground() {
        for (route in listOf("home", "cart/current", "tickets/purchases", "tickets/purchase-detail",
            "results/home/{modality}", "results/modalities", "results/detail", "marketplace/filter",
            "onboarding/tutorial", "onboarding/update", "onboarding/unavailable", "onboarding/age",
            "support/terms?tab=FAQ", "payment/pix-copy", "payment/pix-expired", "payment/success",
            "responsible/start/required", "account/personal-data")) {
            assertTrue("Audited CX Base host must consume background: $route", ForegroundSessionRoutePolicy.eligible(route))
        }
    }

    @Test fun everyCurrentCatalogueRouteHasAnAuditedForegroundDecision() {
        val routes = listOf(
            br.gov.caixa.loterias.feature.onboarding.onboardingScreens,
            br.gov.caixa.loterias.feature.account.accountScreens,
            br.gov.caixa.loterias.feature.betting.bettingScreens,
            br.gov.caixa.loterias.feature.cart.cartScreens,
            br.gov.caixa.loterias.feature.payment.paymentScreens,
            br.gov.caixa.loterias.feature.tickets.ticketScreens,
            br.gov.caixa.loterias.feature.results.resultScreens,
            br.gov.caixa.loterias.feature.responsiblegaming.responsibleGamingScreens,
            br.gov.caixa.loterias.feature.notifications.notificationScreens,
            br.gov.caixa.loterias.feature.retail.retailScreens,
            br.gov.caixa.loterias.feature.support.supportScreens,
        ).flatten().map { it.route } + listOf("home", "marketplace/filter", "results/home/{modality}", "responsible/start/required")
        assertEquals(80, routes.size)
        val nonBase = setOf("account/login", "account/token", "onboarding/splash", "onboarding/introduction",
            "tickets/my-bets", "tickets/bet-detail", "retail/prize")
        routes.forEach { route -> assertEquals("Audited foreground decision for $route", route !in nonBase,
            ForegroundSessionRoutePolicy.eligible(route)) }
    }

    @Test fun actualRegisteredPathTemplatesUseTheirAuditedBaseHosts() {
        for (route in listOf("betting/combo-detail/{comboId}", "betting/pool-detail/{poolCode}",
            BettingSimulationRoute.pattern, "cart/favorite-detail/{cartId}", "payment/card-flow/{methodId}?cardId={cardId}",
            "cart/local-review", PoolTutorialRoute.pattern)) {
            assertTrue("Registered template must keep legacy Base behavior: $route", ForegroundSessionRoutePolicy.eligible(route))
        }
        assertFalse(ForegroundSessionRoutePolicy.eligible("retail/prize/{betId}"))
        assertFalse(ForegroundSessionRoutePolicy.eligible("betting/combo-detail/{unknown}"))
        assertFalse(ForegroundSessionRoutePolicy.eligible("home/unknown"))
    }

    @Test fun onlyExplicitGenericPrizeChannelContextEnablesMixedHost() {
        assertFalse(ForegroundSessionRoutePolicy.eligible("retail/prize/{betId}"))
        assertTrue(ForegroundSessionRoutePolicy.eligible("retail/prize/{betId}", prizeChannelHost = true))
        assertTrue(ForegroundSessionRoutePolicy.eligible("retail/prize/{betId}?variant=channel", prizeChannelHost = true))
        for (route in listOf("retail/prize", "retail/prize/{unknown}", "unknown/future", "tickets/my-bets", "tickets/bet-detail")) {
            assertFalse("A stage flag cannot authorize unrelated hosts: $route",
                ForegroundSessionRoutePolicy.eligible(route, prizeChannelHost = true))
        }
    }

    @Test fun prizeStageUsesActualLegacyControllerWithoutInferringFromLoadingOrOptions() {
        val baseStages = setOf(
            br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.LOTTERY,
            br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.AGENCY,
            br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.PIX_ENTRY,
            br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.PIX_CONFIRM,
            br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.MERCADO_PAGO,
            br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.SUCCESS,
        )
        br.gov.caixa.loterias.feature.retail.PrizeRedemptionStage.entries.forEach { stage ->
            assertEquals("CX controller host for $stage", stage in baseStages, legacyPrizeForegroundHost(stage))
        }
    }

    @Test fun inactiveHistoricalDetailRequiresExplicitOriginAndDoesNotChangeCurrentDetail() {
        assertFalse(ForegroundSessionRoutePolicy.eligible("tickets/bet-detail?resgate=true"))
        assertTrue(ForegroundSessionRoutePolicy.eligible("tickets/bet-detail?resgate=true", historicalBetDetail = true))
        assertFalse(ForegroundSessionRoutePolicy.eligible("tickets/my-bets", historicalBetDetail = true))
    }
}
