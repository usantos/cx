package br.gov.caixa.loterias.feature.home.navigation

import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction
import br.gov.caixa.loterias.feature.home.presentation.mapper.homeBettingAction

/** Aplica a opção de ativação da Home às rotas de atalho de apostas pertencentes à Home. */
fun homeBettingRouteVisible(route: String, features: HomeFeatureConfiguration): Boolean =
    homeBettingAction(route) != HomeBettingAction.RAPID || features.rapidBettingEnabled
