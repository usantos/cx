package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.apostas.navigation.startup.startupDestination
import br.gov.caixa.loterias.apostas.navigation.startup.mockStartDestination
import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize
import br.gov.caixa.loterias.feature.tickets.navigation.visualTestPurchase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import br.gov.caixa.loterias.core.auth.session.ForegroundSessionStatus

class MockScenarioNavigationTest {
    @Test
    fun `startup opens home only with an active owned session`() {
        assertEquals("home", startupDestination(ForegroundSessionStatus.ACTIVE, hasIdentity = true))
        assertEquals("account/login", startupDestination(ForegroundSessionStatus.ACTIVE, hasIdentity = false))
        listOf(ForegroundSessionStatus.MISSING, ForegroundSessionStatus.EXPIRED,
            ForegroundSessionStatus.AUTHORIZATION_PENDING).forEach { status ->
            assertEquals("account/login", startupDestination(status, hasIdentity = true))
            assertEquals("account/login", startupDestination(status, hasIdentity = false))
        }
    }

    @Test
    fun `remembered credential starts authorization without granting direct home access`() {
        assertEquals("account/login?authorize=true", startupDestination(ForegroundSessionStatus.MISSING, false, true))
        assertEquals("account/login?authorize=true", startupDestination(ForegroundSessionStatus.EXPIRED, true, true))
        assertEquals("home", startupDestination(ForegroundSessionStatus.ACTIVE, true, true))
        assertEquals("account/login", startupDestination(ForegroundSessionStatus.AUTHORIZATION_PENDING, false, true))
    }

    @Test
    fun `business scenarios open their observable screen`() {
        val destinations = mapOf(
            MockBackendScenario.REGISTRATION_REQUIRED to "account/register",
            MockBackendScenario.TERMS_REQUIRED to "support/terms-acceptance",
            MockBackendScenario.SELF_ASSESSMENT_REQUIRED to "responsible/start/required",
            MockBackendScenario.CART_EXPIRED_POOL to "cart/current",
            MockBackendScenario.PURCHASE_PROCESSING to "payment/confirmation",
            MockBackendScenario.PURCHASE_APPROVED to "payment/success",
            MockBackendScenario.PURCHASE_REJECTED to "payment/failure",
            MockBackendScenario.PURCHASE_RESPONSE_LOST to "payment/confirm-card",
            MockBackendScenario.PURCHASE_LOGIN_REQUIRED to "payment/confirm-card",
            MockBackendScenario.PURCHASE_TERMS_REQUIRED to "payment/confirm-card",
            MockBackendScenario.PURCHASE_REGISTRATION_REQUIRED to "payment/confirm-card",
            MockBackendScenario.PURCHASE_UNAVAILABLE to "payment/confirm-card",
            MockBackendScenario.PURCHASE_HTTP_UNAUTHORIZED to "payment/confirm-card",
            MockBackendScenario.PURCHASE_CART_REDIRECT to "payment/confirm-card",
            MockBackendScenario.PURCHASE_FAVORITE_CARTS_REDIRECT to "payment/confirm-card",
            MockBackendScenario.PURCHASE_MORE_BETS_REQUIRED to "payment/confirm-card",
            MockBackendScenario.PURCHASE_BUSINESS_RULE to "payment/confirm-card",
            MockBackendScenario.PURCHASE_SERVER_MESSAGE to "payment/confirm-card",
            MockBackendScenario.PURCHASE_SERVER_FAILURE to "payment/confirm-card",
            MockBackendScenario.PURCHASE_INVALID_IDENTIFICATION to "payment/confirm-card",
            MockBackendScenario.PURCHASE_MP_REGISTRATION_REQUIRED to "payment/confirm-card",
            MockBackendScenario.PURCHASE_MP_REGISTRATION_TOKEN_FAILURE to "payment/confirm-card",
            MockBackendScenario.PAYMENT_NO_SAVED_CARDS to "payment/method",
            MockBackendScenario.PIX_PENDING to "payment/pix-copy",
            MockBackendScenario.PIX_EXPIRED to "payment/pix-expired",
            MockBackendScenario.DAM_BLOCKED to "payment/method",
            MockBackendScenario.TEIMOSINHA_RESCUED to "tickets/bet-detail?original=true&resgate=true",
            MockBackendScenario.PRIZE_AVAILABLE to "results/confirmed-bet",
            MockBackendScenario.PRIZE_MP_REGISTRATION_REQUIRED to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_LOGIN_REQUIRED to "retail/prize-mercado-pago",
            MockBackendScenario.MP_TOKEN_LOGIN_REQUIRED to "retail/prize-mercado-pago",
            MockBackendScenario.MP_TOKEN_LOGIN_REDIRECT to "retail/prize-mercado-pago",
            MockBackendScenario.MP_TOKEN_TERMS_REQUIRED to "retail/prize-mercado-pago",
            MockBackendScenario.MP_TOKEN_REPEAT_REGISTRATION to "retail/prize-mercado-pago",
            MockBackendScenario.MP_TOKEN_BUSINESS_RULE to "retail/prize-mercado-pago",
            MockBackendScenario.MP_TOKEN_UNCHANGED to "retail/prize-mercado-pago",
            MockBackendScenario.MP_TOKEN_RESPONSE_TIMEOUT to "retail/prize-mercado-pago",
            MockBackendScenario.MP_TOKEN_CONNECTION_FAILURE to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_RESPONSE_TIMEOUT to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_CONNECTION_FAILURE to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_PROCESSING_REDIRECT to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_LOGIN_REDIRECT to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_TERMS_REQUIRED to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_ACCOUNT_REGISTRATION_REQUIRED to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_UNAVAILABLE to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_CART_REDIRECT to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_FAVORITE_CARTS_REDIRECT to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_MORE_BETS_REQUIRED to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_BUSINESS_RULE to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_SERVER_MESSAGE to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_SERVER_FAILURE to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_INVALID_IDENTIFICATION to "retail/prize-mercado-pago",
            MockBackendScenario.PRIZE_MP_REGISTRATION_TOKEN_FAILURE to "retail/prize-mercado-pago"
        )

        destinations.forEach { (scenario, destination) ->
            assertEquals(destination, mockStartDestination(scenario))
        }
        (MockBackendScenario.entries - destinations.keys).forEach { scenario ->
            assertEquals("home", mockStartDestination(scenario))
        }
    }

    @Test
    fun `purchase detail has deterministic data only in mock builds`() {
        assertNull(visualTestPurchase(false))
        val purchase = requireNotNull(visualTestPurchase(true))
        assertEquals(99L, purchase.id)
        assertEquals("Aprovada", purchase.status)
        assertEquals("Pix", purchase.paymentMethod)
    }
}
