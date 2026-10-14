package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.betting.LotecaMatchSelection
import br.gov.caixa.loterias.core.domain.betting.LuckyMonth
import br.gov.caixa.loterias.core.domain.betting.SportsMatch
import br.gov.caixa.loterias.core.domain.betting.SportsTeam
import br.gov.caixa.loterias.core.network.LotecaBetMatchDto
import br.gov.caixa.loterias.core.network.LuckyMonthParameterDto
import br.gov.caixa.loterias.core.network.SportsBetTeamDto

/** Gson may omit the request DTO's non-null metadata in the smaller legacy response. */
internal fun SportsBetTeamDto?.toDomainSportsTeam(): SportsTeam? {
    val team = this ?: return null
    val nested = team.parameterTeam
    val number = nested?.number ?: team.number
    val name = nested?.name ?: team.name.orEmpty()
    if (number <= 0 && name.isBlank()) return null
    return SportsTeam(
        number = number,
        name = name,
        state = nested?.state ?: team.state.orEmpty(),
        shortDescription = nested?.shortDescription ?: team.shortDescription.orEmpty(),
        longDescription = nested?.longDescription ?: team.longDescription.orEmpty(),
        countryNumber = nested?.countryNumber ?: team.countryNumber,
        country = nested?.country ?: team.country.orEmpty(),
        countryCode = nested?.countryCode ?: team.countryCode.orEmpty(),
        className = nested?.className ?: team.className.orEmpty(),
        selectionIndicator = nested?.selectionIndicator ?: team.selectionIndicator,
    )
}

internal fun LuckyMonthParameterDto?.toDomainLuckyMonth(): LuckyMonth? {
    val month = this ?: return null
    return LuckyMonth(month.number ?: return null, month.name.orEmpty(), month.abbreviation.orEmpty())
}

internal fun LotecaBetMatchDto.toDomainLotecaSelection(): LotecaMatchSelection? {
    val homeTeam = home.toDomainSportsTeam() ?: return null
    val awayTeam = away.toDomainSportsTeam() ?: return null
    return LotecaMatchSelection(
        match = SportsMatch(number, homeTeam, awayTeam, legendDescription.orEmpty(), legendLetter.orEmpty()),
        homeWin = home.win == true,
        draw = draw,
        awayWin = away.win == true,
    )
}
