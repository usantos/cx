package br.gov.caixa.loterias.feature.betting.presentation.route

import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameActions
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

internal fun bettingGameActions(viewModel: BettingGameViewModel, onFavoriteLoginRequired: (() -> Unit)?): BettingGameActions =
    BettingGameActions(
        toggleNumber = viewModel::toggleNumber,
        chooseNumberQuantity = viewModel::chooseNumberQuantity,
        decreaseSurpriseQuantity = viewModel::decreaseSurpriseQuantity,
        increaseSurpriseQuantity = viewModel::increaseSurpriseQuantity,
        decreaseSurpriseCloverQuantity = viewModel::decreaseSurpriseCloverQuantity,
        increaseSurpriseCloverQuantity = viewModel::increaseSurpriseCloverQuantity,
        toggleMirror = viewModel::toggleMirror,
        toggleClover = viewModel::toggleClover,
        toggleSuperSevenDigit = viewModel::toggleSuperSevenDigit,
        selectLuckyMonth = viewModel::selectLuckyMonth,
        selectLotogolScore = viewModel::selectLotogolScore,
        clearSelection = viewModel::clearSelection,
        requestFavorite = { onFavoriteLoginRequired?.invoke() ?: viewModel.requestFavorite() },
        fillShake = viewModel::fillShake,
        primaryAction = viewModel::primaryAction
    )
