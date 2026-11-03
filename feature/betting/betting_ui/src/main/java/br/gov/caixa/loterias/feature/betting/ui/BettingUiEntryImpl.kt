package br.gov.caixa.loterias.feature.betting.ui

import androidx.annotation.Keep
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBetComboDetailScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBetCombosScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBettingEntryContentEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBettingGameScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBettingLotogolContentEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBettingScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingBettingSportsContentEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingFavoriteBetsContentEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingLotteryModalitiesScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingModeToolbarEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingRapidConfigurationContentEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingRapidListScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingShakeTutorialScreenEntry
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry

@Keep
class BettingUiEntryImpl :
    BettingUiEntry,
    BettingModeToolbarEntry by BettingModeToolbarRenderer(),

    BettingBettingGameScreenEntry by BettingBettingGameScreenRenderer(),
    BettingBetCombosScreenEntry by BettingBetCombosScreenRenderer(),

    BettingFavoriteBetsContentEntry by BettingFavoriteBetsContentRenderer(),

    BettingBetComboDetailScreenEntry by BettingBetComboDetailScreenRenderer(),

    BettingBettingEntryContentEntry by BettingBettingEntryContentRenderer(),

    BettingRapidListScreenEntry by BettingRapidListScreenRenderer(),
    BettingBettingLotogolContentEntry by BettingBettingLotogolContentRenderer(),
    BettingBettingSportsContentEntry by BettingBettingSportsContentRenderer(),
    BettingRapidConfigurationContentEntry by BettingRapidConfigurationContentRenderer(),

    BettingBettingScreenEntry by BettingBettingScreenRenderer(),

    BettingShakeTutorialScreenEntry by BettingShakeTutorialScreenRenderer(),
    BettingLotteryModalitiesScreenEntry by BettingLotteryModalitiesScreenRenderer()
