package br.gov.caixa.loterias.feature.home.navigation

import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingTarget

fun homeBettingDestination(target: HomeBettingTarget): String = when (target) {
    HomeBettingTarget.COMBOS -> "betting/combos"
    HomeBettingTarget.INSTANT -> "https://instantanea.caixa.gov.br"
    HomeBettingTarget.RAPID_CONFIGURATION -> "betting/rapid"
    HomeBettingTarget.RAPID_LIST -> "betting/rapid-list"
}
