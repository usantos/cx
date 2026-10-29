package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.apostas.navigation.session.ForegroundSessionRoutePolicy
import br.gov.caixa.loterias.core.navigation.destination.BettingSimulationRoute
import br.gov.caixa.loterias.core.navigation.destination.PoolTutorialRoute
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize
import br.gov.caixa.loterias.feature.retail.navigation.legacyPrizeForegroundHost
import org.junit.Assert.*
import org.junit.Test

class ForegroundSessionRoutePolicyTest {
    @Test fun nonBaseControllersAndProviderEntryNeverConsumeBackground() {
        for (route in listOf(
            "tickets/my-bets",
            "tickets/bet-detail",
            "account/login",
            "account/token",
            "onboarding/splash",
            "onboarding/introduction",
            "retail/prize",
            "unknown/future"
        )) {
            assertFalse("Route must preserve pending background: $route", ForegroundSessionRoutePolicy.eligible(route))
        }
        assertFalse(ForegroundSessionRoutePolicy.eligible(null))
    }

    @Test fun auditedBaseHostsAndSameActivityStatesConsumeBackground() {
        for (route in listOf(
            "home", "cart/current", "tickets/purchases", "tickets/purchase-detail",
            "results/home/{modality}", "results/modalities", "results/detail", "marketplace/filter",
            "onboarding/tutorial", "onboarding/update", "onboarding/unavailable", "onboarding/age",
            "support/terms?tab=FAQ", "payment/pix-copy", "payment/pix-expired", "payment/success",
            "responsible/start/required", "account/personal-data", "betting/rapid-config"
        )) {
            assertTrue("Audited CX Base host must consume background: $route", ForegroundSessionRoutePolicy.eligible(route))
        }
    }

    @Test fun everyCurrentCatalogueRouteHasAnAuditedForegroundDecision() {
        val routes = listOf(
            br.gov.caixa.loterias.feature.onboarding.presentation.screen.onboardingScreens { it.toString() },
            br.gov.caixa.loterias.feature.account.presentation.screen.accountScreens { it.toString() },
            br.gov.caixa.loterias.feature.betting.presentation.screen.bettingScreens { it.toString() },
            br.gov.caixa.loterias.feature.cart.presentation.screen.cartScreens { it.toString() },
            br.gov.caixa.loterias.feature.payment.presentation.screen.paymentScreens { it.toString() },
            br.gov.caixa.loterias.feature.tickets.presentation.screen.ticketScreens { it.toString() },
            br.gov.caixa.loterias.feature.results.presentation.screen.resultScreens { it.toString() },
            br.gov.caixa.loterias.feature.responsiblegaming.presentation.screen.responsibleGamingScreens { it.toString() },
            br.gov.caixa.loterias.feature.notifications.presentation.screen.notificationScreens { it.toString() },
            br.gov.caixa.loterias.feature.retail.presentation.screen.retailScreens { it.toString() },
            br.gov.caixa.loterias.feature.support.presentation.screen.supportScreens { it.toString() }
        ).flatten().map { it.route } + listOf("home", "marketplace/filter", "results/home/{modality}", "responsible/start/required")
        assertEquals(80, routes.size)
        val nonBase = setOf(
            "account/login",
            "account/token",
            "onboarding/splash",
            "onboarding/introduction",
            "tickets/my-bets",
            "tickets/bet-detail",
            "retail/prize"
        )
        routes.forEach { route ->
            assertEquals(
                "Audited foreground decision for $route",
                route !in nonBase,
                ForegroundSessionRoutePolicy.eligible(route)
            )
        }
    }

    @Test fun actualRegisteredPathTemplatesUseTheirAuditedBaseHosts() {
        for (route in listOf(
            "betting/combo-detail/{comboId}",
            "betting/pool-detail/{poolCode}",
            BettingSimulationRoute.pattern,
            "cart/favorite-detail/{cartId}",
            "payment/card-flow/{methodId}?cardId={cardId}",
            "cart/local-review",
            PoolTutorialRoute.pattern
        )) {
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
            assertFalse(
                "A stage flag cannot authorize unrelated hosts: $route",
                ForegroundSessionRoutePolicy.eligible(route, prizeChannelHost = true)
            )
        }
    }

    @Test fun prizeStageUsesActualLegacyControllerWithoutInferringFromLoadingOrOptions() {
        val baseStages = setOf(
            br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.LOTTERY,
            br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.AGENCY,
            br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.PIX_ENTRY,
            br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.PIX_CONFIRM,
            br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.MERCADO_PAGO,
            br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.SUCCESS
        )
        br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage.entries.forEach { stage ->
            assertEquals("CX controller host for $stage", stage in baseStages, legacyPrizeForegroundHost(stage))
        }
    }

    @Test fun inactiveHistoricalDetailRequiresExplicitOriginAndDoesNotChangeCurrentDetail() {
        assertFalse(ForegroundSessionRoutePolicy.eligible("tickets/bet-detail?resgate=true"))
        assertTrue(ForegroundSessionRoutePolicy.eligible("tickets/bet-detail?resgate=true", historicalBetDetail = true))
        assertFalse(ForegroundSessionRoutePolicy.eligible("tickets/my-bets", historicalBetDetail = true))
    }
}
