package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.betting.HomeBettingAction
import br.gov.caixa.loterias.core.domain.betting.HomeBettingTarget
import br.gov.caixa.loterias.core.domain.configuration.HomeFeatureConfiguration

internal fun homeBettingAction(route: String): HomeBettingAction? = when (route) {
    "betting/rapid", "betting/rapid-list" -> HomeBettingAction.RAPID
    "betting/combos" -> HomeBettingAction.COMBO
    else -> null
}

internal fun homeBettingDestination(target: HomeBettingTarget): String = when (target) {
    HomeBettingTarget.COMBOS -> "betting/combos"
    HomeBettingTarget.INSTANT -> "https://instantanea.caixa.gov.br"
    HomeBettingTarget.RAPID_CONFIGURATION -> "betting/rapid"
    HomeBettingTarget.RAPID_LIST -> "betting/rapid-list"
}

internal fun homeBettingRouteVisible(route: String, features: HomeFeatureConfiguration): Boolean =
    homeBettingAction(route) != HomeBettingAction.RAPID || features.rapidBettingEnabled
