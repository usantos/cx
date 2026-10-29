package br.gov.caixa.loterias.apostas.navigation.startup

import br.gov.caixa.loterias.core.network.mock.MockBackendScenario
import br.gov.caixa.loterias.feature.home.presentation.mapper.prize

internal fun mockStartDestination(scenario: MockBackendScenario): String = when (scenario) {
    MockBackendScenario.REGISTRATION_REQUIRED -> "account/register"
    MockBackendScenario.TERMS_REQUIRED -> "support/terms-acceptance"
    MockBackendScenario.SELF_ASSESSMENT_REQUIRED -> "responsible/start/required"
    MockBackendScenario.CART_EXPIRED_POOL -> "cart/current"
    MockBackendScenario.PURCHASE_PROCESSING -> "payment/confirmation"
    MockBackendScenario.PURCHASE_REJECTED -> "payment/failure"
    MockBackendScenario.PURCHASE_RESPONSE_LOST,
    MockBackendScenario.PURCHASE_LOGIN_REQUIRED,
    MockBackendScenario.PURCHASE_TERMS_REQUIRED,
    MockBackendScenario.PURCHASE_REGISTRATION_REQUIRED,
    MockBackendScenario.PURCHASE_UNAVAILABLE,
    MockBackendScenario.PURCHASE_HTTP_UNAUTHORIZED,
    MockBackendScenario.PURCHASE_CART_REDIRECT,
    MockBackendScenario.PURCHASE_FAVORITE_CARTS_REDIRECT,
    MockBackendScenario.PURCHASE_MORE_BETS_REQUIRED,
    MockBackendScenario.PURCHASE_BUSINESS_RULE,
    MockBackendScenario.PURCHASE_SERVER_MESSAGE,
    MockBackendScenario.PURCHASE_SERVER_FAILURE,
    MockBackendScenario.PURCHASE_INVALID_IDENTIFICATION,
    MockBackendScenario.PURCHASE_MP_REGISTRATION_REQUIRED,
    MockBackendScenario.PURCHASE_MP_REGISTRATION_TOKEN_FAILURE,
    -> "payment/confirm-card"

    MockBackendScenario.PURCHASE_APPROVED -> "payment/success"

    MockBackendScenario.PAYMENT_NO_SAVED_CARDS -> "payment/method"

    MockBackendScenario.PIX_PENDING -> "payment/pix-copy"

    MockBackendScenario.PIX_EXPIRED -> "payment/pix-expired"

    MockBackendScenario.DAM_BLOCKED -> "payment/method"

    MockBackendScenario.TEIMOSINHA_RESCUED -> "tickets/bet-detail?original=true&resgate=true"

    MockBackendScenario.PRIZE_AVAILABLE -> "results/confirmed-bet"

    MockBackendScenario.PRIZE_MP_REGISTRATION_REQUIRED,
    MockBackendScenario.PRIZE_LOGIN_REQUIRED,
    MockBackendScenario.MP_TOKEN_LOGIN_REQUIRED,
    MockBackendScenario.MP_TOKEN_LOGIN_REDIRECT,
    MockBackendScenario.MP_TOKEN_TERMS_REQUIRED,
    MockBackendScenario.MP_TOKEN_REPEAT_REGISTRATION,
    MockBackendScenario.MP_TOKEN_BUSINESS_RULE,
    MockBackendScenario.MP_TOKEN_UNCHANGED,
    MockBackendScenario.MP_TOKEN_RESPONSE_TIMEOUT,
    MockBackendScenario.MP_TOKEN_CONNECTION_FAILURE,
    MockBackendScenario.PRIZE_RESPONSE_TIMEOUT,
    MockBackendScenario.PRIZE_CONNECTION_FAILURE,
    MockBackendScenario.PRIZE_PROCESSING_REDIRECT,
    MockBackendScenario.PRIZE_LOGIN_REDIRECT,
    MockBackendScenario.PRIZE_TERMS_REQUIRED,
    MockBackendScenario.PRIZE_ACCOUNT_REGISTRATION_REQUIRED,
    MockBackendScenario.PRIZE_UNAVAILABLE,
    MockBackendScenario.PRIZE_CART_REDIRECT,
    MockBackendScenario.PRIZE_FAVORITE_CARTS_REDIRECT,
    MockBackendScenario.PRIZE_MORE_BETS_REQUIRED,
    MockBackendScenario.PRIZE_BUSINESS_RULE,
    MockBackendScenario.PRIZE_SERVER_MESSAGE,
    MockBackendScenario.PRIZE_SERVER_FAILURE,
    MockBackendScenario.PRIZE_INVALID_IDENTIFICATION,
    MockBackendScenario.PRIZE_MP_REGISTRATION_TOKEN_FAILURE,
    -> "retail/prize-mercado-pago"

    else -> "home"
}
