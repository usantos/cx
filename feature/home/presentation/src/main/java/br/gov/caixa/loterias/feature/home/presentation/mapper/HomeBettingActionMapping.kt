package br.gov.caixa.loterias.feature.home.presentation.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction

fun homeBettingAction(route: String): HomeBettingAction? = when (route) {
    "betting/rapid", "betting/rapid-list" -> HomeBettingAction.RAPID
    "betting/combos" -> HomeBettingAction.COMBO
    else -> null
}

