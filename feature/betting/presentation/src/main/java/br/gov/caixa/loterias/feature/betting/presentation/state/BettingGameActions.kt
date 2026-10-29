package br.gov.caixa.loterias.feature.betting.presentation.state

internal data class BettingGameActions(
    val toggleNumber: (Int) -> Unit,
    val chooseNumberQuantity: (Int) -> Unit,
    val decreaseSurpriseQuantity: () -> Unit,
    val increaseSurpriseQuantity: () -> Unit,
    val decreaseSurpriseCloverQuantity: () -> Unit,
    val increaseSurpriseCloverQuantity: () -> Unit,
    val toggleMirror: () -> Unit,
    val toggleClover: (Int) -> Unit,
    val toggleSuperSevenDigit: (Int, Int) -> Unit,
    val selectLuckyMonth: (br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth) -> Unit,
    val selectLotogolScore: (Int, Boolean, Int) -> Unit,
    val clearSelection: () -> Unit,
    val requestFavorite: () -> Unit,
    val fillShake: (Boolean) -> Unit,
    val primaryAction: () -> Unit,
)
