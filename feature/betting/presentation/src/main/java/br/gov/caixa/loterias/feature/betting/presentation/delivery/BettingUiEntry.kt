package br.gov.caixa.loterias.feature.betting.presentation.delivery

import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry

interface BettingUiEntry :
    FeatureUiEntry,
    BettingModeToolbarEntry,
    BettingFavoriteBetsContentEntry,
    BettingBettingScreenEntry,
    BettingBettingLotogolContentEntry,
    BettingBettingSportsContentEntry,
    BettingBettingGameScreenEntry,
    BettingRapidListScreenEntry,
    BettingBetComboDetailScreenEntry,
    BettingLotteryModalitiesScreenEntry,
    BettingBettingEntryContentEntry,
    BettingBetCombosScreenEntry,
    BettingRapidConfigurationContentEntry,
    BettingShakeTutorialScreenEntry
