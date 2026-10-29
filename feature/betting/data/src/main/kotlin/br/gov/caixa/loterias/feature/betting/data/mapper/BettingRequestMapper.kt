package br.gov.caixa.loterias.feature.betting.data.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.data.dto.AddBetRequestDto
import br.gov.caixa.loterias.feature.betting.data.dto.DomainCharacterDto
import br.gov.caixa.loterias.feature.betting.data.dto.DomainIntegerDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotecaBetMatchDto
import br.gov.caixa.loterias.feature.betting.data.dto.LotogolBetMatchDto
import br.gov.caixa.loterias.feature.betting.data.dto.LuckyMonthParameterDto
import com.google.gson.Gson

internal fun BetDraft.toAddBetRequest(): AddBetRequestDto {
    val draft = this
    val code = bettingModalityCode(draft.modalityId)
    val isSpecial = draft.contestType.equals("ESPECIAL", ignoreCase = true)
    return AddBetRequestDto(
        modality = code,
        targetContest = draft.contestNumber,
        amount = draft.amount.takeUnless { draft.surprise },
        contestType = DomainCharacterDto(if (isSpecial) "2" else "1", draft.contestType),
        selectedNumbers = Gson().toJsonTree(
            if (draft.superSevenColumns.isNotEmpty()) draft.superSevenColumns else draft.numbers,
        ),
        selectedClovers = draft.clovers,
        surpriseIndicator = DomainIntegerDto(if (!draft.surprise) 1 else if (draft.modalityId == 20 && draft.surpriseTeamSelected) 3 else 2, ""),
        recurringDraws = draft.recurringDraws,
        quantity = draft.quantity,
        numberQuantity = draft.numberQuantity,
        cloverQuantity = draft.cloverQuantity,
        surprise = draft.surprise,
        favoriteTeam = draft.favoriteTeam?.toBettingTeamRequest(),
        lotecaMatches = draft.lotecaSelections.map { selection ->
            LotecaBetMatchDto(
                home = selection.match.home.toBettingTeamRequest(win = selection.homeWin),
                away = selection.match.away.toBettingTeamRequest(win = selection.awayWin),
                draw = selection.draw,
                number = selection.match.number,
            )
        }.takeIf { it.isNotEmpty() },
        lotogolMatches = draft.lotogolSelections.map { selection ->
            LotogolBetMatchDto(
                home = selection.match.home.toBettingTeamRequest(score = selection.homeScore.toString()),
                away = selection.match.away.toBettingTeamRequest(score = selection.awayScore.toString()),
            )
        }.takeIf { it.isNotEmpty() },
        luckyMonth = draft.luckyMonth?.toBettingMonthRequest(),
        generateMirror = draft.generateMirror.takeIf { it },
    )
}

private fun LuckyMonth.toBettingMonthRequest() = LuckyMonthParameterDto(
    number = number,
    name = name,
    abbreviation = abbreviation,
)
