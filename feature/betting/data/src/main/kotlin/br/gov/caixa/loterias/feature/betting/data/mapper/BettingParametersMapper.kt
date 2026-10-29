package br.gov.caixa.loterias.feature.betting.data.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.betting.data.dto.BettingParametersDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotteryGameParameterDto
import java.math.BigDecimal

internal fun BettingParametersDto.toBettingParameters(shields: Map<Int, String?>): BettingParameters {
    val dto = this
    return BettingParameters(
        dailyLimit = dto.dailyLimit ?: BigDecimal.ZERO,
        minimumCartAmount = dto.minimumCartAmount ?: BigDecimal.ZERO,
        maximumCartBets = dto.maximumCartBets ?: 0,
        games = dto.parameters.orEmpty().mapNotNull { parameter ->
            (parameter.game ?: parameter.nextGame)?.toBettingGame(shields)?.copy(hasNextContest = parameter.nextGame != null)
        },
        availablePoolModalities = dto.availablePoolModalities.orEmpty().map { available ->
            PoolModalityAvailability(
                modalityId = available.modality?.id ?: bettingModalityId(available.modalityCode),
                modalityCode = available.modalityCode.orEmpty(),
                contestType = available.contestType.orEmpty().ifBlank { "NORMAL" },
                contestNumber = available.contestNumber,
                resaleClosingDate = available.resaleClosingDate.orEmpty(),
                resaleClosingTime = available.resaleClosingTime.orEmpty(),
                modality = available.modality?.toBettingModality(),
                estimatedPrize = available.estimatedPrize,
                drawDateTime = available.drawDateTime.orEmpty(),
            )
        }.filter { it.modalityId > 0 || it.modalityCode.isNotBlank() },
    )
}

internal fun LotteryGameParameterDto.toBettingGame(shields: Map<Int, String?>): LotteryGameParameters {
    val detailed = contest?.modality
    val modality = detailed?.toBettingModality() ?: LotteryModality(
        id = bettingModalityId(contest?.modalityCode),
        name = contest?.modalityCode.displayBettingModalityName(),
        code = contest?.modalityCode.orEmpty(),
    )
    return LotteryGameParameters(
        modality = modality,
        contestNumber = contest?.number ?: 0,
        drawDate = contest?.drawDateTime ?: contest?.drawDate.orEmpty(),
        maximumNumber = maximumNumber ?: 0,
        minimumSelection = minimumSelection ?: 0,
        maximumSelection = maximumSelection ?: 0,
        maximumSurprises = maximumSurprises ?: 0,
        recurringOptions = recurringOptions.orEmpty(),
        prices = prices.orEmpty().map { price ->
            BetPrice(
                numberQuantity = price.numberQuantity ?: 0,
                cloverQuantity = price.cloverQuantity ?: 0,
                amount = price.amount ?: BigDecimal.ZERO,
                doubleQuantity = price.doubleQuantity ?: 0,
                tripleQuantity = price.tripleQuantity ?: 0,
            )
        },
        minimumClovers = clovers?.minimumSelection ?: 0,
        maximumClovers = clovers?.maximumSelection ?: 0,
        maximumCloverNumber = clovers?.maximumNumber ?: 0,
        open = open != false && contest?.open != false,
        contestType = contest?.contestType.orEmpty().ifBlank { "NORMAL" },
        estimatedPrize = contest?.estimatedPrize ?: BigDecimal.ZERO,
        closingDateTime = contest?.closingDateTime.orEmpty(),
        teams = teams.orEmpty().map { it.toBettingTeam(shields) },
        matches = matches.orEmpty().mapNotNull { it.toBettingMatch(shields) },
        luckyMonths = luckyMonths.orEmpty().map { month ->
            LuckyMonth(
                number = month.number ?: 0,
                name = month.name.orEmpty(),
                abbreviation = month.abbreviation.orEmpty(),
            )
        }.filter { it.number > 0 && it.name.isNotBlank() },
        acceptsMirror = acceptsMirror == true,
        minimumBetAmount = minimumBetAmount,
    )
}
